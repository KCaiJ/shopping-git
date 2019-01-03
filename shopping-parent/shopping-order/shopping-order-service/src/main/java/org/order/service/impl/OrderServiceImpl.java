package org.order.service.impl;
/**
 * 订单管理
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.oder.service.OrderService;
import org.shopping.common.IdWorker;
import org.shopping.mapper.TbOrderItemMapper;
import org.shopping.mapper.TbOrderMapper;
import org.shopping.mapper.TbPayLogMapper;
import org.shopping.pojo.TbOrder;
import org.shopping.pojo.TbOrderItem;
import org.shopping.pojo.TbPayLog;
import org.shopping.pojogroup.Cart;
import org.shopping.pojogroup.Order;
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
	@Autowired
	private TbPayLogMapper payLogMapper;

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
		List<String> orderIdList=new ArrayList();//订单ID列表
		double total_money=0;//总金额 （元）
		for (Cart cart : cartList) {
			long orderId = idWorker.nextId();
			System.out.println("sellerId:" + cart.getSellerId());
			TbOrder tborder = new TbOrder();// 新创建订单对象
			tborder.setOrderId(orderId);// 订单ID
			tborder.setUserId(order.getUserId());// 用户名
			tborder.setPaymentType(order.getPaymentType());// 支付类型
			tborder.setStatus("1");// 状态：未付款
			
			if (order.getPaymentType().equals("2")) {
				tborder.setStatus("3");// 选择货到付款  状态：未发货
			}
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
			
			orderIdList.add(orderId+"");//添加到订单列表	
			total_money+=money;//累加到总金额 	
		}
		if("1".equals(order.getPaymentType())){//如果是微信支付		
			TbPayLog payLog=new TbPayLog();
			String outTradeNo=  idWorker.nextId()+"";//支付订单号
			payLog.setOutTradeNo(outTradeNo);//支付订单号
			payLog.setCreateTime(new Date());//创建时间
			//订单号列表，逗号分隔
			String ids=orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
			payLog.setOrderList(ids);//订单号列表，逗号分隔
			payLog.setPayType("1");//支付类型
			payLog.setTotalFee( (long)(total_money*100 ) );//总金额(分)
			payLog.setTradeState("0");//支付状态
			payLog.setUserId(order.getUserId());//用户ID			
			payLogMapper.insert(payLog);//插入到支付日志表			
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存		
			redisTemplate.expire("payLog", 60*30, TimeUnit.SECONDS);
		}		

		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
		return 1;
	}

	
	/**
	 * 根据字段名和状态返回订单列表
	 */
	@Override
	public List<Order> findByUserName(String poro,String id, String status) {
		List<Order> list=new ArrayList<>();
		Example example = new Example(TbOrder.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo(poro,id);
		if (status != null&&!status.equals("")) {
			criteria.andEqualTo("status",status);
		}		
		List<TbOrder> orderlist=orderMapper.selectByExample(example);
		for(TbOrder tbOrder:orderlist){
			Example exampleItem = new Example(TbOrderItem.class);
			Criteria criteriaItem = exampleItem.createCriteria();
			criteriaItem.andEqualTo("orderId",tbOrder.getOrderId());
			List<TbOrderItem> oderlist=orderItemMapper.selectByExample(exampleItem);
			Order order=new Order();
			order.setOrder(tbOrder);
			order.setOrderItemlist(oderlist);
			order.setOrderId(tbOrder.getOrderId()+"");
			list.add(order);
				
		}
		return list;
	}

	/**
	 * 根据订单id返回订单详细
	 */
	@Override
	public Order findByOrderId(Long orderid) {
		TbOrder tbOrder=orderMapper.selectByPrimaryKey(orderid);
		Example exampleItem = new Example(TbOrderItem.class);
		Criteria criteriaItem = exampleItem.createCriteria();
		criteriaItem.andEqualTo("orderId",orderid);
		List<TbOrderItem> oderlist=orderItemMapper.selectByExample(exampleItem);	
		Order order=new Order();
		order.setOrder(tbOrder);
		order.setOrderId(orderid+"");
		order.setOrderItemlist(oderlist);
		return order;
	}
	/**
	 * 根据用户查询payLog
	 * @param userId
	 * @return
	 */
	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);		
	}

	/**
	 * 更改订单状态
	 */
	@Override
	public TbPayLog updateOrderStatus(String out_trade_no) {
		//1.修改支付日志状态
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());
		payLog.setTradeState("1");//已支付
		payLogMapper.updateByPrimaryKey(payLog);		
		//2.修改订单状态
		String orderList = payLog.getOrderList();//获取订单号列表
		String[] orderIds = orderList.split(",");//获取订单号数组
		
		for(String orderId:orderIds){
			TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId) );
			if(order!=null){
				order.setStatus("3");//等待发货
				orderMapper.updateByPrimaryKey(order);
			}			
		}
		//清除redis缓存数据		
		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
		return payLog;
	}

	

}
