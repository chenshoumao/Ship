package com.solar.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 

public class ConnectUtil {

	private static String driver = "";
	private static String protocol = "";
	// String dbName = "jdbc://192.168.3.45:1527//Users/Administrator/dedb";
	
	String dbName = ""; 
	String username = "";
	String password = "";
	public Connection getConn() {
		try {
			ResourceBundleUtil bundleUtil = new ResourceBundleUtil();
			driver = bundleUtil.getInfo("config/db", "driver");
			username = bundleUtil.getInfo("config/db", "username");
			password = bundleUtil.getInfo("config/db", "password");
			dbName = bundleUtil.getInfo("config/db", "dbName");
			Class.forName(driver).newInstance();
			protocol = bundleUtil.getInfo("config/db", "protocol");
			Connection conn = DriverManager.getConnection(protocol +  dbName + ";user="+username+";password="+password+";create=true");
			 
			return conn;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		ConnectUtil connectUtil = new ConnectUtil();
		try {
			Connection conn = connectUtil.getConn();
			String sql = "select count(*) from shipline";
		    PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				System.out.println(rs.getInt(1));
			System.out.println("连接成功");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
	}
	public void closeConn(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean excuteSQL(Connection conn, String sql) {
		boolean result = false;
		try {
			Statement statement = conn.createStatement();
			// 要执行的SQL语句
			result = statement.execute(sql);
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
		}
		return result;
	}
}
