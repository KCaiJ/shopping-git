package org.sellergoods.service.impl;
/**
 * 商品SPU管理
 */

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sellergoods.service.GoodsService;
import org.shopping.mapper.TbBrandMapper;
import org.shopping.mapper.TbGoodsDescMapper;
import org.shopping.mapper.TbGoodsMapper;
import org.shopping.mapper.TbItemCatMapper;
import org.shopping.mapper.TbSellerMapper;
import org.shopping.mapper.TbItemMapper;
import org.shopping.pojo.TbBrand;
import org.shopping.pojo.TbGoods;
import org.shopping.pojo.TbGoodsDesc;
import org.shopping.pojo.TbItem;
import org.shopping.pojo.TbItemCat;
import org.shopping.pojo.TbSeller;
import org.shopping.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
@Transactional
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods>implements GoodsService {
	@Autowired
	private TbGoodsMapper GoodsMapper;
	@Autowired
	private TbGoodsDescMapper GoodsDescMapper;
	@Autowired
	private TbItemMapper ItemMapper;
	@Autowired
	private TbItemCatMapper ItemCatMapper;
	@Autowired
	private TbSellerMapper SellerMapper;
	@Autowired
	private TbBrandMapper BrandMapper;

	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbGoods bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbGoods.class);
		Criteria criteria = example.createCriteria();
		if (bean != null) {
			if (bean.getSellerId() != null && bean.getSellerId().length() > 0) {
				criteria.andEqualTo("sellerId", bean.getSellerId());
			}
			if (bean.getAuditStatus() != null && bean.getAuditStatus().length() > 0) {
				criteria.andEqualTo("auditStatus", bean.getAuditStatus());
			}
			if (bean.getGoodsName() != null && bean.getGoodsName().length() > 0) {
				criteria.andLike("goodsName", "%" + bean.getGoodsName() + "%");
			}
			criteria.andIsNull("isDelete");
		}
		Page<TbGoods> page = (Page<TbGoods>) GoodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Transactional
	@Override
	public boolean add(Goods goods) {
		try {
			goods.getGoods().setAuditStatus("0");// 设置未申请状态
			goods.getGoods().setIsMarketable("0");// 设置未上架状态
			GoodsMapper.insertToId(goods.getGoods());
			goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());// 设置ID
			GoodsDescMapper.insert(goods.getGoodsDesc());// 插入商品扩展数据
			saveItemList(goods);// 插入商品SKU列表数据
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚事务
			return false;
		}

	}

	/**
	 * SKU数据保存
	 * 
	 * @param goods
	 */
	private void saveItemList(Goods goods) {
		if ("1".equals(goods.getGoods().getIsEnableSpec())) {
			for (TbItem item : goods.getItemList()) {
				// 标题
				String title = goods.getGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goods, item);
				ItemMapper.insert(item);
			}
		} else {
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());// 商品SPU+规格描述串作为SKU名称
			item.setPrice(goods.getGoods().getPrice());// 价格
			item.setIsDefault("1");// 是否默认
			item.setNum(99999);// 库存数量
			item.setSpec("{}");// 规格列表
			setItemValus(goods, item);
			ItemMapper.insert(item);
		}
	}

	/**
	 * 提取SKU提取
	 * @param goods
	 * @param item
	 */
	@SuppressWarnings("rawtypes")
	private void setItemValus(Goods goods, TbItem item) {
		item.setGoodsId(goods.getGoods().getId());// 商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId());// 商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id());// 商品分类编号（3级）
		item.setCreateTime(new Date());// 创建日期
		item.setUpdateTime(new Date());// 修改日期
		item.setStatus("1");//状态正常
		// 品牌名称
		TbBrand brand = BrandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		// 分类名称
		TbItemCat itemCat = ItemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		// 商家名称
		TbSeller seller = SellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		// 图片地址（取spu的第一个图片）
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (imageList.size() > 0) {
			item.setImage((String) imageList.get(0).get("url"));
		}
	}

	/**
	 * 批量数据删除
	 */
	@Transactional
	@Override
	public boolean delete(Long[] ids) {
		try {
			for (Long id : ids) {
				TbGoods goods = GoodsMapper.selectByPrimaryKey(id);
				goods.setIsDelete("1");
				GoodsMapper.updateByPrimaryKey(goods);
			}
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚事务
			return false;
		}

	}

	/**
	 * 批量修改审核状态
	 */
	@Transactional
	@Override
	public boolean updateStatus(Long[] ids, String status) {
		try {
			for (Long id : ids) {
				TbGoods goods = GoodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				GoodsMapper.updateByPrimaryKey(goods);
			}
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚事务
			return false;
		}
	}

	/**
	 * 获取商品信息
	 */
	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();
		TbGoods tbGoods = GoodsMapper.selectByPrimaryKey(id); // 获取goods信息
		goods.setGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = GoodsDescMapper.selectByPrimaryKey(id);// 获取goodsDesc信息
		goods.setGoodsDesc(tbGoodsDesc);
		// 查询SKU商品列表
		Example example = new Example(TbItem.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("goodsId", id);
		List<TbItem> itemList = ItemMapper.selectByExample(example);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 更新商品信息
	 * 
	 * @param goods
	 */
	@Transactional
	@Override
	public boolean update(Goods goods) {
		try {
			goods.getGoods().setAuditStatus("0");
			GoodsMapper.updateByPrimaryKey(goods.getGoods());// 保存商品表
			GoodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());// 保存商品扩展表
			// 删除原有的sku列表数据
			Example example = new Example(TbItem.class);
			Criteria criteria = example.createCriteria();
			criteria.andEqualTo("goodsId", goods.getGoods().getId());
			ItemMapper.deleteByExample(example);
			// 添加新的sku列表数据
			saveItemList(goods);// 插入商品SKU列表数据
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚事务
			return false;
		}

	}

	/**
	 * 批量修改审核状态
	 */
	@Transactional
	@Override
	public boolean isMarketable(Long[] ids, String status) {
		try {
			for (Long id : ids) {
				TbGoods goods = GoodsMapper.selectByPrimaryKey(id);
				goods.setIsMarketable(status);
				GoodsMapper.updateByPrimaryKey(goods);
			}
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚事务
			return false;
		}
	}

	/**
	 * 根据商品ID和状态查询实体  
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
		Example example=new Example(TbItem.class);
		Criteria criteria = example.createCriteria();
		criteria.andIn("goodsId", Arrays.asList(goodsIds));
		criteria.andEqualTo("status",status);
		return ItemMapper.selectByExample(example);
	}
}
