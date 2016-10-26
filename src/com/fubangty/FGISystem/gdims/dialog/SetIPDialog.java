package com.fubangty.FGISystem.gdims.dialog;


import com.fubangty.FGISystem.R;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

public class SetIPDialog extends Dialog {

	private Context context;
	public EditText IP_edit, Port_edit,Mobile_edit;
	private Button Sure_btn,Clean_btn;
	private android.view.View.OnClickListener onClickListener;
	public SetIPDialog(Context context,
			android.view.View.OnClickListener onClickListener) { 
		super(context, R.style.transparent_dialog);
		this.context = context;
		this.onClickListener = onClickListener;
		setContentView(R.layout.setting);
		
		init();
	}
	private void init() {
		IP_edit = (EditText) findViewById(R.id.ip);
		Port_edit = (EditText) findViewById(R.id.port);
		Mobile_edit = (EditText) findViewById(R.id.mobile);
		Sure_btn =(Button) findViewById(R.id.sure_btns);
		Clean_btn =(Button)findViewById(R.id.clean_btns);
		
		Sure_btn.setOnClickListener(onClickListener);
		Clean_btn.setOnClickListener(onClickListener);
	}

}
