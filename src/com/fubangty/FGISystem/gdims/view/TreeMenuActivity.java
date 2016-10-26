
package com.fubangty.FGISystem.gdims.view;

import java.util.List;

import com.fubangty.FGISystem.gdims.adapter.TreeViewAdapter;
import com.fubangty.FGISystem.gdims.dao.TabMonitorDao;
import com.fubangty.FGISystem.gdims.entity.Node;
import com.fubangty.FGISystem.gdims.entity.Tree;
import com.fubangty.FGISystem.gdims.util.AsyncDataTask;
import com.fubangty.FGISystem.gdims.util.DataBaseHelper;
import com.fubangty.FGISystem.gdims.util.AsyncDataTask.Callback;
import com.fubangty.FGISystem.gdims.view.BaseTreeMenuActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

/**
 * @author liqingsong 
 * @version 0.1
 *
 */
public class TreeMenuActivity extends BaseTreeMenuActivity {
	
	private ProgressDialog pd ;
	private static final String MSG = "正在获取数据请稍等。。。";
	private AsyncDataTask task ;
	
	DataBaseHelper dbHelper = new DataBaseHelper(this);       
	
	private Callback callback = new Callback() {
		
		@Override
		public void onUpdate(Object... values) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public Object onStart(Object... params) {
			TabMonitorDao dao = new TabMonitorDao(TreeMenuActivity.this);
			List<Tree> tree = dao.queryForList();
			return tree;
		}
		
		@Override
		public void onPrepare() {
			pd = ProgressDialog.show(TreeMenuActivity.this, null, MSG,true,true);
			
		}
		
		@Override
		public void onFinish(Object result) {
			pd.dismiss();
			if(result==null){
				showToast("数据读取失败,请查阅日志");
			}
			
			adapter.UpdateTreeNode((List<Tree>)result);
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		task = new AsyncDataTask(this, callback);
		task.execute();
		
		super.onCreate(savedInstanceState);
		
		expandableList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				
				TreeViewAdapter adapter = (TreeViewAdapter)parent.getExpandableListAdapter();
				Tree tree = (Tree)adapter.getGroup(groupPosition);
				Node node = (Node)adapter.getChild(groupPosition, childPosition);
				//点击后将灾害点编号名称 监测点编号名称传入下一个activity
				Log.d(TAG, node.getValue());
				
				Bundle bundle = new Bundle();
				
				if(childPosition==0){
					bundle.putString("disNo", node.getValue());
					forward(MacroActivity.class,bundle);
				}else{
					bundle.putString("dName", tree.getTitle());//灾害点名称
					bundle.putString("dValue", tree.getValue());//灾害点编号
					bundle.putString("mName", node.getName());//监测点名称
					bundle.putString("mValue", node.getValue());//监测点编号
					forward(MonitorActivity.class,bundle);//点击后将绑定的数据传送过去
				}
				
				return false;
			}

			
		});
	}
	
	

}
