package org.gRPC;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.Kademlia.Node;

import java.util.List;
import java.util.logging.Logger;

public class serverImpl extends ledgerServiceGrpc.ledgerServiceImplBase{
    private final Logger logger;
    private final Node node;
    public serverImpl(Logger logger, Node node) {
        this.logger = logger;
        this.node = node;
    }

    @Override
    public void ping(ping request, StreamObserver<ping> responseObserver){
        //enviar resposta para a requisiçao grpc
        responseObserver.onNext(request);
        responseObserver.onCompleted();

    }

    @Override
    public void store(storeRequest request, StreamObserver<storeResponse> responseObserver){

        var nodeIDtemp = request.getNodeId();
        var valuetemp = request.getValue();
        var timestamptemp = request.getTimestamp();

        //toDo: make the server efectevely store the information luis
        boolean result = ??

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
        List<Node> closestNodes = this.node.findClosestNodes(nodeId_temp);

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
        var nodeId_request = request.getNodeId;
        var port_temp = request.getNodePublicPort;
        var nodeIp = request.getNodeIP;

        //toDo: make the findValue in node class
        var valueResult = this.node.findValue(nodeId_request);

        //encontrou o valor
        if(valueResult != null){
            FValue valueRes = FValue.newBuilder()
                    .setFoundValue(true)
                    .setValue(ByteString.copyFrom(valueResult))
                    .build();

            responseObserver.onNext(valueRes);

        }else{ // nao encontrou o valor -> comporta se como um findNode

            //ToDo: make the findClosestNodes in Node class
            List<Node> closestNodes = this.node.findClosestNodes(nodeId_request);

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
