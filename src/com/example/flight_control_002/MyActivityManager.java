package com.example.flight_control_002;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * Activity������ 1��activity():����Activity 2��exit():�ر����б����Activity
 * 
 * @author �κ��
 * 
 */
public class MyActivityManager extends Application {
	/**
	 * Activity�б�
	 */
	private List<Activity> activityList = new LinkedList<Activity>();

	/**
	 * ȫ��Ψһʵ��
	 */
	private static MyActivityManager instance;

	/**
	 * ������õ���ģʽ������ʵ����
	 */
	private MyActivityManager() {
	}

	/**
	 * ��ȡ��ʵ������
	 * 
	 * @return MyActivityManager
	 */
	public static MyActivityManager getInstance() {
		if (null == instance) {
			instance = new MyActivityManager();
		}
		return instance;
	}

	/**
	 * ����Activity�������б���
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * �رձ����Activity
	 */
	public void exit() {
		if (activityList != null) {
			Activity activity;

			for (int i = 0; i < activityList.size(); i++) {
				activity = activityList.get(i);

				if (activity != null) {
					if (!activity.isFinishing()) {
						activity.finish();
					}

					activity = null;
				}

				activityList.remove(i);
				i -= 1;
			}
		}
	}
}
