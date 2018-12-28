package org.shopping.page.controller;

import java.util.Map;
import org.page.service.ItemPageService;
import org.shopping.pojogroup.Goods;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

@RestController
@RequestMapping("/ItemPage")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ItemPageController {

	@Reference
	private ItemPageService itemPageService;
	
	/**
	 * 返回商品信息
	 * @param goodsId
	 * @return
	 */
	@RequestMapping("/getById")
	public Map getById(Long goodsId) {
		return itemPageService.genItem(goodsId);
	}
	
	/**
	 * 生成静态网页
	 * @param goodsId
	 */
	@RequestMapping("/getHtml")
	public void genHtml(Long goodsId){
		itemPageService.getItemHtml(goodsId);	
	}

}
