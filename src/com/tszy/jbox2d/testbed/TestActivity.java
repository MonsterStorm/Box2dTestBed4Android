package com.tszy.jbox2d.testbed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.tszy.jbox2d.testbed.framework.TestbedView;

public class TestActivity extends Activity {
	private static Intent intent;
	private static TestbedView view;
	
	public static void show(Context context, TestbedView v){
		if(null == intent)
			intent = new Intent();
		
		intent.setClass(context, TestActivity.class);
		context.startActivity(intent);
		view = v;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		
		if(view != null)
			setContentView(view);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return true;
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		
		//setContentView(null);
		//this.getWindowManager().removeView(view);
		view = null;
	}
}
