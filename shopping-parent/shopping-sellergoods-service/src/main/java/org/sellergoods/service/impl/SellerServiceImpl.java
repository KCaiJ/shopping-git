package org.sellergoods.service.impl;
/**
 * 商家管理
 */

import java.util.ArrayList;
import java.util.Date;
import org.sellergoods.service.SellerService;
import org.shopping.mapper.TbSellerMapper;
import org.shopping.pojo.TbSeller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
@Transactional
public class SellerServiceImpl extends BaseServiceImpl<TbSeller>implements SellerService {
	@Autowired
	private TbSellerMapper SellerMapper;

	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbSeller bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbSeller.class);
		Criteria criteria = example.createCriteria();
		if (bean != null) {
			if (bean.getNickName() != null && bean.getNickName().length() > 0) {
				criteria.andLike("nickName", "%" + bean.getNickName() + "%");
			}
			if (bean.getName() != null && bean.getName().length() > 0) {
				criteria.andLike("name", "%" + bean.getName() + "%");
			}
			criteria.andEqualTo("status", "0");
		}
		Page<TbSeller> page = (Page<TbSeller>) SellerMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 保存商家入驻
	 */
	@Override
	public Integer save(TbSeller param) {
		param.setStatus("0");
		param.setCreateTime(new Date());
		param.getPassword();
		// String encryptSHA = SHAencrypt.encryptSHA("Yan230Kd");
		return super.save(param);
	}

	/**
	 * 入驻商家状态更改
	 */
	@Override
	public void updateStatus(String sellerId, String status) {
		TbSeller seller = SellerMapper.selectByPrimaryKey(sellerId);
		seller.setStatus(status);
		SellerMapper.updateByPrimaryKey(seller);

	}

	@Override
	public TbSeller findOne(String name) {
		return SellerMapper.selectByPrimaryKey(name);
	}

	/**
	 * 根据商家用户名查询 验证登录
	 */
	@Override
	public TbSeller login(String name) {
		Example example = new Example(TbSeller.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("sellerId", name);
		ArrayList<TbSeller> list = (ArrayList<TbSeller>) SellerMapper.selectByExample(example);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
