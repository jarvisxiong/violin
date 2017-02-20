package com.wolf.test.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:zk的watch全面查看
 * <br/> Created on 2017/2/14 9:59
 *
 * @author 李超
 * @since 1.0.0
 */
public class AllZooKeeperWatcher implements Watcher {

    private static final Logger LOG = LoggerFactory.getLogger(AllZooKeeperWatcher.class);
    AtomicInteger seq = new AtomicInteger();
    private static final int SESSION_TIMEOUT = 10000;
    private static final String CONNECTION_STRING = "127.0.0.1:2181," + "127.0.0.1:2181," + "127.0.0.1:2181";
    private static final String ZK_PATH = "/nileader";
    private static final String CHILDREN_PATH = "/nileader/ch";
    private static final String LOG_PREFIX_OF_MAIN = "【Main】";

    private ZooKeeper zk = null;

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    /**
     * 创建ZK连接
     *
     * @param connectString  ZK服务器地址列表
     * @param sessionTimeout Session超时时间
     */
    public void createConnection(String connectString, int sessionTimeout) {
        this.releaseConnection();
        try {
            zk = new ZooKeeper(connectString, sessionTimeout, this);
            LOG.info(LOG_PREFIX_OF_MAIN + "开始连接ZK服务器");
            connectedSemaphore.await();
        } catch (Exception e) {
        }
    }

    /**
     * 关闭ZK连接
     */
    public void releaseConnection() {
        if(null != this.zk) {
            try {
                this.zk.close();
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 创建节点
     *
     * @param path 节点path
     * @param data 初始数据内容
     * @return
     */
    public boolean createPath(String path, String data) {
        try {
            this.zk.exists(path, true);
            LOG.info(LOG_PREFIX_OF_MAIN + "节点创建成功, Path: " + this.zk.create(path, //
            data.getBytes(), //
            ZooDefs.Ids.OPEN_ACL_UNSAFE, //
            CreateMode.PERSISTENT) + ", content: " + data);
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 读取指定节点数据内容
     *
     * @param path 节点path
     * @return
     */
    public String readData(String path, boolean needWatch) {
        try {
            return new String(this.zk.getData(path, needWatch, null));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 更新指定节点数据内容
     *
     * @param path 节点path
     * @param data 数据内容
     * @return
     */
    public boolean writeData(String path, String data) {
        try {
            LOG.info(LOG_PREFIX_OF_MAIN + "更新数据成功，path：" + path + ", stat: " + this.zk.setData(path, data.getBytes(), -1));
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 删除指定节点
     *
     * @param path 节点path
     */
    public void deleteNode(String path) {
        try {
            this.zk.delete(path, -1);
            LOG.info(LOG_PREFIX_OF_MAIN + "删除节点成功，path：" + path);
        } catch (Exception e) {
            //TODO
        }
    }

    /**
     * 删除指定节点
     *
     * @param path 节点path
     */
    public Stat exists(String path, boolean needWatch) {
        try {
            return this.zk.exists(path, needWatch);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取子节点
     *
     * @param path 节点path
     */
    private List<String> getChildren(String path, boolean needWatch) {
        try {
            return this.zk.getChildren(path, needWatch);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteAllTestPath() {
        this.deleteNode(CHILDREN_PATH);
        this.deleteNode(ZK_PATH);
    }


    public static void main(String[] args) throws InterruptedException {

        //PropertyConfigurator.configure("src/main/resources/log4j.properties");

        AllZooKeeperWatcher sample = new AllZooKeeperWatcher();
        sample.createConnection(CONNECTION_STRING, SESSION_TIMEOUT);
        //清理节点
        sample.deleteAllTestPath();
        if(sample.createPath(ZK_PATH, System.currentTimeMillis() + "")) {
            Thread.sleep(3000);
            //读取数据
            sample.readData(ZK_PATH, true);
            //读取子节点
            sample.getChildren(ZK_PATH, true);

            //更新数据
            sample.writeData(ZK_PATH, System.currentTimeMillis() + "");
            Thread.sleep(3000);
            //创建子节点
            sample.createPath(CHILDREN_PATH, System.currentTimeMillis() + "");
        }
        Thread.sleep(3000);
        //清理节点
        sample.deleteAllTestPath();
        Thread.sleep(3000);
        sample.releaseConnection();
    }


    /**
     * 收到来自Server的Watcher通知后的处理。
     */
    @Override
    public void process(WatchedEvent event) {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(null == event) {
            return;
        }
        // 连接状态
        Event.KeeperState keeperState = event.getState();
        // 事件类型
        Event.EventType eventType = event.getType();
        // 受影响的path
        String path = event.getPath();
        String logPrefix = "【Watcher-" + this.seq.incrementAndGet() + "】";

        LOG.info(logPrefix + "收到Watcher通知");
        LOG.info(logPrefix + "连接状态:\t" + keeperState.toString());
        LOG.info(logPrefix + "事件类型:\t" + eventType.toString());

        if(Event.KeeperState.SyncConnected == keeperState) {
            // 成功连接上ZK服务器
            if(Event.EventType.None == eventType) {
                LOG.info(logPrefix + "成功连接上ZK服务器");
                connectedSemaphore.countDown();
            } else if(Event.EventType.NodeCreated == eventType) {
                LOG.info(logPrefix + "节点创建");
                this.exists(path, true);
            } else if(Event.EventType.NodeDataChanged == eventType) {
                LOG.info(logPrefix + "节点数据更新");
                //todo 原来这里出发节点变更后又重新注册了下，用于下次的删除
                LOG.info(logPrefix + "数据内容: " + this.readData(ZK_PATH, true));
            } else if(Event.EventType.NodeChildrenChanged == eventType) {
                LOG.info(logPrefix + "子节点变更");
                LOG.info(logPrefix + "子节点列表：" + this.getChildren(ZK_PATH, true));
            } else if(Event.EventType.NodeDeleted == eventType) {
                LOG.info(logPrefix + "节点 " + path + " 被删除");
            }

        } else if(Event.KeeperState.Disconnected == keeperState) {
            LOG.info(logPrefix + "与ZK服务器断开连接");
        } else if(Event.KeeperState.AuthFailed == keeperState) {
            LOG.info(logPrefix + "权限检查失败");
        } else if(Event.KeeperState.Expired == keeperState) {
            LOG.info(logPrefix + "会话失效");
        }

        LOG.info("--------------------------------------------");

    }
}
