package org.cart.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cart.service.CartService;
import org.shopping.common.CookUtils;
import org.shopping.common.Enumeration;
import org.shopping.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;

import entity.Result;

@RestController
@RequestMapping("/Cart")
public class CartController {

	@Reference(timeout=6000)
	private CartService cartService;
	

	
	/**
	 * 购物车列表
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		String username = CookUtils.getCookieName(request, Enumeration.CURRENT_USER);//获取登录用户名
		String cartListString = CookUtils.getCookieName(request, Enumeration.COOKIE_CART_NAME);//获取cookie购物车
		if(cartListString==null || cartListString.equals("")){
			cartListString="[]";
		}
		cartListString=URLDecoder.decode(cartListString,"utf-8");
		List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
		//未登录
		if (username == null) {
			return cartList_cookie;	
		}else{
			//已经登录
			List<Cart> cartList_redis =cartService.findCartListFromRedis(username);//从redis中提取					
			if(cartList_cookie.size()>0){//如果本地存在购物车
				//合并购物车
				cartList_redis=cartService.mergeCartList(cartList_redis, cartList_cookie);	
				//清除cookie的数据
				Cookie cookie = new Cookie(Enumeration.COOKIE_CART_NAME,URLEncoder.encode("", "utf-8"));
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);	
				//将合并后的数据存入redis 
				cartService.saveCartListToRedis(username, cartList_redis); 
			}			
			return cartList_redis;
		}
	}
	
	/**
	 * 添加商品到购物车
	 * @param request
	 * @param response
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId,Integer num,HttpServletRequest request,HttpServletResponse response){
		String username = CookUtils.getCookieName(request, Enumeration.CURRENT_USER);
		System.out.println("当前登录用户："+username);
		try {			
			List<Cart> cartList =findCartList(request, response);//获取购物车列表
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);	
			//CookUtils.setCookie(request, response, "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
			if(username==null){ //如果是未登录，保存到cookie
				Cookie cookie = new Cookie(Enumeration.COOKIE_CART_NAME,
						URLEncoder.encode(JSON.toJSONString(cartList), "utf-8"));
				cookie.setMaxAge(3600 * 3);
				cookie.setPath("/");
				response.addCookie(cookie);	
				System.out.println("向cookie存入数据");
			}else{//如果是已登录，保存到redis
				cartService.saveCartListToRedis(username, cartList);			
			}	
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.INSETR_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.INSETR_FAIL);
		}
	}	
}
