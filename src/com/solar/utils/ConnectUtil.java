package com.solar.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectUtil {

	private static String driver = "org.apache.derby.jdbc.ClientDriver";
	private static String protocol = "jdbc:derby://";
	// String dbName = "jdbc://192.168.3.45:1527//Users/Administrator/dedb";
	String dbName = "";

	String ip = "";
	String port = "";
	String username = "";
	String password = "";
	public Connection getConn() {
		try {
			ResourceBundleUtil bundleUtil = new ResourceBundleUtil();
			ip = bundleUtil.getInfo("config/db", "ip");
			port =  bundleUtil.getInfo("config/db", "port");
			username = bundleUtil.getInfo("config/db", "username");
			password = bundleUtil.getInfo("config/db", "password");
			dbName = bundleUtil.getInfo("config/db", "dbName");
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(protocol + ip + ":" + port + dbName + ";user="+username+";password="+password+";create=true");
			Statement statement = conn.createStatement();
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
