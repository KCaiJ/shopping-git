package org.sellergoods.service;

import org.shopping.pojo.TbSpecification;
import org.shopping.pojogroup.Specification;

import Base.BaseService;

public interface SpecificationService extends BaseService<TbSpecification>{
	boolean save(Specification param);
	Specification findOne(Long id);
	boolean update(Specification specification);
}
