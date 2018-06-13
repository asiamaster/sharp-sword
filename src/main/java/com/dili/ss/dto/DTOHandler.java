package com.dili.ss.dto;

/**
 * Created by asiamaster on 2017/7/31 0031.
 */

import com.alibaba.fastjson.JSON;
import com.dili.ss.util.POJOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
				delegate.put(field, args[0]);
				// 取值情况
			} else {
				// 如果包括这个字段，则空就是空
				Class<?> returnType = method.getReturnType();
				if (delegate.containsKey(field)) {
					retval = delegate.get(field);
					// 如果是基本类型
					if (returnType.isPrimitive()) {
						// 如果当前没有值,则取出初始值
						if (retval == null) {
							return POJOUtils.getPrimitiveDefault(returnType);
							// 如果返回值却不是该类型,则需要对基进行转换
						} else if (!returnType.equals(retval.getClass())) {
							return POJOUtils.getPrimitiveValue(returnType, retval);
						}
					} // 如果是空就不处理
					else if (retval == null){
						return null;
					} //如果是String型
					else if(String.class.equals(returnType)){
						return (String)retval;
					}
					//这里有可能不是String类型，但是通过aset方法传入一个空串，以下的类型都不接受空串，所以返回null
					if(StringUtils.isBlank(retval.toString())){
						return null;
					}
					// 如果返回值要求是枚举，但是结果却是字符串是需在此进行转换
					if (returnType.isEnum() && retval instanceof String) {
						retval = Enum.valueOf((Class<? extends Enum>) returnType, (String) retval);
					} // 如果是日期型
					else if (Date.class.isAssignableFrom(returnType)) {
						// 如果当前字段的值不是日期型, 转换返回值，并且将新的返回值填入委托对象中
						if(String.class.equals(retval.getClass())){
							retval = StringUtils.isNumeric(retval.toString()) ? new Date(Long.parseLong(retval.toString())) : DateUtils.parseDate(retval.toString(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
						} else if (Long.class.equals(retval.getClass())) {
							retval = new Date((Long)retval);
						}
					}//如果是Integer型
					else if(Integer.class.equals(returnType)){
						retval = Integer.parseInt(retval.toString());
					} //如果是Long型
					else if(Long.class.equals(returnType)){
						retval = Long.parseLong(retval.toString());
					} //如果是Float型
					else if(Float.class.equals(returnType)){
						retval =  Float.parseFloat(retval.toString());
					} //如果是Double型
					else if(Double.class.equals(returnType)){
						retval = Double.parseDouble(retval.toString());
					} //如果是Boolean型
					else if(Boolean.class.equals(returnType)){
						retval = Boolean.parseBoolean(retval.toString());
					}//如果是BigDecimal型
					else if(BigDecimal.class.equals(returnType)){
						retval = new BigDecimal(retval.toString());
					}//如果是Clob型
					else if(java.sql.Clob.class.equals(returnType)){
						retval = getClobString((java.sql.Clob)retval);
					}//如果是Instant型
					else if (Instant.class.isAssignableFrom(returnType)){
						if(String.class.equals(retval.getClass())){
							retval = Instant.from(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").withZone(ZoneId.systemDefault()).parse((String)retval));
						} else if(Long.class.equals(retval.getClass())){
							retval = Instant.ofEpochMilli((Long) retval);
						}
					}//如果是LocalDateTime型
					else if (LocalDateTime.class.isAssignableFrom(returnType)){
						if(String.class.equals(retval.getClass())){
							retval = LocalDateTime.parse((String)retval, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").withZone(ZoneId.systemDefault()));
						}else if(Long.class.equals(retval.getClass())){
							retval = LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) retval), ZoneId.systemDefault());
						}
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
			return delegate.get(((String) args[0]));
		} else if ("aset".equals(method.getName())) {
			return delegate.put(((String) args[0]), args[1]);
		} else if ("mget".equals(method.getName())) {
			if(args == null) {
				return metadata;
			}else {
				return metadata.get(((String) args[0]));
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
}