package com.example.photo;

import java.util.List;

import com.example.flight_control_001.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	//声明变量
	int mGalleryItemBackgroud;
	private Context mContext;
	private List<String> lis;
	
	//ImageAdapter的构造器
	public ImageAdapter(Context c, List<String> li){
		mContext=c;
		lis=li;
		
		/*//使用在res/values/attrs.xml中的<declare-styleable>定义的Gallery属性
		TypedArray a=ImageAdapter.this.obtainStyledAttributes(R.styleable.Gallery);
		//取得Gallery属性的Index id
		mGalleryItemBackgroud=a.getResourceId(R.styleable.Gallery_android_galleryItemBackgroud,0);
		//让对象的styleable属性能够反复使用
		a.recycle();*/
	}
	//重写的方法getCount，返回图片数目
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lis.size();
	}
	//重写的方法getItem，返回position
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	//重写的方法getItemId，返回position
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	//重写的方法getView，返回一View对象
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//产生ImageView对象
		ImageView i=new ImageView(mContext);
		//设置图片给imageView对象
		Bitmap bm=BitmapFactory.decodeFile(lis.get(position).toString());
		i.setImageBitmap(bm);
		//重写设置图片的宽、高
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		//重新设置layout的宽、高
		i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//使Gallery图片自适应屏幕分辨率，以免图片bound超出屏幕范围  
		i.setAdjustViewBounds(true); 
		//设置Gallery背景图
		i.setBackgroundResource(mGalleryItemBackgroud);
		//返回imageView对象
		return i;
	}

}
