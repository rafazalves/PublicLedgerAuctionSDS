package org.App;

import org.Kademlia.Node;
import org.blockchain.Blockchain;
import org.gRPC.clientManager;
import org.gRPC.serverSetUp;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Menu {

    private static final Logger logger = Logger.getLogger(serverSetUp.class.getName());

    public static void main(String[] args) {
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        Map<Node, Auction> MyAuctions = new HashMap<Node, Auction>();
        Map<Node, Auction> SubAuctions = new HashMap<Node, Auction>();
        Scanner stdin = new Scanner(System.in);
        if (args.length < 2) {
            System.out.println("Usage: java -jar <jarfile> <ip> <port>");
            System.exit(1);
        }
        int ip = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);

        serverSetUp server = new serverSetUp();
        Node node = new Node(port, ip); // port, IP

        clientManager clientManager= new clientManager();
        node.setClientManager(clientManager);
        try {
            server.start(node);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.setLevel(Level.FINEST);

        try {
            server.blockUntilShutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server started");
        Blockchain blockchain = new Blockchain(1, 3, null);

        // cria node bootstrap cliente
        // ler ficheiro leilao

        System.out.println("Menu:");
        System.out.println("1 - Criar leilão");
        System.out.println("2 - Licitar");
        System.out.println("3 - Ver leilões");
        System.out.println("4 - Subscrever leilão");
        System.out.println("5 - Meus leilões");
        System.out.println("6 - Sair");

        System.out.print("Escolha uma opção:");
        int op = stdin.nextInt();
        switch (op){
            case 1:
                System.out.println("Criar leilão");
                System.out.println("Insira o nome do leilão:");
                String name = stdin.next();
                PublicKey owner = node.getPubKey();
                System.out.println("Insira o preço inicial:");
                int price = stdin.nextInt();
                System.out.println("Insira o preço máximo:");
                float maxPrice = stdin.nextFloat();

                Auction lastElement = auctions.get(auctions.size() - 1);
                int id = lastElement.getAuctionID() + 1;

                Auction auction = new Auction(id,name, owner, price, maxPrice);
                auctions.add(auction);
                MyAuctions.put(node, auction);

                AuctionHandler auctionHandler = new AuctionHandler(auction, node);
                break;
            case 2:
                System.out.println("Licitar");
                for (Auction a : auctions) {
                    System.out.println(a.getAuctionID() + " - " + a.getAuctionName());
                }
                System.out.println("Insira o ID do leilão:");
                int auctionID = stdin.nextInt();
                System.out.println("Insira o valor da licitação:");
                float bid = stdin.nextFloat();
                // verificar se num licitado é maior que o atual e menor que maximo
                // verificar se o leilão ainda está ativo
                // verificar se id pertence a um leilao existente
                break;
            case 3:
                System.out.println("Ver leilões");
                break;
            case 4:
                System.out.println("Subscrever leilão");
                break;
            case 5:
                System.out.println("Meus leilões");
                break;
            case 6:
                System.exit(0);
                break;
            default:
                System.out.println("Opção inválida");
                System.out.println("Menu:");
                System.out.println("1 - Criar leilão");
                System.out.println("2 - Licitar");
                System.out.println("3 - Ver leilões");
                System.out.println("4 - Subscrever leilão");
                System.out.println("5 - Meus leilões");
                System.out.println("6 - Sair");
                break;

        }
    }
}
