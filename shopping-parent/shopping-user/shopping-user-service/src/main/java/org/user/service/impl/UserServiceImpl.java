package org.user.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.shopping.mapper.TbUserMapper;
import org.shopping.pojo.TbSeller;
import org.shopping.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.user.service.UserService;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;

import Base.BaseServiceImpl;
import entity.PageResult;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServiceImpl extends BaseServiceImpl<TbUser>implements UserService {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private TbUserMapper mapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination smsDestination;

	@Override
	public PageResult findPage(TbUser bean, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 生成短信验证码
	 */
	public void createSmsCode(final String phone) {
		// 生成6位随机数
		final String code = (long) (Math.random() * 1000000) + "";
		System.out.println("验证码：" + code);
		// 存入缓存
		redisTemplate.boundHashOps("smscode").put(phone, code);
		redisTemplate.expire("smscode", 100, TimeUnit.SECONDS);
		// 发送到activeMQ
		jmsTemplate.send(smsDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("mobile", phone);//手机号
				mapMessage.setString("code", code);//验证码
				return mapMessage;
			}
		});

	}

	/**
	 * 判断验证码是否正确
	 */
	public boolean checkSmsCode(String phone, String code) {
		// 得到缓存中存储的验证码
		String sysCode = (String) redisTemplate.boundHashOps("smscode").get(phone);
		if (sysCode == null) {
			return false;
		}
		if (!sysCode.equals(code)) {
			return false;
		}
		return true;
	}

	@Override
	public TbUser findOneByName(String name) {
		Example example = new Example(TbUser.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("username", name);
		ArrayList<TbUser> list = (ArrayList<TbUser>) mapper.selectByExample(example);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 检查字段名是否存在
	 */
	@Override
	public boolean check(String pro,String name) {
		Example example = new Example(TbUser.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo(pro, name);
		ArrayList<TbUser> list = (ArrayList<TbUser>) mapper.selectByExample(example);
		if (list.size() > 0) {
			return true;
		}
		return false;
	}
}
