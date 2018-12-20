package org.sellergoods.service;

import org.shopping.pojo.TbGoods;
import org.shopping.pojogroup.Goods;

import Base.BaseService;

public interface GoodsService extends BaseService<TbGoods>{
	/**
	 * 增加
	*/
	public boolean add(Goods goods);
	
	/**
	 * 批量修改状态
	 */
	public boolean updateStatus(Long[] ids ,String status);
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	/**
	 * 更改属性
	 * @param goods
	 */
	public boolean update(Goods goods);
	/**
	 * 上下架状态更改
	 * @param ids
	 * @param status
	 * @return
	 */
	public boolean isMarketable(Long[] ids ,String status);
}
