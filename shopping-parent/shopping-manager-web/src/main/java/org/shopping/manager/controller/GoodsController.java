package org.shopping.manager.controller;

import java.util.Arrays;
/**
 * 商品
 */
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import org.sellergoods.service.GoodsService;
import org.shopping.common.CookUtils;
import org.shopping.common.Enumeration;
import org.shopping.pojo.TbGoods;
import org.shopping.pojo.TbItem;
import org.shopping.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/Goods")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GoodsController {

	@Autowired
	private Destination queueSolrDeleteDestination;//用户在索引库中删除记录

	@Autowired
	private Destination queueSolrDestination;//用于发送solr导入的消息

	@Autowired
	private Destination topicPageDestination;//发布订阅生成网页详细页
	
	@Autowired
	private Destination topicPageDeleteDestination;//发布定义删除网页详细页
	
	@Autowired
	private JmsTemplate jmsTemplate;

	@Reference
	private GoodsService Service;

	/**
	 * 获取全部信息
	 * 
	 * @return
	 */

	@RequestMapping("/findAll")
	public List<TbGoods> findAll() {
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
		return Service.queryPageListByWhere(new TbGoods(), page, rows);
	}
	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	@RequestMapping("/delete")
	public Result delete(final Long[] ids) {
		if (Service.delete(ids)) {
			//从solr中删除商品
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {		
				@Override
				public Message createMessage(Session session) throws JMSException {	
					return session.createObjectMessage(ids);
				}
			});	
			
			//删除页面
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {		
				@Override
				public Message createMessage(Session session) throws JMSException {	
					return session.createObjectMessage(ids);
				}
			});	

			
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.DELETE_SUCCESS);
		}
		return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.DELETE_FAIL);

	}
	/**
	 * 根据id获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id) {
		return Service.findOne(id);
	}

	/**
	 * 查询+分页
	 * 
	 * @param Goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(int page, int rows, @RequestBody TbGoods bean, HttpServletRequest request) {
		return Service.findPage(bean, page, rows);
	}

	/**
	 * 更新状态
	 * 
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status) {
		if (Service.updateStatus(ids, status)) {
			if (status.equals("2")) {// 审核通过
				List<TbItem> itemList = Service.findItemListByGoodsIdandStatus(ids, "1");
				
				// 数据批量导入solr
				if (itemList.size() > 0) {
					final String jsonString = JSON.toJSONString(itemList);		
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {	
						@Override
						public Message createMessage(Session session) throws JMSException {							
								return session.createTextMessage(jsonString);
						}
					});					
					System.out.println(itemList.size()+"  "+jsonString);
				} else {
					System.out.println("没有明细数据");
				}
				
				//网页静态生成
				for(final Long goodsId:ids){
					jmsTemplate.send(topicPageDestination,new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub
							return session.createTextMessage(goodsId+"");
						}
					});
				}
				
				
			}
			return new Result(Enumeration.CODE_SUCCESS, true, Enumeration.UPDATA_SUCCESS);
		}
		return new Result(Enumeration.CODE_SUCCESS, false, Enumeration.UPDATA_FAIL);
	}

}
