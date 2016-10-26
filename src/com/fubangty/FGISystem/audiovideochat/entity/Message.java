package com.fubangty.FGISystem.audiovideochat.entity;

import java.io.Serializable;

public class Message implements Serializable{
	
	private int id;
	private String invite_man;  //邀请人
	private int invite_userId;  //邀请人id
	private String phone_ids;   //手机IMEI
	private String push_msg;    //推送消息
	private String push_time;   //推送时间
	private int room_id;        //房间ID
	private String area_id;     //区域ID
	private int push_type;      //邀请类别
	
	public int getPush_type() {
		return push_type;
	}

	public void setPush_type(int push_type) {
		this.push_type = push_type;
	}

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}
	
	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getInvite_man() {
		return invite_man;
	}
	
	public void setInvite_man(String invite_man) {
		this.invite_man = invite_man;
	}
	
	public int getInvite_userId() {
		return invite_userId;
	}
	
	public void setInvite_userId(int invite_userId) {
		this.invite_userId = invite_userId;
	}
	
	public String getPush_msg() {
		return push_msg;
	}
	
	public void setPush_msg(String push_msg) {
		this.push_msg = push_msg;
	}
	
	public String getPhone_ids() {
		return phone_ids;
	}
	
	public void setPhone_ids(String phone_ids) {
		this.phone_ids = phone_ids;
	}
	
	public String getPush_time() {
		return push_time;
	}
	
	public void setPush_time(String push_time) {
		this.push_time = push_time;
	}
	
}
