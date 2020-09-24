package com.meritdata.redis.common;

import java.util.*;
import java.util.Map.Entry;

import com.meritdata.redis.util.PropertiesUtil;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.meritdata.redis.util.RedisUtil;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

public class CommonDAOImpl implements CommonDAOIntfs {
	private Logger log = Logger.getLogger(CommonDAOImpl.class);


	@Override
	public List<Map<String, Object>> queryPageRelative(String key, String field, String value) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, String> map = new HashMap<String, String>();
		//
		if ((field == null || "".equals(field)) && (value == null || "".equals(value))) {
			map = RedisUtil.hgetall(key);
		} else if (field != null && !"".equals(field) && value != null && !"".equals(value)) {
			map = RedisUtil.hsearchfv(key, field, value);
		} else if (field != null && !"".equals(field) && (value == null || "".equals(value))) {
			map = RedisUtil.hsearchf(key, field);
		} else if (value != null && !"".equals(value) && (field == null || "".equals(field))) {
			map = RedisUtil.hsearchv(key, value);
		}
		// 排序
//		List<Map.Entry<String, String>> arrayList = new ArrayList<Map.Entry<String, String>>(map.entrySet());
//		Collections.sort(arrayList, new Comparator<Map.Entry<String, String>>(){
//			// 升序
//			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
//				return o1.getKey().compareTo(o2.getKey());
//			}
//		});
		Map<String, String> treeMap = new TreeMap<String, String>(new Comparator<String>(){
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		for (Entry<String, String> entry : map.entrySet()) {
			String key1 = entry.getKey();
			String value1 = entry.getValue();
			// 将公共配置项放第一个的特殊处理
			if ("common".equals(key1)) {
				key1 = "0008_" + key1;
			}
			treeMap.put(key1, value1);
		}
		
		
		for (Entry<String, String> entry : treeMap.entrySet()) {
			Map<String, Object> m = new HashMap<String, Object>();
			String key1 = entry.getKey();
			String value1 = entry.getValue();
			// 还原公共配置项的key值
			if (key1.startsWith("0008_")) {
				key1 = key1.substring(5);
			}
			m.put("field", key1);
			m.put("value", value1);
			list.add(m);
		}
		return list;
	}
	
	@Override
	public List<Map<String, Object>> queryListMap(String key, String field, String value) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, String> map = new HashMap<String, String>();
		if ((field == null || "".equals(field)) && (value == null || "".equals(value))) {
			map = RedisUtil.hgetall(key);
		} else if (field != null && !"".equals(field) && value != null && !"".equals(value)) {
			map = RedisUtil.hsearchfv(key, field, value);
		} else if (field != null && !"".equals(field) && (value == null || "".equals(value))) {
			map = RedisUtil.hsearchf(key, field);
		} else if (value != null && !"".equals(value) && (field == null || "".equals(field))) {
			map = RedisUtil.hsearchv(key, value);
		}
		
		// 排序
		Map<String, String> treeMap = new TreeMap<String, String>(new Comparator<String>(){
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		for (Entry<String, String> entry : map.entrySet()) {
			treeMap.put(entry.getKey(), entry.getValue());
		}
		
		for (Entry<String, String> entry : treeMap.entrySet()) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("field", entry.getKey());
			String mapv = entry.getValue();
			if ("".equals(mapv) || null == mapv) {
				m.put("value", "");
				m.put("desc", "");
			} else {
				//
				if (mapv.startsWith("{'")) {
					JSONObject jsonmap = JSONObject.fromObject(mapv);
					m.put("value", jsonmap.get("value"));
					m.put("desc", jsonmap.get("desc"));
				} else {
					m.put("value", mapv);
				}
			}
			list.add(m);
		}
		return list;
	}
	
	@Override
	public List<Map<String, Object>> queryList(String key, String field, String value) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, String> map = new HashMap<String, String>();
		
		List<String> ls = RedisUtil.lrange(key, 0, -1);
		if (field != null && !"".equals(field)) {
			Iterator<String> it = ls.iterator();
            while (it.hasNext()) {
            	String f = it.next();
            	JSONObject m = JSONObject.fromObject(f);
            	String fi = String.valueOf(m.get("field"));
            	if (!fi.contains(field)) {
            		it.remove();
            	}
            }
		}
		if (value != null && !"".equals(value)) {
			Iterator<String> it = ls.iterator();
            while (it.hasNext()) {
            	String f = it.next();
            	JSONObject m = JSONObject.fromObject(f);
            	String va = String.valueOf(m.get("value"));
            	if (!va.contains(value)) {
            		it.remove();
            	}
            }
		}
		for (String str : ls) {
			JSONObject fromObject = JSONObject.fromObject(str);
			list.add(fromObject);
		}
		
		return list;
	}

