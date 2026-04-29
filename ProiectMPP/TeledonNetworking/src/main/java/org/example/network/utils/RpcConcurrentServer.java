package org.example.network.utils;
import org.example.network.rpcprotocol.TeledonClientRpcWorker;
import org.example.services.ITeledonServices;
import java.net.Socket;

public class RpcConcurrentServer extends AbstractServer {
    private ITeledonServices teledonServer;

    public RpcConcurrentServer(int port, ITeledonServices teledonServer) {
        super(port);
        this.teledonServer = teledonServer;
    }

    @Override
    protected void processRequest(Socket client) {
        TeledonClientRpcWorker worker = new TeledonClientRpcWorker(teledonServer, client);
        Thread tw = new Thread(worker);
        tw.start();
    }
}