package edu.youzg.rmi_impl.server;

import edu.youzg.util.PropertiesParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RMI服务器端
 */
public class RMIServer implements Runnable {
    private static final int CORE_THREAD_COUNT = 20;
    private static final int MAX_THREAD_COUNT = 180;
    private static final long ALIVE_TIME = 5000;

    private int rmiPort;
    private ServerSocket server;
    private volatile boolean startUp;
    private ThreadPoolExecutor threadPool;
    private int coreThreadCount;
    private int maxThreadCount;
    private long aliveTime;

    public RMIServer() {
        this.coreThreadCount = CORE_THREAD_COUNT;
        this.maxThreadCount = MAX_THREAD_COUNT;
        this.aliveTime = ALIVE_TIME;
    }

    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }

    public void setCoreThreadCount(int coreThreadCount) {
        this.coreThreadCount = coreThreadCount;
    }

    public void setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    public void setAliveTime(long aliveTime) {
        this.aliveTime = aliveTime;
    }

    public void initRmiServer(String configFilePath) {
        PropertiesParser.loadProperties(configFilePath);

        String rmiPortStr = PropertiesParser.value("rmiServerPort");
        if (rmiPortStr.length() > 0) {
            this.rmiPort = Integer.valueOf(rmiPortStr);
        }

        String poolCoreThreadCount = PropertiesParser.value("coreThreadCount");
        if (poolCoreThreadCount.length() > 0) {
            this.coreThreadCount = Integer.valueOf(poolCoreThreadCount);
        }

        String poolMaxThreadCount = PropertiesParser.value("maxThreadCount");
        if (poolMaxThreadCount.length() > 0) {
            this.maxThreadCount = Integer.valueOf(poolMaxThreadCount);
        }

        String poolAliveTime = PropertiesParser.value("aliveTime");
        if (poolAliveTime.length() > 0) {
            this.aliveTime = Integer.valueOf(poolAliveTime);
        }
    }

    public void startUp() {
        if (startUp) {
            return;
        }

        try {
            this.threadPool = new ThreadPoolExecutor(
                    coreThreadCount,
                    maxThreadCount,
                    aliveTime,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
            this.server = new ServerSocket(this.rmiPort);
            this.startUp = true;
            new Thread(this, "RMI Server").start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        this.startUp = false;
        if (this.server == null || this.server.isClosed()) {
            return;
        }

        try {
            this.server.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.server = null;
        }
    }

    public void shutdown() {
        if (!startUp) {
            return;
        }
        close();
        this.threadPool.shutdown();
    }

    @Override
    public void run() {
        while (startUp) {
            try {
                Socket client = this.server.accept();
                threadPool.execute(new RMIService(client));
            } catch (IOException e) {
                if (this.startUp == true) {
                    this.startUp = false;
                }
            }
        }
    }
}