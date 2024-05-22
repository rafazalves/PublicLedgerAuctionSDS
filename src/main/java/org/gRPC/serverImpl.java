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
    public void ping(ping request, StreamObserver<ping> responseObserver){
        //enviar resposta para a requisiçao grpc
        responseObserver.onNext(request);
        responseObserver.onCompleted();

    }

    @Override
    public void store(storeRequest request, StreamObserver<storeResponse> responseObserver){

        var key = new BigInteger(1, request.getNodeId().toByteArray());
        var valuetemp = request.getValue().toByteArray();
        var timestamptemp = request.getTimestamp();

        var Value = new StorageValue(new BigInteger(1, valuetemp), timestamptemp);
        boolean result = storageManager.addValue(key, Value);

        responseObserver.onNext(storeResponse
                                .newBuilder()
                        .setTimestamp(timestamptemp)
                        .setNodeId(nodeIDtemp)
                        .setResult(result)
                                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void findNode(target request, StreamObserver<FNodes> responseObserver){
        var nodeId_temp = request.getNodeId;
        var port_temp = request.getNodePublicPort;
        var nodeIp = request.getNodeIP;

        //ToDo: make the findClosestNodes in Node class
        var neighbors = routingTable.findClosest(nodeId_temp, Utils.K);
        ArrayList<Node> closestNodes = new ArrayList<Node>();

        for (var ntemp : neighbors) {
            closestNodes.add(ntemp);
        }
        for(Node closeNode : closestNodes) {
            final var clnode = FNodes.newBuilder()
                    .setNodeId(ByteString.copyFrom(closeNode.getNodeId()))
                    .setNodeIp(closeNode.getNodeIP())
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
        var key = new BigInteger(1, request.getKey().toByteArray());
        var value = storageManager.getValue(key);


        //encontrou o valor
        if(value != null){
            FValue valueRes = FValue.newBuilder()
                    .setFoundValue(true)
                    .setValue(ByteString.copyFrom(value))
                    .build();

            responseObserver.onNext(valueRes);

        }else{ // nao encontrou o valor -> comporta se como um findNode

            //ToDo: make the findClosestNodes in Node class
            var neighbors = routingTable.findClosest(nodeId_request, Utils.K);
            ArrayList<Node> closestNodes = new ArrayList<Node>();

            for (var ntemp : neighbors) {
                closestNodes.add(ntemp);
            }

            for(Node closeNode : closestNodes) {
                final var clnode = Fvalues.newBuilder()
                        .setFoundValue(false)
                        .setNodeId(ByteString.copyFrom(closeNode.getNodeId()))
                        .setNodeIp(closeNode.getNodeIP())
                        .setPort(closeNode.getNodePublicPort())
                        .setTimestamp(closeNode.getNodeTimestamp())
                        .build();

                responseObserver.onNext(clnode);
            }
        }

        responseObserver.onCompleted();
    }
}
