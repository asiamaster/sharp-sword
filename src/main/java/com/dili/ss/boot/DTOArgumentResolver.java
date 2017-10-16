package com.dili.ss.boot;

import com.dili.ss.dto.DTO;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.dto.IDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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
		// 填充值
		for (Map.Entry<String, String[]> entry : webRequest.getParameterMap().entrySet()) {
			String attrName = entry.getKey();
			if(attrName.startsWith("metadata[") && attrName.endsWith("]")){
				dto.setMetadata(attrName.substring(9, attrName.length()-1), getParamValueByForce(entry.getValue()));
			}else if (Character.isLowerCase(attrName.charAt(0))) {
				String paramValue = getParamValueByForce(entry.getValue());
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
	 * 直接从值对象中取得其值<br>
	 * 由于Structs将值全部处理成了数组,但在通用情况下都是取数组中的一个值,但对Radio和checkbox等情况,可能会有多个值
	 *
	 * @param obj
	 * @return
	 */
	private String getParamValue(Object obj) {
		return (String) (obj == null ? null : obj.getClass().isArray() ? java.io.File.class.isAssignableFrom(((Object[]) obj)[0].getClass()) ? null  : ((Object[]) obj)[0] : obj);
	}

}
