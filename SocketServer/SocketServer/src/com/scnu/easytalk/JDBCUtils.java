package com.scnu.easytalk;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;


public class JDBCUtils {
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static Connection getConnection() throws SQLException{
		
			return (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/area_talk", "root", "123456");
		
	}
}
