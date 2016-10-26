package com.fubangty.FGISystem.gdims.view;

import java.util.List;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.adapter.TreeViewAdapter;
import com.fubangty.FGISystem.gdims.base.BaseActivity;
import com.fubangty.FGISystem.gdims.entity.Tree;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class BaseTreeMenuActivity extends BaseActivity {
	/** Called when the activity is first created. */
	protected ExpandableListView expandableList;
	protected TreeViewAdapter adapter;
	protected String[] title;
	protected String[][] child;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_menu);
		// setTitle(R.string.dcpm_logo);
		adapter = new TreeViewAdapter(this);
		expandableList = (ExpandableListView) BaseTreeMenuActivity.this
				.findViewById(R.id.ExpandableListView01);
		expandableList.setGroupIndicator(null);
		
		adapter.RemoveAll();
		adapter.notifyDataSetChanged();
		List<Tree> treeNode = adapter.GetTreeNode();

		adapter.UpdateTreeNode(treeNode);
		expandableList.setAdapter(adapter);
		ProgressBar pb =(ProgressBar)findViewById(R.id.progressBar1);
		pb.setVisibility(View.GONE);
		Button location = (Button)findViewById(R.id.location);
		location.setVisibility(View.GONE);
		TextView title = (TextView)findViewById(R.id.title);
		title.setText("灾害点监测");
		ImageView star = (ImageView)findViewById(R.id.star);
		star.setVisibility(View.GONE);
	}
	
	public void initData(TreeMenuService service){
		
		//groups = service.initGroupData();
		//child = service.initChild();
	}
	
	public interface TreeMenuService{
		
		String[] initGroupData();
			
		String[][] initChild();
	}
	
}


