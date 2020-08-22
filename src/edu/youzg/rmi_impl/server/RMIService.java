package edu.youzg.rmi_impl.server;

import com.alibaba.fastjson.JSON;
import edu.youzg.rmi_impl.core.MethodInformation;
import edu.youzg.rmi_impl.core.RMIDefinition;
import edu.youzg.rmi_impl.core.RMIFactory;
import edu.youzg.util.ArgumentMaker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Socket;

public class RMIService implements Runnable {
    private Socket socket;

    public RMIService(Socket socket) {
        this.socket = socket;
    }

    /**
         * 解析字符串，获取 参数的值
     * @param method
     * @param argString
     * @return
     */
    private Object[] getParameterValues(Method method, String argString) {
        Object[] res = null;

        Parameter[] parameters = method.getParameters();
        int cnt = parameters.length;

        if (cnt <= 0) {
            return new Object[] {};
        }
        res = new Object[cnt];

        ArgumentMaker argumentMaker = new ArgumentMaker(argString);
        for (int i = 0; i < cnt; i++) {
            res[i] = argumentMaker.getValue("arg" + i, parameters[i].getParameterizedType());	// getParameterizedType 带泛型
        }

        return res;
    }

    /**
         * 调用该方法
     * @param methodInfo
     * @param argStr
     * @return
     */
    private Object invokeMethod(String methodInfo, String argStr) {
        MethodInformation methodInfomation = new MethodInformation()
                .parseString(methodInfo);
        String interfaceName = methodInfomation.getClassName(); // RMI客户端传过来的参数 是 接口名称
        RMIDefinition rmiDefinition = RMIFactory.getRmiDefinition(interfaceName);
        Class<?> klass = rmiDefinition.getKlass();
        Object object = rmiDefinition.getObject();

        Object result = null;
        try {
            Method method = methodInfomation.getMethod(klass);
            Object[] values = getParameterValues(method, argStr);
            result = method.invoke(object, values);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(this.socket.getInputStream());
            String methodInfo = dis.readUTF();
            String argStr = dis.readUTF();

            DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
            Object result = null;
            result = invokeMethod(methodInfo, argStr);
            
            dos.writeUTF(JSON.toJSONString(result));

            dis.close();
            dos.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}