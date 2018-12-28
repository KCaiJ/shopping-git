package org.content.service.impl;
/**
 * 广告类目管理
 */

import org.content.service.ContentCategoryService;
import org.shopping.mapper.TbContentCategoryMapper;
import org.shopping.pojo.TbContentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class ContenCategoryServiceImpl extends BaseServiceImpl<TbContentCategory> implements ContentCategoryService {
	@Autowired
	private TbContentCategoryMapper ContenCategoryMapper;

	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbContentCategory bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbContentCategory.class);
		Criteria criteria = example.createCriteria();
		if (bean != null) {
			if (bean.getName() != null && bean.getName().length() > 0) {
				criteria.andLike("name", "%" + bean.getName() + "%");
			}
		}
		Page<TbContentCategory> page = (Page<TbContentCategory>) ContenCategoryMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
}
