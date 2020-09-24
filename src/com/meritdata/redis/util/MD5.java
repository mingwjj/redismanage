package com.meritdata.redis.util;

import java.security.MessageDigest;

public class MD5
{

	public MD5()
	{
	}

	public static String getDigestedString(String source) throws Exception
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(source.getBytes());
		byte digested[] = md5.digest();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < digested.length; i++)
		{
			byte b = digested[i];
			int value = (b & 0x7f) + (b >= 0 ? 0 : 128);
			buffer.append(value >= 16 ? "" : "0");
			buffer.append(Integer.toHexString(value));
		}

		return buffer.toString();
	}
	
	/**
	 * 主函数测试用
	 * @param args
	 */
	public static void main(String[] args) {
	    String str = "infodba20160324151515HHPDM001";
		try{
		    String res = getDigestedString(str);
		    System.out.println("加密前字符串为:"+str);
		    System.out.println("加密后字符串为:"+res);
		}catch(Exception e){
		    System.out.println("对字符串"+str+"进行处理失败");
		}
	}
}