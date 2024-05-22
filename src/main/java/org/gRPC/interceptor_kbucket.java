package org.gRPC;

import io.grpc.*;
import org.Kademlia.Node;
import org.checkerframework.checker.units.qual.N;

import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


public class interceptor_kbucket implements ServerInterceptor {
    private final Node node;

    public interceptor_kbucket(Node n) {
        this.node = n;
    }

    // Extracts the client IP address as an integer
    public static int extractClientAddressAsInt(SocketAddress remoteAddr) {
        if (remoteAddr instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddr;
            InetAddress inetAddress = inetSocketAddress.getAddress();
            return ipToInt(inetAddress);
        }
        return -1; // or throw an exception, or handle it as you see fit
    }

    // Converts IP address to its integer representation
    public static int ipToInt(InetAddress inetAddress) {
        byte[] addressBytes = inetAddress.getAddress();
        ByteBuffer buffer = ByteBuffer.wrap(addressBytes);
        if (addressBytes.length == 4) { // IPv4
            return buffer.getInt();
        } else if (addressBytes.length == 16) { // IPv6
            // IPv6 is more complex and doesn't fit into a single integer
            // Here we return only the first 4 bytes as an example, or you could handle it differently
            return buffer.getInt(0);
        } else {
            throw new IllegalArgumentException("Invalid IP address length: " + addressBytes.length);
        }
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {

        SocketAddress clientAddress = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
        int clientNodeIp = extractClientAddressAsInt(clientAddress);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(next.startCall(call, headers)){
            @Override
            public void onMessage(ReqT message) {

                byte[] nodeId;
                int port;

                if(message instanceof ping){
                    nodeId = ((ping) message).getNodeId;
                    port = ((ping) message).getNodePublicPort;
                } else if(message instanceof storeRequest){
                    nodeId = ((storeRequest) message).getNodeId;
                    port = ((storeRequest) message).getNodePublicPort;
                } else if(message instanceof target){
                    nodeId = ((target) message).getNodeId;
                    port = ((target) message).getNodePublicPort;
                } else{
                    super.onMessage(message); // continue with normal onMessage
                    return;
                }

                Node newNode = new Node(port, clientNodeIp);

                Context fork = Context.current().fork();

                //todo
                fork.run(() -> node.handleSeenNode(newNode));

                super.onMessage(message);
            }
        };
    }
}
