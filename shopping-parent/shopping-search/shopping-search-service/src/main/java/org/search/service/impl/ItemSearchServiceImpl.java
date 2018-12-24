package org.search.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.search.service.ItemSearchService;
import org.shopping.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;

@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService{
	@Autowired
	private SolrTemplate solrTemplate;

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String,Object> map=new HashMap<>();
		Query query=new SimpleQuery();
		//添加查询条件
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		map.put("rows", page.getContent());
		return map;
	}
}
