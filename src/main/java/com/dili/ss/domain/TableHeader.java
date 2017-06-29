package com.dili.ss.domain;

/**
 * 表头
 * Created by asiamaster on 2017/6/15 0015.
 */
public class TableHeader {
    //字段名
    private String field;
    //标题
    private String title;

    public TableHeader(String field, String title) {
        this.field = field;
        this.title = title;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
