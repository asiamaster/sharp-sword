package com.dili.ss.uid.handler;

public interface BizNumberManager {
    void setFixedStep(int fixedStep);

    void setRangeStep(int rangeStep);

    void setBizNumberComponent(BizNumberComponent bizNumberComponent);

    /**
     * 根据业务类型获取业务号
     * @param type
     * @param dateFormat
     * @param length
     * @param range
     * @return
     */
    Long getBizNumberByType(String type, String dateFormat, int length, String range);

}
