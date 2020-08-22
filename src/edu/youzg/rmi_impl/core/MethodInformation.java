package edu.youzg.rmi_impl.core;

import edu.youzg.util.TypeConverter;

import java.lang.reflect.Method;

/**
 * 记录 客户端调用方法 的“全部信息”
 */
public class MethodInformation {
    private String className;
    private String methodName;
    private String[] strParameterTypes;

    public MethodInformation() {
    }

    public MethodInformation(String methodInfo) {
        this.parseString(methodInfo);
    }

    /**
     * 获取 该方法的 参数类型数组
     *
     * @return 该方法的 参数类型数组
     * @throws ClassNotFoundException
     */
    private Class<?>[] getParameterTypes() throws ClassNotFoundException {
        int cnt = this.strParameterTypes.length;
        if (cnt <= 0) {
            return new Class<?>[]{};
        }
        Class<?>[] res = new Class<?>[cnt];
        for (int i = 0; i < cnt; i++) {
            res[i] = TypeConverter.toType(strParameterTypes[i]);
        }

        return res;
    }

    /**
     * 获取该方法 的 Method对象<br/>便于反射调用
     *
     * @param klass
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public Method getMethod(Class<?> klass)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<?>[] paraTypes = getParameterTypes();
        Method method = klass.getDeclaredMethod(methodName, paraTypes);
        return method;
    }

    /**
     * 从拆分出来的 String数组 中，挑出 方法的签名
     *
     * @param strs
     * @return
     */
    private String getMethodString(String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].endsWith(")")) {
                return strs[i];
            }
        }

        return null;
    }

    /**
     * 将整个参数字符串，分割成 一个个的 String类型参数 数组
     *
     * @param parameterTypesStr
     */
    private void parseParameterString(String parameterTypesStr) {
        if (parameterTypesStr.length() <= 0) {
            this.strParameterTypes = new String[]{}; // 使其不为null，便于后续步骤中当作参数使用
            return;
        }
        this.strParameterTypes = parameterTypesStr.split(",");
    }

    /**
     * 将给定的字符串 解析为 该变量的成员属性
     *
     * @param methodInfo
     * @return 该变量本身，便于链式调用
     */
    public MethodInformation parseString(String methodInfo) {
        // “方法字符串”的构成 —— 类全路径名.方法名(参数字符串)
        String[] strs = methodInfo.split(" ");

        String methodString = getMethodString(strs);
        int leftBracketIndex = methodString.indexOf("(");
        String methodFullName = methodString.substring(0, leftBracketIndex);

        int lastDotIndex = methodFullName.lastIndexOf(".");
        this.className = methodFullName.substring(0, lastDotIndex);
        this.methodName = methodFullName.substring(lastDotIndex + 1);

        String parameterTypesStr = methodString.substring(leftBracketIndex + 1, methodString.length() - 1);
        parseParameterString(parameterTypesStr);

        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String[] getStrParameterTypes() {
        return this.strParameterTypes;
    }

}