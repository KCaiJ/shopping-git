package org.shopping.manager.controller;
/**
 * 商家入驻
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sellergoods.service.SellerService;
import org.shopping.common.Encrypt;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbSeller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/Seller")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SellerController {

	@Reference
	private SellerService Service;

	/**
	 * 获取全部信息
	 * 
	 * @return
	 */

	@RequestMapping("/findAll")
	public List<TbSeller> findAll() {
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
		return Service.queryPageListByWhere(new TbSeller(), page, rows);
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
	 * @param Seller
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeller bean) {
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
	public TbSeller findOne(String id) {
		return Service.findOne(id);
	}

	/**
	 * 查询+分页
	 * 
	 * @param Seller
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows, @RequestBody TbSeller bean) {
		return Service.findPage(bean, page, rows);
	}

	/**
	 * 更改状态
	 * 
	 * @param sellerId
	 *            商家ID
	 * @param status
	 *            状态
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(String sellerId, String status) {
		try {
			Service.updateStatus(sellerId, status);
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.FAIL);
		}
	}
}
