package org.shopping.portal.controller;

/**
 * 广告品牌
 */
import java.util.List;
import org.content.service.ContentService;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbContent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/Content")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContentController {

	@Reference
	private ContentService Service;
	/**
	 * 根据广告分类ID查询广告列表
	 * 
	 * @param categoryId
	 * @return
	 */
	@RequestMapping("/findByCategoryId")
	public List<TbContent> findByCategoryId(Long categoryId) {
		return Service.findByCategoryId(categoryId);
	}

}
