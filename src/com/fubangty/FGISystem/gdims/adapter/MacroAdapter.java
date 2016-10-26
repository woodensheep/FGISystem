
package com.fubangty.FGISystem.gdims.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.entity.Macro;
import com.fubangty.FGISystem.gdims.util.BitmapUtil;
import com.fubangty.FGISystem.gdims.util.Session;
import com.fubangty.FGISystem.gdims.util.StaticValue;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author liqingsong
 * @version 0.1
 *
 */
public class MacroAdapter extends BaseAdapter {
	
	private List<Macro> macros;
	private Context mContext;
	private static final int IMAGE_REQUEST_CODE = 100;
	private Map<Integer, String> value = new HashMap<Integer, String>();
	
	private Map<Integer, String> files = new HashMap<Integer, String>();

	private Animation animationScale;
	
	public MacroAdapter(Context mContext,List<Macro> macros) {
		this.macros = macros;
		this.mContext = mContext;
	}
	

	@Override
	public int getCount() {
		return macros == null?0 : macros.size();
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return macros.get(position);
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setMacros(List<Macro> macros) {
		this.macros = macros;
	}

	/**
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup group) {
		
		convertView = View.inflate(mContext, R.layout.macrolist, null);
		final TextView text = (TextView)convertView.findViewById(R.id.macro_name);//选项文字
		text.setText(macros.get(position).getMacroName());
		final Button btn = (Button) convertView.findViewById(R.id.camera);//照相按钮
		
		final ImageView iv = (ImageView)convertView.findViewById(R.id.pic_camera);//照相缩略图
		//判断当前项是否已经存在照片,如果是就显示
		String path=files.get(position);
		if(path!=null){
			//按比例缩小10倍
			Bitmap bit = BitmapUtil.decodeBitmap(10,path);
			//再和imageview高宽匹配
			bit = BitmapUtil.resizeImage(bit, 64, 64);
			iv.setImageBitmap(bit);
			notifyDataSetChanged();
			ImageListener(position, iv);
			
		}
		
		
		RadioGroup r = (RadioGroup)convertView.findViewById(R.id.myRadioGroup);//单选按钮组
		final RadioButton radio = (RadioButton) convertView.findViewById(R.id.macro_v1);
		//因为list滚动时候如果某项滚出屏幕后 该选项相当就被销毁,再滚入屏幕也是重新构建,需要用判断以前是否是选中状态
		if(text.getText().toString().equals(value.get(position))){
			
			radio.setChecked(true);
		}
		
		EditText e = (EditText)convertView.findViewById(R.id.macro_else);//其他异常填选框
		//判断如果是最后一行,隐藏单选按钮
		if(position==(macros.size()-1)){
			r.setVisibility(View.GONE);
			e.setVisibility(View.VISIBLE);
			
			if(Session.getEntity("other", false)!=null){
				e.setText(Session.getEntity("other", false).toString());
			}
			
			e.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					EditText et =(EditText)v;
					if(!hasFocus){//焦点离开时
						if((et.getText()!=null)&&(!"".equals(et.getText().toString()))){
							value.put(position, text.getText().toString());
							Session.putObjct("other", et.getText().toString());
						}else{
							value.remove(position);
						}
					}
					
				}
			});
		}
		
		//监听RadioGroup选项子项 改变选中事件,如果"是"选项被选中则需要记录在某个map里面,一是提交数据时方便收集,二是listview滚动时记录选中的位置
		r.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if(checkedId==R.id.macro_v1){
					//如选择是,将数据存入map
					value.put(position, text.getText().toString());
				}else{
					//如选择否,将数据从map去除
					value.remove(position);
					files.remove(position);
					//通知listview 刷新数据
					notifyDataSetChanged();
				}
				
			}
		});
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击照相按钮时，自动选择为是
				radio.setChecked(true);
				value.put(position, text.getText().toString());
				//添加放大动画
				btn.startAnimation(setAnimScale(1.5f, 1.5f));
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Activity ac = (Activity)mContext ;

				String path = StaticValue.MACRO_PIC_PATH;// 存放照片的文件夹
				String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";// 照片命名
				File out = new File(path);
				if (!out.exists()) {
					out.mkdirs();
				}
				out = new File(path, fileName);
				path = path + fileName;// 该照片的绝对路径
				Uri uri = Uri.fromFile(out);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				Session.putObjct("path", path);
				Session.putObjct("img",iv);
				Session.putObjct("id", position);
				ac.startActivityForResult(intent, IMAGE_REQUEST_CODE);
			}
		});
		
		
		return convertView;
	}


	private void ImageListener(final int position, final ImageView iv) {
		iv.setOnClickListener(new OnClickListener() {
			   
			   @Override
			   public void onClick(View arg0) {
			    // TODO Auto-generated method stub
				   AlertDialog.Builder builder=new AlertDialog.Builder(mContext);  //先得到构造器  
			        builder.setTitle("警告"); //设置标题  
			        builder.setMessage("是否删除照片?"); //设置内容  
			        builder.setIcon(R.drawable.ic_launcher);//设置图标，图片id即可  
			        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮  
			            @Override  
			            public void onClick(DialogInterface dialog, int which) {  
			            	value.remove(position);
							files.remove(position);
							//通知listview 刷新数据
							notifyDataSetChanged();
			            	dialog.dismiss(); //关闭dialog  
			               
			            }  
			        });  
			        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮  
			            @Override  
			            public void onClick(DialogInterface dialog, int which) {  
			                dialog.dismiss();  
			                
			            }  
			        });  
			  
			        builder.setNeutralButton("忽略", new DialogInterface.OnClickListener() {//设置忽略按钮  
			            @Override  
			            public void onClick(DialogInterface dialog, int which) {  
			                dialog.dismiss();  
			              
			            }  
			        });  
			        //参数都设置完成了，创建并显示出来  
			        builder.create().show();  
		
			   }
			  });
	}
	
	/**
	 * 放大缩小动画效果
	 * @param toX
	 * @param toY
	 * @return
	 */
	protected Animation setAnimScale(float toX, float toY) {
		// TODO Auto-generated method stub
		animationScale = new ScaleAnimation(1f, toX, 1f, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.45f);
		animationScale.setInterpolator(mContext, anim.accelerate_decelerate_interpolator);
		animationScale.setDuration(500);
		animationScale.setFillAfter(false);
		return animationScale;
		
	}

	public Map<Integer, String> getValue() {
		return value;
	}
	
	public Map<Integer, String> getFiles() {
		return files;
	}
	
	public void setFiles(Map<Integer, String> files) {
		this.files = files;
	}
	
	public void setValue(Map<Integer, String> value) {
		this.value = value;
	}
}
