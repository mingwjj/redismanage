package com.meritdata.redis.webservice;


import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface RedisService {

    @WebMethod
    public String syncLdap2Redis();
}
