package com.jack.graphql.utils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StringUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_CHART = "UTF-8";


    public static Long timeStrToLong(String time) {
        String str = time.replaceAll(":", "");
        return toLong(str);
    }


    public static String filterNull(Object o) {
        return o != null && !"null".equals(o.toString()) ? o.toString().trim() : "";
    }


    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }
        if ("".equals(filterNull(o.toString()))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(Object o) {
        if (o == null) {
            return false;
        }
        if ("".equals(filterNull(o.toString()))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 是否可转化为数字
     *
     * @param o
     * @return
     */
    public static boolean isNumber(Object o) {
        try {
            new BigDecimal(o.toString());
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 是否可转化为Long型数字
     *
     * @param o
     * @return
     */
    public static boolean isLong(Object o) {
        try {
            new Long(o.toString());
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 转化为Long型数字, 不可转化时返回0
     *
     * @param o
     * @return
     */
    public static Long toLong(Object o) {
        if (isLong(o)) {
            return new Long(o.toString());
        } else {
            return 0L;
        }
    }

    /**
     * 转化为int型数字, 不可转化时返回0
     *
     * @param o
     * @return
     */
    public static int toInt(Object o) {
        if (isNumber(o)) {
            return new Integer(o.toString());
        } else {
            return 0;
        }
    }

    /**
     * 替换字符串,支持字符串为空的情形
     *
     * @param strData
     * @param regex
     * @param replacement
     * @return
     */
    public static String replace(String strData, String regex, String replacement) {
        return strData == null ? "" : strData.replaceAll(regex, replacement);
    }

    /**
     * 字符串转为HTML显示字符
     *
     * @param strData
     * @return
     */
    public static String string2HTML(String strData) {
        if (strData == null || "".equals(strData)) {
            return "";
        }
        strData = replace(strData, "&", "&amp;");
        strData = replace(strData, "<", "&lt;");
        strData = replace(strData, ">", "&gt;");
        strData = replace(strData, "\"", "&quot;");
        return strData;
    }

    /**
     * 从指定位置截取指定长度的字符串
     *
     * @param
     * @return
     */
    public static String getMiddleString(String input, int index, int count) {
        if (isEmpty(input)) {
            return "";
        }
        count = (count > input.length() - index + 1) ? input.length() - index + 1 : count;
        return input.substring(index - 1, index + count - 1);
    }

    /**
     * 将"/"替换成"\"
     *
     * @param strDir
     * @return
     */
    public static String changeDirection(String strDir) {
        String s = "/";
        String a = "\\";
        if (strDir != null && !" ".equals(strDir)) {
            if (strDir.contains(s)) {
                strDir = strDir.replace(s, a);
            }
        }
        return strDir;
    }

    /**
     * 去除字符串中 头和尾的空格，中间的空格保留
     *
     * @return String
     * @throws
     * @Title: trim
     * @Description: TODO
     */
    public static String trim(String s) {
        int i = s.length();// 字符串最后一个字符的位置
        int j = 0;// 字符串第一个字符
        int k = 0;// 中间变量
        char[] arrayOfChar = s.toCharArray();// 将字符串转换成字符数组
        while ((j < i) && (arrayOfChar[(k + j)] <= ' '))
            ++j;// 确定字符串前面的空格数
        while ((j < i) && (arrayOfChar[(k + i - 1)] <= ' '))
            --i;// 确定字符串后面的空格数
        return (((j > 0) || (i < s.length())) ? s.substring(j, i) : s);// 返回去除空格后的字符串
    }

    /**
     * 得到大括号中的内容
     *
     * @param str
     * @return
     */
    public static String getBrackets(String str) {
        int a = str.indexOf("{");
        int c = str.indexOf("}");
        if (a >= 0 && c >= 0 & c > a) {
            return (str.substring(a + 1, c));
        } else {
            return str;
        }
    }

    public static String extractBlank(String name) {
        if (name != null && !"".equals(name)) {
            return name.replaceAll(" +", "");
        } else {
            return name;
        }
    }

    public static String convertStr(String str) {
        return str != null && !"null".equals(str) ? str.trim() : "";
    }


    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }


    public static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }
        return string.toString();
    }


    public static String truncateUrlPage(String url) {
        if (url == null) {
            return null;
        }
        url = url.trim().toLowerCase();

        if (url.contains("?")) {
            String[] arrSplit = url.split("[?]");
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    return arrSplit[1];
                }
            }
        }
        return url;
    }


    public static Map<String, String> urlParam(String urlParamStr) {
        Map<String, String> map = new HashMap<String, String>();
        String strUrlParam = truncateUrlPage(urlParamStr);
        if (strUrlParam == null) {
            return map;
        }
        // 每个键值为一组
        String[] arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = strSplit.split("[=]");

            // 解析出键值
            if (arrSplitEqual.length > 1) {
                if (arrSplitEqual[1] != null && arrSplitEqual[1].contains("%")) {
                    // 正确解析
                    try {
                        map.put(arrSplitEqual[0], URLDecoder.decode(arrSplitEqual[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    // 正确解析
                    map.put(arrSplitEqual[0], trim(arrSplitEqual[1]));
                }
            } else {
                if (arrSplitEqual[0] != "") {
                    // 只有参数没有值，不加入
                    map.put(arrSplitEqual[0], "");
                }
            }
        }
        return map;
    }


    public static String join(Object[] array, String sep) {
        return join(array, sep, null);
    }


    public static String join(Collection<?> list, String sep) {
        return join(list, sep, null);
    }


    public static String join(Collection<?> list, String sep, String prefix) {
        Object[] array = list == null ? null : list.toArray();
        return join(array, sep, prefix);
    }


    public static String join(Object[] array, String sep, String prefix) {
        if (array == null) {
            return "";
        }

        int arraySize = array.length;

        if (arraySize == 0) {
            return "";
        }

        if (sep == null) {
            sep = "";
        }

        if (prefix == null) {
            prefix = "";
        }

        StringBuilder buf = new StringBuilder(prefix);
        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(sep);
            }
            buf.append(array[i] == null ? "" : array[i]);
        }
        return buf.toString();
    }


    public static String jsonJoin(String[] array) {
        int arraySize = array.length;
        int bufSize = arraySize * (array[0].length() + 3);
        StringBuilder buf = new StringBuilder(bufSize);
        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(',');
            }

            buf.append('"');
            buf.append(array[i]);
            buf.append('"');
        }
        return buf.toString();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || "".equals(s);
    }

    public static boolean inStringArray(String s, String[] array) {
        for (String x : array) {
            if (x.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static byte[] utf8Bytes(String data) {
        try {
            return data.getBytes(DEFAULT_CHART);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String utf8String(byte[] data) {
        try {
            return new String(data, DEFAULT_CHART);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isBlank(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBlankLoop(String... values) {
        int strLen;
        if (values == null || (strLen = values.length) == 0) {
            return true;
        }
        for (String value : values
        ) {
            if (value == null || (strLen = value.length()) == 0) {
                continue;
            }
            for (int i = 0; i < strLen; i++) {
                if ((Character.isWhitespace(value.charAt(i)) == false)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static boolean isNotBlankLoop(String... values) {
        return !isBlankLoop(values);
    }


    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }


    public static String getFirstUpper(String str) {
        String newStr = "";
        if (str.length() > 0) {
            newStr = str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
        }
        return newStr;
    }

}
