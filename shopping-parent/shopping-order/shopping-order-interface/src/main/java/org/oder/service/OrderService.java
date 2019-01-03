package org.oder.service;

import java.util.List;

import org.shopping.pojo.TbOrder;
import org.shopping.pojo.TbPayLog;
import org.shopping.pojogroup.Order;

import Base.BaseService;

public interface OrderService extends BaseService<TbOrder> {
	
	/**
	 * 根据用户名和状态返回订单详细
	 */

	public List<Order> findByUserName(String pojo,String userId, String status);
	
	/**
	 * 根据订单号返回实体
	 */
	public Order findByOrderId(Long orderid);
	
	/**
	 * 根据用户查询payLog
	 * @param userId
	 * @return
	 */
	public TbPayLog searchPayLogFromRedis(String userId);
	
	
	/**
	 * 修改订单状态
	 * @param out_trade_no 支付订单号
	 */
	public TbPayLog updateOrderStatus(String out_trade_no);


}
