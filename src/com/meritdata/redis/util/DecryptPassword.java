package com.meritdata.redis.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 解密门户传递登录密码类
 * 
 * @author maojp
 * 
 */
public class DecryptPassword {   
	
    public String decryptPassword(HttpServletRequest request, HttpServletResponse response)  throws Exception {  
        String password = "";  
        String params = (String) request.getParameter("params");  
        System.out.println(params);
        System.out.println("系统编码>>>>>>>>>>>>>>>>" + params.split("@@")[0]);  
        System.out.println("系统密码>>>>>>>>>>>>>>>>" + params.split("@@")[1]);  
		try {
			MeritSecurity ms = new MeritSecurity(params.split("@@")[0]);
			String decryPwd = ms.decrypt(params.split("@@")[1]);
			System.out.println("解密后密码>>>>>>>>>>>>>>>"+decryPwd);
			password = decryPwd;
		} catch (Exception e) {
			e.printStackTrace();
		}
        response.setContentType("text/html;charset=gb2312");  
        PrintWriter out = response.getWriter();  
        out.println(password);  
        out.flush();  
        out.close();  
        return password;
    }  

}