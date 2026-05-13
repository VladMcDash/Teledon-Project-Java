package org.example.network.rpcprotocol;

import org.example.domain.CharityCase;
import org.example.domain.Donor;
import org.example.domain.Volunteer;
import org.example.services.ITeledonObserver;
import org.example.services.ITeledonServices;
import org.example.services.TeledonException;
import org.example.network.rpcprotocol.Request;
import org.example.network.rpcprotocol.Response;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TeledonServerRpcProxy implements ITeledonServices {
    private String host;
    private int port;

    private ITeledonObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public TeledonServerRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }
    @Override
    public void updateDonor(Long id, String nume, String adresa, String telefon) throws TeledonException {
        Donor donor = new Donor(nume, adresa, telefon);
        donor.setId(id);

        Request req = new Request.Builder().type(RequestType.UPDATE_DONOR).data(donor).build();
        sendRequest(req);
        Response response = readResponse();

        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getData().toString());
        }
    }

    private void initializeConnection() throws TeledonException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (Exception e) {
            throw new TeledonException("Eroare la conectarea cu serverul: " + e);
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws TeledonException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (Exception e) {
            throw new TeledonException("Eroare la trimiterea request-ului: " + e);
        }
    }

    private Response readResponse() throws TeledonException {
        Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException {
        initializeConnection();
        Volunteer v = new Volunteer(username, password, "");
        Request req = new Request.Builder().type(RequestType.LOGIN).data(new Volunteer[]{v}).build();
        sendRequest(req);
        Response response = readResponse();

        if (response.getType() == ResponseType.OK) {
            this.client = client;
            return (Volunteer) response.getData();
        }
        if (response.getType() == ResponseType.ERROR) {
            closeConnection();
            throw new TeledonException(response.getData().toString());
        }
        return null;
    }

    @Override
    public void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException {
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(volunteer).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getData().toString());
        }
    }

    @Override
    public List<CharityCase> getAllCases() throws TeledonException {
        Request req = new Request.Builder().type(RequestType.GET_ALL_CASES).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getData().toString());
        }
        return (List<CharityCase>) response.getData();
    }

    @Override
    public List<Donor> searchDonors(String namePart) throws TeledonException {
        Request req = new Request.Builder().type(RequestType.SEARCH_DONORS).data(namePart).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getData().toString());
        }
        return (List<Donor>) response.getData();
    }

    @Override
    public void addDonation(String name, String address, String phone, Long caseId, double amount) throws TeledonException {
        Object[] args = new Object[]{name, address, phone, caseId, amount};
        Request req = new Request.Builder().type(RequestType.ADD_DONATION).data(args).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new TeledonException(response.getData().toString());
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    Response res = (Response) response;
                    if (res.getType() == ResponseType.UPDATE) {
                        CharityCase c = (CharityCase) res.getData();
                        System.out.println("Proxy primit update de la server!");
                        if (client != null) {
                            client.donationAdded(c);
                        } else {
                            System.out.println("EROARE: Referinta 'client' in Proxy este NULL!");
                        }
                            if (((Response) response).getType() == ResponseType.UPDATE_DONOR) {
                            Donor updatedDonor = (Donor) ((Response) response).getData();
                            try {
                                client.donorUpdated(updatedDonor);
                            } catch (TeledonException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            qresponses.put(res);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Eroare la citirea din retea " + e);
                }
            }
        }
    }
}