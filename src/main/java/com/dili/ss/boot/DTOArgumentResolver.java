package com.dili.ss.boot;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.dto.DTO;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.dto.IDTO;
import com.dili.ss.util.POJOUtils;
import com.google.common.collect.Lists;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.support.WebArgumentResolver.UNRESOLVED;

/**
 * springMVC controller方法参数注入DTO
 * Created by asiamaster on 2017/8/2 0002.
 */
//@Component
public class DTOArgumentResolver implements HandlerMethodArgumentResolver {


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return IDTO.class.isAssignableFrom(parameter.getParameterType()) && parameter.getParameterType().isInterface();
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Class clazz = parameter.getParameterType();
		if(clazz != null && IDTO.class.isAssignableFrom(clazz)){
			return getDTO(clazz, webRequest);
		}
		return UNRESOLVED;
	}

	/**
	 * 取得当前的DTO对象<br>
	 * 注意： <li>此处没有考虑缓冲dto，主要是不ResourceData和QueryData是完全不同的类</li> <li>
	 * 应减少调用本方法的次数,如果今后有需求，可考虑加入缓冲</li>
	 *
	 * @param clazz
	 *            DTO对象的类，不允许为空
	 * @return 正常情况下不可能为空，但如果程序内部有问题时只能以null返回
	 */
	@SuppressWarnings("unchecked")
	protected <T extends IDTO> T getDTO(Class<T> clazz, NativeWebRequest webRequest) {
		// 实例化一个DTO数据对象
		DTO dto = new DTO();
		//处理restful调用时，传入的参数不在getParameterMap，而在getInputStream中的情况
		if(webRequest.getParameterMap().isEmpty()){
			try {
				ServletInputStream servletInputStream = ((RequestFacade)webRequest.getNativeRequest()).getInputStream();
				String inputString = InputStream2String(servletInputStream, "UTF-8");
				if(StringUtils.isNotBlank(inputString)) {
					JSONObject jsonObject = JSONObject.parseObject(inputString);
					for(Map.Entry<String, Object> entry : jsonObject.entrySet()){
						//单独处理metadata
						if(entry.getKey().startsWith("metadata[") && entry.getKey().endsWith("]")){
							dto.setMetadata(entry.getKey().substring(9, entry.getKey().length()-1), entry.getValue());
						}else{
							dto.put(entry.getKey(), entry.getValue());
						}
					}
					return DTOUtils.proxy(dto, clazz);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 填充值
		for (Map.Entry<String, String[]> entry : webRequest.getParameterMap().entrySet()) {
			String attrName = entry.getKey();
			if(attrName.startsWith("metadata[") && attrName.endsWith("]")){
				dto.setMetadata(attrName.substring(9, attrName.length()-1), getParamValueByForce(entry.getValue()));
			}else if (Character.isLowerCase(attrName.charAt(0))) {
				Object paramValue = null;
				//前台传入的多个相同name的value是数组，这里key以[]结尾
				if(attrName.endsWith("[]")){
					//去掉属性名后面的[]
					attrName = attrName.substring(0, attrName.length()-2);
					try {
						//有get方法的属性，需要判断返回值如果是Array或List，需要转换前台传的有多个相同name的value数组。
						Method getMethod = clazz.getMethod("get"+ attrName.substring(0, 1).toUpperCase() + attrName.substring(1));
						Class<?> returnType = getMethod.getReturnType();
						//List需要转换数组
						//这里entry.getValue()肯定是String[]
						if(List.class.isAssignableFrom(returnType)){
							paramValue = Lists.newArrayList((Object[])getParamObjValue(entry.getValue()));
						}else if(returnType.isArray()){
							paramValue = getParamObjValue(entry.getValue());
						}
					} catch (NoSuchMethodException e) {
						//没get方法的属性不处理
					}
				}
				if(paramValue == null) {
					paramValue = getParamValueByForce(entry.getValue());
				}
				dto.put(attrName, paramValue);
			}
		}
		return (T) DTOUtils.proxy(dto, (Class<IDTO>) clazz);
	}

	/**
	 * 强制取参数的值
	 *
	 * @param obj
	 *            当前的值对象
	 * @return 如果返回的字符串为空串,则认为是null
	 */
	private String getParamValueByForce(Object obj) {
		String val = getParamValue(obj);
		return val == null ? null : StringUtils.isBlank(val) ? null : val;
	}

	/**
	 * 取参数的对象值
	 * @param obj
	 * @return
	 */
	private Object getParamObjValue(Object obj) {
		return obj == null ? null : obj.getClass().isArray() ? java.io.File.class.isAssignableFrom(((Object[]) obj)[0].getClass()) ? null  : obj : obj;
	}

	/**
	 * 直接从值对象中取得其值<br>
	 * 由于Structs将值全部处理成了数组,但在通用情况下都是取数组中的一个值,但对Radio和checkbox等情况,可能会有多个值
	 *
	 * @param obj
	 * @return
	 */
	private String getParamValue(Object obj) {
		return (String) (obj == null ? null : obj.getClass().isArray() ? java.io.File.class.isAssignableFrom(((Object[]) obj)[0].getClass()) ? null  : ((Object[]) obj)[0] : obj);
	}

	final static int BUFFER_SIZE = 4096;
	/**
	 * 将InputStream转换成某种字符编码的String
	 * @param in
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String InputStream2String(InputStream in, String encoding) throws IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while((count = in.read(data,0,BUFFER_SIZE)) != -1) {
			outStream.write(data, 0, count);
		}
		data = null;
		return new String(outStream.toByteArray(), encoding);
	}

}
