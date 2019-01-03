package org.user.service;

import java.util.List;

import org.shopping.pojo.TbAddress;
import org.shopping.pojo.TbAreas;
import org.shopping.pojo.TbCities;
import org.shopping.pojo.TbItemCat;
import org.shopping.pojo.TbProvinces;

import Base.BaseService;
import entity.Result;

public interface AddressService extends BaseService<TbAddress>{

	/**
	 * 根据用户查询地址
	 * @param userId
	 * @return
	 */
	public List<TbAddress> findListByUserId(String userId );
	
	/**
	 * 根据ID设置默认地址
	 * @param id
	 * @return
	 */
	public boolean setteingDefault(Long id,String userId);
	
	/**
	 * 根据省份ID查市区
	 */
	public List<TbCities> findByCityId(String parentId);
	
	/**
	 * 根据市区ID查县区
	 */
	public List<TbAreas> findByAreasId(String parentId);

	/**
	 * 返回省份列表
	 * @return
	 */
	public List<TbProvinces> findByProvinces();
	

	/**
	 * 返回城市列表
	 * @return
	 */
	public List<TbCities> findByCities();
	

	/**
	 * 返回县区列表
	 * @return
	 */
	public List<TbAreas> findByAreas();
}
