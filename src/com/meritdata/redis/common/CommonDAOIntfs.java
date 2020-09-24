package com.meritdata.redis.common;

import java.util.List;
import java.util.Map;

import com.meritdata.redis.bean.TreeModel;

/**
 * 获取Portlet首选项信息的接口类
 * 
 * @author miaoyu
 * @since 2011-12-13
 */
public interface CommonDAOIntfs
{
	/**
	 * 通过key查询v
	 * alue（map）
	 * @param key
	 * @param start
	 * @param number
	 * @return
	 */
	public List<Map<String, Object>> queryPageRelative(String key, String field, String value);
	
	public List<Map<String, Object>> queryListMap(String key, String field, String value);
	
	public List<Map<String, Object>> queryList(String key, String field, String value);
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public long getDataCount(String key, String type);

	/**
	 * init ldap data
	 * @return
	 */
	public boolean initData();

}
