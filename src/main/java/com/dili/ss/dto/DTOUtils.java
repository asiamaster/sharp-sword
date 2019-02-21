package com.dili.ss.dto;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.exception.InternalException;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.MetadataUtils;
import com.dili.ss.metadata.ObjectMeta;
import com.dili.ss.util.BeanConver;
import com.dili.ss.util.CloneUtils;
import com.dili.ss.util.POJOUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * DTOData的工具类
 *
 * @author WangMi
 * @create 2017-7-31
 */
public class DTOUtils {
	// 日志对象
	protected static final Logger logger = LoggerFactory
			.getLogger(DTOUtils.class);

	private static final String INVALID_DELEGATE_ERROR = "类型不符合要求, 转换DTO的代理对象出错！";
	private static final String CREATE_PROXY_ERROR = "要创建代理的代理类({0})不是接口或者没有表示代理的接口！";
	private static final String TRANS_PROXY_ERROR = "转换DTO的代理对象出错！";
	// 错误消息
	private static final String TO_ENTITY_ERROR = "类为{0}的DTO对象转实体{1}出错!";
	/**
	 * 判断object是否DTO的一个代理对象
	 *
	 * @param object
	 * @return
	 */
	public static boolean isDTOProxy(Object object) {
		assert (object != null);
		return internalIsProxy(object, DTOHandler.class);
	}


	/**
	 * 将DTO对象或其代理对象统一还原回DTO对象
	 *
	 * @param obj
	 * @return 如果不是DTO对象实例或其代理对象，则返回null;
	 */
	public static DTO go(Object obj) {
		if (obj == null)
			return null;
		else if (obj instanceof DTO)
			return (DTO) obj;
		else if (isProxy(obj)) {
			DTOHandler handler = (DTOHandler) Proxy.getInvocationHandler(obj);
			return handler.getDelegate();
		}
		return null;
	}

	/**
	 * 转成其它任意的IDTO接口的对象
	 *
	 * @param <T>
	 * @param source 源DTO接口
	 * @param proxyClazz 目标DTO类型
	 * @return
	 */
	public static <T extends IDTO> T cast(IDTO source, Class<T> proxyClazz) {
		assert (source != null);
		assert (proxyClazz != null);
		return proxy((DTOHandler) Proxy.getInvocationHandler(source),
				proxyClazz);
	}

	/**
	 * 深克隆
	 * @param obj
	 * @param proxyClazz
	 * @param <T>
	 * @return
	 */
	public static <T extends IDTO> T clone(T obj, Class<T> proxyClazz){
		assert (obj != null);
		DTO dto = go(obj);
		if(dto == null){
			return null;
		}
		return DTOUtils.proxy(CloneUtils.clone(dto), proxyClazz);
	}

	/**
	 * 取DTO的实际类<br>
	 * <li>是代理对象时,则返回其代理接口</li>
	 * <li>是实际的DTO子类的实例时,则返回子类的类名</li>
	 *
	 * @param dto dto接口
	 * @return
	 */
	public final static Class<?> getDTOClass(Object dto) {
		assert (dto != null);
		if (Proxy.isProxyClass(dto.getClass())) {
			InvocationHandler handler = Proxy.getInvocationHandler(dto);
			if (handler instanceof DTOHandler) {
				return ((DTOHandler<?>) handler).getProxyClazz();
			}//这里单独处理fastjson的JSONObject，解决本框架retrofitful远程调用嵌套DTO问题
//			else if(handler instanceof JSONObject){
			else if("com.alibaba.fastjson.JSONObject".equals(handler.getClass().getName())){
				return IDTO.class;
			} else {
				throw new InternalException("当前代理对象不是DTOHandler能处理的对象!");
			}
		} else {
			return dto.getClass();
		}
	}

