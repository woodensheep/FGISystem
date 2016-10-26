/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.entity;


/**
 * <p>Title: 宏观观测配置表</p>
 * <p>Description: </p>
 * <p>Company:重庆富邦科技发展有限责任公司 </p>
 * @author liuhongliang create 2012-5-15
 * @version 0.1
 *
 */
public class Macroscopic implements java.io.Serializable{

	private String unifiedNumber;//统一编号
	private String warnLevel;//预警等级
	private String receiverPhone;//预警接收手机
	private String macroscopicPhenomenon;//宏观现象
	private double legalR;//合法半径 
	private int redValve;//红色预警阀
	private int orangeValve;//橙色预警阀
	private int yellowValve;//黄色预警阀
	private int blueValve;//蓝色预警阀
	
	public Macroscopic() {
		super();
	}

	public Macroscopic(String unifiedNumber, String warnLevel,
			String receiverPhone, String macroscopicPhenomenon, double legalR,
			int redValve, int orangeValve, int yellowValve, int blueValve) {
		super();
		this.unifiedNumber = unifiedNumber;
		this.warnLevel = warnLevel;
		this.receiverPhone = receiverPhone;
		this.macroscopicPhenomenon = macroscopicPhenomenon;
		this.legalR = legalR;
		this.redValve = redValve;
		this.orangeValve = orangeValve;
		this.yellowValve = yellowValve;
		this.blueValve = blueValve;
	}
	public String getUnifiedNumber() {
		return unifiedNumber;
	}

	public void setUnifiedNumber(String unifiedNumber) {
		this.unifiedNumber = unifiedNumber;
	}
	
	public String getWarnLevel() {
		return warnLevel;
	}

	public void setWarnLevel(String warnLevel) {
		this.warnLevel = warnLevel;
	}
	
	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	public String getMacroscopicPhenomenon() {
		return macroscopicPhenomenon;
	}

	public void setMacroscopicPhenomenon(String macroscopicPhenomenon) {
		this.macroscopicPhenomenon = macroscopicPhenomenon;
	}

	public double getLegalR() {
		return legalR;
	}

	public void setLegalR(double legalR) {
		this.legalR = legalR;
	}

	public int getRedValve() {
		return redValve;
	}

	public void setRedValve(int redValve) {
		this.redValve = redValve;
	}

	public int getOrangeValve() {
		return orangeValve;
	}

	public void setOrangeValve(int orangeValve) {
		this.orangeValve = orangeValve;
	}

	public int getYellowValve() {
		return yellowValve;
	}

	public void setYellowValve(int yellowValve) {
		this.yellowValve = yellowValve;
	}

	public int getBlueValve() {
		return blueValve;
	}

	public void setBlueValve(int blueValve) {
		this.blueValve = blueValve;
	}
	
	
}
