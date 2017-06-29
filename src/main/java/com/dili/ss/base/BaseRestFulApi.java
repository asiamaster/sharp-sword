package com.dili.ss.base;


import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.DataErrorException;
import com.dili.ss.exception.NotAuthException;
import com.dili.ss.exception.ParamErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * restful api基类
 * Created by asiamastor on 2016/12/28.
 */
public class BaseRestFulApi {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseRestFulApi.class);

    @Autowired
    HttpServletRequest request; //这里可以获取到request

    public static BaseOutput<?> handleException(BaseOutput<?> out, Exception e){
        if(out == null){
            out = BaseOutput.failure();
        }
        if(e instanceof ParamErrorException){
            ParamErrorException exception=(ParamErrorException)e;
            if(StringUtils.isNotBlank(exception.getCode())){
                out.setCode(exception.getCode()); 
            }else{
                out.setCode(ResultCode.PARAMS_ERROR);
            }
            out.setResult(exception.getMessage());
            out.setErrorData(exception.getErrorData());
            LOGGER.error("失败:",e);
            return out;
        } 
        if(e instanceof IllegalArgumentException){
            out.setCode(ResultCode.PARAMS_ERROR);
            out.setResult(e.getMessage());
            LOGGER.error("失败:",e);
            return out;
        } 
        if(e instanceof NotAuthException){
            NotAuthException exception=(NotAuthException)e;
            if(StringUtils.isNotBlank(exception.getCode())){
                out.setCode(exception.getCode()); 
            }else{
                out.setCode(ResultCode.NOT_AUTH_ERROR);                
            }
            out.setResult(exception.getMessage());
            out.setErrorData(exception.getErrorData());
            LOGGER.error("失败:",e);
            return out;
        }
        if(e instanceof DataErrorException){
            DataErrorException exception=(DataErrorException)e;
            if(StringUtils.isNotBlank(exception.getCode())){
                out.setCode(exception.getCode()); 
            }else{
                out.setCode(ResultCode.DATA_ERROR);                
            }
            out.setResult(e.getMessage());
            out.setErrorData(exception.getErrorData());
            LOGGER.error("失败:",e);
            return out;
        }
        out.setCode(ResultCode.APP_ERROR);
        //系统内部异常不直接抛到前端
        out.setResult("系统处理发生异常");
        LOGGER.error("失败:", e);
        return out;
    }


    protected List<String>  getBizData(){
       return Arrays.asList(request.getParameter("bizIds"));
    }

}
