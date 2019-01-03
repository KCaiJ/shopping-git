package org.user.service.impl;
/**
 * 地址管理
 */

import java.util.List;

import org.shopping.mapper.TbAddressMapper;
import org.shopping.mapper.TbAreasMapper;
import org.shopping.mapper.TbCitiesMapper;
import org.shopping.mapper.TbProvincesMapper;
import org.shopping.pojo.TbAddress;
import org.shopping.pojo.TbAreas;
import org.shopping.pojo.TbCities;
import org.shopping.pojo.TbGoods;
import org.shopping.pojo.TbProvinces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.user.service.AddressService;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import Base.BaseServiceImpl;
import entity.PageResult;
import entity.Result;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class AddressServiceImpl extends BaseServiceImpl<TbAddress>implements AddressService {
	@Autowired
	private TbAddressMapper AddressMapper;
	@Autowired
	private TbAreasMapper areasMapper;
	@Autowired
	private TbCitiesMapper citiesMapper;
	@Autowired
	private TbProvincesMapper provincesMapper;

	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbAddress bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbAddress.class);
		Criteria criteria = example.createCriteria();
		if (bean != null) {
			/*if (bean.getName() != null && bean.getName().length() > 0) {
				criteria.andLike("name", "%" + bean.getName() + "%");
			}
			if (bean.getFirstChar() != null && bean.getFirstChar().length() > 0) {
				criteria.andEqualTo("firstChar", bean.getFirstChar());
			}*/
		}
		Page<TbAddress> page = (Page<TbAddress>) AddressMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	
	/**
	 * 根据用户查询地址
	 * @param userId
	 * @return
	 */
	public List<TbAddress> findListByUserId(String userId ){				
		Example example=new Example(TbAddress.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId",userId);		
		return AddressMapper.selectByExample(example);
	}


	@Override
	public List<TbCities> findByCityId(String parentId) {
		Example example=new Example(TbCities.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("provinceid",parentId);		
		return citiesMapper.selectByExample(example);
	}


	@Override
	public List<TbAreas> findByAreasId(String parentId) {
		Example example=new Example(TbAreas.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("cityid",parentId);		
		return areasMapper.selectByExample(example);
	}

	
	/**
	 * 设置默认地址
	 */
	@Transactional
	@Override
	public boolean setteingDefault(Long id,String userId) {
		try {
			List<TbAddress>  list=findListByUserId(userId);
			for(TbAddress tbAddress:list){
				if (tbAddress.getId().longValue()==id.longValue()) {
					tbAddress.setIsDefault("1");
				}else{
					tbAddress.setIsDefault("0");
				}
				AddressMapper.updateByPrimaryKey(tbAddress);
			}
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚事务
			return false;
		}
	}
	/**
	 * 返回省份列表
	 */
	public List<TbProvinces> findByProvinces() {
		return provincesMapper.selectAll();
	}
	
	/**
	 * 返回城市列表
	 * @return
	 */
	public List<TbCities> findByCities(){
		return citiesMapper.selectAll();
	}
	

	/**
	 * 返回县区列表
	 * @return
	 */
	public List<TbAreas> findByAreas(){
		return areasMapper.selectAll();
	}
	
	
}
