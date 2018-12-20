package org.sellergoods.service;

import java.util.List;
import java.util.Map;

import org.shopping.pojo.TbTypeTemplate;

import Base.BaseService;

public interface TypeTemplateService extends BaseService<TbTypeTemplate>{
	/**
	 * 返回规格列表
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> findSpecList(Long id);
}
