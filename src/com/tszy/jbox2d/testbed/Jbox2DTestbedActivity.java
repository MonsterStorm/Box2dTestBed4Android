package com.tszy.jbox2d.testbed;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tszy.jbox2d.testbed.framework.TestbedView;
import com.tszy.jbox2d.testbed.tests.Domino;
import com.tszy.jbox2d.testbed.tests.SliderCrankTest;
import com.tszy.jbox2d.testbed.tests.VaryingFriction;
import com.tszy.jbox2d.testbed.tests.VaryingRestitution;

public class Jbox2DTestbedActivity extends Activity {
    /** Called when the activity is first created. */
	private ListView listView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //setContentView(new SliderCrankTest(this));
      
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(new MyAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				TestActivity.show(Jbox2DTestbedActivity.this, 
						(TestbedView)listView.getAdapter().getItem(position));
			}
		});
    }
    
    private class MyAdapter extends BaseAdapter {
    	private ArrayList<TestbedView> testList;
    	private LayoutInflater mInflater; // 布局文件解析器
    	
    	public MyAdapter(Context context) {
			// TODO Auto-generated constructor stub
    		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		
    		testList = new ArrayList<TestbedView>();
    		addTests(context);
		}
    	
    	private void addTests(Context context){
    		testList.add(new VaryingRestitution(context));
    		testList.add(new VaryingFriction(context));
    		testList.add(new Domino(context));
    		testList.add(new SliderCrankTest(context));
    	}
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return testList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return testList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, null);
			}
			
			//设置显示文本
			((TextView)convertView).setText(testList.get(position).getName());
			
			return convertView;
		}
    }  
}