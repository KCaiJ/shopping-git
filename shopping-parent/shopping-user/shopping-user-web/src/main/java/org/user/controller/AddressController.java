package org.user.controller;

import java.util.Date;
/**
 * 地址
 */
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.shopping.common.CookUtils;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbAddress;
import org.shopping.pojo.TbAreas;
import org.shopping.pojo.TbCities;
import org.shopping.pojo.TbProvinces;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.user.service.AddressService;

import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/Address")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AddressController {

	@Reference
	private AddressService Service;

	/**
	 * 获取全部信息
	 * 
	 * @return
	 */

	@RequestMapping("/findAll")
	public List<TbAddress> findAll() {
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
		return Service.queryPageListByWhere(new TbAddress(), page, rows);
	}

	/**
	 * 增加
	 * 
	 * @param bean
	 * @return
	 */

	@RequestMapping("/add")
	public Result add(@RequestBody TbAddress bean) {
		try {
			bean.setIsDefault("0");
			bean.setCreateDate(new Date());
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
	 * @param Address
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbAddress bean) {
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
	public TbAddress findOne(Long id) {
		return Service.queryById(id);
	}

	/**
	 * 查询+分页
	 * 
	 * @param Address
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows, @RequestBody TbAddress bean) {
		return Service.findPage(bean, page, rows);
	}
	/**
	 * 根据用户查询地址
	 * @return
	 */
	@RequestMapping("/findListByLoginUser")
	public List<TbAddress> findListByLoginUser(HttpServletRequest request ){
		String username = CookUtils.getCookieName(request, Enumeration.CURRENT_USER);//获取登录用户名
		return Service.findListByUserId(username);
	}

	/**
	 * 设置默认地址
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/setteingDefault")
	public Result setteingDefault(Long id,HttpServletRequest request ){
		String username = CookUtils.getCookieName(request, Enumeration.CURRENT_USER);//获取登录用户名
		if( Service.setteingDefault(id,username)){
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.UPDATA_SUCCESS);
		}else{
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.UPDATA_FAIL);
		}
	}
	
	/**
	 * 根据省份ID查市区
	 */
	@RequestMapping("/findByCityId")
	public List<TbCities> findByCityId(String parentId){
		return Service.findByCityId(parentId);
	}
	
	/**
	 * 根据市区ID查县区
	 */
	@RequestMapping("/findByAreasId")
	public List<TbAreas> findByAreasId(String parentId){
		return Service.findByAreasId(parentId);
	}
	/**
	 * 返回省份列表
	 * @param parentId
	 * @return
	 */
	@RequestMapping("/findByProvinces")
	public List<TbProvinces> findByProvinces(){
		return Service.findByProvinces();
	}
	
	/**
	 * 返回城市列表
	 * @return
	 */
	@RequestMapping("/findByCities")
	public List<TbCities> findByCities(){
		return Service.findByCities();
	}
	

	/**
	 * 返回县区列表
	 * @return
	 */
	@RequestMapping("/findByAreas")
	public List<TbAreas> findByAreas(){
		return Service.findByAreas();
	}
}
