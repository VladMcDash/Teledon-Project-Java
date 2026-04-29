package org.example.server;

import org.example.network.utils.AbstractServer;
import org.example.network.utils.RpcConcurrentServer;
import org.example.repository.*;
import org.example.services.ITeledonServices;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties loaded.");
        } catch (IOException e) {
            System.err.println("Cannot find server.properties " + e);
            return;
        }

        Properties dbProps = new Properties();
        try {
            dbProps.load(StartRpcServer.class.getResourceAsStream("/db.properties"));
            System.out.println("DB properties loaded.");
        } catch (IOException e) {
            System.err.println("Cannot find db.properties " + e);
            return;
        }

        VolunteerRepository volunteerRepo = new VolunteerJdbcRepository(dbProps);
        CharityCaseRepository caseRepo = new CharityCaseJdbcRepository(dbProps);
        DonorRepository donorRepo = new DonorJdbcRepository(dbProps);
        DonationRepository donationRepo = new DonationJdbcRepository(dbProps);

        ITeledonServices teledonServerImpl = new TeledonServicesImpl(volunteerRepo, donorRepo, caseRepo, donationRepo);

        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port").trim());
        } catch (NumberFormatException nef) {
            System.err.println("Wrong Port Number, using default 55555");
        }

        AbstractServer server = new RpcConcurrentServer(serverPort, teledonServerImpl);
        try {
            System.out.println("Pornim serverul pe portul: " + serverPort);
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }
    }
}

