package org.example.network.rpcprotocol;

import org.example.domain.CharityCase;
import org.example.domain.Donor;
import org.example.domain.Volunteer;
import org.example.services.ITeledonObserver;
import org.example.services.ITeledonServices;
import org.example.services.TeledonException;
import org.example.network.rpcprotocol.Request;
import org.example.network.rpcprotocol.Response;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class TeledonClientRpcWorker implements Runnable, ITeledonObserver {
    private ITeledonServices server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public TeledonClientRpcWorker(ITeledonServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                connected = false;
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        try {
            switch (request.getType()) {
                case LOGIN:
                    Volunteer[] logArgs = (Volunteer[]) request.getData();
                    Volunteer user = server.login(logArgs[0].getUsername(), logArgs[0].getPassword(), this);
                    return new Response.Builder().type(ResponseType.OK).data(user).build();
                case LOGOUT:
                    Volunteer vol = (Volunteer) request.getData();
                    server.logout(vol, this);
                    connected = false;
                    return new Response.Builder().type(ResponseType.OK).build();
                case GET_ALL_CASES:
                    List<CharityCase> cases = server.getAllCases();
                    return new Response.Builder().type(ResponseType.GET_ALL_CASES_RESPONSE).data(cases).build();
                case SEARCH_DONORS:
                    String namePart = (String) request.getData();
                    List<Donor> donors = server.searchDonors(namePart);
                    return new Response.Builder().type(ResponseType.SEARCH_DONORS_RESPONSE).data(donors).build();
                case ADD_DONATION:
                    Object[] donArgs = (Object[]) request.getData();
                    server.addDonation((String) donArgs[0], (String) donArgs[1], (String) donArgs[2], (Long) donArgs[3], (Double) donArgs[4]);
                    return new Response.Builder().type(ResponseType.OK).build();
                case UPDATE_DONOR:
                    Object[] updArgs = (Object[]) request.getData();
                    server.updateDonor((Long) updArgs[0], (String) updArgs[1], (String) updArgs[2], (String) updArgs[3]);
                    return new Response.Builder().type(ResponseType.OK).build();
            }
        } catch (TeledonException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException {
        output.writeObject(response);
        output.flush();
    }
    @Override
    public void donorUpdated(Donor updatedDonor) throws TeledonException {
        Response resp = new Response.Builder().type(ResponseType.UPDATE_DONOR).data(updatedDonor).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new TeledonException("Sending error: " + e);
        }
    }

    @Override
    public void donationAdded(CharityCase updatedCase) throws TeledonException {
        System.out.println("Trimit update prin socket...");
        Response resp = new Response.Builder().type(ResponseType.UPDATE).data(updatedCase).build();
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new TeledonException("Eroare la trimiterea update-ului: " + e);
        }
    }
}