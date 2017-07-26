package com.solar.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectUtil { 
	
	public Connection getConn(){ 
		
		ResourceBundleUtil bundleUtil = new ResourceBundleUtil();
		String ip = bundleUtil.getInfo("config/db", "ip");
		String port = bundleUtil.getInfo("config/db", "port");
		String databases = bundleUtil.getInfo("config/db", "databases");
		String username = bundleUtil.getInfo("config/db", "username");
		String password = bundleUtil.getInfo("config/db", "password");
		// 驱动程序名
		String driver = "com.mysql.jdbc.Driver"; 
		// URL指向要访问的数据库名scutcs
		String url = "jdbc:mysql://"+ip+":"+port+"/"+databases+"?useUnicode=true&characterEncoding=UTF-8"; 
		 
		try {
			// 加载驱动程序
			Class.forName(driver); 
			// 连续数据库
			Connection conn = DriverManager.getConnection(url, username, password); 
			if (!conn.isClosed())
				System.out.println("Succeeded connecting to the Database!"); 
			// statement用来执行SQL语句

			return conn; 
		} catch (ClassNotFoundException e) {
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void closeConn(Connection conn){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean excuteSQL(Connection conn,String sql){
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
