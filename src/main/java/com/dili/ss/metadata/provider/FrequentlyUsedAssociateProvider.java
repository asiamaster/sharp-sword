package com.dili.ss.metadata.provider;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.ss.service.CommonService;
import com.dili.ss.util.RedisUtil;
import com.dili.ss.util.SpringUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 基于LRU(Least Recently Used,最近最少使用)算法的常用数据联想框<br/>
 * <pre>
 * 使用示例:
 * <b>html:</b>
 * &lt;input name="name" id="name" style="width:100%;" panelWidth="auto" panelHeight="auto" label="名称(联想输入):" labelWidth="100" data-options="editable:true, hasDownArrow:false, onChange:chengeName"/&gt;
 * <#comboProvider _id="name" _provider="lruProvider" _valueField="value" _textField="label" _queryParams='{model:"producing_area"}'/>
 * <b>javascript:</b>
 * function chengeName(newValue, oldValue){
 *  if(newValue && newValue.length >= 2){
 *      var opts = $(this).combobox("options");
 *      opts.queryParams.value = $(this).combobox("getValue");
 *      $(this).combobox("reload");
 *  }
 * }
 * <b>最后在application.properties配置:</b>
 * fuaProvider.models[0]=producing_area
 * fuaProvider.persistenceType=redis
 * </pre>
 * Created by asiamaster on 2017/8/30 0022.
 */
@Component("fuaProvider")
@ConditionalOnExpression("'${fuaProvider.enable}'=='true'")
@ConfigurationProperties(prefix = "fuaProvider")
public class FrequentlyUsedAssociateProvider implements ApplicationListener<ContextRefreshedEvent>, ValueProvider {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FrequentlyUsedAssociateProvider.class);
	//设置并发级别为8，并发级别是指可以同时写缓存的线程数
	private final static int concurrencyLevel = 8;
	//设置缓存容器的初始容量为10
	private final static int initialCapacity = 10;
	//设置缓存最大容量为100，超过100之后就会按照LRU最近最少使用算法来移除缓存项
	private final static int maximumSize = 100;

	//首次命中指定次数后进行持久化
	private final static int FIRST_HIT_SAVE_TIMES = 3;
	//命中指定次数后进行持久化
	private final static int MAX_HIT_SAVE_TIMES = 10;
	//最多联想10条（百度才4条）
	private final static int MAX_ASSOCIATE_COUNT = 10;
	//    初始线程数
	private int corePoolSize = 2;
	//    极限线程数，初始线程不够用时 申请新的线程
	private int maximumPoolSize = 4;
	//    30秒   配合allowCoreThreadTimeOut参数
	private int keepAliveTime = 30;
	//定义comboProvider的queryParams中需要传入的key的参数名
	private final static String MODEL_KEY = "model";
	private Map<String, LoadingCache<String, Integer>> cacheMap = new HashMap<>(8);
	private static final String QUERY_PARAMS_KEY = "queryParams";
	//    多线程执行器
	private ThreadPoolExecutor executor;
	//=============================================  配置文件信息st  =============================================
	//缓存多个lru数据模型,key为表名
	private List<String> models;
	//持久化类型, mysql或redis，默认为redis
	private String persistenceType = "redis";

	//=============================================  配置文件信息end  =============================================
	//用于mysql持久化
	@Autowired
	private CommonService commonService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10000));
		executor.allowCoreThreadTimeOut(true);
		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				LOGGER.error("["+this.getClass().getSimpleName()+"]消息处理线程池：线程池已满，无法接收处理-新任务：" + r.toString());
				if (!executor.isShutdown()) {
					Thread th = new Thread(r);
					th.start();
				}
			}
		});
		//非spring boot项目在这里判断是为了只初始化一次，
		// contextRefreshedEvent.getApplicationContext().getParent() == null说明当前ApplicationContext是web applicationContext
//		if(contextRefreshedEvent.getApplicationContext().getParent() == null) {
			if(models == null || models.isEmpty()) {
				return;
			}
			for (String key : models) {
				initByKey(key);
			}
