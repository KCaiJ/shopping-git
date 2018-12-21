package org.sellergoods.service.impl;
/**
 * 运营商登录管理
 */

import java.util.ArrayList;
import org.sellergoods.service.AdminUserService;
import org.shopping.mapper.TbAdminUserMapper;
import org.shopping.pojo.TbAdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class AdminUserServiceImpl extends BaseServiceImpl<TbAdminUser>implements AdminUserService {
	@Autowired
	private TbAdminUserMapper AdminUserMapper;

	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbAdminUser bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbAdminUser.class);
		Page<TbAdminUser> page = (Page<TbAdminUser>) AdminUserMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public Integer save(TbAdminUser param) {
		return super.save(param);
	}
	/**
	 * 根据id获取对象
	 */
	@Override
	public TbAdminUser findOne(String id) {
		Example example = new Example(TbAdminUser.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("username", id);
		ArrayList<TbAdminUser> list = (ArrayList<TbAdminUser>) AdminUserMapper.selectByExample(example);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
}
