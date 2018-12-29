package org.user.service;

import org.shopping.pojo.TbUser;
import Base.BaseService;
public interface UserService extends BaseService<TbUser>{
	/**
	 * 生成短信验证码
	 * @return
	 */
	public void createSmsCode(String phone);

	/**
	 * 判断短信验证码是否存在
	 * @param phone
	 * @return
	 */
	public boolean  checkSmsCode(String phone,String code);

	/**
	 * 根据用户名获取实体
	 */
	public TbUser findOneByName(String name) ;
}
