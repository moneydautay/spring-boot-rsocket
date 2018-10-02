package org.mvnsearch.spring.boot.rsocket.broker.loadbalance;

/**
 * RSocket Server 2
 *
 * @author linux_china
 */
public class Server2 extends BaseServer {
    public static void main(String[] args) throws Exception {
        new Server2().start();
        System.in.read();
    }

    @Override
    public int port() {
        return 22222;
    }

    @Override
    public String serverId() {
        return "server2";
    }
}
