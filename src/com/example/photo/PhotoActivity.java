package com.example.photo;

import java.io.File;
import java.lang.reflect.Field;  
import java.util.ArrayList;  
import java.util.List;

import com.example.flight_control_001.R;
  
import android.app.Activity;  
import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.os.Bundle;  
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;
  
public class PhotoActivity extends Activity { 
	private static final String PIC_PATH = "/mnt/sdcard/Flight photo/";
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_photo);  
        Gallery g=(Gallery)findViewById(R.id.gallery);
        
        //添加一个ImageAdapter并设置给Gallery对象
        g.setAdapter( new ImageAdapter(this,getSD()));
        
        //设置一个itemclickListener事件
        g.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
		});
    }

	private List<String> getSD() {
		// TODO Auto-generated method stub
		//设置目前所在路径
		List<String> it=new ArrayList<String>();
		File f=new File(PIC_PATH);
		File[] files=f.listFiles();
		
		//将所有文件添加到ArrayList中
		for(int i=0;i<files.length;i++){
			File file=files[i];
			if(getImageFile(file.getPath()))
				it.add(file.getPath());
		}
		return it;
	}

	private boolean getImageFile(String fName) {
		// TODO Auto-generated method stub
		boolean re;		
		//取得扩展名
		String end=fName.substring(fName.lastIndexOf(".")+1, fName.length()).toLowerCase();
		//根据扩展名的类型决定MimeType
		if(end.equals("jpeg")){
			re=true;
		}else {
			re=false;
		}
		return re;			
	}  
      
    
}  
