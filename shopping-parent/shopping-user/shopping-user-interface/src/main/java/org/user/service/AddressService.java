package org.user.service;

import java.util.List;

import org.shopping.pojo.TbAddress;

import Base.BaseService;

public interface AddressService extends BaseService<TbAddress>{

	/**
	 * 根据用户查询地址
	 * @param userId
	 * @return
	 */
	public List<TbAddress> findListByUserId(String userId );

}
