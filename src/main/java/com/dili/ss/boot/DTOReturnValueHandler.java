package com.dili.ss.boot;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by asiam on 2018/3/23 0023.
 */
public class DTOReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;

    public DTOReturnValueHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

//      BaseOutput<List<DTO>>: ((ParameterizedTypeImpl)((ParameterizedTypeImpl)returnType.getMethod().getGenericReturnType()).getActualTypeArguments()[0]).getActualTypeArguments()
        delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }
}