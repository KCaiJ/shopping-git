package org.content.service.impl;
/**
 * 广告管理
 */

import java.util.List;
import org.content.service.ContentService;
import org.shopping.mapper.TbContentMapper;
import org.shopping.pojo.TbContent;
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
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {
	@Autowired
	private TbContentMapper ContentMapper;
	@Autowired
	private RedisTemplate redisTemplate;	

	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbContent bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbContent.class);
		Criteria criteria = example.createCriteria();
		if (bean != null) {
			if (bean.getTitle() != null && bean.getTitle().length() > 0) {
				criteria.andLike("title", "%" + bean.getTitle() + "%");
			}
		}
		Page<TbContent> page = (Page<TbContent>) ContentMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Integer save(TbContent param) {
		redisTemplate.boundHashOps("content").delete(param.getCategoryId());
		return ContentMapper.insert(param);
	}
	/**
	 * 更改
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Integer update(TbContent param) {
		//查询修改前的分类Id
		Long categoryId = ContentMapper.selectByPrimaryKey(param.getId()).getCategoryId();
		redisTemplate.boundHashOps("content").delete(categoryId);
		//如果分类ID发生了修改,清除修改后的分类ID的缓存
		if(categoryId.longValue()!=param.getCategoryId().longValue()){
			redisTemplate.boundHashOps("content").delete(param.getCategoryId());
		}	
		return ContentMapper.updateByPrimaryKey(param);
	}
	
	
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@Transactional
	public boolean delete(Long[] ids) {
		try {
			for (Long id : ids) {
				Long categoryId = ContentMapper.selectByPrimaryKey(id).getCategoryId();//广告分类ID
				redisTemplate.boundHashOps("content").delete(categoryId);
				ContentMapper.deleteByPrimaryKey(id);
			}
			return true;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚事务
			return false;
		}
	}

	/**
	 * 根据类型查询广告
	 */
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		List<TbContent> contentList= (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		if(contentList==null){
			System.out.println("从数据库读取数据放入缓存");
			Example example = new Example(TbContent.class);
			Criteria criteria = example.createCriteria();
			criteria.andEqualTo("categoryId", categoryId);
			criteria.andEqualTo("status", "1");
			example.setOrderByClause("sort_order");// 排序
			contentList = ContentMapper.selectByExample(example);
			redisTemplate.boundHashOps("content").put(categoryId, contentList);//存入缓存 
		}else{
			System.out.println("从缓存读取数据");
		}
		return  contentList; 
	}
	
}
