package com.solar.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.dao.ShipDao;
import com.solar.dao.impl.ShipDaoImpl;
import com.solar.utils.MyException;
import com.solar.utils.PostMethod;
import com.solar.utils.ResourceBundleUtil;
/**
 * Servlet implementation class ShipServlet
 */
@WebServlet("/ShipServlet")
public class ShipServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(ShipServlet.class);
	private static final long serialVersionUID = 1L;
	private ShipDaoImpl dao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ShipServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		try {
			String ip =  InetAddress.getLocalHost().getHostAddress();
			System.out.println(ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		 
		try {
			logger.debug("船端第一步：");
			String data = request.getParameter("data");
			if(data.equals("") || data.equals(null)){
				throw new MyException("	船端请求更新的关键数据为空！");
			}
			data += "db";
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

			// 讲请求的详细请求信息存进数据库
			dao = new ShipDaoImpl();
			dao.writeUpdateLogs(data, json);
			
			String ip =  InetAddress.getLocalHost().getHostAddress();
			logger.debug("	船端ip 是 ：" + ip); 
			logger.debug("	将信息发送到岸端"); 
			
			
			//讲打包好的信息发送到岸段的服务器中进行处理
			ResourceBundleUtil bundleUtil = new ResourceBundleUtil();
			String url = bundleUtil.getInfo("config/ship", "landUrl");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ship",json);
			params.put("ip",ip);
			PostMethod.httpClientPost(url, params, "utf-8");
		//	response.setCharacterEncoding("utf-8");
		//	PrintWriter out = response.getWriter();
		//	out.print(json);
			//request.getRequestDispatcher("http://192.168.3.45:8080/Land/LandListener?ship=" + json).forward(request, response);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void sayHello(String name) {
		System.out.println(name);
	}

	public void sayHello(String name, String sex) {
		System.out.println(name + "," + sex);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
