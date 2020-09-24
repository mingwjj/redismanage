package com.meritdata.redis.util;

import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;


/**
 * 资源文件读取辅助类
 * 
 * @author xuejp
 * 
 */
public class PropertiesUtil {

    private static Logger log = Logger.getLogger(PropertiesUtil.class);
    private static Configuration configuration;
    private static String PREFIX = "mymail.";

    public static void init() {
		try {
			String path = System.getProperty("catalina.home");
			String fileName = path + "/../redis-config-wizard.properties";
			
            configuration = new PropertiesConfiguration(fileName);
            
        } catch (Exception e) {
        	e.printStackTrace();
        	log.error("初始化配置文件对象出错，错误信息：" + e.getMessage());
        }
	}
    
    /**
     * 读取配置文件，根据键读取值，如果值为Null，则返回空字符串。
     */
    public static String getText(String key) {
        String value = "";
        try {
            value = RedisUtil.get(getRedisKey(key));
            if (value == null) {
                return "";
            }
            return value.trim();
        } catch (Exception e) {
            log.error("读取配置文件属性出错,错误信息:" + e.getMessage());
            return value;
        }
    }
    
    /**
     * 读取配置文件，根据键读取值，如果值为Null或空，则返回默认值。
     */
    public static String getText(String key, String defaultValue) {
        String value = "";
        try {
            value = RedisUtil.get(getRedisKey(key));
            if (value == null || "".equals(value)) {
                if(!"true".equals(value) && !"false".equals(value)){
                    return defaultValue;
                }
            }
            return value.trim();
        } catch (Exception e) {
            log.error("读取配置文件属性出错,错误信息:" + e.getMessage());
            return value;
        }
    }
    
    /**
     * 读取配置文件，根据键读取值，返回数字类型
     */
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(RedisUtil.get(getRedisKey(key)));
        } catch (Exception e) {
            log.error("读取配置文件属性出错,错误信息:" + e.getMessage());
            return defaultValue;
        }
    }
    
    /**
     * 读取配置文件，根据键读取值，返回布尔类型
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(RedisUtil.get(getRedisKey(key)));
        } catch (Exception e) {
            log.error("读取配置文件属性出错,错误信息:" + e.getMessage());
            return false;
        }
    }
    
    /**
     * 读取配置文件，根据键读取值，如果值为Null，则返回空字符串。
     */
    public static String getConfFileText(String key) {
        String value = "";
        try {
            value = configuration.getString(key);
            if (value == null) {
                return "";
            }
            return value.trim();
        } catch (Exception e) {
            log.error("读取redis配置文件属性出错,错误信息:" + e.getMessage());
            return value;
        }
    }
    
    /**
     * 读取配置文件，根据键读取值，如果值为Null，则返回空字符串。
     */
    public static int getConfFileText(String key, int def) {
        int val = 0;
    	try {
    		String value = configuration.getString(key);
            if (value == null || "".equals(value)) {
                val = def;
            } else {
            	val = Integer.valueOf(value);
            }
            return val;
        } catch (Exception e) {
            log.error("读取redis配置文件属性出错,错误信息:" + e.getMessage());
            return val;
        }
    }
    
    public static boolean getConfFileText(String key, boolean def) {
    	boolean val = true;
    	try {
    		String value = configuration.getString(key);
            if (value == null || "".equals(value)) {
                val = def;
            } else {
            	val = Boolean.valueOf(value);
            }
            return val;
        } catch (Exception e) {
            log.error("读取redis配置文件属性出错,错误信息:" + e.getMessage());
            return val;
        }
    }
    
    private static String getRedisKey(String prop) {
    	if ("datasource.enable".equalsIgnoreCase(prop) 
    		|| "jndi.name".equalsIgnoreCase(prop)
    		|| "jdbc.driver".equalsIgnoreCase(prop)
    		|| "jdbc.url".equalsIgnoreCase(prop)
    		|| "jdbc.username".equalsIgnoreCase(prop)
    		|| "jdbc.password".equalsIgnoreCase(prop)
    		|| "erds.ldap.host".equalsIgnoreCase(prop)
    		|| "erds.ldap.port".equalsIgnoreCase(prop)
    		|| "erds.ldap.admin".equalsIgnoreCase(prop)
    		|| "erds.ldap.password".equalsIgnoreCase(prop)
    		|| "erds.ldap.basedn".equalsIgnoreCase(prop)
    		|| "erds.user.searchFilter".equalsIgnoreCase(prop)
    		|| "erds.user.userId".equalsIgnoreCase(prop)
    		|| "erds.user.userName".equalsIgnoreCase(prop)) {
    		return prop;
    	} else {
    		return PREFIX + prop;
    	}
    }
    

    public static InputStream getResourceAsStream(String resource) throws Exception {
        String stripped = resource.startsWith("/") ? resource.substring(1)
                : resource;

        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(stripped);
        }
        if (stream == null) {
            stream = PropertiesUtil.class
                    .getResourceAsStream(resource);
        }
        if (stream == null) {
            stream = PropertiesUtil.class.getClassLoader()
                    .getResourceAsStream(stripped);
        }
        if (stream == null) {
            throw new Exception(resource + " not found");
        }
        return stream;
    }

}