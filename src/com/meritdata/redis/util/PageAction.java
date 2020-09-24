package com.meritdata.redis.util;

import java.util.HashMap;

/**
 * <p>
 * Title:PageAction
 * </p>
 * 
 * <p>
 * Description: 分页行为描述类,用于描述分页行为,分页行为有去首页、去最后页、上一页、下一页、跳页
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: 西安美林电子有限公司
 * </p>
 * 
 * @author 卢耀宗
 */
@SuppressWarnings("unchecked")
public class PageAction {
    /***/
    public static final int FIRST_INT = 0;

    public static final int PREVIOUS_INT = 1;

    public static final int NEXT_INT = 2;

    public static final int LAST_INT = 3;

    public static final int JUMP_INT = 4;

    public static final int DEFAULT_INT = 5;

    public static final PageAction FIRST = new PageAction(FIRST_INT);

    public static final PageAction PREVIOUS = new PageAction(PREVIOUS_INT);

    public static final PageAction NEXT = new PageAction(NEXT_INT);

    public static final PageAction LAST = new PageAction(LAST_INT);

    public static final PageAction JUMP = new PageAction(JUMP_INT);

    public static final PageAction DEFAULT = new PageAction(DEFAULT_INT);

    private static final String[] TAGS = { "FIRST", "PREVIOUS", "NEXT", "LAST",
            "JUMP", "DEFAULT" };

    private static final PageAction[] VALUES = { FIRST, PREVIOUS, NEXT, LAST,
            JUMP, DEFAULT };

    private static final HashMap tagMap = new HashMap();

    private final int value;

    static {
        for (int i = 0; i < TAGS.length; i++) {
            tagMap.put(TAGS[i], VALUES[i]);
        }
    }

    public static PageAction fromString(String tag) {
        PageAction pageAction = (PageAction) tagMap.get(tag.toUpperCase());
        if (pageAction == null && tag != null) {
            throw new IllegalArgumentException(tag);
        }
        return pageAction;
    }
    
    /**
     * 计算总页数
     * @param totalItem 总记录数
     * @param itemsInPage 每页条目数
     * @return
     */
    public static int calcuateTotalPage(int totalItem, int itemsInPage)
	{
		int result = 1;
		if (totalItem <= itemsInPage)
		{
			result = 1;
		}
		else if (totalItem % itemsInPage == 0)
		{
			result = totalItem / itemsInPage;
		}
		else
		{
			result = totalItem / itemsInPage + 1;
		}
		return result;
	}

    private PageAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String toString() {
        return TAGS[this.value];
    }

    public Object readResolve() {
        return VALUES[this.value];
    }
}
