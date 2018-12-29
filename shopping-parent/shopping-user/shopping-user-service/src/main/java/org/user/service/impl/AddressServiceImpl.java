package org.user.service.impl;
/**
 * 地址管理
 */

import java.util.List;

import org.shopping.mapper.TbAddressMapper;
import org.shopping.pojo.TbAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.user.service.AddressService;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class AddressServiceImpl extends BaseServiceImpl<TbAddress>implements AddressService {
	@Autowired
	private TbAddressMapper AddressMapper;

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

	
	
	
}
