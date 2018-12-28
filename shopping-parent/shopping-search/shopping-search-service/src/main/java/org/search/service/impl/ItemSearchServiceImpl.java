package org.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.search.service.ItemSearchService;
import org.shopping.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.ScoredPage;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 使用solr查询
	 */
	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String, Object> map = new HashMap<>();
		
		// 获取关键字列表
		map.putAll(searchNameList(searchMap));
		
		// 获取类型列表
		List categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		
		// 查询品牌和规格列表
		String categoryName=(String)searchMap.get("category");
		if(!"".equals(categoryName)){//如果有分类名称
			map.putAll(searchBrandAndSpecList(categoryName));			
		}else{//如果没有分类名称，按照第一个查询
			if(categoryList.size()>0){
				map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
			}
		}
		return map;
	}

	/**
	 * 查询关键字列表
	 */
	private Map searchNameList(Map searchMap) {
		Query query = new SimpleQuery("*:*");
		
		//去除空格
		String keywords=(String) searchMap.get("keywords");
		keywords=keywords.replace(" ", "");
		// 添加查询条件
		Criteria criteria = new Criteria("item_keywords").is(keywords);
		query.addCriteria(criteria);
		
		// 筛选分类
		if (!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}

		// 筛选品牌
		if (!"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}

		//筛选规格
		if (searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map) searchMap.get("spec");
			for (String key : specMap.keySet()) {
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		
		//筛选价格
		if(!"".equals(searchMap.get("price"))){
			String[] price = ((String) searchMap.get("price")).split("-");
			if(!price[0].equals("0")){//如果区间起点不等于0
				Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);				
			}		
			if(!price[1].equals("*")){//如果区间终点不等于*
				Criteria filterCriteria=new  Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);				
			}
		}
		Integer pageNo= (Integer) searchMap.get("pageNo");//提取页码
		
		if(pageNo==null){
			pageNo=1;//默认第一页
		}
		
		Integer pageSize=(Integer) searchMap.get("pageSize");//每页记录数 
		if(pageSize==null){
			pageSize=20;//默认20
		}
		
		query.setOffset((pageNo-1)*pageSize);//从第几条记录查询
		query.setRows(pageSize);	
		
		//排序处理
		
		String sortValue=(String) searchMap.get("sort");//ASC DESC
		String sortFieId=(String) searchMap.get("sortFieId");//需要排序的字段
		if (sortValue.equals("ASC")) {
			Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortFieId);
			query.addSort(sort);
		}
		if (sortValue.equals("DESC")) {
			Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortFieId);
			query.addSort(sort);
		}

		//获取数据
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		Map<String, Object> map = new HashMap<>();
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());//总页数
		map.put("total", page.getTotalElements());//总记录数
		return map;
	}

	/**
	 * 查询类型列表
	 * 
	 * @param searchMap
	 * @return
	 */
	private List searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList();
		Query query = new SimpleQuery("*:*");
		// 根据关键字查询出需要分组的列表
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		// 分组查询
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 获取分组结果
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>> entrylist = groupEntries.getContent();
		for (GroupEntry<TbItem> entry : entrylist) {
			list.add(entry.getGroupValue());
		}
		return list;
	}

	/**
	 * 查询品牌和规格列表
	 * 
	 * @param category
	 *            分类名称
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap();
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);// 获取模板ID
		if (typeId != null) {
			// 根据模板ID查询品牌列表
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);// 返回值添加品牌列表
			// 根据模板ID查询规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);
		}
		return map;
	}

	/**
	 * 批量导入数据到solr
	 */
	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);	
		solrTemplate.commit();
	}

	/**
	 * 从solr删除数据
	 */
	@Override
	public void deleteByGoodsIds(List goodsIds) {		
		Query query=new SimpleQuery("*:*");		
		Criteria criteria=new Criteria("item_goodsid").in(goodsIds);
		query.addCriteria(criteria);		
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
}
