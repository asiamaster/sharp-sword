package com.dili.ss.quartz.provider;

import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import com.dili.ss.quartz.domain.QuartzConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-10-24 09:32:32.
 */
@Component
public class JobStatusProvider implements ValueProvider {
    private static final List<ValuePair<?>> buffer;

    static {
        buffer = new ArrayList<ValuePair<?>>();
        QuartzConstants.JobStatus[] jobStatuses = QuartzConstants.JobStatus.values();
        for(QuartzConstants.JobStatus jobStatus : jobStatuses){
            buffer.add(new ValuePairImpl(jobStatus.getDesc(), jobStatus.getCode()));
        }
//        buffer.add(new ValuePairImpl("无", "0"));
//        buffer.add(new ValuePairImpl("正常", "1"));
//        buffer.add(new ValuePairImpl("暂停", "2"));
//        buffer.add(new ValuePairImpl("完成", "3"));
//        buffer.add(new ValuePairImpl("错误", "4"));
//        buffer.add(new ValuePairImpl("阻塞", "5"));
    }

    @Override
    public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
        return buffer;
    }

    @Override
    public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
        if(obj == null || "".equals(obj)) return null;
        for(ValuePair<?> valuePair : buffer){
            if(obj.equals(valuePair.getValue())){
                return valuePair.getText();
            }
        }
        return null;
    }
}