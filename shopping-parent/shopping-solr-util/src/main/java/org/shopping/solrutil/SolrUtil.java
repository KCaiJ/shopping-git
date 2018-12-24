package org.shopping.solrutil;

import java.util.List;
import java.util.Map;

import org.shopping.mapper.TbItemMapper;
import org.shopping.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper ItemMapper;
	@Autowired
	private SolrTemplate solrTemplate;

	/**
	 * 导入商品数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void importItemData(){
		Example example=new Example(TbItem.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("status","1");//已审核
		List<TbItem> itemList = ItemMapper.selectByExample(example);
		System.out.println("===商品列表===");
		for(TbItem item:itemList){
			System.out.println(item.getTitle());
			Map specMap= JSON.parseObject(item.getSpec(),Map.class);//将spec字段中的json字符串转换为map
			item.setSpecMap(specMap);//给带注解的字段赋值	
		}	
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		System.out.println("===结束===");			
	}	

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil=  (SolrUtil) context.getBean("solrUtil");
		solrUtil.importItemData();
	}
}