//		}
	}

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		if(null == val || StringUtils.isBlank(val.toString())) {
			return null;
		}
		List<ValuePair<?>> buffer = new ArrayList<ValuePair<?>>();
		JSONObject params = JSONObject.parseObject(metaMap.get(QUERY_PARAMS_KEY).toString());
		String key = params.getString(MODEL_KEY);
		if(StringUtils.isBlank(key)) {
			return null;
		}
		ConcurrentMap<String, Integer> loadingCacheMap = cacheMap.get(key).asMap();
		TreeSet<String> treeSet = new TreeSet<>(new ValueComparator(loadingCacheMap));
		//使用TreeSet排序
		for (Map.Entry<String, Integer> entry : loadingCacheMap.entrySet()) {
			if(entry.getKey().contains(val.toString())){
				treeSet.add(entry.getKey());
			}
		}

		int treeSetIndex = 0;
		//将排好序的TreeSet放入buffer, 大于MAX_ASSOCIATE_COUNT后跳出，以限制联想数量
		for(String value : treeSet){
			buffer.add(new ValuePairImpl(value, value));
			treeSetIndex++;
			if(treeSetIndex > MAX_ASSOCIATE_COUNT) {
				break;
			}
		}
		return buffer;
	}

	/**
	 * 根据key(表名)加入新的值
	 * @param key   表名
	 * @param value 搜索词
	 */
	public void save(String key, String value){
		if(StringUtils.isBlank(value)) {
			return;
		}
		//获取命中次数
		Integer hitTimes = cacheMap.get(key).getUnchecked(value);
		//首次命中FIRST_HIT_SAVE_TIMES次或者每命中MAX_HIT_SAVE_TIMES次，进行持久化
		if (hitTimes.equals(FIRST_HIT_SAVE_TIMES) || hitTimes % MAX_HIT_SAVE_TIMES == 0) {
			persist(key, value, hitTimes);
		}
		cacheMap.get(key).refresh(value);
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		return null;
	}

	//根据key初始化一个LoadingCache,提高运行时效率
	private void initByKey(String key) {
		cacheMap.put(key, initCache(key));
	}

	//初始化并获取一个LoadingCache
	private LoadingCache<String, Integer> initCache(String key) {
		//缓存接口这里是LoadingCache，LoadingCache在缓存项不存在时可以自动加载缓存
		//CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
		LoadingCache<String, Integer> loadingCache = CacheBuilder.newBuilder()
				//设置并发级别为8，并发级别是指可以同时写缓存的线程数
				.concurrencyLevel(concurrencyLevel)
				//设置写缓存后8秒钟过期
//				.expireAfterWrite(8, TimeUnit.SECONDS)
				//设置缓存容器的初始容量为10
				.initialCapacity(initialCapacity)
				//设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
				.maximumSize(maximumSize)
				//设置缓存的移除通知
//				.removalListener(new RemovalListener<Object, Object>() {
//					@Override
//					public void onRemoval(RemovalNotification<Object, Object> notification) {
//						System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
//					}
//				})
				//build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
				.build(
						new CacheLoader<String, Integer>() {
							@Override
							public Integer load(String key) throws Exception {
								return 1;
							}

							@Override
							public ListenableFuture<Integer> reload(String key, Integer oldValue) throws Exception {
								checkNotNull(key);
								checkNotNull(oldValue);
								return Futures.immediateFuture(oldValue + 1);
							}
						}
				);
		initFromDb(loadingCache, key);
		return loadingCache;
	}

	//从数据库或redis中初始化数据
	private void initFromDb(LoadingCache<String, Integer> loadingCache, String key) {
		if ("redis".equals(persistenceType)) {
			//没数据则直接返回
			if(!getRedisUtil().getRedisTemplate().hasKey(key)) {
				return;
			}
			Map<String, Integer> map = getRedisUtil().getRedisTemplate().boundHashOps(key).entries();
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				loadingCache.put(entry.getKey(), entry.getValue());
			}
		}else{
			StringBuilder createTableSql = new StringBuilder("create table if not exists ")
					.append(key)
					.append("\n(\n")
					.append("   id                   bigint not null auto_increment,\n")
					.append("   name                 varchar(120) not null comment '联想字段',\n")
					.append("   value                int comment '出现次数',\n")
					.append("   primary key (id),\n")
					.append("   unique key AK_uk_name (name)\n")
					.append(")");
			//检查到没有表，则创建
			commonService.execute(createTableSql.toString());
			List<JSONObject> list = commonService.selectJSONObject("select name, value from " + key, 1, Integer.MAX_VALUE);
			for(JSONObject jo : list) {
				loadingCache.put(jo.getString("name"), jo.getInteger("value"));
			}
		}
	}

	//持久化数据
	private void persist(String key, String value, Integer hitTimes) {
		LoadingCache<String, Integer> loadingCache = cacheMap.get(key);
		executor.execute(() ->{
			if ("redis".equals(persistenceType)) {
				//先清空,再重新持久化
//				getRedisUtil().getRedisTemplate().delete(key);
//				for (Map.Entry<String, Integer> entry : loadingCache.asMap().entrySet()) {
//					getRedisUtil().getRedisTemplate().boundHashOps(key).put(entry.getKey(), entry.getValue());
//				}
				getRedisUtil().getRedisTemplate().boundHashOps(key).put(value, hitTimes);
			} else {
				//先清空
//				StringBuilder sql = new StringBuilder("delete from " + key + ";");
//				for (Map.Entry<String, Integer> entry : loadingCache.asMap().entrySet()) {
//					sql.append("insert into " + key + " (`name`, `value`) values ('" + entry.getKey() + "', '" + entry.getValue() + "');");
//				}
				StringBuilder sql = new StringBuilder("replace into `").append(key).append("` (`name`, `value`) values ('").append(value).append("', ").append(hitTimes).append(")");
//				sql = new StringBuilder("update ").append(key).append(" set `value`=").append(hitTimes).append(" where `name`='").append(value).append("'");
				commonService.execute(sql.toString());
			}
		});
	}

	public List<String> getModels() {
		return models;
	}

	public void setModels(List<String> models) {
		this.models = models;
	}

	public String getPersistenceType() {
		return persistenceType;
	}

	public void setPersistenceType(String persistenceType) {
		this.persistenceType = persistenceType;
	}

	//因为有可能用户不配置redis.enable=true，所以只能在运行时获取
	private RedisUtil getRedisUtil() {
		return SpringUtil.getBean(RedisUtil.class);
	}

}
