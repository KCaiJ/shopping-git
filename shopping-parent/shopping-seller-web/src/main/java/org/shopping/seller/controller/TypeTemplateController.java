package org.shopping.seller.controller;

/**
 * 商品类型模板管理
 */
import java.util.List;
import java.util.Map;

import org.sellergoods.service.TypeTemplateService;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbTypeTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/TypeTemplate")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TypeTemplateController {

	@Reference
	private TypeTemplateService Service;

	/**
	 * 获取全部信息
	 * 
	 * @return
	 */

	@RequestMapping("/findAll")
	public List<TbTypeTemplate> findAll() {
		return Service.queryAll();
	}

	/**
	 * 返回分页列表
	 * 
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return Service.queryPageListByWhere(new TbTypeTemplate(), page, rows);
	}

	/**
	 * 增加
	 * 
	 * @param bean
	 * @return
	 */

	@RequestMapping("/add")
	public Result add(@RequestBody TbTypeTemplate bean) {
		try {
			Service.save(bean);
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.INSETR_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.INSETR_FAIL);
		}
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		if (Service.delete(ids)) {
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.DELETE_SUCCESS);
		}
		return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.DELETE_FAIL);

	}

	/**
	 * 修改对象
	 * 
	 * @param TypeTemplate
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbTypeTemplate bean) {
		try {
			Service.update(bean);
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.UPDATA_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.UPDATA_FAIL);
		}
	}

	/**
	 * 根据id获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbTypeTemplate findOne(Long id) {
		return Service.queryById(id);
	}

	/**
	 * 查询+分页
	 * 
	 * @param TypeTemplate
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows, @RequestBody TbTypeTemplate bean) {
		return Service.findPage(bean, page, rows);
	}

	/**
	 * 获取模板规格列表
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findSpecList")
	public List<Map> findSpecList(Long id) {
		return Service.findSpecList(id);
	}

}
