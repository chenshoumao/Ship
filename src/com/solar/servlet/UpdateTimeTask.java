package com.solar.servlet;

 
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask; 
import org.apache.log4j.Logger;
 

 

/**
 * @author Administrator
 * 系统定时任务
 */
public class UpdateTimeTask extends TimerTask {

	private static Logger log = Logger.getLogger(UpdateTimeTask.class);

	// 定时任务类
	private static Timer timer;
	
	public static void main(String[] args) {
		UpdateTimeTask task2 = new UpdateTimeTask();
		task2.executeTimeTask();
	}

	public  void executeTimeTask() {
		
		System.out.println("运行定时！");
		
		// 加载配置文件
		ResourceBundle ship = ResourceBundle.getBundle("config/ship");
		// 获取时间间隔
		String periodTime = "30000";
		// 获取延迟时间
		String delayTime = "30000";
		ResourceBundle.clearCache();	// 清理内存中的properties键值对
		long period = Long.parseLong(periodTime);
		long delay = Long.parseLong(delayTime);
		timer = new Timer();
		timer.schedule(this, delay, period);//

	}

	/**
	 * 执行定时任务
	 */
	@Override
	public void run() {
		matchHostIp();
	}

	/**
	 * 匹配系统ip与配置文件中ip
	 */
	public void matchHostIp() {
		// 加载配置文件
		ShipUpdate shipUpdate = new ShipUpdate();
		shipUpdate.startUpdate();
	}
 
}
