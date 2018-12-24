package org.search.service;

import java.util.Map;

public interface ItemSearchService {
	/**
	 * 搜索
	 * @param keywords
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,Object> search(Map searchMap);
}
