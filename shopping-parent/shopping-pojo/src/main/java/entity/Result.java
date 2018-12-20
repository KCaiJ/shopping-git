package entity;
/**
 * 增加 修改 删除 返回结果类
 */
import java.io.Serializable;

public class Result implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	private int code;
	private boolean success;
	private String message;
	public Result(int code,boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
		this.code = code;
	}
	
}
