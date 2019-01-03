package org.pay.service;
import java.util.Map;
/**
 * 微信支付接口
 * @author Administrator
 *
 */
public interface PayService {

	/**
	 * 返回参数
	 */
	public Map createNative(String out_trade_no,String total_fee,String return_url,int type,String orderuid);


}
