package org.shopping.portal.controller;

/**
 * 商品SKU
 */
import java.util.List;
import org.sellergoods.service.ItemCatService;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbItemCat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/ItemCat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ItemCatController {

	@Reference
	private ItemCatService Service;

	/**
	 * 获取全部信息
	 * 
	 * @return
	 */

	@RequestMapping("/findAll")
	public List<TbItemCat> findAll() {
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
		return Service.queryPageListByWhere(new TbItemCat(), page, rows);
	}

	/**
	 * 增加
	 * 
	 * @param bean
	 * @return
	 */

	@RequestMapping("/add")
	public Result add(@RequestBody TbItemCat bean) {
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
	 * @param ItemCat
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbItemCat bean) {
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
	public TbItemCat findOne(Long id) {
		return Service.queryById(id);
	}

	/**
	 * 查询+分页
	 * 
	 * @param ItemCat
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows, @RequestBody TbItemCat bean) {
		return Service.findPage(bean, page, rows);
	}

	/**
	 * 根据上级ID返回下级列表
	 * @param parentId
	 * @return
	 */
	@RequestMapping("/findByParentId")
	public List<TbItemCat> findByParentId(Long parentId) {
		return Service.findByParentIdWEB(parentId);
	}

}
