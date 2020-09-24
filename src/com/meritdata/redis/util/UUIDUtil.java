package com.meritdata.redis.util;

import java.util.UUID;
public class UUIDUtil {
	public static String nextCode() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");

	}
}