	/**
	 * 两个对象是否相等<br>
	 * 检查项：
	 * <li>两个对象的地址是否相同</li>
	 * <li>两个对象的标识是否相同，只要其中有一个的标识为null,则认为是不等的</li>
	 * 注意:此处没有检查两个都是DTO代理对象是否相等<br>
	 * 如果要做这样检查应该用对象的eqauls方法.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean isEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		Object id1 = getId(o1);
		Object id2 = getId(o2);
		return id1 == null ? false : id1.equals(id2);
	}

	/**
	 * 直接从DTO数据中取数据的唯一标识<br>
	 * 此处不管是否DTO对象，只是按照DTO数据的操作规范，统一用POJOUtils来取值
	 *
	 * @param object
	 *          DTO对象的实例
	 * @return
	 */
	public static Object getId(Object object) {
		return getProperty(object, IBaseDomain.ID);
	}

	/**
	 * 是否DTO的一个代理对象
	 *
	 * @param object
	 * @return
	 */
	public static boolean isProxy(Object object) {
		assert (object != null);
		return internalIsProxy(object, DTOHandler.class);
	}

	/**
	 * 将DTO对象的实例转成代理的目标接口或对象
	 *
	 * @param realObj
	 *          DTO对象实例
	 * @param <T>
	 *          结果类
	 * @return proxyClz不是接口或者没有父接口，有可能出DTOProxyException异常
	 */
	public static <T extends IDTO> T proxy(DTO realObj, Class<T> proxyClz) {
		assert (realObj != null);
		assert (proxyClz != null);
		T dto = internalProxy(realObj, proxyClz, DTOHandler.class);
		dto.mset(realObj.getMetadata());
		return dto;
	}

	/**
	 * 根据proxyClz new 一个DTO对象
	 * @param proxyClz <T extends IDTO>接口
	 * @return
	 */
	public static <T extends IDTO> T newDTO(Class<T> proxyClz) {
		return proxy(new DTO(), proxyClz);
	}

	/**
	 * 将一个实体、DTO实例或DTO的代理类列表，重新转成另外一个代理对象列表
	 *
	 * @param <T>
	 * @param sources
	 *          DTO、DTO接口或实体的List
	 * @param proxyClz<T extends IDTO>
	 *          要转成的目标代理类
	 * @return 有可能出DTOProxyException异常
	 */
	public static <T extends IDTO> List<T> as(List sources, Class<T> proxyClz) {
		assert (sources != null);
		assert (proxyClz != null);

		List<T> list = new ArrayList<T>(sources.size());
		for (Object source : sources) {
			list.add(internalAs(source, proxyClz, DTOHandler.class));
		}
		return list;
	}

	/**
	 * 将一个实体、DTO实例或DTO的代理类，重新转成另外一个代理对象
	 *
	 * @param <T>
	 * @param source
	 *          已被代理的DTO对象
	 * @param proxyClz
	 *          要转成的目标代理类
	 * @return 有可能出DTOProxyException异常
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IDTO> T as(Object source, Class<T> proxyClz) {
		assert (source != null);
		assert (proxyClz != null);
		return internalAs(source, proxyClz, DTOHandler.class);
	}

	/**
	 * 将实体集合转换成DTO代理对象集合
	 *
	 * @param <T>
	 * @param sources
	 *          实体对象List
	 * @param proxyClz
	 *          要转成的目标代理类
	 * @return 有可能出异常
	 */
	public static <T extends IDTO> List<T> switchEntityListToDTOList(List<BaseDomain> sources, Class<T> proxyClz) {
		assert (sources != null);
		assert (proxyClz != null);
		List<T> list = new ArrayList<T>(sources.size());
		for (BaseDomain source : sources) {
			list.add(switchEntityToDTO(source, proxyClz));
		}
		return list;
	}

	/**
	 * 将实体转换DTO代理对象
	 *
	 * @param <T>
	 * @param source
	 * @param proxyClz
	 * @return
	 */
	public static <T extends IDTO> T switchEntityToDTO(BaseDomain source, Class<T> proxyClz) {
		if(source==null||proxyClz==null) return null;
		T temp = DTOUtils.newDTO(proxyClz);
		try {
			org.springframework.beans.BeanUtils.copyProperties(temp, source);
		}catch(Exception e) {
			e.printStackTrace(System.err);
		}
		return temp;
	}

