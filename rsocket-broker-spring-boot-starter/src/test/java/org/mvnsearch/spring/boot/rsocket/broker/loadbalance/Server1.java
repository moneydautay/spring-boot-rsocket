package org.mvnsearch.spring.boot.rsocket.broker.loadbalance;

/**
 * RSocket Server 1
 *
 * @author linux_china
 */
public class Server1 extends BaseServer {

    public static void main(String[] args) throws Exception {
        (new Server1()).start();
        System.in.read();
    }

    @Override
    public int port() {
        return 11111;
    }

    @Override
    public String serverId() {
        return "server1";
    }
}