	@Override
	public long getDataCount(String key, String type) {
		if ("hash".equals(type)) {
			return RedisUtil.hlen(key);
		} else if ("list".equals(type)) {
			return RedisUtil.llen(key);
		} else {
			return 0;
		}
	}

	public boolean initData() {
		boolean flag = false;
		DirContext dc = null;

		try {
			dc = openADConnection();

			erdsData(dc, "erdsu");
			erdsData(dc, "erdso");
			erdsData(dc, "aduser");
			erdsData(dc, "adgroup");
			erdsData(dc, "timpeople");
			erdsData(dc, "timaccounts");

			flag = true;

			return flag;

		} catch (Exception e) {
			e.printStackTrace();
			return  false;
		} finally {
			if (dc != null) {
				try {
					dc.close();
				} catch (NamingException e) {
					log.error("关闭目录连接错误", e);
				}
			}
		}
	}

	private boolean erdsData(DirContext dc, String type) throws Exception {
		String id = "";
		String baseDn = "";
		String fields = "";
		String objectClass = "";

		try {
			if ("erdsu".equals(type)) {
				id = PropertiesUtil.getConfFileText("user_id");
				baseDn = PropertiesUtil.getConfFileText("ERDS_BASEDN");// erds basedn
				//fields = PropertiesUtil.getConfFileText("erdsUserAttr");//
				objectClass = PropertiesUtil.getConfFileText("erdsUserObjectClass");// 目录用户使用对象类

			} else if ("erdso".equals(type)) {
				id = PropertiesUtil.getConfFileText("org_id");
				baseDn = PropertiesUtil.getConfFileText("ERDS_BASEDN");// basedn
				//fields = PropertiesUtil.getConfFileText("erdsOrgAttr");//
				objectClass = PropertiesUtil.getConfFileText("erdsOrgObjectClass");// 目录用户使用对象类

			} else if ("aduser".equals(type)) {
				id = PropertiesUtil.getConfFileText("user_id");
				baseDn = PropertiesUtil.getConfFileText("TAM_BASEDN");// tam basedn
				//fields = PropertiesUtil.getConfFileText("erdsUserAttr");//
				objectClass = PropertiesUtil.getConfFileText("erdsUserObjectClass");// 目录用户使用对象类

			} else if ("adgroup".equals(type)) {
				id = PropertiesUtil.getConfFileText("group_id");
				baseDn = PropertiesUtil.getConfFileText("TAM_BASEDN");// tam basedn
				//fields = PropertiesUtil.getConfFileText("TAM_group_attrs");//
				objectClass = PropertiesUtil.getConfFileText("TAM_group_ObjectClass");// 目录用户使用对象类

			}else if ("timpeople".equals(type)) {
				id = PropertiesUtil.getConfFileText("tim_people_id");
				baseDn = PropertiesUtil.getConfFileText("tim_people_basedn");// tam basedn
				//fields = "";//
				objectClass = PropertiesUtil.getConfFileText("erdsUserObjectClass");// 目录用户使用对象类

			}else if ("timaccounts".equals(type)) {
				id = PropertiesUtil.getConfFileText("tim_account_id");
				baseDn = PropertiesUtil.getConfFileText("tim_account_basedn");// tam basedn
				//fields = "";//
				objectClass = PropertiesUtil.getConfFileText("tim_account_objectClass");// 目录用户使用对象类

			}

			Set<String> attrSet = null;
			if (!"".equals(fields) && null != fields) {
				attrSet = new HashSet<String>();
				for (String field : fields.split(",")) {
					attrSet.add(field);

				}
			}

			List<Map<String, Object>> list = null;
			list = getDataList(dc, baseDn, "", null, attrSet, objectClass);

			for (Map<String, Object> map : list) {
				String key = String.valueOf(map.get(id));
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String field = entry.getKey();
					String value = String.valueOf(entry.getValue());
					System.out.println("category- key:" + field + "; value:" + value);

					//String v = "{'value':'"+value+"'}";
					RedisUtil.hset(type + ":" + key, field, value);
				}

			}

			return true;

		} catch (Exception e) {
			log.error("获取LDAP数据失败！", e);
			return false;
		}
	}

	private List<Map<String, Object>> getDataList(DirContext dc,
												  String baseDn,
												  String scope,
												  Map<String, String> filterMap,
												  Set<String> attrSet,
												  String classes) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();



		SearchControls sc = new SearchControls();
		if ("base".equals(scope)) {
			sc.setSearchScope(SearchControls.OBJECT_SCOPE);
		} else if ("one".equals(scope)) {
			sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		} else {
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		}
		String filter = "";
		if (filterMap != null) {
			for (Iterator<String> it = filterMap.keySet().iterator(); it
					.hasNext();) {
				String key = it.next();
				String val = (String) filterMap.get(key);
				if (val != null && val.length() > 0) {
					filter += "(" + key + "=" + val + ")";
				}
			}
		}
		if (classes == null || classes.length() == 0) {
			classes = "*";
		}
		NamingEnumeration<?> ne = null;
		try {

			ne = dc.search(baseDn, "(&" + filter + "(objectclass=" + classes
					+ "))", sc);
			while (ne.hasMoreElements()) {
				Map<String, Object> map = new HashMap<String, Object>();
				SearchResult sr = (SearchResult) ne.next();
				String name = sr.getName();
				if (baseDn != null && !"".equals(baseDn)) {
					if (name != null && !"".equals(name)) {
						name = name + "," + baseDn;
					} else {
						name = baseDn;
					}
				}
				map.put("dn", name);
				/**
				 if (attrSet == null || attrSet.contains("DN")) {
				     map.put("DN", name);
				}
				**/
				Attributes at = sr.getAttributes();
				NamingEnumeration<?> ane = at.getAll();
				while (ane.hasMore()) {
					Attribute attr = (Attribute) ane.next();
					//String attrType = attr.getID().toUpperCase();
					String attrType = attr.getID().toLowerCase();
					NamingEnumeration<?> values = attr.getAll();
					while (values.hasMore()) {
						Object oneVal = values.nextElement();
						if (attrSet == null || attrSet.contains(attrType)) {
							String val = "";
							if (oneVal instanceof String) {
								val = (String) oneVal;
							} else {
								val = new String((byte[]) oneVal);
							}
							if (map.get(attrType) != null) {
								map.put(attrType, map.get(attrType) + ";" + val);
							} else {
								map.put(attrType, val);
							}
						}
					}
				}
				list.add(map);
			}
			//LdapUtil.closeLdap(dc);
		} catch (Exception e) {
			//LdapUtil.closeLdap(dc);
			throw new Exception(e);
		}
		return list;

	}

	private DirContext openADConnection() {
		DirContext dc = null;
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		String icf = PropertiesUtil.getConfFileText("INITIAL_CONTEXT_FACTORY");
		String sa = PropertiesUtil.getConfFileText("SECURITY_AUTHENTICATION");
		String host = PropertiesUtil.getConfFileText("PROVIDER_HOST");
		String port = PropertiesUtil.getConfFileText("PROVIDER_PORT");
		String admin = PropertiesUtil.getConfFileText("SECURITY_PRINCIPAL");
		String password = PropertiesUtil.getConfFileText("SECURITY_CREDENTIALS");


		env.put(Context.INITIAL_CONTEXT_FACTORY, icf);
		env.put(Context.SECURITY_AUTHENTICATION, sa);
		env.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);
		env.put(Context.SECURITY_PRINCIPAL, admin);
		env.put(Context.SECURITY_CREDENTIALS, password);
		try {
			dc = (DirContext) (new InitialDirContext(env));
		} catch (Exception e) {
			log.error("打开windows域目录连接错误", e);
			return null;
		}
		return dc;
	}
}