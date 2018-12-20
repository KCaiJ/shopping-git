package org.content.service.impl;
/**
 * 广告管理
 */

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
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {
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
}
