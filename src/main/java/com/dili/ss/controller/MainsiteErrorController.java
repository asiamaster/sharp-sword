package com.dili.ss.controller;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.util.SystemConfigUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by asiamaster on 2017/11/30 0030.
 */
@Controller("error")//参考ErrorMvcAutoConfiguration
@RequestMapping("/error")
public class MainsiteErrorController implements ErrorController {

	protected static final Logger log = LoggerFactory.getLogger(MainsiteErrorController.class);
	private static final String ERROR_PATH = "/error";
	@Autowired
	private ErrorAttributes errorAttributes;

	/**
	 * ajax请求错误<br/>
	 * 前端ajax获取方式:
	 * error: function(XMLHttpRequest, textStatus, errorThrown){
	 * 		alert("responseText:"+XMLHttpRequest.responseText);
	 * }
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public ResponseEntity<Map<String, Object>> ajaxError(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> body = getErrorAttributes(request, true);
		HttpStatus status = getStatus(request);
		log.error(JSONObject.toJSONString(buildBody(request,true)));
		return new ResponseEntity(body, status);
	}

	//	页面错误
	@RequestMapping(produces = "text/html")
	public String handleError(HttpServletRequest request, HttpServletResponse response){
		return SystemConfigUtils.getProperty("error.page.404", "error/404");
	}

	//	没有权限
	@RequestMapping("/noAccess.do")
	public String noAccess(HttpServletRequest request, HttpServletResponse response){
//		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
//		}else {
		return SystemConfigUtils.getProperty("error.page.404", "error/404");
//		}
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH+"/default";
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	}

	protected HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request
				.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		}
		catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	private BaseOutput buildBody(HttpServletRequest request,Boolean includeStackTrace){
		Map<String,Object> errorAttributes = getErrorAttributes(request, includeStackTrace);
		Integer status=(Integer)errorAttributes.get("status");
		String path=(String)errorAttributes.get("path");
		String messageFound=(String)errorAttributes.get("message");
		String message="";
		String trace ="";
		if(!StringUtils.isEmpty(path)){
			message=String.format("Requested path %s with result %s",path,messageFound);
		}
		if(includeStackTrace) {
			trace = (String) errorAttributes.get("trace");
			if(!StringUtils.isEmpty(trace)) {
				message += String.format(" and trace %s", trace);
			}
		}
		return BaseOutput.failure(message).setCode(status.toString());
	}
}