	/**
	 * 将一个DTO的列表代理成另一个DTO接口对象的列表
	 *
	 * @param <T>
	 * @param realList
	 * @param proxyClz
	 * @return 有可能出异常,但没有异常时必定会有返回值
	 */
	public static <T extends IDTO> List<T> proxy(List<? extends DTO> realList, Class<T> proxyClz) {
		assert (proxyClz != null);
		// 如果列表为空，则返回一个空的
		if (realList == null)
			return Collections.EMPTY_LIST;

		// 进行处理
		return new DTOList<T>(proxyClz, realList);
	}

	/**
	 * 判断是否为DTO对象的实例<br>
	 * 包括以下两种情况：
	 * <li>本身就是一个DTO对象的实例</li>
	 * <li>DTO对象的代理对象</li>
	 *
	 * @param object
	 * @return
	 */
	public static boolean isInstance(Object object) {
		return object instanceof DTO || internalIsProxy(object, DTOHandler.class);
	}

	/**
	 * DTO对象转成实体对象
	 *
	 * @param <M>
	 *          DTO的类,注可以是接口
	 * @param <N>
	 *          Entity的类, 必须是从EntityBase上继承
	 * @param sourceDTO
	 *          DTO对象的实例或其代理,作为源
	 * @param entityClazz
	 *          实体的类名, 作为目标
	 * @param enhance
	 *          增强转换，即会把隐藏的属性也转换到Entity里面
	 * @return
	 */
	public static <M, N extends BaseDomain> N toEntity(M sourceDTO, Class<N> entityClazz, boolean enhance) {
		assert (sourceDTO != null);
		assert (DTOUtils.isInstance(sourceDTO));
		assert (entityClazz != null);
		try {
			if(enhance) {
				return BeanConver.copyMap(go(sourceDTO), entityClazz);
			}else {
				return BeanConver.copyBean(sourceDTO, entityClazz);
			}
		} catch (Exception e) {
			String message = MessageFormat.format(TO_ENTITY_ERROR, sourceDTO.getClass().getName(), entityClazz.getName());
			logger.error(message, e);
		}
		return null;
	}

	/**
	 * 将DTO的属性值都转换为UTF-8字符串的
	 * @param dto
	 * @param <T>
	 * @throws UnsupportedEncodingException
	 */
	public static <T extends IDTO> void decodeDTO2UTF8(T dto) throws UnsupportedEncodingException {
		ObjectMeta om = MetadataUtils.getDTOMeta(DTOUtils.getDTOClass(dto));
		for(FieldMeta fm : om){
			if(String.class.isAssignableFrom(fm.getType())){
				DTO dd = DTOUtils.go(dto);
				if(dd == null || dd.isEmpty()) return;
				if(dd.get(fm.getName()) != null) {
					dd.put(fm.getName(), new String(dd.get(fm.getName()).toString().getBytes("ISO8859-1"), "UTF-8"));
				}
			}
		}
	}

	/**
	 * 将两个DTO连接起来<br>
	 * 要求两个DTO的字段没有重复的,有重复的则以master为准
	 *
	 * @param <T>
	 * @param master
	 * @param second
	 * @param masterClazz
	 * @return
	 */
	public static <T extends IDTO> T link(T master, IDTO second, Class<T> masterClazz) {
		if (second == null)
			return master;
		if (master == null) {
			return as(second, masterClazz);
		}
		DTO temp = go(second);
		temp.putAll(go(master));
		return as(second, masterClazz);
	}

	//  ===================================  内部方法  ===================================

	/**
	 * 按属性名取值<br>
	 * 不提供给外部程序来使用
	 *
	 * @param object
	 * @return
	 */
	final static Object getProperty(Object object, String name) {
		if(isDTOProxy(object)){
			return POJOUtils.getProperty(go(object), name);
		}
		return POJOUtils.getProperty(object, name);
	}

