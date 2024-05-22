package org.gRPC;

import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.ServerBuilder;
import org.Kademlia.Client;
import org.Kademlia.Node;
import org.Kademlia.Server;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class serverSetUp {

    private io.grpc.Server server;
    private serverImpl serverImplInst;

    private static final Logger logger = Logger.getLogger(serverSetUp.class.getName());

   public void start(Node node) throws IOException {
       assert node != null;

       int port = node.getNodePublicPort();

       serverImplInst = new serverImpl(logger, node);

       server = ServerBuilder
               .forPort(port)
               .intercept(new interceptor_kbucket(node))
               .addService((BindableService) serverImplInst)
               .build()
               .start();


       System.out.println("Server started, listening on " + node.getNodeIdString() + ":" + port);

       Runtime.getRuntime().addShutdownHook(new Thread(() -> {
           System.err.println("*** shutting down gRPC server since JVM is shutting down");
           try {
               serverSetUp.this.stop();
           } catch (InterruptedException e) {
               e.printStackTrace(System.err);
           }
           System.err.println("*** server shut down");
       }));
   }

   public void stop() throws InterruptedException {
       if (server != null) {
           server.shutdown().awaitTermination(10, TimeUnit.MINUTES);
       }
   }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


   public static void main(String[] args){
       final serverSetUp server = new serverSetUp();
       if(args.length == 2) {
           Node node = new Node(Integer.parseInt(args[0]), Integer.parseInt(args[1])); // port, IP

           clientManager clientManager= new clientManager();
           node.setClientManager(clientManager);
           try {
               server.start(node);

           } catch (IOException e) {
               throw new RuntimeException(e);
           }
       } else {
           Node node = new Node(80,12345);; // port, IP
           try {

               clientManager clientManager= new clientManager();
               node.setClientManager(clientManager);

               server.start(node);
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
       }

       logger.setLevel(Level.FINEST);

       try {
           server.blockUntilShutdown();
       } catch (InterruptedException e) {
           throw new RuntimeException(e);
       }
   }
}


