package com.fubangty.FGISystem.gdims.adapter;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company:重庆富邦科技发展有限责任公司 </p>
 * @author zhoulei create 2011-1-16
 * @version 0.1
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.entity.Node;
import com.fubangty.FGISystem.gdims.entity.Tree;
import com.fubangty.FGISystem.gdims.util.GobalApplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class TreeViewAdapter extends BaseExpandableListAdapter {


	List<Tree> treeNodes = new ArrayList<Tree>();
	Context parentContext;

	public TreeViewAdapter(Context view) {
		parentContext = view;

	}

	public List<Tree> GetTreeNode() {
		return treeNodes;
	}

	public void UpdateTreeNode(List<Tree> nodes) {
		treeNodes = nodes;
	}

	public void RemoveAll() {
		treeNodes.clear();
	}

	public Object getChild(int groupPosition, int childPosition) {
		return treeNodes.get(groupPosition).getChilds().get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return treeNodes.get(groupPosition).getChilds().size();
	}


	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		Node node = (Node)getChild(groupPosition, childPosition);
		
	
		ChildHolder holder;
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = LayoutInflater.from(parentContext).inflate(
                    R.layout.item_child, null);
            holder.name = (TextView) convertView.findViewById(R.id.child_name);
           
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
	
        Node cb = treeNodes.get(groupPosition).getChilds().get(childPosition);
GobalApplication gobal = (GobalApplication)parentContext.getApplicationContext();
		
		Map<String, String> cache = gobal.getCache();
		
		String title = node.getName();
		if(cache.get(node.getValue()+node.getTitle())!=null){
			System.out.println("有数据么？"+cache.toString());
			 holder.name.setText(node.getName()+"(已上报)");
		}else{
			 holder.name.setText(node.getName());
		}
       
        return convertView;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		  GroupHolder holder;
		  Tree tree = (Tree)getGroup(groupPosition);
	        if (convertView == null) {
	            holder = new GroupHolder();
	            convertView = LayoutInflater.from(parentContext).inflate(
	                    R.layout.item_group, null);
	            holder.title = (TextView) convertView
	                    .findViewById(R.id.group_title);
	            holder.iv = (ImageView) convertView.findViewById(R.id.group_ico);
	            convertView.setTag(holder);
	        } else {
	            holder = (GroupHolder) convertView.getTag();
	        }
	        holder.title.setText(treeNodes.get(groupPosition).getTitle());
	        if (isExpanded) {
	            holder.iv.setImageResource(R.drawable.dizai1);
	        } else {
	            holder.iv.setImageResource(R.drawable.dizai2);
	        }
	        return convertView;	
		
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public Object getGroup(int groupPosition) {
		return treeNodes.get(groupPosition);
	}

	public int getGroupCount() {
		return treeNodes.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}
    class GroupHolder {
        TextView title;
        ImageView iv;
    }

    class ChildHolder {
        TextView name, sign;
    }
}