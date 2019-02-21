package com.dili.ss.metadata.provider;

import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValueProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 空值提供者
 */
@Component
public class EmptyValueProvider implements ValueProvider {
	@Override
	public String getDisplayText(Object val, Map metadata, FieldMeta fieldMeta) {
		return "";
	}

	@Override
    public List<ValuePair<?>> getLookupList(Object val, Map metadata, FieldMeta fieldMeta) {
		return null;
	}
}
