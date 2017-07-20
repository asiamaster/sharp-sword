package com.dili.ss.metadata.provider;

import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValueProvider;

import java.util.List;
import java.util.Map;

/**
 * 空值提供者
 */
public class EmptyValueProvider implements ValueProvider {
	public String getDisplayText(Object val, Map metadata, FieldMeta fieldMeta) {
		return "";
	}

	public List<ValuePair<?>> getLookupList(Object val, Map metadata, FieldMeta fieldMeta) {
		return null;
	}
}
