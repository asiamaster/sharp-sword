package com.dili.ss.metadata.provider;

import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/31 0031.
 */
@Component
public class DateProvider implements ValueProvider {

    private static final List<ValuePair<?>> buffer;

    static {
        buffer = new ArrayList<ValuePair<?>>();
        buffer.add(new ValuePairImpl(EMPTY_ITEM_TEXT, null));
    }

    @Override
    public List<ValuePair<?>> getLookupList(Object obj, Map metaMap, FieldMeta fieldMeta) {
        return buffer;
    }

    @Override
    public String getDisplayText(Object obj, Map metaMap, FieldMeta fieldMeta) {
        if(obj == null || "".equals(obj)) return "";
        if(obj instanceof LocalDate){
            //输出yyyy-MM-dd格式字符串
            return obj.toString();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(obj instanceof Date){
            return sdf.format((Date)obj);
        }
        Long time = obj instanceof Long ? (Long)obj : obj instanceof String ? Long.parseLong(obj.toString()) : 0;
        return sdf.format(new Date(time));
    }
}
