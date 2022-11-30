package com.mm.freedom.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * 字符串工具类
 * <p>
 * see at: https://github.com/GangJust/AndroidUtils
 */
public class GTextUtils {

    private GTextUtils() {
        ///
    }

    //英文字母表
    public static final String alphabets = "abcdefghijklmnopqrstuvwxyz";

    //字母、数字、符号表
    public static final String charTable = "abcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_+[]{}:'\"\\|,<.>/?";

    //默认随机数种子
    public static final Random defaultRandom = new Random();

    /**
     * NULL字符串对象判断
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean isNull(S text) {
        return text == null;
    }

    public static <S extends CharSequence> boolean isNotNull(S text) {
        return text != null;
    }

    /**
     * 空字符串判断
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean isEmpty(S text) {
        return isNull(text) || isEmpty(text.toString());
    }

    public static boolean isEmpty(String value) {
        return isNull(value) || value.trim().isEmpty() || value.trim().equals("null");
    }

    /**
     * 空字符串数组判断
     *
     * @param texts
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean isEmpties(S... texts) {
        if (texts.length == 0) return true;

        String[] strings = new String[texts.length];
        for (int i = 0; i < texts.length; i++) {
            strings[i] = texts[i].toString();
        }

        return isEmpties(strings);
    }

    public static boolean isEmpties(String... values) {
        if (values.length == 0) return false;

        ArrayList<Boolean> booleans = new ArrayList<>(); //Boolean列表, 当出现 false 后, 则表示这并不是一个空的字符数组
        for (String value : values) {
            booleans.add(isEmpty(value));
        }

        return !(booleans.contains(false)); //当不包含false, 表示整个数组都是空的
    }

    /**
     * 非空字符串判断
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean isNotEmpty(S text) {
        return !isEmpty(text);
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * 非空字符串数组判断
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean isNotEmpties(S... text) {
        return !isEmpties(text);
    }

    public static boolean isNotEmpties(String... values) {
        return !isEmpties(values);
    }

    /**
     * 判断某个字符串是否是全空白
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean isSpace(S text) {
        if (isEmpty(text)) return true;
        return isSpace(text.toString());
    }

    public static boolean isSpace(String value) {
        if (isEmpty(value)) return true;

        char[] chars = value.toCharArray();
        ArrayList<Boolean> booleans = new ArrayList<>();
        for (char c : chars) {
            booleans.add(Character.isWhitespace(c));
        }

        return !(booleans.contains(false)); //不包含false
    }

    /**
     * 字符完全包含, 与给定字符串数组做匹配比较, 如果每项都包含, 则表示完全包含.
     *
     * @param text
     * @param comps
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean contains(S text, String... comps) {
        if (isNull(text)) return false;
        return contains(text.toString(), comps);
    }

    public static boolean contains(String value, String... comps) {
        if (value == null || comps == null || comps.length == 0) return false;

        ArrayList<Boolean> booleans = new ArrayList<>(); //Boolean列表, 当出现 false 后, 则表示这并不是完全匹配
        for (String comp : comps) {
            booleans.add(value.contains(comp));
        }

        return !(booleans.contains(false)); //不包含false, 表示全部匹配
    }

    /**
     * 字符任意包含, 与给定字符串数组做匹配比较, 如果其中一项包含, 这表示匹配成功
     *
     * @param text
     * @param comps
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean anyContains(S text, String... comps) {
        if (isNull(text)) return false;
        return anyContains(text.toString(), comps);
    }

    public static boolean anyContains(String value, String... comps) {
        if (value == null || comps == null || comps.length == 0) return false;
        for (String comp : comps) {
            if (value.contains(comp)) return true;
        }
        return false;
    }

    /**
     * 字符比较, 与给定字符串数组做比较, 如果有任意一项匹配, 都表示匹配成功.
     *
     * @param value
     * @param equals
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> boolean anyEquals(S value, String... equals) {
        if (value == null) return false;
        return anyEquals(value.toString(), equals);
    }

    public static boolean anyEquals(String value, String... equals) {
        if (value == null || equals == null || equals.length == 0) return false;
        for (String e : equals) {
            if (value.equals(e)) return true;
        }
        return false;
    }

    /**
     * 去掉字符串前后的空白字符
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> String to(S text) {
        if (isEmpty(text)) return "";
        return to(text.toString());
    }

    public static String to(String value) {
        if (isEmpty(value)) return "";
        return value.trim();
    }

    /**
     * 去掉字符串中的所有空白字符
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> String toAll(S text) {
        if (isEmpty(text)) return "";
        return toAll(text.toString());
    }

    public static String toAll(String value) {
        if (isEmpty(value)) return "";
        return value.trim().replaceAll("\\s", "");
    }


    /**
     * 如果某个字符为空, 则返回给定默认字符串
     *
     * @param maybeNullValue
     * @param defaultValue
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> String get(S maybeNullValue, String defaultValue) {
        if (maybeNullValue == null) return defaultValue;
        return get(maybeNullValue.toString(), defaultValue);
    }

    public static String get(String maybeNullValue, String defaultValue) {
        if (isEmpty(maybeNullValue)) return defaultValue;
        return maybeNullValue;
    }

    /**
     * 如果某个对象为空, 则返回给定的默认字符串, 否则调用它的toString()
     *
     * @param maybeNull
     * @param defaultValue
     * @return
     */
    public static String get(Object maybeNull, String defaultValue) {
        if (maybeNull == null) return defaultValue;
        return maybeNull.toString();
    }

