package org.shopping.search.controller;

import java.util.Map;

import org.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
	@Reference
	private ItemSearchService itemSearchService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/search")
	public Map<String, Object> search(@RequestBody Map searchMap ){
		return  itemSearchService.search(searchMap);
	}	
}

