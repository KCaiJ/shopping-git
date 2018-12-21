package org.content.service;

import java.util.List;

import org.shopping.pojo.TbContent;

import Base.BaseService;

public interface ContentService extends BaseService<TbContent> {
	/**
	 * 根据类型ID返回广告条目
	 * 
	 * @param categoryId
	 * @return
	 */
	public List<TbContent> findByCategoryId(Long categoryId);
}
