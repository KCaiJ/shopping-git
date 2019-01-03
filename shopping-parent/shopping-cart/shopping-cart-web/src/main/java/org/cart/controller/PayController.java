package org.cart.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.oder.service.OrderService;
import org.pay.service.PayService;
import org.shopping.common.CookUtils;
import org.shopping.common.Enumeration;
import org.shopping.common.IdWorker;
import org.shopping.pojo.TbPayLog;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;

import entity.Result;

/**
 * 支付控制层
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/Pay")
public class PayController {
	@Reference
	private  PayService PayService;
	@Reference
	private OrderService orderService;

	/**
	 * 返回参数
	 * @return
	 */
	@RequestMapping("/createNative")
	public Map createNative(String return_url,int type,HttpServletRequest request){
		//获取当前用户		
		String userId = CookUtils.getCookieName(request, Enumeration.CURRENT_USER);//获取登录用户名
		if (userId==null) {
			return new HashMap<>();
		}
		//到redis查询支付日志
		TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
		//判断支付日志存在
		if(payLog!=null){
			return PayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"",return_url,type,userId);
		}else{
			return new HashMap();
		}			
	}
		
	/**
	 * 修改订单状态
	 * @param out_trade_no
	 */
	@RequestMapping("/UpdatePayStatus")
	public TbPayLog UpdatePayStatus(String out_trade_no){
		return	orderService.updateOrderStatus(out_trade_no);
	}

}

