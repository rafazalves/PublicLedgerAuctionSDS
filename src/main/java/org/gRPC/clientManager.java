package org.gRPC;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.Kademlia.Node;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.Kademlia.KadNode;
import org.Kademlia.Storage.StorageValue;

public class clientManager {

    private static clientManager instance;

    private static final Logger logger = Logger.getLogger(serverSetUp.class.getName());
    ManagedChannel channel = null;

    ledgerServiceGrpc.ledgerServiceStub stub = null;




    public clientManager() {
        instance = this;
    }

    public static clientManager getInstance() {
        return instance;
    }
    private ManagedChannel generateConnection(Node n){

        return ManagedChannelBuilder
                .forAddress("127.0.0.1", n.getNodePublicPort() )
                .usePlaintext()
                .build();
    }

    public ledgerServiceGrpc.ledgerServiceStub newStub(Node n) throws IOException {
        channel = generateConnection(n);
        return ledgerServiceGrpc.newStub(channel);
    }

    public ledgerServiceGrpc.ledgerServiceFutureStub newFutureStub(Node n) throws IOException {
        channel = generateConnection(n);
        return ledgerServiceGrpc.newFutureStub(channel);
    }

    public void registerNode(Node n) {

        try {
            stub = this.newStub(n);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        NodeInfo nodeInfo = NodeInfo.newBuilder()
                .setNodeIP(n.getNodeIP())
                .setPort(n.getNodePublicPort())
                .build();

       stub.registerNode(nodeInfo, new StreamObserver<RegisterResponse>() {
            @Override
            public void onNext(RegisterResponse registerResponse) {

            }

            @Override
            public void onError(Throwable throwable) {
                logger.severe("Node registration failed");
            }

            @Override
            public void onCompleted() {
                logger.info("Node registered successfully");
            }
        });
    }

    public List<Node> getNodes(Node n) {
        try {
            stub = this.newStub(n);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NodeInfo nodeInfo = NodeInfo.newBuilder()
                .setNodeIP(n.getNodeIP())
                .setPort(n.getNodePublicPort())
                .build();

        List<Node> listaNodef = new LinkedList<>();
        stub.getNodes(nodeInfo, new StreamObserver<NodeList1>() {
            List<Node> nodeList = new LinkedList<>();
            @Override
            public void onNext(NodeList1 nodeList1) {
                for (NodeInfo info : nodeList1.getNodesList()) {
                    Node node = new Node((int) info.getPort(), (int) info.getNodeIP());
                    nodeList.add(node);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                logger.info("No nodes");
            }

            @Override
            public void onCompleted() {
                logger.info("Acabou");
                channel.shutdown();
            }
        });
        return listaNodef;
    }


    public void doPing(KadNode n, KadNode n1)  {
        logger.info("Will try to ping server with nodeId: " + Arrays.toString(n.getNode().getNodeId()));
        try {
            stub = this.newStub(n.getNode());
            stub.ping(pingP.newBuilder().setNodeId(ByteString.copyFrom(n.getNode().getNodeId())).build(),
                new StreamObserver<pingP>() {
                    @Override
                    public void onNext(pingP pingP) {
                        logger.info("Doing ping");
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
            try {
                n.printErrorHandle(n1.getNode());
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    //store value tem que ter a timestamp atualizada
    public void doStore(KadNode n, KadNode n1, StorageValue storeValue){
        logger.info("Will try to Store with nodeId: " + Arrays.toString(n.getNode().getNodeId()));

        try {
            stub = this.newStub(n.getNode());
        } catch (IOException e) {
           e.printStackTrace();
            try {
                n.printErrorHandle(n1.getNode()); //todo
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        }

        byte[] valueByte = storeValue.getValue().toByteArray();

        storeRequest storeRequest = org.gRPC.storeRequest.newBuilder()
                .setNodeId(ByteString.copyFrom(n.getNode().getNodeId()))
                .setValue(ByteString.copyFrom(valueByte))
                .setTimestamp(storeValue.getTimestamp())
                .setNodePublicPort(n.getNode().getNodePublicPort())
                .build();

        stub.store(storeRequest,
                new StreamObserver<storeResponse>() {
                    @Override
                    public void onNext(storeResponse storeResponse) {
                        logger.info("Doing store");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        try {
                            n.printErrorHandle(n1.getNode());
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }}

                    @Override
                    public void onCompleted() {
                        logger.info("store Completed");
                        try {
                            n.handleSeenNode(n1.getNode());
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    public void findNode(KadNode ourNode, Node destino ){
        logger.info("Trying to find Node");
        try {
            stub = this.newStub(ourNode.getNode());
        } catch (IOException e) {
            e.printStackTrace();
            try {
                ourNode.printErrorHandle(destino);
            } catch (NoSuchAlgorithmException i) {
                throw new RuntimeException(i);
            }
        }

        target targetNode = target.newBuilder()
                .setNodeId(ByteString.copyFrom(destino.getNodeId()))
                .setNodePublicPort(destino.getNodePublicPort())
                .setNodeIP(destino.getNodeIP())
                .build();

        LinkedList<Node> nodeList = new LinkedList<>();


        stub.findNode(targetNode,
                new StreamObserver<FNodes>() {
                    @Override
                    public void onNext(FNodes fNodes) {
                        ByteString nodeId = fNodes.getNodeId();
                        int nodeIp = Integer.parseInt(fNodes.getNodeIp());
                        int port = Math.toIntExact(fNodes.getPort());
                        long timestamp = fNodes.getTimestamp();

                        Node nn = new Node(port, nodeIp);

                        nodeList.add(nn);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        logger.info("Erro em Find Node");
                        try {
                            ourNode.printErrorHandle(destino);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (nodeList.isEmpty()){
                            logger.info("Nao encontrou nada");
                        }else {
                            logger.info("nodes encontrados");
                        }
                    }
                });

    }

    public void findValue(KadNode node, KadNode destinoNode, byte[] targetByte){
        BigInteger printnodeId = new BigInteger(1, node.getNode().getNodeId());
        logger.info("trying to Find the value for the node: " + printnodeId);


        try {
            stub = this.newStub(node.getNode());
        } catch (IOException e) {
            e.printStackTrace();
        }

         target targetValue = org.gRPC.target.newBuilder()
                .setNodeId(ByteString.copyFrom(node.getNode().getNodeId()))
                 .setNodePublicPort(node.getNode().getNodePublicPort())
                 .setNodeIP(node.getNode().getNodeIP())
                 .build();

        LinkedList<Node> ValueList = new LinkedList<>();

        stub.findValue(targetValue,
                new StreamObserver<FValues>() {
                    @Override
                    public void onNext(FValues fValues) {
                        ByteString nodeId = fValues.getNodeId();
                        int nodeIp = Integer.parseInt(fValues.getNodeIp());
                        int port = Math.toIntExact(fValues.getPort());
                        long timestamp = fValues.getTimestamp();

                        Node nn = new Node(port, nodeIp);

                        ValueList.add(nn);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        logger.info("Erro em Find Value");
                        try {
                            node.printErrorHandle(destinoNode.getNode());
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (ValueList.isEmpty()){
                            logger.info("Nao encontrou nada");
                        }else {
                            logger.info("nodes encontrados");
                        }
                    }
                });
    }

    public static void main(String[] args){

        logger.info("---------------------------------");
        logger.info("--          gRPC client        --");

        //ManagedChannel channel = getInstance().generateConnection();



    }
}
