package com.scnu.easytalk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * 与数据库的交互
 * @author Jiang ZengShun
 *
 */
public class UserDao {
	private static Connection conn=null;
	private static PreparedStatement pstmt=null;
	private static ResultSet rs=null;

	/**
	 * 通过频道ID获取用户ID
	 * @param string
	 * @return
	 */
	public  List<String> getUserID(String string) {
		List<String> list=new ArrayList<String>();
		try {
			Connection connection=JDBCUtils.getConnection();
			pstmt=(PreparedStatement) connection.prepareStatement("select * from userpd where pd=?");
			pstmt.setString(1, string);
			rs=pstmt.executeQuery();
			while(rs.next()){
				list.add(rs.getString("userid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public  void close(){
		if(rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				rs=null;
			}
		}

		if(pstmt!=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				pstmt=null;
			}
		}

		if (conn!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				conn=null;
			}

		}

	}
//	@Test
//	public void test(){
//		List<String> list=getUserID("456");
//		for(int i=0;i<list.size();i++){
//			System.out.println(list.get(i));
//		}
//		close();
//	}

}