    /**
     * 截取某字符串前面的所有内容, 成功返回操作后的值, 失败返回它本身
     *
     * @param value
     * @param target
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> String front(S value, String target) {
        if (value == null) return "";
        return front(value.toString(), target);
    }

    public static String front(String value, String target) {
        if (isEmpty(value) || isEmpty(target)) return "";
        if (!value.contains(target)) return value;
        return value.substring(0, value.indexOf(target));
    }

    /**
     * 截取某字符串后面的所有内容, 成功返回操作后的值, 失败返回它本身
     *
     * @param value
     * @param target
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> String after(S value, String target) {
        if (value == null) return "";
        return after(value.toString(), target);
    }

    public static String after(String value, String target) {
        if (isEmpty(value) || isEmpty(target)) return "";
        if (!value.contains(target)) return value;
        return value.substring(value.indexOf(target) + target.length());
    }

    /**
     * 截取某字符串中间的内容, 成功返回操作后的值, 失败返回它本身
     *
     * @param value
     * @param start
     * @param end
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> String middle(S value, String start, String end) {
        if (value == null) return "";
        return middle(value.toString(), start, end);
    }

    public static String middle(String value, String start, String end) {
        if (isEmpty(value) || isEmpty(start) || isEmpty(end)) return value;

        //如果前置包含, 则截取后段
        if (value.contains(start)) {
            value = value.substring(value.indexOf(start) + start.length());
        }
        //如果后置包含, 则截取前段
        if (value.contains(end)) {
            value = value.substring(0, value.indexOf(end));
        }
        return value;
    }

    /**
     * 统计某个字符串中目标子串出现的次数
     *
     * @param text   字符转
     * @param target 目标子串
     * @param <S>
     * @return 出现次数
     */
    public static <S extends CharSequence> int count(S text, String target) {
        if (isNull(text)) return 0;
        return count(text.toString(), target);
    }

    public static int count(String value, String target) {
        if (isEmpty(value) || isEmpty(target)) return 0;
        if (!value.contains(target)) return 0;
        int count = 0;
        while (true) {
            int indexOf = value.indexOf(target);
            if (indexOf == -1) return count;
            value = value.substring(indexOf + target.length());
            count++;
        }
    }

    /**
     * 字符转整型
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> Integer toInt(S text) {
        if (isNull(text)) return 0;
        return toInt(text.toString());
    }

    public static Integer toInt(String str) {
        if (isEmpty(str)) return 0;
        return Integer.parseInt(str, 10);
    }

    public static <S extends CharSequence> Integer toInt(S text, int radix) {
        if (isNull(text)) return 0;
        return toInt(text.toString(), radix);
    }

    public static Integer toInt(String str, int radix) {
        if (isEmpty(str)) return 0;
        try {
            return Integer.parseInt(str, radix);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 字符转单精度小数
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> Float toFloat(S text) {
        if (isNull(text)) return 0.0f;
        return toFloat(text.toString());
    }

    public static Float toFloat(String str) {
        if (isEmpty(str)) return 0.0f;
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    /**
     * 字符双精度小数
     *
     * @param text
     * @param <S>
     * @return
     */
    public static <S extends CharSequence> Double toDouble(S text) {
        if (isNull(text)) return 0.0;
        return toDouble(text.toString());
    }

