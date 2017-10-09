package com.solar.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.log4j.Logger;


public class UnzipUtil {
	private static Logger logger = Logger.getLogger(UnzipUtil.class);
	/**
	 * @author 陈守貌
	 * @Time 2017-07-14
	 * @Funtion 解压的代码
	 * @param sourcePath
	 *            压缩包所在的路径
	 * @param outPath
	 *            解压的后文件的存放路径
	 */
	public static boolean unzip(String sourcePath, String outPath) {
		logger.debug("进入解压缩的工具类...");
		logger.debug("源文件 ： " + sourcePath);
		logger.debug("解压路径 ： " + outPath);
		boolean stateResult = false;

		long zipFileSize = 0;
		try {
			// 文件输入流
			FileInputStream fin = null;

			int runCount = 0;
			while (!stateResult && runCount < 20) {
				try {
					//String source = sourcePath.replaceAll("/", "\\\\");
				//	source = "D:\\ttt.zip";
					System.out.println(sourcePath);
				//	sourcePath = "D:\\海图项目\\临时文件\\压缩文件\\192.168.3.25_20170726.zip";
					fin = new FileInputStream(sourcePath);
					File fileJudge = new File(sourcePath);
					String name = "";
					name = fileJudge.getName(); 
					stateResult = true;
				} catch (Exception e) { 
					System.out.println(e);
					stateResult = false;
					runCount++;
					logger.debug(e);
					logger.debug("文件暂时不可解压,4秒后再尝试解压，目前已经失败"+runCount+"次");
					Thread.sleep(4000);
				}
			}

			if (stateResult) {
				logger.debug("开始解压。。。");
				// 需要维护所读取数据校验和的输入流。校验和可用于验证输入数据的完整性
				CheckedInputStream checkIn = new CheckedInputStream(fin, new CRC32());
				// 指定编码 否则会出现中文文件解压错误
				Charset gbk = Charset.forName("GBK");
				// zip格式的输入流
				ZipInputStream zin = new ZipInputStream(checkIn, gbk);

				// 遍历压缩文件中的所有压缩条目
				ZipEntry zinEntry;

				while ((zinEntry = zin.getNextEntry()) != null) {
					System.out.println(zinEntry);
					File targetFile = new File(outPath + File.separator + zinEntry.getName());

					System.out.println("..." + targetFile + "   " + targetFile.getParentFile());

					// String sourceUpdatePath = targetFile.toString();
					if (!targetFile.getParentFile().exists()) {
						System.out.println("..." + targetFile + "   " + targetFile.getParentFile());
						targetFile.getParentFile().mkdirs();
					}
					if (zinEntry.isDirectory()) {
						targetFile.mkdirs();
					} else {
						FileOutputStream fout = new FileOutputStream(targetFile);
						byte[] buff = new byte[1024];
						int length;
						while ((length = zin.read(buff)) > 0) {
							fout.write(buff, 0, length);
						}
						fout.close();
					}
				}

				zin.close();
				fin.close();
				System.out.println(checkIn.getChecksum().getValue());
				checkIn.close();

				//Thread.sleep(4000);
			//	File file = new File(sourcePath); 
			}

		} catch (Exception e) {
			// TODO: handle exception
			stateResult = false;
			System.out.println(e);
		}
		if (stateResult) {
			logger.debug("解压数据成功");
			System.out.println("解压数据成功");
			stateResult = true;
		} else {
			logger.debug("解压数据失败");
			System.out.println("解压数据失败");
			stateResult = false;
		}
		return stateResult; 
	}
 

	

}