	/**
	 * 设置属性<br>
	 * 不提供给外部程序来使用
	 *
	 * @param object
	 * @return
	 */
	final static Object setProperty(Object object, String name,
	                                Object value) {
		POJOUtils.setProperty(object, name, value);
		return object;
	}

	/**
	 * 将DTO对象的实例转成代理的目标接口或对象<br>
	 * 缺省值，只有在代理成功后才加入
	 *
	 * @param <T>
	 *            要输出的DTO接口类名
	 * @param realObj
	 *            DTO对象实例
	 * @return 有可能出异常
	 */
	@SuppressWarnings("unchecked")
	final static <T extends IDTO> T internalProxy(DTO realObj,
	                                              Class<T> proxyClz, Class<? extends DTOHandler> handlerClazz) {
		assert (handlerClazz != null);
		assert (realObj != null);
		assert (proxyClz != null);

		T retval = null;
		// 如果是接口方式,则直接根据接口来创建
		if (proxyClz.isInterface()) {
			retval = (T) Proxy.newProxyInstance(proxyClz.getClassLoader(),
					new Class<?>[] { proxyClz }, newDTOHandler(
							handlerClazz, proxyClz, realObj));
		} else {
			// 否则,查找类实现的接口来创建
			Class<?>[] interfaces = proxyClz.getInterfaces();
			if (interfaces != null) {
				retval = (T) Proxy.newProxyInstance(proxyClz.getClassLoader(),
						interfaces, newDTOHandler(handlerClazz, proxyClz,
								realObj));
			} else {
				String message = MessageFormat.format(CREATE_PROXY_ERROR,
						proxyClz.getName());
				logger.warn(message);
				throw new DTOProxyException(message);
			}
		}
		// 加入缺省值
		generateDefaultValue(realObj, proxyClz);
		return retval;
	}

	/**
	 * 将一个DTO实例或DTO的代理类，重新转成另外一个代理对象
	 *
	 * @param <T>
	 * @param source
	 *            已被代理的DTO对象
	 * @param proxyClz
	 *            要转成的目标代理类
	 * @return 有可能出异常
	 */
	@SuppressWarnings("unchecked")
	final static <T extends IDTO> T internalAs(Object source,
	                                           Class<T> proxyClz, Class<? extends DTOHandler> handlerClazz) {
		assert (handlerClazz != null);
		assert (source != null);
		assert (proxyClz != null);

		if (source instanceof DTO) {
			return internalProxy((DTO) source, proxyClz, handlerClazz);
		} else if (source.getClass().isAssignableFrom(proxyClz)) {
			return (T) source;
		} else if (internalIsProxy(source, handlerClazz)) {
			try {
				DTOHandler handler = (DTOHandler) Proxy
						.getInvocationHandler(source);
				return proxy(handler, proxyClz);
			} catch (Exception ex) {
				logger.warn(TRANS_PROXY_ERROR);
				throw new DTOProxyException(TRANS_PROXY_ERROR);
			}
		} else if( source instanceof BaseDomain){
			switchEntityToDTO((BaseDomain)source, proxyClz);
		} else {
			DTO dto = new DTO();
			Method[] methods = source.getClass().getMethods();
			for(Method method : methods){
				//get方法，且不能有参数
				if(POJOUtils.isGetMethod(method) && method.getParameters().length == 0){
					String fieldName = POJOUtils.getBeanField(method);
					try {
						dto.put(fieldName, method.invoke(source));
					} catch (Exception ex) {
						logger.warn(TRANS_PROXY_ERROR);
						throw new DTOProxyException(TRANS_PROXY_ERROR);
					}

				}
			}
		}
		logger.warn(INVALID_DELEGATE_ERROR);
		throw new DTOProxyException(INVALID_DELEGATE_ERROR);
	}

