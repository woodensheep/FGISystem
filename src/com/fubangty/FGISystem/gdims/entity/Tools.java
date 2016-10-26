package com.fubangty.FGISystem.gdims.entity;
/**
 * 
 * 按钮时间控制
 *
 * @author 11540
 *
 */
public class Tools {
  private static long lastClickTime;
  public static  boolean  isFastDoubleClick(){
	  long time =System.currentTimeMillis();
	  if(time - lastClickTime <10000){
		  return true;
	  }
	  lastClickTime=time;
	  
	  return false;
  }
}
