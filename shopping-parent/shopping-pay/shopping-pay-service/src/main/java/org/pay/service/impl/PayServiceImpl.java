package org.pay.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.pay.service.PayService;
import org.shopping.common.Encrypt;
import org.shopping.common.HttpClient;
import org.shopping.common.HttpUtil;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;

@Service
public class PayServiceImpl implements PayService {

	@Value("${identification}")
	private String identification;//商户唯一标识
	
	@Value("${token}")
	private String token;//token
	
	/**
	 * 返回参数
	 * @return
	 */
	public Map createNative(String out_trade_no,String total_fee,String return_url,int type,String orderuid){

		//创建参数
		int price=Integer.valueOf(total_fee);//价格
		//int type=Integer.valueOf("1");//支付渠道  1：微信支付；2：支付宝
		String notify_url="http://127.0.0.1:8088/sucess_notify";//通知回调网址
		//String return_url="http://127.0.0.1:8088/sucess";//跳转网址
		String orderid=out_trade_no;//	商户自定义订单号	
	//	String orderuid="ckj";//商户自定义客户号  用户名
	//	String goodsname="shopping";  //商品名称
		String key= identification + notify_url + orderid + orderuid + price + return_url + token + type;
		
		//构建Map
		Map map=new HashMap<Object, Object>(); 
		map.put("identification", identification);
		map.put("price", price);
		map.put("type", type);
		map.put("notify_url",notify_url );
		map.put("return_url", return_url);
		map.put("orderid", orderid);
		map.put("orderuid",orderuid);
	//	map.put("token",token);
		map.put("key", Encrypt.md5(key));
		return map;
	}


	
}

