package org.gRPC;

public class serverSetUp {

    public static void main(String[] args ){

    }

}
/*
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(DistLedgerServer.class.getSimpleName());

        Server server = ServerBuilder.forPort(Integer.parseInt(args[0]))
                .addService(new DistLedgerServerImpl())
                .intercept(new ConnInterceptor())
                .build();

        server.start();
        System.out.println("Server started");

        server.awaitTermination();
    }


 */
