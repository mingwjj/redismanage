package com.meritdata.redis.webservice;

import com.meritdata.redis.common.CommonDAOFactory;
import com.meritdata.redis.common.CommonDAOIntfs;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(name = "RedisService", serviceName = "RedisService",
        targetNamespace = "http://localhost:8080/ws/RedisService")
public class RedisServiceImpl implements RedisService {

    private CommonDAOIntfs common = CommonDAOFactory.getCommonDAO();

    @WebMethod
    public String syncLdap2Redis(){
        return common.initData() ? "1" : "0";
    }
}
