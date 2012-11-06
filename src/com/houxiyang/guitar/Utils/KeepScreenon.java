package com.houxiyang.guitar.Utils;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class KeepScreenon {
	
	 private static WakeLock wl;  
     
	    /** 
	     * 保持屏幕唤醒状态（即背景灯不熄灭） 
	     * @param on 是否唤醒 
	     */  
	    @SuppressWarnings("deprecation")
		public static void keepScreenOn(Context context, boolean on) {  
	        if (on) {  
	            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
	            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");  
	            wl.acquire();  
	        }else {  
	            wl.release();  
	            wl = null;  
	        }  
	    }  
}
