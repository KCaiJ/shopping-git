package org.sellergoods.service;
import java.util.List;

import org.shopping.pojo.TbItemCat;

import Base.BaseService;

public interface ItemCatService extends BaseService<TbItemCat>{
	public List<TbItemCat> findByParentId(Long parentId);
	
}

