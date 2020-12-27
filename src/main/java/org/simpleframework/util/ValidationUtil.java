package org.simpleframework.util;

import java.util.Collection;
import java.util.Map;

public class ValidationUtil {
    /**
     * Collection是否为null或size为0
     *
     * @param obj Collection
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> obj){
        return obj == null || obj.isEmpty();
    }
    /**
     * String是否为null或""
     *
     * @param obj Collection
     * @return 是否为空
     */
    public static boolean isEmpty(String obj){
        return (obj == null || "".equals(obj));
    }
    /**
     * Map是否为null或size为0
     *
     * @param obj Collection
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?,?> obj){
        return obj == null || obj.isEmpty();
    }
    /**
     * Array是否为null或length为0
     *
     * @param obj Collection
     * @return 是否为空
     */
    public static boolean isEmpty(Object[] obj){
        return obj == null || obj.length == 0;
    }
}
