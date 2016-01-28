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

	//��������
	int mGalleryItemBackgroud;
	private Context mContext;
	private List<String> lis;
	
	//ImageAdapter�Ĺ�����
	public ImageAdapter(Context c, List<String> li){
		mContext=c;
		lis=li;
		
		/*//ʹ����res/values/attrs.xml�е�<declare-styleable>�����Gallery����
		TypedArray a=ImageAdapter.this.obtainStyledAttributes(R.styleable.Gallery);
		//ȡ��Gallery���Ե�Index id
		mGalleryItemBackgroud=a.getResourceId(R.styleable.Gallery_android_galleryItemBackgroud,0);
		//�ö����styleable�����ܹ�����ʹ��
		a.recycle();*/
	}
	//��д�ķ���getCount������ͼƬ��Ŀ
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lis.size();
	}
	//��д�ķ���getItem������position
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	//��д�ķ���getItemId������position
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	//��д�ķ���getView������һView����
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//����ImageView����
		ImageView i=new ImageView(mContext);
		//����ͼƬ��imageView����
		Bitmap bm=BitmapFactory.decodeFile(lis.get(position).toString());
		i.setImageBitmap(bm);
		//��д����ͼƬ�Ŀ���
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		//��������layout�Ŀ���
		i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//ʹGalleryͼƬ����Ӧ��Ļ�ֱ��ʣ�����ͼƬbound������Ļ��Χ  
		i.setAdjustViewBounds(true); 
		//����Gallery����ͼ
		i.setBackgroundResource(mGalleryItemBackgroud);
		//����imageView����
		return i;
	}

}
