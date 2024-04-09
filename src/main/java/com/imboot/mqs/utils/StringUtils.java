package com.imboot.mqs.utils;

import cn.hutool.core.text.StrFormatter;
import com.imboot.mqs.constants.Constants;
import org.springframework.util.AntPathMatcher;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 字符串工具类
 *
 * @author annos
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    static final String NULLSTR = "";

    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    public static String substring(final String str, int start) {
        if (str == null) {
            return NULLSTR;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }

        return str.substring(start);
    }

    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return NULLSTR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    public static String format(String template, Object... params) {
        if (isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    /**
     * 字符串转set
     *
     * @param str 字符串
     * @param sep 分隔符
     * @return set集合
     */
    public static final Set<String> str2Set(String str, String sep) {
        return new HashSet<String>(str2List(str, sep, true, false));
    }

    public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isEmpty(str)) {
            return list;
        }

        // 过滤空白字符串
        if (filterBlank && StringUtils.isBlank(str)) {
            return list;
        }
        String[] split = str.split(sep);
        for (String string : split) {
            if (filterBlank && StringUtils.isBlank(string)) {
                continue;
            }
            if (trim) {
                string = string.trim();
            }
            list.add(string);
        }

        return list;
    }

    public static boolean containsAny(Collection<String> collection, String... array) {
        if (isEmpty(collection) || isEmpty(array)) {
            return false;
        } else {
            for (String str : array) {
                if (collection.contains(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean containsAnyIgnoreCase(CharSequence cs, CharSequence... searchCharSequences) {
        if (isEmpty(cs) || isEmpty(searchCharSequences)) {
            return false;
        }
        for (CharSequence testStr : searchCharSequences) {
            if (containsIgnoreCase(cs, testStr)) {
                return true;
            }
        }
        return false;
    }


    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    public static boolean matches(String str, List<String> strs) {
        if (isEmpty(str) || isEmpty(strs)) {
            return false;
        }
        for (String pattern : strs) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static final String padl(final Number num, final int size) {
        return padl(num.toString(), size, '0');
    }

    public static final String padl(final String s, final int size, final char c) {
        final StringBuilder sb = new StringBuilder(size);
        if (s != null) {
            final int len = s.length();
            if (s.length() <= size) {
                for (int i = size - len; i > 0; i--) {
                    sb.append(c);
                }
                sb.append(s);
            } else {
                return s.substring(len - size, len);
            }
        } else {
            for (int i = size; i > 0; i--) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String format(Date date) {
        if (date == null) {
            return "";
        } else {
            return new SimpleDateFormat(Constants.DEFAULT_TIME_FORMAT).format(date);
        }
    }

    public static float parseFloat(String amount) {
        if(org.apache.commons.lang3.StringUtils.isBlank(amount)){
            return 0f;
        }
        return Float.parseFloat(amount);
    }


    public static String getString(Map<String, Object> params, String key){
        if(params == null || params.size() < 1){
            return null;
        }
        if(!params.containsKey(key)){
            return null;
        }
        return params.get(key).toString().trim();
    }

    public static long getLong(Map<String, Object> params, String key) {
        String v = getString(params,key);
        if(v != null) {
            return Long.parseLong(v.trim());
        } else {
            return 0;
        }
    }

    public static boolean getBool(Map<String, Object> params, String key) {
        String v = getString(params,key);
        return v != null && v.trim().equalsIgnoreCase("true");
    }


    public static int getInt(Map<String, Object> params, String key) {
        return getInt(params, key, 0);
    }
    public static int getInt(Map<String, Object> params, String key, int defaultVal) {
        String v = getString(params,key);
        if(v != null) {
            return Integer.parseInt(v.trim());
        } else {
            return defaultVal;
        }
    }

    public static int parseInt(Object o) {
        if(o == null){
            return 0;
        }
        try{
            return Integer.parseInt(o.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isPositive(String v) {
        if (v == null) {
            return false;
        } else {
            v = v.trim();
            if (v.equalsIgnoreCase("1")) {
                return true;
            } else {
                return v.equalsIgnoreCase("true");
            }
        }
    }

    public static String stackTrace(StackTraceElement[] stackTrace) {
        if (stackTrace != null && stackTrace.length >= 1) {
            StringBuilder sb = new StringBuilder("\n");
            StackTraceElement[] var2 = stackTrace;
            int var3 = stackTrace.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                StackTraceElement s = var2[var4];
                sb.append("\t").append(s).append("\n");
            }

            return sb.toString();
        } else {
            return null;
        }
    }
}