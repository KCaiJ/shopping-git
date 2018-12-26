package org.page.service;
/**
 * 商品详细页
 * @author root
 *
 */
public interface ItemPageService {
	/**
	 * 生成商品详细页
	 * @param goodsId
	 */
	public boolean genItemHtml(Long goodsId);

}
