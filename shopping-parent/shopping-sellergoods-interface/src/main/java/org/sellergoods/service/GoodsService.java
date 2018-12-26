package org.sellergoods.service;

import java.util.List;

import org.shopping.pojo.TbGoods;
import org.shopping.pojo.TbItem;
import org.shopping.pojogroup.Goods;

import Base.BaseService;

public interface GoodsService extends BaseService<TbGoods> {
	/**
	 * 增加
	 */
	public boolean add(Goods goods);

	/**
	 * 批量修改状态
	 */
	public boolean updateStatus(Long[] ids, String status);

	/**
	 * 获取实体
	 * 
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);

	/**
	 * 更改属性
	 * 
	 * @param goods
	 */
	public boolean update(Goods goods);

	/**
	 * 上下架状态更改
	 * 
	 * @param ids
	 * @param status
	 * @return
	 */
	public boolean isMarketable(Long[] ids, String status);
	
	
	/**
	 * 根据商品ID和状态查询实体  
	 * @param goodsId
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status );

}
