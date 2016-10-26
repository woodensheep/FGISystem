package com.fubangty.FGISystem.deffend.entity;

import android.widget.EditText;

/**
 * 周报小组成员实体类
 * @author lemon
 *
 */
public class WeeklyMember {
	/**姓名**/
	private String memberName;
	/** 巡查情况 **/
	private String xunchaSituation;
	/** 监测工作 **/
	private String monitor;
	/** 培训、指导等其它方面工作 **/
	private String othersWork;
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getXunchaSituation() {
		return xunchaSituation;
	}
	public void setXunchaSituation(String xunchaSituation) {
		this.xunchaSituation = xunchaSituation;
	}
	public String getMonitor() {
		return monitor;
	}
	public void setMonitor(String monitor) {
		this.monitor = monitor;
	}
	public String getOthersWork() {
		return othersWork;
	}
	public void setOthersWork(String othersWork) {
		this.othersWork = othersWork;
	}
}