	/**
	 * 是否DTO的一个代理对象
	 *
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final static boolean internalIsProxy(Object object,
	                                     Class<? extends DTOHandler> handlerClazz) {
		assert (object != null);
		assert (handlerClazz != null);
		// 如果是代理类，则检查代理处理器是否为DTOHandler,此处认为所有的DTO的代理处理器都是DTOHandler
		if (Proxy.isProxyClass(object.getClass())) {
			try {
				InvocationHandler handler = Proxy.getInvocationHandler(object);
				return handlerClazz.isAssignableFrom(handler.getClass());
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	/**
	 * 为减少一个处理器对象，直接将当前的代理对象强制转型<br>
	 * 缺省值，只有在代理成功后才加入
	 *
	 * @param <T>
	 * @param handler
	 * @param proxyClz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final static <T extends IDTO> T proxy(DTOHandler handler,
	                                      Class<T> proxyClz) {
		T retval = null;
		// 是接口
		if (proxyClz.isInterface()) {
			retval = (T) Proxy.newProxyInstance(proxyClz.getClassLoader(),
					new Class<?>[] { proxyClz }, handler);
			// 不是接口
		} else {
			Class<?>[] interfaces = proxyClz.getInterfaces();
			if (interfaces != null) {
				retval = (T) Proxy.newProxyInstance(proxyClz.getClassLoader(),
						interfaces, handler);
			} else {
				String message = MessageFormat.format(CREATE_PROXY_ERROR,
						proxyClz.getName());
				logger.warn(message);
				throw new DTOProxyException(message);
			}
		}
		// 根据MetaData生成缺省值
		generateDefaultValue(handler.getDelegate(), proxyClz);
		// 改变代理对象
		handler.setProxyClazz(proxyClz);
		return retval;
	}

	/**
	 * DTO的Handler
	 *
	 * @param handlerClazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final static DTOHandler newDTOHandler(
			Class<? extends DTOHandler> handlerClazz, Class proxyClazz,
			Object realObj) {
		try {
			Constructor<? extends DTOHandler> method = null;
			if (DTOHandler.class.isAssignableFrom(handlerClazz)) {
				method = handlerClazz.getConstructor(new Class[] { Class.class,
						DTO.class });
			}
			else {
				method = handlerClazz.getConstructor(new Class[] { Class.class,
						realObj.getClass() });
			}
			return method.newInstance(new Object[] { proxyClazz, realObj });
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DTOProxyException(CREATE_PROXY_ERROR, e);
		}
	}

	/**
	 * 根据Meta生成默认值
	 *
	 * @param dtoData
	 * @param proxyClz
	 */
	@SuppressWarnings("unchecked")
	static void generateDefaultValue(DTO dtoData, Class<?> proxyClz) {
		ObjectMeta objectMeta = MetadataUtils.getDTOMeta(proxyClz);
		for (FieldMeta fieldMeta : objectMeta) {
			// 如果在DTO中已经有值，就不管了
			if (dtoData.containsKey(fieldMeta.getName()))
				continue;

			// 检查是否有缺省值
			String defStr = fieldMeta.getDefValue();
			Class<?> type = fieldMeta.getType();

			// 有默认值
			if (StringUtils.isNotBlank(defStr)) {
				if (type.isEnum()) { // 枚举值
					try {
						dtoData.put(fieldMeta.getName(), Enum.valueOf(
								(Class<? extends Enum>) type, defStr));
					} catch (RuntimeException e) {
						logger.warn("设置默认值时出错：", e);
					}
				} else if (type.isPrimitive()) {// 基本类型
					dtoData.put(fieldMeta.getName(), POJOUtils
							.getPrimitiveValue(type, defStr));
				} else if (Date.class.isAssignableFrom(type)) {// 日期
					try {
						dtoData.put(fieldMeta.getName(), DateUtils.parseDate(defStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"));
					} catch (ParseException e) {
						e.printStackTrace();
						logger.warn("设置默认值时出错：", e);
					}
				} else {
					try {
						dtoData.put(fieldMeta.getName(), ConvertUtils.convert(
								defStr, type));
					} catch (Exception e) {
						logger.warn("设置默认值时出错：", e);
					}
				}
				// 无默认值且是基本类型
//			} else if (type.isPrimitive()) {
//				dtoData.put(fieldMeta.getName(), POJOUtils
//						.getPrimitiveDefault(type));
			}
		}
	}

}