package org.sellergoods.service;

import org.shopping.pojo.TbAdminUser;

import Base.BaseService;

public interface AdminUserService extends BaseService<TbAdminUser>{
	public TbAdminUser findOne(String name) ;
}
