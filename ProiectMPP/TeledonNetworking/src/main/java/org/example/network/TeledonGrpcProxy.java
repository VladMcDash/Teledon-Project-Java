package org.example.network;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.grpc.*;
import org.example.grpc.TeledonProto.*;
import org.example.domain.*;
import org.example.services.ITeledonObserver;
import org.example.services.ITeledonServices;
import org.example.services.TeledonException;

import java.util.List;
import java.util.stream.Collectors;

public class TeledonGrpcProxy implements ITeledonServices {

    private TeledonServiceGrpc.TeledonServiceBlockingStub blockingStub;
    private TeledonServiceGrpc.TeledonServiceStub asyncStub;

    private ITeledonObserver clientObserver;
    private ManagedChannel channel;

    public TeledonGrpcProxy(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = TeledonServiceGrpc.newBlockingStub(channel);
        asyncStub = TeledonServiceGrpc.newStub(channel);
    }

    @Override
    public Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException {
        this.clientObserver = client;

        LoginRequest req = LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        asyncStub.login(req, new StreamObserver<Notification>() {
            @Override
            public void onNext(Notification value) {
                try {
                    if (value.getType() == Notification.Type.DONATION_ADDED) {
                        String[] parts = value.getMessage().split("\\|");
                        if (parts.length == 2) {
                            Long id = Long.parseLong(parts[0]);
                            Double totalAmount = Double.parseDouble(parts[1]);

                            CharityCase updatedCase = new CharityCase();
                            updatedCase.setId(id);
                            updatedCase.setTotalAmount(totalAmount);

                            clientObserver.donationAdded(updatedCase);
                        }
                    } else if (value.getType() == Notification.Type.DONOR_UPDATED) {
                        Donor updatedDonor = parseDonorNotification(value.getMessage());
                        if (updatedDonor != null) {
                            clientObserver.donorUpdated(updatedDonor);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Eroare la procesarea notificarii: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Eroare de la server (Stream): " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream-ul de notificări a fost închis de server.");
            }
        });

        return null;
    }

    @Override
    public void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException {
        UserRequest req = UserRequest.newBuilder()
                .setUsername(volunteer.getUsername())
                .build();
        blockingStub.logout(req);
        this.clientObserver = null;
    }

    @Override
    public List<CharityCase> getAllCases() throws TeledonException {
        Empty req = Empty.newBuilder().build();
        CaseListResponse response = blockingStub.getAllCases(req);

        // Mapăm din Proto list în Java list
        return response.getCasesList().stream().map(c -> {
            CharityCase cc = new CharityCase(c.getName(), c.getTotalDonated());
            cc.setId(c.getId());
            return cc;
        }).collect(Collectors.toList());
    }

    @Override
    public void addDonation(String name, String address, String phoneNumber, Long caseId, double amount) throws TeledonException {

        ProtoDonor pd = ProtoDonor.newBuilder()
                .setName(name != null ? name : "")
                .setAddress(address != null ? address : "")
                .setPhoneNumber(phoneNumber != null ? phoneNumber : "")
                .build();

        ProtoCharityCase pcc = ProtoCharityCase.newBuilder()
                .setId(caseId != null ? caseId : 0L)
                .build();

        DonationRequest req = DonationRequest.newBuilder()
                .setAmount(amount)
                .setDonor(pd)
                .setCharityCase(pcc)
                .build();

        blockingStub.addDonation(req);
    }

    @Override
    public List<Donor> searchDonors(String keyword) throws TeledonException {
        SearchDonorRequest req = SearchDonorRequest.newBuilder().setKeyword(keyword).build();
        DonorListResponse response = blockingStub.searchDonors(req);

        return response.getDonorsList().stream().map(d -> {
            Donor dn = new Donor(d.getName(), d.getAddress(), d.getPhoneNumber());
            dn.setId(d.getId());
            return dn;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateDonor(Long id, String name, String address, String phoneNumber) throws TeledonException {
        ProtoDonor pd = ProtoDonor.newBuilder()
                .setId(id)
                .setName(name != null ? name : "")
                .setAddress(address != null ? address : "")
                .setPhoneNumber(phoneNumber != null ? phoneNumber : "")
                .build();

        DonorRequest req = DonorRequest.newBuilder().setDonor(pd).build();
        blockingStub.updateDonor(req);
    }



    private static String buildFullName(String firstName, String lastName) {
        String safeFirst = firstName == null ? "" : firstName.trim();
        String safeLast = lastName == null ? "" : lastName.trim();
        if (safeFirst.isEmpty()) {
            return safeLast;
        }
        if (safeLast.isEmpty()) {
            return safeFirst;
        }
        return safeFirst + " " + safeLast;
    }

    private static CharityCase parseCaseNotification(String message) {
        if (message == null || message.trim().isEmpty()) {
            return null;
        }
        String[] parts = splitMessage(message);
        if (parts.length >= 3) {
            try {
                long id = Long.parseLong(parts[0].trim());
                String name = parts[1].trim();
                double total = Double.parseDouble(parts[2].trim());
                CharityCase charityCase = new CharityCase(name, total);
                charityCase.setId(id);
                return charityCase;
            } catch (NumberFormatException ignored) {
                // Fall through to try a simpler parse.
            }
        }
        try {
            long id = Long.parseLong(message.trim());
            CharityCase charityCase = new CharityCase("Refresh Required", 0.0);
            charityCase.setId(id);
            return charityCase;
        } catch (NumberFormatException ignored) {
            return new CharityCase(message.trim(), 0.0);
        }
    }

    private static Donor parseDonorNotification(String message) {
        if (message == null || message.trim().isEmpty()) {
            return null;
        }
        String[] parts = splitMessage(message);
        Donor donor = new Donor();
        if (parts.length >= 4) {
            try {
                donor.setId(Long.parseLong(parts[0].trim()));
            } catch (NumberFormatException ignored) {
            }
            donor.setName(parts[1].trim());
            donor.setAddress(parts[2].trim());
            donor.setPhoneNumber(parts[3].trim());
            return donor;
        }
        try {
            donor.setId(Long.parseLong(message.trim()));
            return donor;
        } catch (NumberFormatException ignored) {
            donor.setName(message.trim());
            return donor;
        }
    }

    private static String[] splitMessage(String message) {
        String[] parts = message.split("\\|");
        if (parts.length > 1) {
            return parts;
        }
        return message.split(",");
    }
}