package com.dili.ss.uid.handler;

import com.dili.http.okhttp.utils.B;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnExpression("'${uid.enable}'=='true'")
public class BizNumberHandler {

    @Autowired
    private BizNumberComponent bizNumberComponent;
    /**
     * 固定步长值，默认为50
     */
    @Value("${uid.fixedStep:50}")
    private int fixedStep;

    /**
     * 范围步长值，默认为最大范围的20倍
     */
    @Value("${uid.rangeStep:20}")
    private int rangeStep;

    private BizNumberManager bizNumberManager;

    @PostConstruct
    public void init() throws IllegalAccessException, InstantiationException {
        B.b.dae("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");
        bizNumberManager = (BizNumberManager)((Class)B.b.g("clazz")).newInstance();
        bizNumberManager.setBizNumberComponent(bizNumberComponent);
        bizNumberManager.setFixedStep(fixedStep);
        bizNumberManager.setRangeStep(rangeStep);
    }
    /**
     * 根据业务类型获取业务号
     * @param type
     * @param dateFormat
     * @param length
     * @param range
     * @return
     */
    public Long getBizNumberByType(String type, String dateFormat, int length, String range) {
        return bizNumberManager.getBizNumberByType(type, dateFormat, length, range);
    }



}
