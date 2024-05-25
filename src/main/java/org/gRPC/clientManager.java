package org.gRPC;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.Kademlia.Node;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;
import org.Kademlia.KadNode;

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
        final ManagedChannel connection = generateConnection(n);
        return ledgerServiceGrpc.newStub(connection);
    }

    public ledgerServiceGrpc.ledgerServiceFutureStub newFutureStub(Node n) throws IOException {
        final ManagedChannel connection = generateConnection(n);
        return ledgerServiceGrpc.newFutureStub(connection);
    }

    public void doPing(KadNode n, KadNode n1)  {
        logger.info("Will try to ping server with nodeId: " + Arrays.toString(n.getNode().getNodeId()));
        ledgerServiceGrpc.ledgerServiceStub stub = null;
        try {
            stub = this.newStub(n.getNode());
        stub.ping(pingP.newBuilder().setNodeId(ByteString.copyFrom(n.getNode().getNodeId())).build(),
                new StreamObserver<pingP>() {
                    @Override
                    public void onNext(pingP pingP) {
                        logger.info("ping");
                        try {
                            n.handleSeenNode(n1.getNode());
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        try {
                            n.printErrorHandle(n1.getNode());
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        logger.info("ping Completed");

                    }
                }
        );
        } catch (IOException e) {
            e.printStackTrace();
            //todo
            try {
                n.printErrorHandle(n1.getNode());
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    public void doStore(Node n){

    }

    public static void main(String[] args){

        logger.info("---------------------------------");
        logger.info("--          gRPC client        --");

        //ManagedChannel channel = getInstance().generateConnection();



    }
}
