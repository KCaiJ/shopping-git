package org.page.service.impl;


import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.page.service.ItemPageService;
import org.shopping.mapper.TbGoodsDescMapper;
import org.shopping.mapper.TbGoodsMapper;
import org.shopping.mapper.TbItemCatMapper;
import org.shopping.mapper.TbItemMapper;
import org.shopping.pojo.TbGoods;
import org.shopping.pojo.TbGoodsDesc;
import org.shopping.pojo.TbItem;
import org.shopping.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.alibaba.dubbo.config.annotation.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;


@Service
public class ItemPageServiceImpl implements ItemPageService {
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;

	@Value("${pagedir}")
	private String pagedir;
	
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;

	/**
	 * 获取商品实体信息
	 */
	@Override
	public Map genItem(Long goodsId){	
		Map map=new HashMap<>();
		// 获取goods信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId); 
		map.put("goods", tbGoods);
		// 获取goodsDesc信息
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
		map.put("goodsDesc", tbGoodsDesc);
		//类别信息
		String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
		String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
		String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
		map.put("itemCat1", itemCat1);
		map.put("itemCat2", itemCat2);
		map.put("itemCat3", itemCat3);
		//SKU列表	
		Example example=new Example(TbItem.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("status","1");//状态为有效
		criteria.andEqualTo("goodsId",goodsId);//指定SPU ID
		example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认			
		List<TbItem> itemList = itemMapper.selectByExample(example);	
		map.put("itemList", itemList);
		return map;
	}

	/***
	 * 生成html文件
	 */
	@Override
	public boolean getItemHtml(Long goodsId){				
		try {
			Configuration configuration = freeMarkerConfig.getConfiguration();
			configuration.setDefaultEncoding("utf-8");
			Template template = configuration.getTemplate("item.ftl");
			Map dataModel=new HashMap<>();			
			//1.加载商品表数据
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", goods);			
			//2.加载商品扩展表数据			
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", goodsDesc);	
			
			//3.商品分类
			String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			dataModel.put("itemCat1", itemCat1);
			dataModel.put("itemCat2", itemCat2);
			dataModel.put("itemCat3", itemCat3);

			//4.SKU列表			
			Example example=new Example(TbItem.class);
			Criteria criteria = example.createCriteria();
			criteria.andEqualTo("status","1");//状态为有效
			criteria.andEqualTo("goodsId",goodsId);//指定SPU ID
			example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认			
			List<TbItem> itemList = itemMapper.selectByExample(example);	
			dataModel.put("itemList", itemList);
			
			Writer out=new FileWriter(pagedir+goodsId+".html");
			template.process(dataModel, out);
			out.close();
			return true;			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 删除商品详情页
	 */
	@Override
	public boolean deleteItemHtml(Long[] goodsIds) {		
		try {
			for(Long goodsId:goodsIds){
				new File(pagedir+goodsId+".html").delete();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}


}

