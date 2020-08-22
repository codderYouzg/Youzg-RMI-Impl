package edu.youzg.rmi_impl.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.alibaba.fastjson.JSON;

import edu.youzg.rmi_impl.exceptions.ConnectServerFailureException;
import edu.youzg.util.ArgumentMaker;
import edu.youzg.util.proxy.IMethodInvoker;

/**
 * RMI方法执行器
 */
public class RMIMethodInvoker implements IMethodInvoker {
    private String rmiServerIp;
    private int rmiServerPort;

    public RMIMethodInvoker() {
    }

    /**
         * 将所有参数，都转换为：<br/>以argi为键，<br/>参数值的json字符串 为值 <br/>的map 的json字符串
     *
     * @param args 目标 参数数组
     * @return 相应的 json字符串
     */
    private String getArgs(Object[] args) {
        if (args == null || args.length <= 0) {
            return "";
        }
        ArgumentMaker argumentMaker = new ArgumentMaker();
        for (int i = 0; i < args.length; i++) {
            argumentMaker.add("arg" + i, args[i]);
        }
        return argumentMaker.toString();
    }

    @Override
    public <T> T methodInvoke(Object object, Method method, Object[] args) {
        Socket socket = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        boolean ok = true;

        String ip = null;
        int port = 6666;

        try {
            ip = this.rmiServerIp;
            port = this.rmiServerPort;
            socket = new Socket(ip, port);
            SocketAddress addr = new InetSocketAddress(ip, port);
            /*
	                * 发送method 的字符串
	                * 发送参数数组的map化json
            **/
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(method.toString());
            dos.writeUTF(getArgs(args));

            /*
                       * 接收 结果json
            **/
            dis = new DataInputStream(socket.getInputStream());
            String resultStr = dis.readUTF();

            /*
                       * 转换结果为目标类型，并返回
             **/
            Type returnType = method.getGenericReturnType();
            
            T result = null;
            if (returnType.toString().equals("void")) {	// 若是void类型，gson无法转换，交由fastjson转换
            	result = JSON.parseObject(resultStr, returnType);
            } else {	// 若不是void类型，为了避免fastjson无法转换集合，交由gson转换
            	result = ArgumentMaker.gson.fromJson(resultStr, returnType);
            }
            
            return result;
        } catch (IOException e) {
           ok = false;
        } finally { //无论如何，释放资源
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!ok) {
        	try {
        		throw new ConnectServerFailureException("连接RMI服务器失败！");
        	} catch(ConnectServerFailureException e) {
        		e.printStackTrace();
        	}
        }

        return null;
    }

    public String getRmiServerIp() {
        return rmiServerIp;
    }

    public void setRmiServerIp(String rmiServerIp) {
        this.rmiServerIp = rmiServerIp;
    }

    public int getRmiServerPort() {
        return rmiServerPort;
    }

    public void setRmiServerPort(int rmiServerPort) {
        this.rmiServerPort = rmiServerPort;
    }

}

