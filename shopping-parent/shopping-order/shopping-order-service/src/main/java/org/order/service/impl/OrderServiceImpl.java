package org.order.service.impl;
/**
 * 订单管理
 */

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.oder.service.OrderService;
import org.shopping.common.IdWorker;
import org.shopping.mapper.TbOrderItemMapper;
import org.shopping.mapper.TbOrderMapper;
import org.shopping.pojo.TbOrder;
import org.shopping.pojo.TbOrderItem;
import org.shopping.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class OrderServiceImpl extends BaseServiceImpl<TbOrder>implements OrderService {
	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private IdWorker idWorker;

	/**
	 * 查询+分页
	 */
	public PageResult findPage(TbOrder bean, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Example example = new Example(TbOrder.class);
		Criteria criteria = example.createCriteria();
		if (bean != null) {
			/*
			 * if (bean.getName() != null && bean.getName().length() > 0) {
			 * criteria.andLike("name", "%" + bean.getName() + "%"); } if
			 * (bean.getFirstChar() != null && bean.getFirstChar().length() > 0)
			 * { criteria.andEqualTo("firstChar", bean.getFirstChar()); }
			 */
		}
		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Transactional
	@Override
	public Integer save(TbOrder order) {
		// 得到购物车数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		for (Cart cart : cartList) {
			long orderId = idWorker.nextId();
			System.out.println("sellerId:" + cart.getSellerId());
			TbOrder tborder = new TbOrder();// 新创建订单对象
			tborder.setOrderId(orderId);// 订单ID
			tborder.setUserId(order.getUserId());// 用户名
			tborder.setPaymentType(order.getPaymentType());// 支付类型
			tborder.setStatus("1");// 状态：未付款
			tborder.setCreateTime(new Date());// 订单创建日期
			tborder.setUpdateTime(new Date());// 订单更新日期
			tborder.setReceiverAreaName(order.getReceiverAreaName());// 地址
			tborder.setReceiverMobile(order.getReceiverMobile());// 手机号
			tborder.setReceiver(order.getReceiver());// 收货人
			tborder.setSourceType(order.getSourceType());// 订单来源
			tborder.setSellerId(cart.getSellerId());// 商家ID
			// 循环购物车明细
			double money = 0;
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);// 订单ID
				orderItem.setSellerId(cart.getSellerId());
				money += orderItem.getTotalFee().doubleValue();// 金额累加
				orderItemMapper.insert(orderItem);
			}
			tborder.setPayment(new BigDecimal(money));
			orderMapper.insert(tborder);
		}
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
		return 1;
	}

}
