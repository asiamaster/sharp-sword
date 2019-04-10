package com.dili.ss.dto;

/**
 * Created by asiamaster on 2017/7/31 0031.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.ss.metadata.annotation.FieldDef;
import com.dili.ss.util.POJOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * DTO层的代理处理器
 *
 * @author WangMi
 * @create 2010-6-2
 * @param <T>
 *          DTO的子类
 */
public class DTOHandler<T extends DTO> implements InvocationHandler, Serializable {
	private static final long serialVersionUID = -7340937653355927470L;
	// 代理的类名
	private Class<?> proxyClazz;
	// 委托对象
	private T delegate;

	private Map<String, Object> metadata;

	/**
	 * 约定的构造器
	 *
	 * @param proxyClazz
	 * @param delegate
	 */
	public DTOHandler(Class<?> proxyClazz, T delegate) {
		this.proxyClazz = proxyClazz;
		this.delegate = delegate;
		metadata = new HashMap<String, Object>(4);
	}

	/**
	 * 进行代理调用<br>
	 * 当前没有进行类型转换，估计需要进行类型转换
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 是Bean的Get或Set方法时
		if (POJOUtils.isBeanMethod(method)) {
			String field = POJOUtils.getBeanField(method);
			Object retval = null;
			// 设置情况
			if (POJOUtils.isSetMethod(method)) {
				assert (args != null);
				assert (args.length > 0);
				FieldDef fieldDef = method.getAnnotation(FieldDef.class);
				if(fieldDef == null || fieldDef.handler() == Function.class){
					delegate.put(field, args[0]);
				}else{
					delegate.put(field, fieldDef.handler().newInstance().apply(args[0]));
				}

			// 取值情况
			} else {
				//getter方法返回类型
				Class<?> returnType = method.getReturnType();
				FieldDef fieldDef = method.getAnnotation(FieldDef.class);
				//如果FieldDef注解不为空，并且有实现类
				if(fieldDef != null && !fieldDef.handler().isInterface()) {
					return fieldDef.handler().newInstance().apply(delegate.get(field));
				}
				//代理对象的key中包含getter方法的属性名称，需要设置(转换)当前代理对象中该值的类型
				if (delegate.containsKey(field)) {
					retval = delegate.get(field);
					//先判断可以提前返回的条件(不需要转换类型)
					// 必须先判断是基本类型(基本类型的返回值不允许为空)
					if (returnType.isPrimitive()) {
						// 如果当前没有值,则取出初始值
						if (retval == null) {
							return POJOUtils.getPrimitiveDefault(returnType);
							// 如果有返回值却不是该类型,则需要进行转换
						} else if (!returnType.equals(retval.getClass())) {
							return POJOUtils.getPrimitiveValue(returnType, retval);
						} else{
							return retval;
						}
					} // 如果是空就不处理
					else if (retval == null){
						return null;
					} //如果是String型,直接toString()
					else if(String.class.equals(returnType)){
						return retval.toString();
					}//如果返回类型和取值对象的类型相同，则直接返回取值对象
					else if(returnType.isAssignableFrom(DTOUtils.getDTOClass(retval))){
						return retval;
					}//这里有可能不是String类型，但是通过aset方法传入一个空串，以下的类型都不接受空串，所以返回null
					else if(StringUtils.isBlank(retval.toString())){
						return null;
					}
					//再判断需要转换类型的字段
					//通过转换工厂减少if else， 提高效率
					Object convertedValue = ReturnTypeHandlerFactory.convertValue(returnType, retval);
					if(convertedValue != null){
						delegate.put(field, convertedValue);
						return convertedValue;
					}
					//如果返回值是代理IDTO对象且返回类型是IDTO对象的子类，需要根据返回值转为相应的DTO
					//这里主要处理fastjson的远程调用转换问题
					if(IDTO.class.isAssignableFrom(DTOUtils.getDTOClass(retval)) && IDTO.class.isAssignableFrom(returnType)){
						JSONObject jo = ((JSONObject) Proxy.getInvocationHandler(retval));
						retval = DTOUtils.as(new DTO(jo), (Class<IDTO>)returnType);
					}
					//如果返回值要求是枚举，但是结果却是字符串是需在此进行转换
					else if (returnType.isEnum() && retval instanceof String) {
						retval = Enum.valueOf((Class<? extends Enum>) returnType, (String) retval);
					}
					delegate.put(field, retval);
					// 否则需要返回缺省值
				} else if (returnType.isPrimitive()) {
					return POJOUtils.getPrimitiveDefault(returnType);
				}
			}
			return retval;
			// 否则直接调用这个方法
		} else if ("aget".equals(method.getName())) {
			return delegate.get(args[0]);
		} else if ("aset".equals(method.getName())) {
			return delegate.put(((String) args[0]), args[1]);
		} else if ("mget".equals(method.getName())) {
			if(args == null) {
				return metadata;
			}else {
				return metadata.get(args[0]);
			}
		} else if ("mset".equals(method.getName())) {
			if(args.length == 1 && args[0] instanceof Map){
				metadata.putAll((Map) args[0]);
				return null;
			}else {
				return metadata.put(((String) args[0]), args[1]);
			}
		} else if ("toString".equals(method.getName()) && args == null) {
			String data = delegate == null ? "" : JSON.toJSONString(delegate);
			String meta = metadata == null ? "" : JSON.toJSONString(metadata);
			StringBuilder stringBuilder = new StringBuilder(proxyClazz.getName());
			stringBuilder.append("\r\ndata:").append(data).append("\r\nmeta:").append(meta);
			return stringBuilder.toString();
		}else {
			return method.invoke(delegate, args);
		}
	}

	public static String getClobString(java.sql.Clob c) {
		try {
			Reader reader=c.getCharacterStream();
			if (reader == null) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
			char[] charbuf = new char[4096];
			for (int i = reader.read(charbuf); i > 0; i = reader.read(charbuf)) {
				sb.append(charbuf, 0, i);
			}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 当前的代理接口
	 *
	 * @return proxyClazz
	 */
	public Class<?> getProxyClazz() {
		return proxyClazz;
	}

