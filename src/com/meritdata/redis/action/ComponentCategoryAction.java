package com.meritdata.redis.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meritdata.redis.bean.TreeBS;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.meritdata.redis.common.CommonDAOFactory;
import com.meritdata.redis.common.CommonDAOIntfs;
import com.meritdata.redis.util.PropertiesUtil;
import com.meritdata.redis.util.RedisUtil;


@Controller
public class ComponentCategoryAction{

	private static final long serialVersionUID = 3178419537395335971L;

	private static Logger log = Logger.getLogger(ComponentCategoryAction.class);
	private CommonDAOIntfs common = CommonDAOFactory.getCommonDAO();
	private JSONObject resultObj ;
	private JSONArray jsonArray;
	
    
	@RequestMapping("ccList")
	public void ccList(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		PrintWriter pw;
		try
		{
			String key = request.getParameter("key");
			String field = request.getParameter("field");
			String value = request.getParameter("value");
	        
	        List<Map<String, Object>> list = common.queryPageRelative(key, field, value);
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
	@RequestMapping("ComponentCategory_addOrUpdate4ajax")
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
			String action = request.getParameter("action");
			
			if ("add".equals(action)) {
				if (RedisUtil.hexists(key, field)) {
					result = "exist";
				} else {
					RedisUtil.hset(key, field, value);
					result = "ok";
				}
			} else {
				if (!field.equals(field_old)) {
					RedisUtil.hdel(key, field_old);
				}
				RedisUtil.hset(key, field, value);
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
	

	@RequestMapping("ComponentCategory_delete")
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

	/**
	 * 配置数据导出
	 * @param response
	 */
	@RequestMapping("exportData")
	public void exportData(HttpServletResponse response) {
		String filename = "";
		try {
			filename = "attachment;filename=\""
					+ URLEncoder.encode("组件配置项数据.xml", "UTF-8") + "\";";
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("Content-disposition", filename);
			
			OutputStream out = response.getOutputStream();
			
			// 分类
			List<Map<String, Object>> category = common.queryPageRelative("component_category", "", "");
			
			Document document = createDocument(category);
			
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setIndent("    ");
			format.setEncoding(document.getXMLEncoding());
			StringWriter stringWriter = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
			xmlWriter.write(document);
			xmlWriter.flush();
			
			//String xml = document.asXML();
			String xml = stringWriter.toString();
			byte[] byteArray = xml.getBytes();
			
			out.write(byteArray);
			
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 初始化配置文件选项
	 * 
	 * @param response
	 * @return
	 */
	@RequestMapping("initData")
	public void initData(HttpServletResponse response) {
		try {
			InputStream is = PropertiesUtil.getResourceAsStream("initData.xml");
			if (initData(is)) {
				response.getWriter().write("ok");
			} else {
				response.getWriter().write("error");
			}
		} catch (Exception e) {
			try {
				response.getWriter().write("error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}

	/**
	 * 初始化配置文件选项
	 *
	 * @param response
	 *
	 */
	@RequestMapping("syncLdapData")
	public void syncLdapData(HttpServletResponse response) {
		try {
			if (common.initData()) {
				response.getWriter().write("ok");
			} else {
				response.getWriter().write("error");
			}
		} catch (Exception e) {
			try {
				response.getWriter().write("error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @param map0
	 * @param response
	 * @return
	 */
	@RequestMapping("exportData2")
	public ResponseEntity<byte[]> exportData2(@RequestParam Map<String,Object> map0, HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders(); 
		String filename = "组件配置项数据.xml";
		byte[] byteArray = {};
		
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", filename);
		
		try {
			// 分类
			List<Map<String, Object>> category = common.queryPageRelative("component_category", "", "");
			
			Document document = createDocument(category);
			String xml = document.asXML();
			byteArray = xml.getBytes();
		} catch (Exception e) {
			log.error("导出配置项数据出错：" + e.getMessage());
		}
		return new ResponseEntity<byte[]>(byteArray, headers, HttpStatus.CREATED);
	}

	private Document createDocument(List<Map<String, Object>> category) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("ROOT");
		Element categoryEle = root.addElement("component_category");
		
		for (Map map : category) {
			String key = String.valueOf(map.get("field"));
			String value = String.valueOf(map.get("value"));
			categoryEle.addElement(key).addCDATA(value);
			
			List<Map<String, Object>> list = common.queryListMap(key, "", "");
			Element componentName = root.addElement(key);
			for (Map items : list) {
				String key2 = String.valueOf(items.get("field"));
				String value2 = String.valueOf(items.get("value"));
				String desc = String.valueOf(items.get("desc"));
				Element ckey = componentName.addElement("item");
				ckey.addElement("key").addCDATA(key2);
				ckey.addElement("value").addCDATA(value2);
				ckey.addElement("desc").addCDATA(desc);
			}
		}
		
		return doc;
	}
	
	private boolean initData(InputStream in) {
		boolean flag = false;
		SAXReader saxReader = new SAXReader();
		
		Document document = null;
		try {
			document = saxReader.read(in);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element rootElement = document.getRootElement();
		List<Element> elements = rootElement.elements();
		
		for (Element e : elements) {
			List<Element> elements2 = e.elements();
			String key = e.getName();
			// 组件类型
			if ("component_category".equals(key)) {
				for (Element category : elements2) {
					String field = category.getName();
					String value = category.getTextTrim();
					
					System.out.println("category- key:" + field + "; value:" + value);
					RedisUtil.hset(key, field, value);
				}
			} else { // 具体组件配置项
				for (Element item : elements2) {
					String field = item.element("key").getTextTrim();
					String value = item.element("value").getTextTrim();
					String desc = item.element("desc").getTextTrim();
					String v = "{'value':'"+value+"','desc':'"+desc+"'}";

					System.out.println(key+"- key:" + field + "; value:" + v);
					RedisUtil.hset(key, field, v);
				}
			}
		}
		flag = true;
		
		return flag;
	}



	/**
	 * 配置数据导出
	 *
	 * @param response
	 * @return
	 */
	@RequestMapping("getTree")
	public void getTree(HttpServletResponse response) {
		try {
			TreeBS tree = new TreeBS();
			String deptname = RedisUtil.hget("erdso:merit", "deptname");
			tree.setId("erdso:merit");
			tree.setText(deptname);

			Set<String> orgKeys = RedisUtil.keys("erdso:*");

			Set<String> userKeys = RedisUtil.keys("erdsu:*");

			tree.setNodes(getChilder("merit", orgKeys, userKeys));

			JSONObject jsonObject = JSONObject.fromObject(tree);//格式化result   一定要是JSONObject 

			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().print(jsonObject);

		} catch (Exception e) {
			try {
				response.getWriter().write("error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	private List<TreeBS> getChilder(String parentDeptCode, Set<String> orgSets, Set<String> userSets) {
		List<TreeBS> nodes = new ArrayList<TreeBS>();
		try {
			Iterator<String> orgIt = orgSets.iterator();
			while (orgIt.hasNext()) {
				String key = orgIt.next();
				Map<String, String> map = RedisUtil.hgetall(key);
				if (parentDeptCode.equals(map.get("parentdeptcode"))) {
					TreeBS treeBS = new TreeBS();
					treeBS.setId("erdso:"+String.valueOf(map.get("ou")));
					treeBS.setText(String.valueOf(map.get("deptname")));
					//orgIt.remove();

					treeBS.setNodes(getChilder(String.valueOf(map.get("ou")), orgSets, userSets));

					nodes.add(treeBS);
				}
			}

			Iterator<String> userIt = userSets.iterator();
			while (userIt.hasNext()) {
				String key = userIt.next();
				Map<String, String> map = RedisUtil.hgetall(key);
				if (parentDeptCode.equals(map.get("deptcode"))) {
					TreeBS treeBS = new TreeBS();
					treeBS.setId("erdsu:"+String.valueOf(map.get("uid")));
					treeBS.setText(String.valueOf(map.get("fullname")));
					userIt.remove();

					nodes.add(treeBS);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodes;

	}
}
