package com.example.flight_control_002;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

public class ClientThread implements Runnable{

	private Socket s;
	private Handler handler;
	public DataInputStream din = null;
	public ClientThread(Socket s, Handler handler) throws IOException{
		this.s = s;
		this.handler = handler;
		din = new DataInputStream(s.getInputStream());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String content=null;
		try {
			while((content=din.readUTF())!=null){
				Message msg=new Message();
				msg.what=0x123;
				msg.obj=content;
				handler.sendMessage(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
