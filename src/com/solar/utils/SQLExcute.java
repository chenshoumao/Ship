package com.solar.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.io.FileUtils;
 
import org.apache.log4j.Logger;


public class SQLExcute {

	private static Logger logger = Logger.getLogger(SQLExcute.class);
	public static void main(String[] args) {
		updateDB();
	}

	public static void updateDB() {
		try {
			String propertyName = "config/ship";
			String prefix = "web";
			String suffix = File.separator + "db";
			String dbConfigPath = ResourceBundleUtil.getInfo(propertyName, prefix);
			File file = new File(dbConfigPath + suffix);
			File[] dbList = file.listFiles();
			for (File db : dbList) {
				if (db.getName().indexOf("sql") > 0) {
					List<String> list = FileUtils.readLines(db);
					ConnectUtil connectUtil = new ConnectUtil();
					Connection conn = connectUtil.getConn();
					for (String str : list) {
						System.out.println(str);
						connectUtil.excuteSQL(conn, str);
					}
					connectUtil.closeConn(conn);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.debug("  出错： " + e);
		}

	}

	public void updateDB(File sqlFile) {
		// TODO Auto-generated method stub
		try {
			ConnectUtil connectUtil = new ConnectUtil();
			Connection conn = connectUtil.getConn();
			if (sqlFile.getName().equals("update.sql")) {
				List<String> list = FileUtils.readLines(sqlFile);
				logger.debug("数据库文件内容是：" + list);
				
				for (String sql : list) { 
					logger.debug("  " + sql);
//					boolean result = connectUtil.excuteSQL(conn, str);
					try {
						Statement statement = conn.createStatement();
						// 要执行的SQL语句
						statement.execute(sql);
					} catch (Exception e) {
						// TODO: handle exception
						logger.debug("  报错" +  e);
					}
					
				}
				
			}
			connectUtil.closeConn(conn);
		} catch (Exception e) {
			// TODO: handle exception
			logger.debug("  出错: " + e);
		}
	}
	
	public static String getVersionByKey(String key,String tableName){
		String sql = "slect new_version from ? where key = ? order by update_time desc";
		ConnectUtil connectUtil = new ConnectUtil();
		Connection conn = connectUtil.getConn();
		
		try {
			PreparedStatement ps =   conn.prepareStatement(sql);
			ps.setString(1, tableName);
			ps.setString(2, key);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				return rs.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
