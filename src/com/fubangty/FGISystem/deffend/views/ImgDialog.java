package com.fubangty.FGISystem.deffend.views;

import com.fubangty.FGISystem.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImgDialog extends Dialog{

	public ImgDialog(Context context, int theme) {
		super(context, theme);
	}

	public ImgDialog(Context context) {
		super(context);
	}
	public static class Builder{
		 private Context context;
	        private Bitmap image;

	        public Builder(Context context) {
	            this.context = context;
	        }

	        public Bitmap getImage() {
	            return image;
	        }

	        public void setImage(Bitmap image) {
	            this.image = image;
	        }

	        public ImgDialog create() {
	            LayoutInflater inflater = (LayoutInflater) context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            final ImgDialog dialog = new ImgDialog(context,R.style.dialogTheme);
	            View layout = inflater.inflate(R.layout.dialog_image, null);
	            dialog.addContentView(layout, new LayoutParams(
	                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
	                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	            dialog.setContentView(layout);
	            ImageView img = (ImageView)layout.findViewById(R.id.iv_dialog_image);
	            img.setImageBitmap(getImage());
	            return dialog;
	        }
	    }
}
