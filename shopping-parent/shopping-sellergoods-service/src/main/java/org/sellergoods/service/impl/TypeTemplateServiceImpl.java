package org.sellergoods.service.impl;
/**
 * 商品类型模板管理
 */

import java.util.List;
import java.util.Map;

import org.sellergoods.service.TypeTemplateService;
import org.shopping.mapper.TbSpecificationOptionMapper;
import org.shopping.mapper.TbTypeTemplateMapper;
import org.shopping.pojo.TbSpecificationOption;
import org.shopping.pojo.TbTypeTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
@Transactional
public class TypeTemplateServiceImpl extends BaseServiceImpl<TbTypeTemplate>implements TypeTemplateService {
	@Autowired
	private TbTypeTemplateMapper TypeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper SpecificationOptionMapper;

	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbTypeTemplate bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbTypeTemplate.class);
		Criteria criteria = example.createCriteria();
		if (bean != null) {
			if (bean.getName() != null && bean.getName().length() > 0) {
				criteria.andLike("name", "%" + bean.getName() + "%");
			}
		}
		Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) TypeTemplateMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map> findSpecList(Long id) {
		// 查询模板
		TbTypeTemplate typeTemplate = TypeTemplateMapper.selectByPrimaryKey(id);

		List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
		for (Map map : list) {
			// 查询规格选项列表
			Example example = new Example(TbSpecificationOption.class);
			Criteria criteria = example.createCriteria();
			criteria.andEqualTo("specId", new Long((Integer) map.get("id")));
			List<TbSpecificationOption> options = SpecificationOptionMapper.selectByExample(example);
			map.put("options", options);
		}
		return list;
	}

}
