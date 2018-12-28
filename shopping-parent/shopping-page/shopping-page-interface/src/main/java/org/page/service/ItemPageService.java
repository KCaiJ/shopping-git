package org.page.service;

import java.util.Map;

import org.shopping.pojogroup.Goods;

/**
 * 商品详细页
 * @author root
 *
 */
public interface ItemPageService {
	/**
	 * 返回商品详细信息
	 * @param goodsId
	 */
	public Map genItem(Long goodsId);
	/**
	 * 生成商品详细页
	 * @param goodsId
	 */
	public boolean getItemHtml(Long goodsId);

	/**
	 * 删除商品详细页
	 * @param goodsId
	 * @return
	 */
	public boolean deleteItemHtml(Long[] goodsIds);

}
