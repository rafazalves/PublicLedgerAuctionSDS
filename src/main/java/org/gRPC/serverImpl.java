package org.gRPC;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.Kademlia.Node;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.Kademlia.RoutingTable.RoutingTable;
import org.Kademlia.Storage.StorageValue;
import org.Kademlia.Storage.StorageManager;
import org.Kademlia.utils.Utils;
import org.Kademlia.Node;



public class serverImpl extends ledgerServiceGrpc.ledgerServiceImplBase{
    private final Logger logger;
    private final Node node;

    private final  RoutingTable routingTable;
    private final  StorageManager storageManager;
    public serverImpl(Logger logger, Node node) {
        this.logger = logger;
        this.node = node;
        routingTable = new RoutingTable(node);
        routingTable.add(node);
        storageManager = new StorageManager();

    }

    @Override
    public void ping(pingP request, StreamObserver<pingP> responseObserver){
        //enviar resposta para a requisiçao grpc
        responseObserver.onNext(request);
        responseObserver.onCompleted();

    }

    @Override
    public void store(storeRequest request, StreamObserver<storeResponse> responseObserver){

        BigInteger key = new BigInteger(1, request.getNodeId().toByteArray());
        byte[] valuetemp = request.getValue().toByteArray();
        long timestamptemp = request.getTimestamp();
        long porttemp= request.getNodePublicPort();


        StorageValue Value = new StorageValue(new BigInteger(1, valuetemp), timestamptemp);
        boolean result = storageManager.addValue(key, Value);

        responseObserver.onNext(storeResponse
                                .newBuilder()
                        .setTimestamp(timestamptemp)
                        .setNodeId(request.getNodeId())
                        .setResult(result)
                                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void findNode(target request, StreamObserver<FNodes> responseObserver){


        List<Node> neighbors = routingTable.findClosest(request.getNodeId().toByteArray(), Utils.K);
        ArrayList<Node> closestNodes = new ArrayList<>();

        for (Node ntemp : neighbors) {
            closestNodes.add(ntemp);
        }
        for(Node closeNode : closestNodes) {
            final FNodes clnode = FNodes.newBuilder()
                    .setNodeId(ByteString.copyFrom(closeNode.getNodeId()))
                    .setNodeIp(Integer.toString(closeNode.getNodeIP()))
                    .setPort(closeNode.getNodePublicPort())
                    .setTimestamp(closeNode.getNodeTimestamp())
                    .build();

            responseObserver.onNext(clnode);
        }

        responseObserver.onCompleted();

    }

    // Find the value store in the node
    // or return the closest node to the targetID
    @Override
    public void findValue(target request, StreamObserver<FValues> responseObserver){
        BigInteger key = new BigInteger(1, request.getNodeId().toByteArray());
        byte[] keyByte = request.getNodeId().toByteArray();
        StorageValue value = storageManager.getValue(key);

        byte[] valueByteArray = value.getValue().toByteArray();
        long timestamp = value.getTimestamp();


        //encontrou o valor
        if(value != null){
            FValues valueRes = FValues.newBuilder()
                    .setFoundValue(true)
                    .setValue(ByteString.copyFrom(valueByteArray))
                    .setTimestamp(timestamp)
                    .build();

            responseObserver.onNext(valueRes);

        }else{ // nao encontrou o valor -> comporta se como um findNode


            List<Node> neighbors = routingTable.findClosest(keyByte, Utils.K);
            ArrayList<Node> closestNodes = new ArrayList<Node>();

            for (Node ntemp : neighbors) {
                closestNodes.add(ntemp);
            }

            for(Node closeNode : closestNodes) {
                final FValues clnode = FValues.newBuilder()
                        .setFoundValue(false)
                        .setNodeId(ByteString.copyFrom(closeNode.getNodeId()))
                        .setNodeIp(String.valueOf(closeNode.getNodeIP()))
                        .setPort(closeNode.getNodePublicPort())
                        .setTimestamp(closeNode.getNodeTimestamp())
                        .build();

                responseObserver.onNext(clnode);
            }
        }

        responseObserver.onCompleted();
    }
}
