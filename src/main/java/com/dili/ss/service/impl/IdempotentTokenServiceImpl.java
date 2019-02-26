package com.dili.ss.service.impl;

import com.dili.ss.service.IdempotentTokenService;
import com.dili.ss.servlet.dto.TokenPair;
import com.dili.ss.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnExpression("'${idempotent.enable}'=='true'")
public class IdempotentTokenServiceImpl implements IdempotentTokenService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public TokenPair getToken(String url) {
        TokenPair tokenPair = new TokenPair();
        String tokenValue = UUID.randomUUID().toString();
        tokenPair.setKey(url + tokenValue);
        tokenPair.setValue(tokenValue);
        redisUtil.set(url + tokenValue, tokenValue);
        return tokenPair;
    }

}
