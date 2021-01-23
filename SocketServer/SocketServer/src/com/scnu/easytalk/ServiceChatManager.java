package com.scnu.easytalk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

/**
 * 频道转发处理类
 * @author hp
 *
 */
public class ServiceChatManager {

	private ServiceChatManager(){}
	private static final ServiceChatManager cm= new ServiceChatManager();
	private  Map<String,ChatSocket> userIDMap=new HashMap<String,ChatSocket>();
	private  Map<String,String> userLocMap=new HashMap<String,String>();
	private  Map<String,String> userRangeMap=new HashMap<String,String>();

	public static  ServiceChatManager getchaChatManager(){
		return cm;
	}

	/**
	 * 将刚登陆的用户统一管理
	 * @param chatSocket
	 * @param string
	 */
	public void add(ChatSocket chatSocket, String userID,String userLoc,String range) {
		userIDMap.put(userID, chatSocket);
		userLocMap.put(userID, userLoc);
		userRangeMap.put(userID,range);
	}

	/**
	 * 将消息进行处理后分配
	 * @param cs
	 * @param chatinfo
	 */
	public  void publish(String chatinfo){
		String[] content=chatinfo.split("&");
		if(content[1]==null){
			return;
		}
		userLocMap.put(content[1], content[2]);
		userRangeMap.put(content[1], content[6]);
		if("000".equals(content[0])){
			Set<String> set=userLocMap.keySet();
			for(String str:set){
				if(!content[1].equals(str)&&isLocation(content[2],userLocMap.get(str),userRangeMap.get(str))){
					if(userIDMap.get(str)!=null){
						userIDMap.get(str).out(chatinfo);
					}
				}
			}
			return;
		}
		UserDao userDao=new UserDao();
		List<String> UserIDList=userDao.getUserID(content[0]);
		userDao.close();
		for(int i=0;i<UserIDList.size();i++){
			String str=UserIDList.get(i);
			if(!content[1].equals(str)&&userIDMap.get(str)!=null){
				userIDMap.get(str).out(chatinfo);
			}
		}

	}

	/**
	 * 是否满足距离要求
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public boolean isLocation(String loc1,String loc2,String length){
		String[] str1=loc1.split(",");
		String[] str2=loc2.split(",");
		GlobalCoordinates source = new GlobalCoordinates(Double.valueOf(str1[0]), Double.valueOf(str1[1]));
		GlobalCoordinates target = new GlobalCoordinates(Double.valueOf(str2[0]), Double.valueOf(str2[1]));
		int meter1 = (int)getDistanceMeter(source, target, Ellipsoid.Sphere);
//		System.out.println(meter1);
		if(meter1<=Integer.valueOf(length)){
			return true;
		}
		return false;
	}
	//	@Test
	//	public void test(){
	//		System.out.println(isLocation("29.615467, 106.581515","29.590295, 106.586654"));
	//	}

	/**
	 * 计算经纬度距离
	 * @param gpsFrom
	 * @param gpsTo
	 * @param ellipsoid
	 * @return
	 */
	public static double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid){

		//创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
		GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);

		return geoCurve.getEllipsoidalDistance();
	}

	/**
	 * 移除用户
	 * @param cs
	 */
	public void remove(String userID) {
		userIDMap.remove(userID);
		userLocMap.remove(userID);
	}
}
