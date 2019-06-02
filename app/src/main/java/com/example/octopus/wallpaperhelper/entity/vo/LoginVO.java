package com.example.octopus.wallpaperhelper.entity.vo;

public class LoginVO {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	/** 用户名 */
	private String userid;
	
	/** 密码 */
	private String userpassword;
	
	/** 昵称 */
	private String nickname;
	
	/** 上次登录时间 */
	private String logindate;
	
	/** field1 */
	private String field1;
	
	/** field2 */
	private String field2;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUserpassword() {
		return userpassword;
	}

	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getLogindate() {
		return logindate;
	}

	public void setLogindate(String logindate) {
		this.logindate = logindate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}
}
