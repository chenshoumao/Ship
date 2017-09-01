package com.solar.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.derby.client.am.PreparedStatement; 
import com.solar.dao.impl.ShipDaoImpl;
import com.solar.utils.ConnectUtil;
import com.solar.utils.PostMethod;
import com.solar.utils.ResourceBundleUtil;

/**
 * Servlet implementation class UpdateShipLogs
 */
@WebServlet("/UpdateShipLogs")
public class UpdateShipLogs extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateShipLogs() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() {
    	UpdateLogs();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
    public static void main(String[] args) {
    	 UpdateLogs();
	}
    public static void UpdateLogs(){
		// TODO Auto-generated method stub
	 
		
		ResourceBundleUtil bundleUtil = new ResourceBundleUtil();
		String data ="app,haitu,ditu,db";
		String[] dataList = data.split(","); 
		ShipDaoImpl dao = new ShipDaoImpl();
		Map<String,Object> localVersionMap = dao.getShipVersion(dataList);
		ConnectUtil connectUtil = new ConnectUtil();
		Connection conn = connectUtil.getConn();
		//向岸端 更新记录表
		try {
			int count = 0;
			for (String key : dataList) {
				String keyInfo = bundleUtil.getInfo("config/module", key);
				String sql = "update ship_update_logs set new_version = ? , update_time = ? , is_over= 1, update_state = '更新完毕' where module =? and original_version != ? and is_over = 0";
				
				PreparedStatement ps; 
				ps = (PreparedStatement) conn.prepareStatement(sql);
				
				String version = (String) localVersionMap.get(key);
				 
				ps.setString(1, version);
				SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				ps.setString(2, simpleFormat.format(date)); 
				ps.setString(3, keyInfo);
				ps.setString(4, version);
				ps.execute();
				ps.close();
			}

		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//更新版本表
		for(String key : dataList){
			String keyInfo = bundleUtil.getInfo("config/module", key);
			String version = (String) localVersionMap.get(key);
			
			
			try {
				PreparedStatement ps = null;
				ResultSet rs = null;
				String sql = "select * from ship_version where module = ?";
				ps = (PreparedStatement) conn.prepareStatement(sql);
				ps.setString(1, keyInfo);
				rs = ps.executeQuery();
				if(rs.next()){
					sql = "update ship_version set version = ? , update_time = ? where module = ? and version != ?"; 
					SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date(); 
					ps = (PreparedStatement) conn.prepareStatement(sql);
					ps.setString(1, version);
					ps.setString(2, simpleFormat.format(date)); 
					ps.setString(3, keyInfo);
					ps.setString(4, version);
					ps.executeUpdate();
				}
				else{
					sql = "insert into ship_version(module,version,update_time) values(?,?,?)";
					SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date(); 
					ps = (PreparedStatement) conn.prepareStatement(sql);
					ps.setString(1, keyInfo);
					ps.setString(2, version);
					ps.setString(3, simpleFormat.format(date)); 
					ps.execute();
				}
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//通知岸端 增量包已经更新到本地
		//
		try {
			Map<String,Object> params = dao.getShipVersion(dataList);
			String ip = InetAddress.getLocalHost().getHostAddress(); 
			params.put("ip",ip);
			String url = bundleUtil.getInfo("config/ship", "updateUrl"); 
			//先判断url是否可以连接
			URL contentUrl = null;
			try {
				contentUrl = new URL(url);
				URLConnection urlConn = contentUrl.openConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						urlConn.getInputStream())); // 实例化输入流，并获取网页代码
				String s;
				if((s = reader.readLine()) != null) {
					PostMethod.httpClientPost(url, params, "utf-8");
				}
			} catch (Exception e) {
				//e.printStackTrace();
				
			} 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		connectUtil.closeConn(conn);		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String info = request.getParameter("info"); 
			 
			try { 
				int count = 0; 
					String sql = "update ship_update_logs set  update_time = ? , update_state = ? , is_over =1 where is_over = 0";
					ConnectUtil connectUtil = new ConnectUtil();
					Connection conn = connectUtil.getConn();
					PreparedStatement ps; 
					ps = (PreparedStatement) conn.prepareStatement(sql);
					
					SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date();
					ps.setString(1, simpleFormat.format(date));  
					ps.setString(2, info);  
					boolean state = ps.execute();
					ps.close();
					connectUtil.closeConn(conn); 
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				 
			}
		 
		
	}

}
