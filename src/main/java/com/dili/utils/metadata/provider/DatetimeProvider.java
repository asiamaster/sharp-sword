package com.dili.utils.metadata.provider;

import com.dili.utils.metadata.ValuePair;
import com.dili.utils.metadata.ValuePairImpl;
import com.dili.utils.metadata.ValueProvider;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/31 0031.
 */
@Component
public class DatetimeProvider implements ValueProvider {

    private static final List<ValuePair<?>> buffer;

    static {
        buffer = new ArrayList<ValuePair<?>>();
        buffer.add(new ValuePairImpl(EMPTY_ITEM_TEXT, null));
    }

    @Override
    public List<ValuePair<?>> getLookupList(Object obj, Map metaMap) {
        return buffer;
    }

    @Override
    public String getDisplayText(Object obj, Map metaMap) {
        if(obj == null || obj.equals("")) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(obj instanceof Date){
            return sdf.format((Date)obj);
        }
        Long time = obj instanceof Long ? (Long)obj : obj instanceof String ? Long.parseLong(obj.toString()) : 0;
        return sdf.format(new Date(time));
    }
}
