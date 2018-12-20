package org.shopping.mapper;

import org.shopping.pojo.TbSpecification;

import tk.mybatis.mapper.common.Mapper;

public interface TbSpecificationMapper extends Mapper<TbSpecification> {
	int insertToId(TbSpecification param);
}