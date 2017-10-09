package com.solar.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException; 
import java.util.ResourceBundle;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.solar.servlet.UnZipMonitor;
 

public class FTPTransterUtil {
	private static Logger logger = Logger.getLogger(FTPTransterUtil.class);
	private String userName;         //FTP 登录用户名   
    private String password;         //FTP 登录密码   
    private String ip;                     //FTP 服务器地址IP地址   
    private int port;                        //FTP 端口   
   
    private static String localDir;         //ftp 本地存储地址
  
    private FTPClient ftpClient = null; //FTP 客户端代理    
    
    //FTP状态码   
    public int i = 1;   
    

    public static ResourceBundle bundle = ResourceBundle.getBundle("config/ship"); 
    
    
    /**  
     * 设置参数  
     *  
     * @param configFile --参数的配置文件  
     */   
    private boolean setArg() {   
        boolean flag = true;   
       
 
             userName = bundle.getString("ftp_username");   
             password = bundle.getString("ftp_password");   
             ip = bundle.getString("ftp_ip");   
             port = Integer.parseInt(bundle.getString("ftp_port"));   
             String path = System.getProperty("catalina.home");
             localDir =  path + File.separator +  bundle.getString("ftp_downloadDir");   
        return flag;  
    }   
    
    
    public static void downloadFile(String filename){
    	FTPTransterUtil ftpClient = new FTPTransterUtil();  
        if(ftpClient.connectServer()){   
        	logger.debug("FTP 服务器连接成功，准备下载 文件 ：" +  filename + "到 " + localDir);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件    
            boolean flag = ftpClient.loadFile(filename, localDir + filename);
            logger.debug("文件下载 " + (flag? "成功":"失败"));
            
            if(flag){
            	try {
					UnZipMonitor.unzip(localDir + filename);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
         //   ftpClient.uploadManyFile("H:\\d", "/d/");   
            ftpClient.closeConnect();// 关闭连接   
        }  else{
        	logger.debug("FTP 服务器连接不上，请检查是否服务出现了问题，或者死账号密码出错。。。");
        }
    }
    
    public static void main(String[] args) {
    	FTPTransterUtil ftpClient = new FTPTransterUtil();  
         if(ftpClient.connectServer()){   
             ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输二进制文件    
             ftpClient.loadFile("192.168.3.44_20170927105353.zip", localDir + "192.168.3.44_20170927105353.zip");
          //   ftpClient.uploadManyFile("H:\\d", "/d/");   
             ftpClient.closeConnect();// 关闭连接   
         }   
	}
    
    
    
    /**  
     * 关闭连接  
     */   
    public void closeConnect() {   
        try {   
            if (ftpClient != null) {   
                ftpClient.logout();   
                ftpClient.disconnect();   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }   
  
    
    /**  
     * 设置传输文件的类型[文本文件或者二进制文件]  
     *  
     * @param fileType--BINARY_FILE_TYPE、ASCII_FILE_TYPE  
     *  
     */   
    public void setFileType(int fileType) {   
        try {   
            ftpClient.setFileType(fileType);   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }   
    
    /**  
     * 下载文件  
     *  
     * @param remoteFileName             --服务器上的文件名  
     * @param localFileName--本地文件名  
     * @return true 下载成功，false 下载失败  
     */   
    public boolean loadFile(String remoteFileName, String localFileName) {   
        boolean flag = true;   
        // 下载文件   
        BufferedOutputStream buffOut = null;   
        try {   
            buffOut = new BufferedOutputStream(new FileOutputStream(localFileName));   
            flag = ftpClient.retrieveFile(remoteFileName, buffOut);   
        } catch (Exception e) {   
            e.printStackTrace();   
            logger.debug("本地文件下载失败！", e);   
        } finally {   
            try {   
                if (buffOut != null)   
                    buffOut.close();   
            } catch (Exception e) {   
                e.printStackTrace();   
            }   
        }   
        return flag;   
    }   
    
    
    /**  
     * 连接到服务器  
     *  
     * @return true 连接服务器成功，false 连接服务器失败  
     */   
    public boolean connectServer() {   
        boolean flag = true;   
        if (ftpClient == null) {   
            int reply;   
            try {   
                if(setArg()){  
                    ftpClient = new FTPClient();   
                    ftpClient.setControlEncoding("GBK");   
                    //ftpClient.configure(getFtpConfig());   
                    ftpClient.connect(ip,port);   
                    ftpClient.login(userName, password);  
                    reply = ftpClient.getReplyCode();   
                    ftpClient.setDataTimeout(120000);   
  
                    if (!FTPReply.isPositiveCompletion(reply)) {   
                        ftpClient.disconnect();   
                        logger.debug("FTP 服务拒绝连接！");   
                        flag = false;   
                    }   
                    i++;   
                }else{  
                    flag = false;   
                }  
            } catch (SocketException e) {   
                flag = false;   
                e.printStackTrace();   
                logger.debug("登录ftp服务器 " + ip + " 失败,连接超时！");   
            } catch (IOException e) {   
                flag = false;   
                e.printStackTrace();   
                logger.debug("登录ftp服务器 " + ip + " 失败，FTP服务器无法打开！");   
            }   
        }   
        return flag;   
    }  
    
    
    
    
}