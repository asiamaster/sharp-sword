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
    public void init() {
        try {
            bizNumberManager = (BizNumberManager)((Class)B.b.g("bizNumberManagerImpl")).newInstance();
        } catch (Exception e) {
        }
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
