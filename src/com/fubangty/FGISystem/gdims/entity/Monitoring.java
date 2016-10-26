/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.entity;


/**
 * <p>Title: 监测点配置表</p>
 * <p>Description: </p>
 * <p>Company:重庆富邦科技发展有限责任公司 </p>
 * @author liuhongliang create 2012-5-15
 * @version 0.1
 *
 */
public class Monitoring implements java.io.Serializable{

	private String monPointNumber;//监测点编号
	private String unifiedNumber;//统一编号
	private String monPointName;//监测点名称
	private String monPointLocation;//监测点位置
	private String longitude;//经度
	private String latitude;//纬度
	private double X;//X坐标
	private double Y;//Y坐标
	private String monContent;//监测内容
	private String instrumentNumber;//仪器编号
	private double instrumentConstant;//仪器常数
	private String dimension;//量纲
	private double monDirection;//监测方向
	private double monAngle;//监测角度
	private Integer monType;//监测点类型
	private String warnLevel;//预警等级
	private String receiverPhone;//预警接收手机
	private double legalR;//合法半径
	private double trojanValue;//累计告警初值
	private double adjacentRedValve;//相邻红色预警阀值
	private String adjacentRedInfo;//相邻红色预警信息
	private double adjacentOrangeValve;//相邻橙色预警阀值
	private String adjacentOrangeInfo;//相邻橙色预警信息
	private double adjacentYellowValve;//相邻黄色预警阀值
	private String adjacentYellowInfo;//相邻黄色预警信息
	private double adjacentBlueValve;//相邻蓝色预警阀值
	private String adjacentBlueInfo;//相邻蓝色预警信息
	private double trojanRedValve;//累计红色预警阀值
	private String trojanRedInfo;//累计红色预警信息
	private double trojanOrangeValve;//累计橙色预警阀值
	private String trojanOrangeInfo;//累计橙色预警信息
	private double trojanYellowValve;//累计黄色预警阀值
	private String trojanYellowInfo;//累计黄色预警信息
	private double trojanBlueValve;//累计蓝色预警阀值
	private String trojanBlueInfo;//累计蓝色预警信息
	
	public Monitoring() {
		super();
	}

	
	public Monitoring(String monPointNumber, String unifiedNumber,
			String monPointName, String monPointLocation, String longitude,
			String latitude, double x, double y, String monContent,
			String instrumentNumber, double instrumentConstant,
			String dimension, double monDirection, double monAngle,
			Integer monType, String warnLevel, String receiverPhone,
			double legalR, double trojanValue, double adjacentRedValve,
			String adjacentRedInfo, double adjacentOrangeValve,
			String adjacentOrangeInfo, double adjacentYellowValve,
			String adjacentYellowInfo, double adjacentBlueValve,
			String adjacentBlueInfo, double trojanRedValve,
			String trojanRedInfo, double trojanOrangeValve,
			String trojanOrangeInfo, double trojanYellowValve,
			String trojanYellowInfo, double trojanBlueValve,
			String trojanBlueInfo) {
		super();
		this.monPointNumber = monPointNumber;
		this.unifiedNumber = unifiedNumber;
		this.monPointName = monPointName;
		this.monPointLocation = monPointLocation;
		this.longitude = longitude;
		this.latitude = latitude;
		X = x;
		Y = y;
		this.monContent = monContent;
		this.instrumentNumber = instrumentNumber;
		this.instrumentConstant = instrumentConstant;
		this.dimension = dimension;
		this.monDirection = monDirection;
		this.monAngle = monAngle;
		this.monType = monType;
		this.warnLevel = warnLevel;
		this.receiverPhone = receiverPhone;
		this.legalR = legalR;
		this.trojanValue = trojanValue;
		this.adjacentRedValve = adjacentRedValve;
		this.adjacentRedInfo = adjacentRedInfo;
		this.adjacentOrangeValve = adjacentOrangeValve;
		this.adjacentOrangeInfo = adjacentOrangeInfo;
		this.adjacentYellowValve = adjacentYellowValve;
		this.adjacentYellowInfo = adjacentYellowInfo;
		this.adjacentBlueValve = adjacentBlueValve;
		this.adjacentBlueInfo = adjacentBlueInfo;
		this.trojanRedValve = trojanRedValve;
		this.trojanRedInfo = trojanRedInfo;
		this.trojanOrangeValve = trojanOrangeValve;
		this.trojanOrangeInfo = trojanOrangeInfo;
		this.trojanYellowValve = trojanYellowValve;
		this.trojanYellowInfo = trojanYellowInfo;
		this.trojanBlueValve = trojanBlueValve;
		this.trojanBlueInfo = trojanBlueInfo;
	}

	public String getMonPointNumber() {
		return monPointNumber;
	}

	public void setMonPointNumber(String monPointNumber) {
		this.monPointNumber = monPointNumber;
	}

	public String getUnifiedNumber() {
		return unifiedNumber;
	}

	public void setUnifiedNumber(String unifiedNumber) {
		this.unifiedNumber = unifiedNumber;
	}

	public String getMonPointName() {
		return monPointName;
	}

