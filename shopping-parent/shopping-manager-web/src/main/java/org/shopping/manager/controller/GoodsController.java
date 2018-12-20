package org.shopping.manager.controller;
/**
 * 商品
 */
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.sellergoods.service.GoodsService;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbGoods;
import org.shopping.pojogroup.Goods;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/Goods")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GoodsController {

	@Reference
	private GoodsService Service;
	
	/**
	 * 获取全部信息
	 * @return
	 */
	 
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return Service.queryAll();
	}
	
	/**
	 * 返回分页列表
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){
		return Service.queryPageListByWhere(new TbGoods(),page, rows);
	}

	/**
	 * 增加
	 * @param bean
	 * @return
	 
	
	@RequestMapping("/add")
	public Result  add(@RequestBody Goods  bean,HttpServletRequest request){	
		String sellerId=CookUtils.getCookieName(request,Enumeration.CURRENT_SELLER);
		if (sellerId!=null) {
			bean.getGoods().setSellerId(sellerId);//获取用户名 并存储
		}
		if (Service.add(bean)) {
			return new Result(Enumeration.CODE_SUCCESS,true, Enumeration.INSETR_SUCCESS);
		}
		return new Result(Enumeration.CODE_SUCCESS,false, Enumeration.INSETR_FAIL);
	}*/
	/**
	 * 批量删除
	 * @param ids
	 */
	@RequestMapping("/delete")
	public Result  delete(Long[] ids) {
		if (Service.delete(ids)) {
			return new Result(Enumeration.CODE_SUCCESS,true, Enumeration.DELETE_SUCCESS); 
		}
		return new Result(Enumeration.CODE_SUCCESS,false, Enumeration.DELETE_FAIL);

	}
	/**
	 * 修改对象
	 * @param Goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result  update(@RequestBody Goods  bean){	
		if (Service.update(bean)) {
			return new Result(Enumeration.CODE_SUCCESS,true, Enumeration.UPDATA_SUCCESS);
		}
		return new Result(Enumeration.CODE_SUCCESS,false, Enumeration.UPDATA_FAIL);
	}
	
	/**
	 * 根据id获取对象
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return Service.findOne(id);		
	}

	/**
	 * 查询+分页
	 * @param Goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows ,@RequestBody TbGoods bean ,HttpServletRequest request){
		return Service.findPage(bean, page, rows);		
	}
	/**
	 * 更新状态
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		if (Service.updateStatus(ids, status)) {
			return new Result(Enumeration.CODE_SUCCESS,true, Enumeration.UPDATA_SUCCESS); 
		}
		return new Result(Enumeration.CODE_SUCCESS,false, Enumeration.UPDATA_FAIL);		
	}
	
}
