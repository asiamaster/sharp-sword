package com.dili.ss.service;

import com.dili.ss.servlet.dto.TokenPair;

public interface IdempotentTokenService {

    /**
     * 获取token
     * @param url
     * @return key: url + tokenValue, value: tokenValue
     */
    TokenPair getToken (String url);


}
