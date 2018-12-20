package org.shopping.pojogroup;

import java.io.Serializable;
import java.util.List;

import org.shopping.pojo.TbGoods;
import org.shopping.pojo.TbGoodsDesc;
import org.shopping.pojo.TbItem;

public class Goods implements Serializable {
	private static final long serialVersionUID = 1L;
	private TbGoods goods;// 商品SPU
	private TbGoodsDesc goodsDesc;// 商品扩展
	private List<TbItem> itemList;// 商品SKU列表

	public TbGoods getGoods() {
		return goods;
	}

	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}

	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public List<TbItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}

}
