
package com.fubangty.FGISystem.gdims.view;

import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.base.BaseActivity;
import com.fubangty.FGISystem.gdims.dao.MacroDao;


/**
 * 宏观观测点列表
 * @author liqingsong 
 * @version 0.1
 *
 */
public class MacroMenuActivity extends BaseActivity {
	
	private SimpleAdapter adapter ;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.macromenu);
		TextView title = (TextView)findViewById(R.id.title);
		title.setText("宏观观测点");
		
		ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar1);
		pb.setVisibility(View.GONE);
		
		ImageView iv = (ImageView)findViewById(R.id.star);
		iv.setVisibility(View.GONE);
		
		listView = (ListView)findViewById(R.id.macromenu);
		dataLoader.execute();
		
	}
	
	private AsyncTask dataLoader = new AsyncTask(){
		
		private static final String MSG = "正在加载数据...";
		private MacroDao dao;
		private ProgressDialog pd;

		@Override
		protected Object doInBackground(Object... params) {
			List<Map<String, String>> list = dao.queryForMacro(null);
			return list;
		}
		
		protected void onPostExecute(Object result) {
			
			pd.dismiss();
			dao.close();
			
			if(result!= null){
				List<Map<String, String>> data = (List<Map<String, String>>)result;
				adapter = new SimpleAdapter(MacroMenuActivity.this, data, R.layout.macromenu_adaper, new String[]{"disName"}, new int[]{R.id.macro_name});
				listView.setAdapter(adapter);
				
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						Map<String, String> item = (Map)parent.getItemAtPosition(position);
						Bundle bundel = new Bundle();
						bundel.putCharSequence("disNo", item.get("disNo"));
						bundel.putCharSequence("disName", item.get("disName"));
						bundel.putCharSequence("content", item.get("content"));
						bundel.putCharSequence("legalR",  item.get("legalR"));
						bundel.putCharSequence("longitude", item.get("longitude"));
						bundel.putCharSequence("latitude", item.get("latitude"));
						forward(MacroActivity.class,bundel);
					}
				});
			}else{
				showToast("没有找到的配置宏观观测点");
			}
		};
		
		protected void onPreExecute() {
			pd = ProgressDialog.show(MacroMenuActivity.this, null, MSG);
			dao = new MacroDao(MacroMenuActivity.this);
			
		};
		
	};

}