    public static Double toDouble(String str) {
        if (isEmpty(str)) return 0.0;
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ------------More------------ //

    /**
     * 将一个字符串数组拼接成一个字符串, 用指定文本分割, 若操作失败, 则返回 "" 字符串
     *
     * @param strings ["a", "b", "s"]
     * @param s       `!`
     * @return "a!b!s"
     */
    public static String splicing(String[] strings, String s) {
        return splicing(new ArrayList<>(Arrays.asList(strings)), s);
    }

    public static String splicing(Collection<String> strings, String s) {
        if (strings == null || strings.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string).append(s);
        }
        int start = builder.length() - s.length();
        int end = builder.length();
        builder.delete(start, end);
        return builder.toString();
    }

    /**
     * 某个字符串不足最小指定长度, 左填充指定字符
     *
     * @param value     "Hello"
     * @param minLength 8
     * @param pad       '0'
     * @return "000Hello"
     */
    public static String padLeft(String value, int minLength, char pad) {
        if (minLength <= 0)
            throw new IllegalArgumentException("need: `minLength > 0`, current: minLength = " + minLength);
        if (value.length() >= minLength) return value;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < minLength - value.length(); i++) builder.append(pad);
        builder.append(value);
        return builder.toString();
    }

    /**
     * 某个字符串不足最小指定长度, 右填充指定字符
     *
     * @param value     "Hello"
     * @param minLength 8
     * @param pad       '0'
     * @return "Hello000"
     */
    public static String padRight(String value, int minLength, char pad) {
        if (minLength <= 0)
            throw new IllegalArgumentException("need: `minLength > 0`, current: minLength = " + minLength);
        if (value.length() >= minLength) return value;
        StringBuilder builder = new StringBuilder();
        builder.append(value);
        for (int i = 0; i < minLength - value.length(); i++) builder.append(pad);
        return builder.toString();
    }

    /**
     * 获取随机符号表组合
     *
     * @return 从符号表中随机返回任意长度的乱序字符串
     */
    public static String randomCharTable() {
        return randomCharTable(defaultRandom);
    }

    public static String randomCharTable(Random random) {
        int length = random.nextInt(charTable.length());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            //随机大小写
            char c = charTable.charAt(i);
            if (random.nextInt() % random.nextInt() != 0) {
                builder.append(c);
            } else {
                if (c >= 'a' && c <= 'z') {
                    builder.append((char) (c - 32));
                } else {
                    builder.append(c);
                }
            }
        }
        return builder.toString();
    }

    /**
     * 从指定样本文本中, 获取随机文本
     *
     * @param sample "abcdef"
     * @param length 3
     * @return "aba" or "abd" or ....
     */
    public static String randomText(String sample, int length) {
        return randomText(sample, length, defaultRandom);
    }

    public static String randomText(String sample, int length, Random random) {
        StringBuilder builder = new StringBuilder();
        char[] chars = sample.toCharArray();
        for (int i = 0; i < length; i++) {
            builder.append(chars[random.nextInt(chars.length)]);
        }
        return builder.toString();
    }

    /**
     * 字符串数组随机获取
     *
     * @param strings "abcd"
     * @return 随机从 abcd 四个字母中返回随机的字母
     */
    public static String random(String[] strings) {
        return random(strings, defaultRandom);
    }

    public static String random(String[] strings, Random random) {
        return strings[random.nextInt(strings.length)];
    }


    /**
     * unicode编码
     *
     * @param unicode "中国"
     * @return "\u4e2d\u56fd"
     */
    public static String enUnicode(String unicode) {
        StringBuilder builder = new StringBuilder();
        char[] chars = unicode.toCharArray();
        for (char c : chars) {
            String hexString = Integer.toHexString(c);
            hexString = "\\u" + padLeft(hexString, 4, '0');
            builder.append(hexString);
        }
        return builder.toString();
    }

    /**
     * unicode解码
     *
     * @param unicode "\u4e2d\u56fd"
     * @return "中国"
     */
    public static String deUnicode(String unicode) {
        StringBuilder builder = new StringBuilder();
        String[] split = unicode.split("\\\\u");
        for (int i = 1; i < split.length; i++) {
            String target = padLeft(split[i], 4, '0');
            builder.append((char) Integer.parseInt(target, 16));
        }
        return builder.toString();
    }
}