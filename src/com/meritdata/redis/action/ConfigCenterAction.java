package com.meritdata.redis.action;

import java.io.PrintWriter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.meritdata.redis.common.CommonDAOFactory;
import com.meritdata.redis.common.CommonDAOIntfs;
import com.meritdata.redis.util.RedisUtil;

@Controller
public class ConfigCenterAction{

	private static final long serialVersionUID = 3178419537395335971L;

	private static Logger log = Logger.getLogger(ConfigCenterAction.class);
	private CommonDAOIntfs common = CommonDAOFactory.getCommonDAO();
	private JSONObject resultObj ;
	
	@RequestMapping("index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
    	ModelAndView mv = new ModelAndView();
		try
		{
			String key = request.getParameter("key");
			
			List<Map<String, Object>> list = common.queryPageRelative(key, null, null);
			mv.addObject("list", list);
			mv.addObject("userId", key);
			mv.setViewName("ConfigCenter/index");
		}
		catch (Exception e)
		{
			log.error("流程中心列表分类列表出错：" + e.getMessage());
			request.setAttribute("exceptionMessage", "查询流程中心列表分类列表出错：" + e);
		}
		return mv;
	}

	@RequestMapping("tam")
	public ModelAndView tam(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ModelAndView mv = new ModelAndView();
		try
		{
			String key = request.getParameter("key");

			Set<String> keys = RedisUtil.keys(key + "*");

			mv.addObject("list", keys);
			mv.addObject("key", key);
			if ("timpeople".equals(key) || "timaccounts".equals(key)) {
				key = "adgroup";
			}
			mv.setViewName("ConfigCenter/"+key);
		}
		catch (Exception e)
		{
			log.error("流程中心列表分类列表出错：" + e.getMessage());
			request.setAttribute("exceptionMessage", "查询流程中心列表分类列表出错：" + e);
		}
		return mv;
	}
    
	@RequestMapping("ccenterList")
	public void ccenterList(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		PrintWriter pw;
		try
		{
			String key = request.getParameter("key");
			String field = request.getParameter("field");
			String value = request.getParameter("value");
	        
	        List<Map<String, Object>> list = common.queryListMap(key, field, value);
	        Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
	        jsonMap.put("total", common.getDataCount(key, "hash"));//total键 存放总记录数，必须的
	        jsonMap.put("rows", list);//rows键 存放每页记录 list
	        resultObj = JSONObject.fromObject(jsonMap);//格式化result   一定要是JSONObject  
			
		    response.setContentType("text/html;charset=UTF-8");
			pw = response.getWriter();
		    pw.print(resultObj.toString());
		    pw.close();
		}
		catch (Exception e)
		{
			log.error("获取datagride数据出错：" + e.getMessage());
			request.setAttribute("exceptionMessage", e);
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(e.getMessage());
		}
		
	}
	
	
	/**
	 * 新增数据
	 * 
	 * @return
	 */
	@RequestMapping("ConfigCenter_addOrUpdate4ajax")
	public void addOrUpdate4ajax(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String result = "Fail";
		PrintWriter pw;
		try
		{
			String key = request.getParameter("key");
			String field_old = request.getParameter("field_old");
			String field = request.getParameter("field");
			String value = request.getParameter("value");
			String desc = request.getParameter("desc");
			String action = request.getParameter("action");
			
			String v = "{'value':'"+value+"','desc':'"+desc+"'}";

			if ("add".equals(action)) {
				if (RedisUtil.hexists(key, field)) {
					result = "exist";
				} else if (RedisUtil.hexists("common", field)) {
					result = "commexist";
				} else {
					RedisUtil.hset(key, field, v);
					result = "ok";
				}
			} else {
				if (!field.equals(field_old)) {
					RedisUtil.hdel(key, field_old);
				}
				RedisUtil.hset(key, field, v);
				result = "ok";
			}
			
			response.setContentType("text/html;charset=UTF-8");
			pw = response.getWriter();
		    pw.print(result);
		    pw.close();
		}
		catch (Exception e)
		{
			log.error("新增数据出错：" + e.getMessage());
			e.printStackTrace();
			request.setAttribute("exceptionMessage", e);
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(e.getMessage());
		}
	}
	
	

	@RequestMapping("ConfigCenter_delete")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try
		{
			String key = request.getParameter("key");
			String[] fields = request.getParameter("fields").split(",");
			RedisUtil.hdel(key, fields);
			
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("ok");
		}
		catch (Exception e)
		{
			log.error("删除数据出错：" + e.getMessage());
			request.setAttribute("exceptionMessage", e);
			response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(e.getMessage());
		}
	}
	
	public void delete_bak(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try
		{
			String key = request.getParameter("key");
			String[] fields = request.getParameter("fields").split(";");
			for (String value : fields) {
				RedisUtil.lrem(key, 0, value);
			}
			
			
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("ok");
		}
		catch (Exception e)
		{
			log.error("删除数据出错：" + e.getMessage());
			request.setAttribute("exceptionMessage", e);
			response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(e.getMessage());
		}
	}
	
	public void addOrUpdate4ajax_bak(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String result = "Fail";
		PrintWriter pw;
		try
		{
			String key = request.getParameter("key");
			String field_old = request.getParameter("field_old");
			String field = request.getParameter("field");
			String value = request.getParameter("value");
			String desc = request.getParameter("desc");
			String action = request.getParameter("action");
			
			String v = "{'field':'"+field+"','value':'"+value+"','desc':'"+desc+"'}";
			if ("add".equals(action)) {
				RedisUtil.rpush(key, v);
				result = "ok";
			} else {
				//获取索引
				List<String> ls = RedisUtil.lrange(key, 0, -1);
				long index = 0;
				for (int i=0; i<ls.size(); i++) {
					if (equals(field_old, ls.get(i))) {
						index = i;
						break;
					}
				}
				RedisUtil.lset(key, index, v);
				result = "ok";
			}
			response.setContentType("text/html;charset=UTF-8");
			pw = response.getWriter();
		    pw.print(result);
		    pw.close();
		}
		catch (Exception e)
		{
			log.error("新增数据出错：" + e.getMessage());
			e.printStackTrace();
			request.setAttribute("exceptionMessage", e);
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(e.getMessage());
		}
	}
	
	private boolean equals(String old, String str) {
		boolean flag = false;
		
		JSONObject oldmap = JSONObject.fromObject(old);
		Collection values = oldmap.values();
		String oldstr = "";
		Iterator iterator = values.iterator();
		while (iterator.hasNext()) {
			oldstr += String.valueOf(iterator.next());
		}
		
		JSONObject map = JSONObject.fromObject(str);
		Collection values2 = map.values();
		String oldstr2 = "";
		Iterator iterator2 = values2.iterator();
		while (iterator2.hasNext()) {
			oldstr2 += String.valueOf(iterator2.next());
		}
		
		if (oldstr.equals(oldstr2)) {
			flag = true;
		}
		
		return flag;
	}
}
