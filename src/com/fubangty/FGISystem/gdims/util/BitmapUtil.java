/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author liyun create 2012-6-10
 * @version 0.1
 * 
 */
public class BitmapUtil {

	/**
	 * 
	 * @param context
	 * @param bm
	 * @param picPath
	 * @return
	 */
	public static Bitmap decodeBitmap(Activity context, Bitmap bm, String picPath) {

		BitmapFactory.Options opt = new BitmapFactory.Options();
		// 这个isjustdecodebounds很重要
		opt.inJustDecodeBounds = true;
		bm = BitmapFactory.decodeFile(picPath, opt);

		// 获取到这个图片的原始宽度和高度
		int picWidth = opt.outWidth;
		int picHeight = opt.outHeight;

		// 获取屏的宽度和高度
		WindowManager windowManager = context.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();

		// isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
		opt.inSampleSize = 1;
		// 根据屏的大小和图片大小计算出缩放比例
		if (picWidth > picHeight) {
			if (picWidth > screenWidth) {
				opt.inSampleSize = picWidth / screenWidth;
			}
		} else {
			if (picHeight > screenHeight) {
				opt.inSampleSize = picHeight / screenHeight;
			}
		}

		// 这次再真正地生成一个有像素的，经过缩放了的bitmap
		opt.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(picPath, opt);

		return bm;
	}
	
	/**
	 * 将指定路径的图片直接按比例缩放
	 * @param inSampleSize
	 * @param picPath
	 * @return
	 */
	public static Bitmap decodeBitmap(int inSampleSize, String picPath) {

		BitmapFactory.Options opt = new BitmapFactory.Options();
		// 这个isjustdecodebounds很重要
		opt.inJustDecodeBounds = true;
		
		// isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
		opt.inSampleSize = inSampleSize;
		// 根据屏的大小和图片大小计算出缩放比例

		// 这次再真正地生成一个有像素的，经过缩放了的bitmap
		opt.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(picPath, opt);

		return bm;
	}

	/**
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

		// load the origial Bitmap
		Bitmap bitmapOrg = bitmap;

		int width = bitmapOrg.getWidth();//获取原始图片宽度
		int height = bitmapOrg.getHeight();//获取原始图片高度
		int newWidth = w;//设置缩放图片宽度
		int newHeight = h;//设置缩放图片高度

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		//return new BitmapDrawable(resizedBitmap);
		return resizedBitmap;
	}
	
	/**
	 * 将bitmap 输出到sdcard上
	 * @param bitmap
	 * @param filePath 输出的目录位置和名称形如/sdcard/feng.png
	 */
	public static void compress(Bitmap bitmap,String path,String fileName){
		
		//filePath = "/sdcard/feng.png";
		
		
		File filePath=new File(path);
		if(!filePath.exists()){
			filePath.mkdir();
		}
		
		File file=new File(path,fileName);
		
	    try {
	        FileOutputStream out=new FileOutputStream(file);
	        if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){
	            out.flush();
	            out.close();
	        }

	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 
	 * @param fromFile
	 * @param toFile
	 * @param width
	 * @param height
	 * @param quality 图片品质 1-100
	 */
	public static void transImage(String fromFile, String toFile, int width,int height, int quality) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			// 缩放图片的尺寸
			float scaleWidth = (float) width / bitmapWidth;
			float scaleHeight = (float) height / bitmapHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 产生缩放后的Bitmap对象
			Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmapWidth, bitmapHeight, matrix, false);
			// save file
			File myCaptureFile = new File(toFile);
			FileOutputStream out = new FileOutputStream(myCaptureFile);
			if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
				out.flush();
				out.close();
			}
			if (!bitmap.isRecycled()) {
				bitmap.recycle();// 记得释放资源，否则会内存溢出
			}
			if (!resizeBitmap.isRecycled()) {
				resizeBitmap.recycle();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


}
