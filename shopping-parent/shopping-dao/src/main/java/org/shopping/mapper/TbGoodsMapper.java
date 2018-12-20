package org.shopping.mapper;

import org.shopping.pojo.TbGoods;

import tk.mybatis.mapper.common.Mapper;

public interface TbGoodsMapper extends Mapper<TbGoods> {
	int insertToId(TbGoods param);
}