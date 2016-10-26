package com.fubangty.FGISystem.gdims.entity;


import java.util.Date;


/**
 * <p>Title:速报数据信息表 </p>
 * <p>Description: </p>
 * <p>Company:重庆富邦科技发展有限责任公司 </p>
 * @author 李青松
 * @version 0.1
 *
 */
public class QuickReported implements java.io.Serializable {
	private   int id;//编号
	private String unifiedCode;//统一编号  
	private String quickReportedType;//速报类型  
	private String  examineType;//审核状态  
	private Date happenTime;//发生时间  
	private String province;//省
	private String ciyt;//市
	private String  county;// 县
	private String   village;//   乡
	private String hamlet;//村
	private String team;//组
	private String place;//地点
	private String longitude;//经度
	private String latitude;//纬度
	private String disasterType;//灾害类型
	private Double disasterScale;//灾害规模
	private int victims;//受灾人数
	private int deadNum;//死亡人数
	private int missNum;//失踪人数
	private int injuredNum;//受伤人数
	private Double directLosses;//直接损失
	private int destroyHouse;// 毁坏房屋户
	private int destroyBuilding;//毁坏房屋间
	private Double destroyScale;//毁坏房屋面积
	private int transferNum;//转移户数
	private int transferPeople;//转移人数
	private String disasterStandard;//灾情达标
	private String disasterLevel;//灾情等级
	private int threatenNum;//威胁人数
	private int threatenProperyt;//威胁财产
	private int threatenHouse;//威胁房屋户
	private int threatenHouseNum;//威胁房屋间
	private Double threatenScale;//威胁房屋面积
	private String factor;//引发因素
	private String development;//发展趋势
	private String controlMethod;//防治措施
	private String newDisaster;//新发灾害
	private String reportedMan;//上报人员
	private String mobile;//上报手机
	private String pictuer;//图片
	public QuickReported() {
	}
	public QuickReported(int id, String unifiedCode, String quickReportedType,
			String examineType, Date happenTime, String province,String ciyt, String county,
			String village, String hamlet, String team, String place,
			String longitude, String latitude, String disasterType,
			Double disasterScale, int victims, int deadNum, int missNum,
			int injuredNum, Double directLosses, int destroyHouse,
			int destroyBuilding, Double destroyScale, int transferNum,
			int transferPeople, String disasterStandard, String disasterLevel,
			int threatenNum, int threatenProperyt, int threatenHouse,
			int threatenHouseNum, Double threatenScale, String factor,
			String development, String controlMethod, String newDisaster,
			String reportedMan, String mobile, String pictuer) {
		super();
		this.id = id;
		this.unifiedCode = unifiedCode;
		this.quickReportedType = quickReportedType;
		this.examineType = examineType;
		this.happenTime = happenTime;
		this.province=province;
		this.ciyt = ciyt;
		this.county = county;
		this.village = village;
		this.hamlet = hamlet;
		this.team = team;
		this.place = place;
		this.longitude = longitude;
		this.latitude = latitude;
		this.disasterType = disasterType;
		this.disasterScale = disasterScale;
		this.victims = victims;
		this.deadNum = deadNum;
		this.missNum = missNum;
		this.injuredNum = injuredNum;
		this.directLosses = directLosses;
		this.destroyHouse = destroyHouse;
		this.destroyBuilding = destroyBuilding;
		this.destroyScale = destroyScale;
		this.transferNum = transferNum;
		this.transferPeople = transferPeople;
		this.disasterStandard = disasterStandard;
		this.disasterLevel = disasterLevel;
		this.threatenNum = threatenNum;
		this.threatenProperyt = threatenProperyt;
		this.threatenHouse = threatenHouse;
		this.threatenHouseNum = threatenHouseNum;
		this.threatenScale = threatenScale;
		this.factor = factor;
		this.development = development;
		this.controlMethod = controlMethod;
		this.newDisaster = newDisaster;
		this.reportedMan = reportedMan;
		this.mobile = mobile;
		this.pictuer = pictuer;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUnifiedCode() {
		return unifiedCode;
	}
	public void setUnifiedCode(String unifiedCode) {
		this.unifiedCode = unifiedCode;
	}
	public String getQuickReportedType() {
		return quickReportedType;
	}
	public void setQuickReportedType(String quickReportedType) {
		this.quickReportedType = quickReportedType;
	}
	public String getExamineType() {
		return examineType;
	}
	public void setExamineType(String examineType) {
		this.examineType = examineType;
	}
	public Date getHappenTime() {
		return happenTime;
	}
	public void setHappenTime(Date happenTime) {
		this.happenTime = happenTime;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCiyt() {
		return ciyt;
	}
	public void setCiyt(String ciyt) {
		this.ciyt = ciyt;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getVillage() {
		return village;
	}
	public void setVillage(String village) {
		this.village = village;
	}
	public String getHamlet() {
		return hamlet;
	}
	public void setHamlet(String hamlet) {
		this.hamlet = hamlet;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
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
	public String getDisasterType() {
		return disasterType;
	}
	public void setDisasterType(String disasterType) {
		this.disasterType = disasterType;
	}
	public Double getDisasterScale() {
		return disasterScale;
	}
	public void setDisasterScale(Double disasterScale) {
		this.disasterScale = disasterScale;
	}
	public int getVictims() {
		return victims;
	}
	public void setVictims(int victims) {
		this.victims = victims;
	}
	public int getDeadNum() {
		return deadNum;
	}
	public void setDeadNum(int deadNum) {
		this.deadNum = deadNum;
	}
	public int getMissNum() {
		return missNum;
	}
	public void setMissNum(int missNum) {
		this.missNum = missNum;
	}
	public int getInjuredNum() {
		return injuredNum;
	}
	public void setInjuredNum(int injuredNum) {
		this.injuredNum = injuredNum;
	}
	public Double getDirectLosses() {
		return directLosses;
	}
	public void setDirectLosses(Double directLosses) {
		this.directLosses = directLosses;
	}
	public int getDestroyHouse() {
		return destroyHouse;
	}
	public void setDestroyHouse(int destroyHouse) {
		this.destroyHouse = destroyHouse;
	}
	public int getDestroyBuilding() {
		return destroyBuilding;
	}
	public void setDestroyBuilding(int destroyBuilding) {
		this.destroyBuilding = destroyBuilding;
	}
	public Double getDestroyScale() {
		return destroyScale;
	}
	public void setDestroyScale(Double destroyScale) {
		this.destroyScale = destroyScale;
	}
	public int getTransferNum() {
		return transferNum;
	}
	public void setTransferNum(int transferNum) {
		this.transferNum = transferNum;
	}
	public int getTransferPeople() {
		return transferPeople;
	}
	public void setTransferPeople(int transferPeople) {
		this.transferPeople = transferPeople;
	}
	public String getDisasterStandard() {
		return disasterStandard;
	}
	public void setDisasterStandard(String disasterStandard) {
		this.disasterStandard = disasterStandard;
	}
	public String getDisasterLevel() {
		return disasterLevel;
	}
	public void setDisasterLevel(String disasterLevel) {
		this.disasterLevel = disasterLevel;
	}
	public int getThreatenNum() {
		return threatenNum;
	}
	public void setThreatenNum(int threatenNum) {
		this.threatenNum = threatenNum;
	}
	public int getThreatenProperyt() {
		return threatenProperyt;
	}
	public void setThreatenProperyt(int threatenProperyt) {
		this.threatenProperyt = threatenProperyt;
	}
	public int getThreatenHouse() {
		return threatenHouse;
	}
	public void setThreatenHouse(int threatenHouse) {
		this.threatenHouse = threatenHouse;
	}
	public int getThreatenHouseNum() {
		return threatenHouseNum;
	}
	public void setThreatenHouseNum(int threatenHouseNum) {
		this.threatenHouseNum = threatenHouseNum;
	}
	public Double getThreatenScale() {
		return threatenScale;
	}
	public void setThreatenScale(Double threatenScale) {
		this.threatenScale = threatenScale;
	}
	public String getFactor() {
		return factor;
	}
	public void setFactor(String factor) {
		this.factor = factor;
	}
	public String getDevelopment() {
		return development;
	}
	public void setDevelopment(String development) {
		this.development = development;
	}
	public String getControlMethod() {
		return controlMethod;
	}
	public void setControlMethod(String controlMethod) {
		this.controlMethod = controlMethod;
	}
	public String getNewDisaster() {
		return newDisaster;
	}
	public void setNewDisaster(String newDisaster) {
		this.newDisaster = newDisaster;
	}
	public String getReportedMan() {
		return reportedMan;
	}
	public void setReportedMan(String reportedMan) {
		this.reportedMan = reportedMan;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPictuer() {
		return pictuer;
	}
	public void setPictuer(String pictuer) {
		this.pictuer = pictuer;
	}
	
}
