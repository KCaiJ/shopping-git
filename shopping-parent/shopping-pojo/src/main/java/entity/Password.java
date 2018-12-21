package entity;

import java.io.Serializable;

/**
 * 更改密码实体类
 * @author root
 *
 */
public class Password implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String oldPassword;
	private String newPassword;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	

}
