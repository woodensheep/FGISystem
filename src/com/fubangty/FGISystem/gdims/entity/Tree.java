/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyun create 2012-6-2
 * @version 0.1
 *
 */
public class Tree{
	
	private String title;
	private String value;
	private List<Node> childs = new ArrayList<Node>();
	
	
	public Tree(String title,String value){
		this.title = title;
		this.value = value;
		
		Node node = new Node();
		node.setName("宏观观测");
		node.setValue(value);
		node.setTitle(value);
		childs.add(node);
	}

	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Node> getChilds() {
		return childs;
	}
	public void setChilds(List<Node> childs) {
		this.childs = childs;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}

}

