package org.App;

import org.App.data.AuctionDTO;
import org.Kademlia.KadNode;
import org.Kademlia.Node;
import org.Kademlia.Storage.StorageManager;
import org.Kademlia.Storage.StorageValue;
import org.blockchain.Block;
import org.blockchain.Blockchain;
import org.blockchain.Wallet;
import org.blockchain.transaction.Transaction;
import org.blockchain.transaction.TransactionPool;
import org.gRPC.clientManager;
import org.gRPC.serverSetUp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Menu {

    private static final Logger logger = Logger.getLogger(serverSetUp.class.getName());
    private static final String USERS_FILE = "src/main/java/org/App/data/leilao.json";
    private static ArrayList<Auction> auctions = new ArrayList<Auction>();
    
    public static Blockchain blockchain = new Blockchain(1, 3, null);
    public static TransactionPool transactionPool = new TransactionPool();
    public static final Scanner scanner = new Scanner(System.in);
    public static Map<Node, Auction> MyAuctions = new HashMap<Node, Auction>();
    public static  Map<Node, Auction> SubAuctions = new HashMap<Node, Auction>();
    public static clientManager clientManager = new clientManager();
    public static AuctionHandler auctionHandler = new AuctionHandler();

    public static void main(String[] args) {

        Scanner stdin = new Scanner(System.in);
        if (args.length < 2) {
            System.out.println("Usage: java -jar <jarfile> <ip> <port>");
            System.exit(1);
        }
        int ip = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);

        System.out.println("Command mode:");
        System.out.println("1 - auction");
        System.out.println("2 - client");
        System.out.println("3 - server");
        System.out.println("4 - help");
        System.out.println("5 - quit");


        int op = scanner.nextInt();

        switch (op) {
            case 1:
                startServer(ip, port, scanner);
                startAuction(ip, port);
                break;

            case 2:
                startServer(ip, port, scanner);
                startClient(ip, port, scanner);
                break;

            case 3:
                help();
                break;

            case 4:
                scanner.close();
                return;

            default:
                System.out.println("Unknown command. Use 'help' for a list of available commands.");
                break;
        }
    }

    public static void help() {
        System.out.println("auction > create auction\nclient > bid in auctions\nserver > create server");
    }


    public static void startAuction(int name, int port) {

        loadUsersFromFile();
        Random random = new Random();
        Node userNode = new Node(port, name);
        KadNode kadNode = new KadNode(userNode);

        List<Node> nodes = clientManager.getNodes();
        System.out.println("Discovered nodes: ");
        for (Node node : nodes) {
            System.out.println("Node IP: " + node.getNodeIP() + ", Port: " + node.getNodePublicPort());
        }

        while(true) {
            System.out.println("Opções");
            System.out.println("1-Criar leilão");
            System.out.println("2-Ver meus leilões");
            System.out.println("3-Leilões a vencer");
            System.out.println("4-Sair");

            int op1 = scanner.nextInt();

            switch (op1) {
                case 1:
                    System.out.println("Criar leilão");
                    System.out.println("Insira o nome do leilão:");
                    String name1 = scanner.next();
                    System.out.println("Insira o preço inicial:");
                    int price = getValidInt(scanner);
                    System.out.println("Insira o preço máximo:");
                    float maxPrice = getValidFloat(scanner);

                    int id = 1;
                    if (!auctions.isEmpty()) {
                        Auction lastElement = auctions.get(auctions.size() - 1);
                        id = lastElement.getAuctionID() + 1;
                    }

                    int randomNumber = random.nextInt(1000 - 100 + 1) + 100;
                    Wallet wallet = new Wallet(randomNumber);
                    Auction auction = new Auction(id, name1, userNode.getPubKey(), price, maxPrice, wallet);
                    auctions.add(auction);
                    MyAuctions.put(userNode, auction);

                    Block block = new Block(blockchain.getLatestBlock().getId() + 1, blockchain.getLatestBlock().getHash(), new ArrayList<Transaction>());
                    blockchain.addBlock(block);
                    auctionHandler = new AuctionHandler(auction, kadNode);
                    long timestamp = Instant.now().getEpochSecond();
                    auctionHandler.storeBid(timestamp, auction.getAuctionID(), kadNode );

                    StorageValue SV = new StorageValue(BigInteger.valueOf(auction.getAuctionID()), timestamp);
                    // fazer um for para os List<Node> nodes = clientManager.getNodes(); se ficar a funcionar e assim manda para os outros nodes
                    clientManager.doStore(kadNode,kadNode, SV );
                break;

                case 2:
                    System.out.println("Meus leilões");
                    for (Auction a : MyAuctions.values()) {
                        if(a.getAuctionStatus()==0){
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                        }else{
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Leilão acabado. Preço final: " + a.getAuctionCurrentPrice() + ") ");
                        }
                    }
                break;

                case 3:
                    System.out.println("Leilões a Vencer");
                    for (Auction a : auctions) {
                        if (a.getAuctionCurrentWinner() == userNode.getPubKey()) {
                            if(a.getAuctionStatus()==0){
                                System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                            }else{
                                System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Leilão acabado. Preço final: " + a.getAuctionCurrentPrice() + ") ");
                            }
                        }
                    }
                    break;

                case 4:
                    System.exit(0);
                    break;

                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }
    }


    public static void startServer(int ip, int port, Scanner scn){
        loadUsersFromFile();

        serverSetUp server = new serverSetUp();

        Node node = new Node(port, ip); // port, IP
        KadNode knode = new KadNode(node);

        knode.setClientManager(clientManager);
        clientManager.registerNode(node);

        new Thread(() -> {
            try {
                server.start(node);
                logger.setLevel(Level.FINEST);
                server.blockUntilShutdown();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        logger.info("Server started");

    }

    public static void startClient(int ip, int port, Scanner scn) {

        loadUsersFromFile();


        Node userNode = new Node(port, ip);
        KadNode knode = new KadNode(userNode);

        List<Node> nodes = clientManager.getNodes();
        System.out.println("Discovered nodes: ");
        for (Node node : nodes) {
            System.out.println("Node IP: " + node.getNodeIP() + ", Port: " + node.getNodePublicPort());
        }

        System.out.println("Do you want to do Ping:");
        System.out.println("1 - Sim");
        System.out.println("2 - Não");


        int op = scn.nextInt();


        switch (op) {
            case 1:
                System.out.println("what is the ip?");
                int targetIp = Menu.scanner.nextInt();
                System.out.println("What is the port?");
                int targetPort = Menu.scanner.nextInt();

                Node targetNode = new Node(targetPort, targetIp);
                KadNode targetKnode = new KadNode(targetNode);

                // Fazer o ping
                clientManager.doPing(knode, targetKnode);
                break;

            case 2:
                System.out.println("Continua");
                break;

            default:
                System.out.println("Opção inválida");
                break;
        }

        while (true) {
            System.out.println("Opção:");
            System.out.println("1-Ver leilões");
            System.out.println("2-Licitar");
            System.out.println("3-Subscrever leilão");
            System.out.println("4-Ver leilões subscritos");
            System.out.println("5-Sair");

            int op1 = scn.nextInt();

            switch (op1) {
                case 1:
                    System.out.println("Ver leilões");
                    for (Auction a : auctions) {
                        if (a.getAuctionStatus() == 0) {
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                        } else {
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Leilão acabado. Preço final: " + a.getAuctionCurrentPrice() + ") ");
                        }
                    }
                    break;

                case 2:
                    System.out.println("Licitar");
                    for (Auction a : auctions) {
                        System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                    }
                    System.out.println("Insira o ID do leilão:");
                    int auctionID = Menu.scanner.nextInt();
                    Auction selectedAuction = null;
                    for (Auction a : auctions) {
                        if (a.getAuctionID() == auctionID) {
                            selectedAuction = a;
                            break;
                        }
                    }
                    if (selectedAuction != null) {
                        System.out.println("Auction Details:");
                        System.out.println("ID: " + selectedAuction.getAuctionID());
                        System.out.println("Name: " + selectedAuction.getAuctionName());
                        System.out.println("Current Price: " + selectedAuction.getAuctionCurrentPrice());
                        System.out.println("Max Price: " + selectedAuction.getAuctionMaxPrice());
                        if (selectedAuction.getAuctionStatus() == 0) {
                            System.out.println("Status do Leilão: Em curso");
                            System.out.println("Insira o valor da licitação:");
                            float bid = getValidFloat(Menu.scanner);
                            auctionHandler = new AuctionHandler(selectedAuction,knode);
                            // verificar se num licitado é maior que o atual e menor que maximo
                            if (bid > selectedAuction.getAuctionCurrentPrice() && bid < selectedAuction.getAuctionMaxPrice()) {
                                if(selectedAuction.getAuctionCurrentWinner() != null){
                                    Transaction transaction = new Transaction(selectedAuction.getAuctionOwner(), selectedAuction.getAuctionCurrentWinner(), selectedAuction.getAuctionCurrentPrice());
                                    transaction.generateSignature(selectedAuction.getOwnerWallet().getPrivateKey());
                                    Transaction transaction2 = new Transaction(userNode.getPubKey(), selectedAuction.getAuctionOwner(), bid);
                                    transaction2.generateSignature(userNode.getPrivKey());
                                    Block selectedBlock = null;
                                    for (Block a : blockchain.getblockchainBlocks()) {
                                        if (a.getId() == selectedAuction.getAuctionID()) {
                                            selectedBlock = a;
                                            break;
                                        }
                                    }
                                    if (selectedBlock.addTransaction(transaction) && selectedBlock.addTransaction(transaction2)) {
                                        transactionPool.addTransaction(transaction);
                                        transactionPool.addTransaction(transaction2);
                                    }
                                }else{
                                    Transaction transaction = new Transaction(userNode.getPubKey(), selectedAuction.getAuctionOwner(), bid);
                                    transaction.generateSignature(userNode.getPrivKey());
                                    Block selectedBlock = null;
                                    for (Block a : blockchain.getblockchainBlocks()) {
                                        if (a.getId() == selectedAuction.getAuctionID()) {
                                            selectedBlock = a;
                                            break;
                                        }
                                    }
                                    if (selectedBlock.addTransaction(transaction)) {
                                        transactionPool.addTransaction(transaction);
                                    }
                                }
                                selectedAuction.setAuctionCurrentWinner(userNode.getPubKey());
                                selectedAuction.setAuctionCurrentPrice(bid);

                                long timestamp = Instant.now().getEpochSecond();
                                auctionHandler.storeBid(timestamp, selectedAuction.getAuctionID(), knode);

                                StorageValue SV = new StorageValue(BigInteger.valueOf(selectedAuction.getAuctionID()), timestamp);
                                clientManager.doStore(knode,knode, SV);

                            } else if (bid > selectedAuction.getAuctionMaxPrice()) {
                                System.out.println("Valor Licitado excede Max Price.");
                                System.out.println("Leilão terminado com licitação de " + selectedAuction.getAuctionMaxPrice());
                                selectedAuction.setAuctionCurrentPrice(selectedAuction.getAuctionMaxPrice());
                                selectedAuction.setAuctionStatus(1);
                                selectedAuction.setAuctionWinner(userNode.getPubKey());
                                Transaction transaction = new Transaction(userNode.getPubKey(), selectedAuction.getAuctionOwner(), selectedAuction.getAuctionMaxPrice());
                                transaction.generateSignature(userNode.getPrivKey());
                                Block selectedBlock = null;
                                for (Block a : blockchain.getblockchainBlocks()) {
                                    if (a.getId() == selectedAuction.getAuctionID()) {
                                        selectedBlock = a;
                                        break;
                                    }
                                }
                                if (selectedBlock.addTransaction(transaction)) {
                                    transactionPool.addTransaction(transaction);
                                }
                                long timestamp = Instant.now().getEpochSecond();
                                auctionHandler.storeBid(timestamp, selectedAuction.getAuctionID(), knode);

                                StorageValue SV = new StorageValue(BigInteger.valueOf(selectedAuction.getAuctionID()), timestamp);
                                clientManager.doStore(knode,knode, SV);
                            } else if (bid == selectedAuction.getAuctionMaxPrice()) {
                                System.out.println("Leilão terminado com licitação de " + bid);
                                selectedAuction.setAuctionCurrentPrice(bid);
                                selectedAuction.setAuctionStatus(1);
                                selectedAuction.setAuctionWinner(userNode.getPubKey());
                                Transaction transaction = new Transaction(userNode.getPubKey(), selectedAuction.getAuctionOwner(), bid);
                                transaction.generateSignature(userNode.getPrivKey());
                                Block selectedBlock = null;
                                for (Block a : blockchain.getblockchainBlocks()) {
                                    if (a.getId() == selectedAuction.getAuctionID()) {
                                        selectedBlock = a;
                                        break;
                                    }
                                }
                                if (selectedBlock.addTransaction(transaction)) {
                                    transactionPool.addTransaction(transaction);
                                }
                                long timestamp = Instant.now().getEpochSecond();
                                auctionHandler.storeBid(timestamp, selectedAuction.getAuctionID(), knode);
                                StorageValue SV = new StorageValue(BigInteger.valueOf(selectedAuction.getAuctionID()), timestamp);
                                clientManager.doStore(knode,knode, SV);
                            } else {
                                System.out.println("Valor Licitado insuficiente.");
                            }
                        } else {
                            System.out.println("Status do Leilão: Fechado");
                        }
                    } else {
                        System.out.println("Auction with ID " + auctionID + " not found.");
                    }
                    break;

                case 3:
                    System.out.println("Subscrever leilão");
                    for (Auction a : auctions) {
                        System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                    }
                    System.out.println("Insira o ID do leilão que pretende subscrever:");
                    int auct = scanner.nextInt();
                    Auction subAuction = null;
                    for (Auction a : auctions) {
                        if (a.getAuctionID() == auct) {
                            subAuction = a;
                            break;
                        }
                    }
                    if (subAuction != null) {
                        SubAuctions.put(userNode, subAuction);
                    } else {
                        System.out.println("Auction with ID " + auct + " not found.");
                    }
                    break;

                case 4:
                    if (SubAuctions.isEmpty()) {
                        System.out.println("Você não tem leilões subscritos.");
                        break;
                    }

                    System.out.println("Leilões Subscritos");
                    for (Auction a : SubAuctions.values()) {
                        if (a.getAuctionStatus() == 0) {
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                        } else {
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Leilão acabado. Preço final: " + a.getAuctionCurrentPrice() + ") ");
                        }
                    }
                    break;

                case 5:
                    System.exit(0);
                    return;

                default:
                    System.out.println("Opção inválida");
                    break;
            }
            System.out.println("-------------------------------");
        }
    }

    private static int getValidInt(Scanner stdin) {
        while (!stdin.hasNextInt()) {
            System.out.println("Entrada inválida. Por favor insira um número inteiro:");
            stdin.next(); // Consume the invalid input
        }
        return stdin.nextInt();
    }

    private static float getValidFloat(Scanner stdin) {
        while (!stdin.hasNextFloat()) {
            System.out.println("Entrada inválida. Por favor insira um número válido:");
            stdin.next(); // Consume the invalid input
        }
        return stdin.nextFloat();
    }

    // Load existing users from json file
    private static void loadUsersFromFile() {
        Gson gson = new Gson();
        Random random = new Random();

        try (FileReader reader = new FileReader(USERS_FILE)) {
            Type auctionListType = new TypeToken<List<AuctionDTO>>() {}.getType();
            List<AuctionDTO> auctionDTOs = gson.fromJson(reader, auctionListType);

            for (AuctionDTO auctionDTO : auctionDTOs) {
                int randomNumber = random.nextInt(1000 - 100 + 1) + 100;
                Wallet ownerWallet = new Wallet(randomNumber);
                Auction auctionInfo = new Auction(
                        auctionDTO.auctionID,
                        auctionDTO.auctionName,
                        ownerWallet.getPublicKey(),
                        auctionDTO.auctionStartPrice,
                        auctionDTO.auctionMaxPrice,
                        ownerWallet
                );
                auctions.add(auctionInfo);

                Block block = new Block(blockchain.getLatestBlock().getId() + 1, blockchain.getLatestBlock().getHash(), new ArrayList<Transaction>());
                blockchain.addBlock(block);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

