package com.meritdata.redis.common;

/**
 * 获得实例的工厂类
 * 
 * @author wangming
 * @since 2012-03-13
 * 
 */
public class CommonDAOFactory
{

	public static CommonDAOImpl commonDAOImpl;

	/**
	 * 获得接口的一个实例
	 * 
	 * @return
	 */
	public static CommonDAOImpl getCommonDAO()
	{
		if (commonDAOImpl == null)
		{
			commonDAOImpl = new CommonDAOImpl();
		}
		return commonDAOImpl;
	}
}
