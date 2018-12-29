package org.shopping.order.controller;

/**
 * 订单
 */
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.oder.service.OrderService;
import org.shopping.common.CookUtils;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbOrder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/Order")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

	@Reference
	private OrderService Service;

	/**
	 * 获取全部信息
	 * 
	 * @return
	 */

	@RequestMapping("/findAll")
	public List<TbOrder> findAll() {
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
		return Service.queryPageListByWhere(new TbOrder(), page, rows);
	}

	/**
	 * 增加
	 * 
	 * @param bean
	 * @return
	 */

	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order,HttpServletRequest request) {
		//获取当前登录人账号
		String username = CookUtils.getCookieName(request, Enumeration.CURRENT_USER);//获取登录用户名
		order.setUserId(username);
		order.setSourceType("2");//订单来源  PC
		try {
			Service.save(order);
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
	 * @param Order
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder bean) {
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
	public TbOrder findOne(Long id) {
		return Service.queryById(id);
	}

	/**
	 * 查询+分页
	 * 
	 * @param Order
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows, @RequestBody TbOrder bean) {
		return Service.findPage(bean, page, rows);
	}

}
