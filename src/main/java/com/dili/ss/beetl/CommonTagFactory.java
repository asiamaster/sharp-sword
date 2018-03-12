package com.dili.ss.beetl;

import org.beetl.core.Tag;
import org.beetl.core.TagFactory;

/**
 * Created by asiamaster on 2017/5/24 0024.
 */
public class CommonTagFactory implements TagFactory {

    private Tag tag;
    public CommonTagFactory(Tag tag){
        this.tag = tag;
    }

    @Override
    public Tag createTag(){
        return tag;
    }
}
