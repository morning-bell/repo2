package com.scnu.easytalk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ChatSocket extends Thread {
	//创建一个Socket对象来接收SocketListener传来的Socket对象
	Socket socket;
	String userID=null;
	public ChatSocket(Socket s) {
		this.socket=s;
	}
	public void out(String out){
		try {
			socket.getOutputStream().write((out+"\n").getBytes("UTF-8"));//接收来自服务器端的数据
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {

		BufferedReader br = null;
		try {
			br=new BufferedReader(
					new InputStreamReader(
							socket.getInputStream(),"UTF-8"));//当前服务器会不断读取当前客户端的数据
			String line=null;
			String[] subStr=br.readLine().split("&");
			this.userID=subStr[0];
			System.out.println("收到连接用户信息:"+subStr[0]+" "+subStr[1]+" "+subStr[2]);
			if(subStr[0]==null){
				return;
			}
			ServiceChatManager.getchaChatManager().add(this,subStr[0],subStr[1],subStr[2]);
			while ((line=br.readLine())!=null) {//客户端发送给服务器的数据
				final String str=line;
				new Thread(){
					public void run(){
						
						//然后服务器再将所有的信息转发给每一个客户端，调用publish方法
						ServiceChatManager.getchaChatManager().publish(str);
					}
				}.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			System.out.println("断开了一个客户端链接");
			ServiceChatManager.getchaChatManager().remove(this.userID);
			try {
				if(socket!=null){
					socket.close();
					socket=null;
				}
				if(br!=null){
					br.close();
					br=null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
