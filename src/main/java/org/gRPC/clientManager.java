package org.gRPC;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.Kademlia.Node;

import java.io.IOException;
import java.util.logging.Logger;

public class clientManager {

    private static clientManager instance;

    private static final Logger logger = Logger.getLogger(serverSetUp.class.getName());


    public clientManager() {
        instance = this;
    }

    public static clientManager getInstance() {
        return instance;
    }
    private ManagedChannel generateConnection(Node n){
        return ManagedChannelBuilder
                .forAddress("localhost", n.getNodePublicPort() )
                .usePlaintext()
                .build();
    }

    public ledgerServiceGrpc.ledgerServiceStub newStub(Node n) throws IOException {
        final var connection = generateConnection(n);
        return ledgerServiceGrpc.newStub(connection);
    }

    public ledgerServiceGrpc.ledgerServiceFutureStub newFutureStub(Node n) throws IOException {
        final var connection = generateConnection(n);

        return ledgerServiceGrpc.newFutureStub(connection);
    }

    public static void main(String[] args){

        logger.info("---------------------------------");
        logger.info("--          gRPC client        --");

        //ManagedChannel channel = getInstance().generateConnection();



    }
}
