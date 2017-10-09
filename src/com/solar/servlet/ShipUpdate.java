package com.solar.servlet;

 
import java.net.InetAddress; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.dao.ShipDao;
import com.solar.dao.impl.ShipDaoImpl;
import com.solar.utils.FTPTransterUtil;
import com.solar.utils.MyException;
import com.solar.utils.PostMethod;
import com.solar.utils.ResourceBundleUtil;
/**
 * Servlet implementation class ShipServlet
 */
 
public class ShipUpdate{
	private static Logger logger = Logger.getLogger(ShipUpdate.class);
 
	private ShipDaoImpl dao; 
	
	public static void main(String[] args) {
		ShipUpdate update = new ShipUpdate();
		update.startUpdate();
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void startUpdate(){
		// TODO Auto-generated method stub
	 
		try {
			logger.debug("船端第一步：");
			String data = "app,haitu,db"; 
			logger.debug("	获取了 请求更新，请求的数据是 : " + data);
			// 第一步 罗列出所有的组件版本
			String shipKey = "app,haitu,ditu,db";
			Map<String, Object> allShipVersion = this.getLocalVersion(shipKey);
			System.out.println(12300);
			// 第二步 在集合中，将请求的组件包含在内
			allShipVersion.put("toUpdate", data);

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(allShipVersion);

			logger.debug("	解析成完整的json格式数据: " + json);

			// 将请求的详细请求信息存进数据库
			dao = new ShipDaoImpl();
			dao.writeUpdateLogs(data, json);
			
			String ip =  InetAddress.getLocalHost().getHostAddress();
			logger.debug("	船端ip 是 ：" + ip); 
			logger.debug("	将信息发送到岸端"); 
			
			
			//讲打包好的信息发送到岸段的服务器中进行处理
			ResourceBundleUtil bundleUtil = new ResourceBundleUtil();
			String path = System.getProperty("catalina.home");
			String url = bundleUtil.getInfo("config/ship", "landUrl");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ship",json);
			params.put("ip",ip);
			String result = PostMethod.httpClientPost(url, params, "utf-8");
			
			System.out.println(1111);
			List<Map<String, Object>> list = mapper.readValue(result, ArrayList.class);
			
			if(list.size() > 0){
				Map<String, Object> map = list.get(0);
				String zipName = (String) map.get("zipName");
				System.out.println(" 接收到的压缩包名字是 ： " + zipName);
				FTPTransterUtil ftpUtil = new FTPTransterUtil();
				ftpUtil.downloadFile(zipName);
			}
			 
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("state", true);
			map.put("list", mapper.readValue(result, ArrayList.class));
			 
			
			 
		} catch (Exception e) {
			// TODO: handle exception
			logger.debug(e); 
			logger.debug("无法连接到岸端,请稍后再试"); 
		}

	}

	 
	/**
	 * @author 陈守貌
	 * @Time 2017-07-14
	 * @Funtion 船端更新版本
	 * 
	 */
	public Map<String, Object> getLocalVersion(String part) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取想要更新的组件的信息
		String[] updatePart = part.split(",");
		// 获取本地对应的组件的版本信息
		ShipDao dao = new ShipDaoImpl();
		map = dao.getShipVersion(updatePart);
		return map;
	}

	 

}
