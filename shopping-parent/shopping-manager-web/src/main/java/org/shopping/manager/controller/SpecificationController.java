package org.shopping.manager.controller;

/**
 * 商品规格
 */
import java.util.List;
import org.sellergoods.service.SpecificationService;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbSpecification;
import org.shopping.pojogroup.Specification;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/Specification")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SpecificationController {

	@Reference
	private SpecificationService Service;

	/**
	 * 获取全部信息
	 * 
	 * @return
	 */

	@RequestMapping("/findAll")
	public List<TbSpecification> findAll() {
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
		return Service.queryPageListByWhere(new TbSpecification(), page, rows);
	}

	/**
	 * 增加
	 * 
	 * @param bean
	 * @return
	 */

	@RequestMapping("/add")
	public Result add(@RequestBody Specification bean) {
		if (Service.save(bean)) {
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.INSETR_SUCCESS);
		}
		return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.INSETR_FAIL);
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
	 * @param Specification
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Specification bean) {
		if (Service.update(bean)) {
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.UPDATA_SUCCESS);
		}
		return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.UPDATA_FAIL);
	}

	/**
	 * 根据id获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Specification findOne(Long id) {
		return Service.findOne(id);
	}

	/**
	 * 查询+分页
	 * 
	 * @param Specification
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows, @RequestBody TbSpecification bean) {
		return Service.findPage(bean, page, rows);
	}

}
