package com.ynshun.data;

import java.util.HashMap;
import java.util.Map;

import com.ynshun.util.TypeUtil;

public class MapData extends HashMap<Object, Object> implements Map<Object, Object> {
	private static final long serialVersionUID = -7151003119037998466L;

	public static MapData getInstance() {
		return new MapData();
	}
	
	public static MapData getInstance(Object data) {
		MapData mapData = MapData.getInstance();
		
		if (data == null || !(data instanceof Map)) {
			return mapData;
		}
		
		Map<?, ?> map = (Map<?, ?>) data;
		for (Object key : map.keySet()) {
			mapData.put(key, map.get(key));
		}
		return mapData;
	}

	public MapData put(Object key, Object value) {
		super.put(key, value);
		return this;
	}

	public <T> T get(Object key, Class<T> type) {
		return this.get(key, type, null);
	}
	
	public String getString(Object key) {
		return this.get(key, String.class, null);
	}
	
	public Integer getInt(Object key) {
		return this.get(key, Integer.class, null);
	}

	public <T> T get(Object key, Class<T> type, T defaultData) {
		Object data = this.get(key);
		if (data == null) {
			return defaultData == null ? null : defaultData;
		}
		return TypeUtil.transfer(data, type);
	}
	
	public MapData getMapData(Object key) {
		return MapData.getInstance(this.get(key));
	}

}
