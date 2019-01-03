package org.shopping.pojogroup;

import java.io.Serializable;
import java.util.List;

import org.shopping.pojo.TbOrder;
import org.shopping.pojo.TbOrderItem;

public class Order implements Serializable {
	private TbOrder order;
	private List<TbOrderItem> orderItemlist;
	private String orderId;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public TbOrder getOrder() {
		return order;
	}
	public void setOrder(TbOrder order) {
		this.order = order;
	}
	public List<TbOrderItem> getOrderItemlist() {
		return orderItemlist;
	}
	public void setOrderItemlist(List<TbOrderItem> orderItemlist) {
		this.orderItemlist = orderItemlist;
	}
	
	
}
