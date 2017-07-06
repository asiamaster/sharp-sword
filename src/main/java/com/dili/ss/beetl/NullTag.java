package com.dili.ss.beetl;

import org.beetl.core.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 用于beetlConfig占位
 * Created by asiamaster on 2017/5/24 0024.
 */
@Component("null")
@ConditionalOnExpression("'${beetl.enable}'=='true'")
public class NullTag extends Tag {
    @Override
    public void render() {
//        BodyContent content = getBodyContent();
//        String c = content.getBody();
//        String tagName = (String) this.args[0];
//        Map attrs = (Map) args[1];
//        String value = (String) attrs.get("beanClass");
//        ctx.set("beanClass", value);
//        HttpServletRequest request = (HttpServletRequest)this.ctx.getGlobal("request");
        try{
            this.ctx.byteWriter.writeString("");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
