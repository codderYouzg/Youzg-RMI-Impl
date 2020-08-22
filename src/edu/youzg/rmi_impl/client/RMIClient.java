package edu.youzg.rmi_impl.client;

import edu.youzg.util.PropertiesParser;
import edu.youzg.util.proxy.ProxyBuilder;

/**
 * RMI客户端
 */
public class RMIClient {
    private RMIMethodInvoker methodInvoker;

    public RMIClient() {
        this.methodInvoker
                = new RMIMethodInvoker();
    }

    public RMIClient(RMIMethodInvoker methodInvoker) {
        this.methodInvoker = methodInvoker;
    }

    public void initRmiClient(String configFilePath) {
        PropertiesParser.loadProperties(configFilePath);
        String rmiPortStr = PropertiesParser.value("rmiServerPort");
        String rmiIpStr = PropertiesParser.value("rmiServerIp");
        if (rmiIpStr.length() > 0) {
            this.methodInvoker.setRmiServerIp(rmiIpStr);
        }
        if (rmiPortStr.length() > 0) {
            this.methodInvoker.setRmiServerPort(Integer.valueOf(rmiPortStr));
        }
    }

    public void setRmiServerIp(String rmiServerIp) {
        this.methodInvoker.setRmiServerIp(rmiServerIp);
    }

    public void setRmiServerPort(int rmiServerPort) {
        this.methodInvoker.setRmiServerPort(Integer.valueOf(rmiServerPort));
    }

    // 由于在“RMI机制”中，无论是jdk代理，还是cglib代理，都可以使用
    // 为了省去导jar包的麻烦，以及效率问题，本人在此处，就使用jdk提供的代理
    /**
         * 获取代理对象
     * @param klass
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<?> klass) {
        ProxyBuilder builder = new ProxyBuilder();
        builder.setMethodInvoker(this.methodInvoker);
        return builder.creatProxy(klass);
    }

}