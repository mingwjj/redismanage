package com.meritdata.redis.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * 资源文件读取类
 * 
 * @author xuejp
 * 
 */
public class PropertiesListener implements ServletContextListener {
	private static Logger log = Logger.getLogger(PropertiesListener.class);
	
    /**
     * 应用服务启动时调用
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	// 获取程序在服务器上的部署路径
        try {
        	PropertiesUtil.init();
        	RedisUtil.init();
		} catch (Exception e) {
			log.error("初始化BPM参数失败："+e.getMessage());
		}
    }

    public void contextDestroyed(ServletContextEvent arg0) {

    }
}
