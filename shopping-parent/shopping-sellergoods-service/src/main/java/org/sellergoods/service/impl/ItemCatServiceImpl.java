package org.sellergoods.service.impl;
/**
 * 商品类型管理
 */

import java.util.List;

import org.sellergoods.service.ItemCatService;
import org.shopping.mapper.TbItemCatMapper;
import org.shopping.pojo.TbItemCat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
@Transactional
public class ItemCatServiceImpl extends BaseServiceImpl<TbItemCat>implements ItemCatService {
	@Autowired
	private TbItemCatMapper ItemCatMapper;
	@Autowired
	private RedisTemplate  redisTemplate;
	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbItemCat bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbItemCat.class);
		Criteria criteria = example.createCriteria();
		if (bean != null) {
			if (bean.getName() != null && bean.getName().length() > 0) {
				criteria.andLike("name", "%" + bean.getName() + "%");
			}
		}
		Page<TbItemCat> page = (Page<TbItemCat>) ItemCatMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 根据上级ID返回下级列表
	 */
	@Override
	public List<TbItemCat> findByParentId(Long parentId) {
		Example example = new Example(TbItemCat.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("parentId", parentId);
		saveToRedis();
		return ItemCatMapper.selectByExample(example);
	}
	
	/**
	 * 缓存模板ID
	 */
	private void saveToRedis() {
		List<TbItemCat> items=queryAll();
		for (TbItemCat tbItemCat : items) {
			redisTemplate.boundHashOps("itemCat").put(tbItemCat.getName(), tbItemCat.getTypeId());
		}		
		System.out.println("缓存模板列表");
	}

	/**
	 * 批量删除
	 */
	@Transactional
	@Override
	public boolean delete(Long[] ids) {
		try {
			for (Long id : ids) {
				List<TbItemCat> itemCats = findByParentId(id);
				for (TbItemCat bean : itemCats) {
					List<TbItemCat> itemCats2 = findByParentId(bean.getId());
					for (TbItemCat bean2 : itemCats2) {
						ItemCatMapper.delete(bean2);
					}
					ItemCatMapper.delete(bean);
				}
				ItemCatMapper.deleteByPrimaryKey(id);
			}
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚事务
			return false;
		}
	}
}
