package com.ynshun.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class TypeUtil {

	/**
	 * 将对象数据类型转换成指定数据类型
	 * @param value
	 * @param targetClass
	 * @return
	 * @createUser ynshun
	 * @createDate 2016年6月2日 下午12:37:41
	 * @lastUpdateDate
	 * @lastUpdateUser 
	 * @lastUpdateMemo
	 * @version 1.0
	 *
	 */
	public static Object transfer(Object value, String targetClass) {
		Class<?> type = getClassType(targetClass);
		return transfer(value, type);
	}

	/**
	 * 将对象数据类型转换成指定数据类型
	 * 
	 * @param value 数据值
	 * @param type  欲转换的数据类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T transfer(Object value, Class<T> type) {
		if (value == null || type == null) {
			return null;
		}
		String targetTypeStr = type.getName();
		String valueType = value.getClass().getName();

		String valueStr = value.toString();
		Object _value = null;

		try {
			if (targetTypeStr.equals(valueType)) {
				_value = value;
			} else if (targetTypeStr.equals("java.math.BigDecimal")) {
				_value = new BigDecimal(valueStr);
			} else if (targetTypeStr.equals("java.lang.Integer") || targetTypeStr.equals("int")) {
				int spotIdx = valueStr.indexOf(".");
				valueStr = spotIdx > 0 ? valueStr.substring(0, spotIdx) : valueStr;
				_value = Integer.parseInt(valueStr);
			} else if (targetTypeStr.equals("java.lang.String")) {
				_value = valueStr;
			} else {
				T a = newInstance(type, new Object[] { "0" });
				Method method = type.getMethod("valueOf", String.class);
				_value = method.invoke(a, valueStr);
			}
		} catch (Exception e) {
			System.err.println(String.format("%S --> %s", value, type.getClass().getName()));
			e.printStackTrace();
		}
		return (T) _value;
	}

	/**
	 * 调用待参数的构造器实例化对象
	 * 
	 * @param className
	 * @param args
	 * @return
	 * @throws Exception
	 * @createUser ynshun
	 * @createDate 2016年6月2日 上午11:54:22
	 * @lastUpdateDate
	 * @lastUpdateUser
	 * @lastUpdateMemo
	 * @version 1.0
	 *
	 */
	public static Object newInstance(String className, Object[] args) throws Exception {
		Class<?> newoneClass = Class.forName(className);
		Class<?>[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		Constructor<?> cons = newoneClass.getConstructor(argsClass);
		return cons.newInstance(args);
	}

	/**
	 * 调用待参数的构造器实例化对象
	 * 
	 * @param type
	 * @param args
	 * @return
	 * @throws Exception
	 * @createUser ynshun
	 * @createDate 2016年6月2日 上午11:56:16
	 * @lastUpdateDate
	 * @lastUpdateUser
	 * @lastUpdateMemo
	 * @version 1.0
	 *
	 */
	public static <T> T newInstance(Class<T> type, Object... args) throws Exception {
		Class<?>[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		Constructor<T> cons = type.getConstructor(argsClass);
		return cons.newInstance(args);
	}

	/**
	 * 根据指定Class路径实例化Class
	 * 
	 * @param fullClassPath
	 * @return
	 * @createUser ynshun
	 * @createDate 2016年5月31日 下午2:32:49
	 * @lastUpdateDate
	 * @lastUpdateUser
	 * @lastUpdateMemo
	 * @version 1.0
	 *
	 */
	public static Class<?> getClassType(String fullClassPath) {
		try {
			String typePrefix = "java.lang.";
			if (fullClassPath.indexOf(".") == -1) {
				fullClassPath = typePrefix + fullClassPath;
			}
			return Class.forName(fullClassPath);
		} catch (Exception e) {
			return null;
		}
	}

}
