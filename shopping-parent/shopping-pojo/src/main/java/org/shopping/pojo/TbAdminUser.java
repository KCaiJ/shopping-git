package org.shopping.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "tb_admin_user")
public class TbAdminUser implements Serializable {
	@Id
	private Long id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码，加密存储
	 */
	private String password;

	private static final long serialVersionUID = 1L;

	/**
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取用户名
	 *
	 * @return username - 用户名
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置用户名
	 *
	 * @param username
	 *            用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 获取密码，加密存储
	 *
	 * @return password - 密码，加密存储
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码，加密存储
	 *
	 * @param password
	 *            密码，加密存储
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", id=").append(id);
		sb.append(", username=").append(username);
		sb.append(", password=").append(password);
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append("]");
		return sb.toString();
	}
}