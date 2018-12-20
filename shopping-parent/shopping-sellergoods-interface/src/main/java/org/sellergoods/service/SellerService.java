package org.sellergoods.service;

import org.shopping.pojo.TbSeller;

import Base.BaseService;

public interface SellerService extends BaseService<TbSeller>{
	//状态更改
	public void updateStatus(String sellerId,String status);
	public TbSeller findOne(String name) ;
	public TbSeller login(String name) ;
}
