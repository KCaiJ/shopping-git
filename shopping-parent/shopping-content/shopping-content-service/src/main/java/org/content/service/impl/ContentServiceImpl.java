package org.content.service.impl;
/**
 * 广告管理
 */

import java.util.List;
import org.content.service.ContentService;
import org.shopping.mapper.TbContentMapper;
import org.shopping.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class ContentServiceImpl extends BaseServiceImpl<TbContent>implements ContentService {
	@Autowired
	private TbContentMapper ContentMapper;

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
	 * 根据类型查询广告
	 */
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		Example example = new Example(TbContent.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("categoryId", categoryId);
		criteria.andEqualTo("status", "1");
		example.setOrderByClause("sort_order");// 排序
		return ContentMapper.selectByExample(example);
	}
}
