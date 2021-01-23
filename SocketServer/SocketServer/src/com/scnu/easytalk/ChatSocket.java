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
	//����һ��Socket����������SocketListener������Socket����
	Socket socket;
	String userID=null;
	public ChatSocket(Socket s) {
		this.socket=s;
	}
	public void out(String out){
		try {
			socket.getOutputStream().write((out+"\n").getBytes("UTF-8"));//�������Է������˵�����
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
							socket.getInputStream(),"UTF-8"));//��ǰ�������᲻�϶�ȡ��ǰ�ͻ��˵�����
			String line=null;
			String[] subStr=br.readLine().split("&");
			this.userID=subStr[0];
			System.out.println("�յ������û���Ϣ:"+subStr[0]+" "+subStr[1]+" "+subStr[2]);
			if(subStr[0]==null){
				return;
			}
			ServiceChatManager.getchaChatManager().add(this,subStr[0],subStr[1],subStr[2]);
			while ((line=br.readLine())!=null) {//�ͻ��˷��͸�������������
				final String str=line;
				new Thread(){
					public void run(){
						
						//Ȼ��������ٽ����е���Ϣת����ÿһ���ͻ��ˣ�����publish����
						ServiceChatManager.getchaChatManager().publish(str);
					}
				}.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			System.out.println("�Ͽ���һ���ͻ�������");
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
