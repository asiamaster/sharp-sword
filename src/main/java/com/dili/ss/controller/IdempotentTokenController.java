package com.dili.ss.controller;

import com.dili.ss.service.IdempotentTokenService;
import com.dili.ss.servlet.dto.TokenPair;
import com.dili.ss.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 幂等token控制器
 */
@RestController
@RequestMapping("/idempotentToken")
@ConditionalOnExpression("'${idempotent.enable}'=='true'")
public class IdempotentTokenController {
//    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
    @Autowired
    private IdempotentTokenService idempotentTokenService;

    @ResponseBody
    @GetMapping("/getToken.api")
    public TokenPair getToken(@RequestParam("url") String url) {
        return idempotentTokenService.getToken(url);
    }


}