	public void setMonPointName(String monPointName) {
		this.monPointName = monPointName;
	}

	public String getMonPointLocation() {
		return monPointLocation;
	}

	public void setMonPointLocation(String monPointLocation) {
		this.monPointLocation = monPointLocation;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}

	public double getY() {
		return Y;
	}

	public void setY(double y) {
		Y = y;
	}

	public String getMonContent() {
		return monContent;
	}

	public void setMonContent(String monContent) {
		this.monContent = monContent;
	}

	public String getInstrumentNumber() {
		return instrumentNumber;
	}

	public void setInstrumentNumber(String instrumentNumber) {
		this.instrumentNumber = instrumentNumber;
	}

	public double getInstrumentConstant() {
		return instrumentConstant;
	}

	public void setInstrumentConstant(double instrumentConstant) {
		this.instrumentConstant = instrumentConstant;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public double getMonDirection() {
		return monDirection;
	}

	public void setMonDirection(double monDirection) {
		this.monDirection = monDirection;
	}

	public double getMonAngle() {
		return monAngle;
	}

	public void setMonAngle(double monAngle) {
		this.monAngle = monAngle;
	}

	public Integer getMonType() {
		return monType;
	}

	public void setMonType(Integer monType) {
		this.monType = monType;
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

	public double getLegalR() {
		return legalR;
	}

	public void setLegalR(double legalR) {
		this.legalR = legalR;
	}

	public double getTrojanValue() {
		return trojanValue;
	}

	public void setTrojanValue(double trojanValue) {
		this.trojanValue = trojanValue;
	}

	public double getAdjacentRedValve() {
		return adjacentRedValve;
	}

	public void setAdjacentRedValve(double adjacentRedValve) {
		this.adjacentRedValve = adjacentRedValve;
	}

	public String getAdjacentRedInfo() {
		return adjacentRedInfo;
	}

	public void setAdjacentRedInfo(String adjacentRedInfo) {
		this.adjacentRedInfo = adjacentRedInfo;
	}

	public double getAdjacentOrangeValve() {
		return adjacentOrangeValve;
	}

	public void setAdjacentOrangeValve(double adjacentOrangeValve) {
		this.adjacentOrangeValve = adjacentOrangeValve;
	}

	public String getAdjacentOrangeInfo() {
		return adjacentOrangeInfo;
	}

	public void setAdjacentOrangeInfo(String adjacentOrangeInfo) {
		this.adjacentOrangeInfo = adjacentOrangeInfo;
	}

	public double getAdjacentYellowValve() {
		return adjacentYellowValve;
	}

	public void setAdjacentYellowValve(double adjacentYellowValve) {
		this.adjacentYellowValve = adjacentYellowValve;
	}

	public String getAdjacentYellowInfo() {
		return adjacentYellowInfo;
	}

	public void setAdjacentYellowInfo(String adjacentYellowInfo) {
		this.adjacentYellowInfo = adjacentYellowInfo;
	}

	public double getAdjacentBlueValve() {
		return adjacentBlueValve;
	}

	public void setAdjacentBlueValve(double adjacentBlueValve) {
		this.adjacentBlueValve = adjacentBlueValve;
	}

	public String getAdjacentBlueInfo() {
		return adjacentBlueInfo;
	}

	public void setAdjacentBlueInfo(String adjacentBlueInfo) {
		this.adjacentBlueInfo = adjacentBlueInfo;
	}

	public double getTrojanRedValve() {
		return trojanRedValve;
	}

	public void setTrojanRedValve(double trojanRedValve) {
		this.trojanRedValve = trojanRedValve;
	}

	public String getTrojanRedInfo() {
		return trojanRedInfo;
	}

	public void setTrojanRedInfo(String trojanRedInfo) {
		this.trojanRedInfo = trojanRedInfo;
	}

	public double getTrojanOrangeValve() {
		return trojanOrangeValve;
	}

	public void setTrojanOrangeValve(double trojanOrangeValve) {
		this.trojanOrangeValve = trojanOrangeValve;
	}

	public String getTrojanOrangeInfo() {
		return trojanOrangeInfo;
	}

	public void setTrojanOrangeInfo(String trojanOrangeInfo) {
		this.trojanOrangeInfo = trojanOrangeInfo;
	}

	public double getTrojanYellowValve() {
		return trojanYellowValve;
	}

	public void setTrojanYellowValve(double trojanYellowValve) {
		this.trojanYellowValve = trojanYellowValve;
	}
	
	public String getTrojanYellowInfo() {
		return trojanYellowInfo;
	}

	public void setTrojanYellowInfo(String trojanYellowInfo) {
		this.trojanYellowInfo = trojanYellowInfo;
	}

	public double getTrojanBlueValve() {
		return trojanBlueValve;
	}

	public void setTrojanBlueValve(double trojanBlueValve) {
		this.trojanBlueValve = trojanBlueValve;
	}

	public String getTrojanBlueInfo() {
		return trojanBlueInfo;
	}

	public void setTrojanBlueInfo(String trojanBlueInfo) {
		this.trojanBlueInfo = trojanBlueInfo;
	}
	
	
}