	/**
	 * 取委托的对象
	 *
	 * @return delegate
	 */
	T getDelegate() {
		return delegate;
	}

	/**
	 * 取meta信息
	 * @return
	 */
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	void setProxyClazz(Class<?> proxyClazz) {
		this.proxyClazz = proxyClazz;
	}

//	public static void main(String[] args) {
//		ScheduleJob scheduleJob = DTOUtils.newDTO(ScheduleJob.class);
//		scheduleJob.setJobStatus(3);
//		scheduleJob.setJobName("jobName");
//		scheduleJob.setCreated(new Date());
//		Test test = DTOUtils.newDTO(Test.class);
//		test.aset("id", 1L);
//		test.aset("created", new Date());
//		test.aset("jobName", "jobName");
//		test.aset("jobStatus", "");
//		test.aset("time", LocalDateTime.now());
//		test.aset("instant", Instant.now());
//		test.aset("job", scheduleJob);
//		test.aset("number", 6);
//		test.aset("relationOperator", "Equal");
//		System.out.println(test.getId());
//		System.out.println(test.getCreated());
//		System.out.println(test.getJobName());
//		System.out.println(test.getJobStatus());
//		System.out.println(test.getTime());
//		System.out.println(test.getInstant());
//		System.out.println(test.getJob());
//		System.out.println(test.getNumber());
//		System.out.println(test.getRelationOperator());
//	}
//
//	interface Test extends ScheduleJob{
//		LocalDateTime getTime();
//		void setTime(LocalDateTime localDateTime);
//
//		Instant getInstant();
//		void setInstant(Instant instant);
//
//		ScheduleJob getJob();
//		void setJob(ScheduleJob scheduleJob);
//
//		BigDecimal getBig();
//		void setBig(BigDecimal big);
//
//		int getNumber();
//		void setNumber(int number);
//
//		RelationOperator getRelationOperator();
//		void setRelationOperator(RelationOperator relationOperator);
//	}
}