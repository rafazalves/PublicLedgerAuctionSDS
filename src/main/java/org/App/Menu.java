package org.App;

import org.App.data.AuctionDTO;
import org.Kademlia.Node;
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
import java.security.PublicKey;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Menu {

    private static final Logger logger = Logger.getLogger(serverSetUp.class.getName());
    private static final String USERS_FILE = "src\\main\\java\\org\\App\\data\\leilao.json";
    private static ArrayList<Auction> auctions = new ArrayList<Auction>();

    public static void main(String[] args) {
        Map<Wallet, Auction> MyAuctions = new HashMap<Wallet, Auction>();
        Map<Wallet, Auction> SubAuctions = new HashMap<Wallet, Auction>();
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
        loadUsersFromFile();
        Wallet userNode = new Wallet(100); // Usar Wallet em vez de usar o Node?
        Blockchain blockchain = new Blockchain(1, 3, null);
        TransactionPool transactionPool = new TransactionPool();

        // cria node bootstrap cliente

       while (true) {
            System.out.println("Menu:");
            System.out.println("1 - Criar leilão");
            System.out.println("2 - Licitar");
            System.out.println("3 - Ver leilões");
            System.out.println("4 - Subscrever leilão");
            System.out.println("5 - Leilões Subscritos");
            System.out.println("6 - Leilões a Vencer");
            System.out.println("7 - Meus leilões");
            System.out.println("8 - Sair");

            System.out.print("Escolha uma opção: ");

            int op = stdin.nextInt();

            switch (op) {
                case 1:
                    System.out.println("Criar leilão");
                    System.out.println("Insira o nome do leilão:");
                    String name = stdin.next();
                    System.out.println("Insira o preço inicial:");
                    int price = getValidInt(stdin);
                    System.out.println("Insira o preço máximo:");
                    float maxPrice = getValidFloat(stdin);

                    int id = 1;
                    if (!auctions.isEmpty()) {
                        Auction lastElement = auctions.get(auctions.size() - 1);
                        id = lastElement.getAuctionID() + 1;
                    }

                    Auction auction = new Auction(id, name, userNode, price, maxPrice);
                    auctions.add(auction);
                    MyAuctions.put(userNode, auction);

                    Block block = new Block(blockchain.getLatestBlock().getId() + 1, blockchain.getLatestBlock().getHash(), new ArrayList<Transaction>());
                    blockchain.addBlock(block);

                    //AuctionHandler auctionHandler = new AuctionHandler(auction, node);
                    break;
                case 2:
                    System.out.println("Licitar");
                    for (Auction a : auctions) {
                        System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                    }
                    System.out.println("Insira o ID do leilão:");
                    int auctionID = stdin.nextInt();
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
                        if(selectedAuction.getAuctionStatus()==0){
                            System.out.println("Status do Leilão: Em curso");
                            System.out.println("Insira o valor da licitação:");
                            float bid = getValidFloat(stdin);

                            // verificar se num licitado é maior que o atual e menor que maximo
                            if(bid>selectedAuction.getAuctionCurrentPrice() && bid<selectedAuction.getAuctionMaxPrice()){
                                Transaction transaction = new Transaction(selectedAuction.getAuctionOwner(), selectedAuction.getAuctionCurrentWinner(), selectedAuction.getAuctionCurrentPrice());
                                transaction.generateSignature(selectedAuction.getOwnerWallet().getPrivateKey());
                                Transaction transaction2 = new Transaction(userNode.getPublicKey(), selectedAuction.getAuctionOwner(), bid);
                                transaction2.generateSignature(userNode.getPrivateKey());
                                Block selectedBlock = null;
                                for (Block a : blockchain.getblockchainBlocks()) {
                                    if (a.getId() == selectedAuction.getAuctionID()) {
                                        selectedBlock = a;
                                        break;
                                    }
                                }
                                if(selectedBlock.addTransaction(transaction) && selectedBlock.addTransaction(transaction2)) {
                                    transactionPool.addTransaction(transaction);
                                    transactionPool.addTransaction(transaction2);
                                }
                                selectedAuction.setAuctionCurrentWinner(userNode.getPublicKey());
                                selectedAuction.setAuctionCurrentPrice(bid);
                            }else if(bid>selectedAuction.getAuctionMaxPrice()){
                                System.out.println("Valor Licitado excede Max Price.");
                                System.out.println("Leilão terminado com licitação de " + selectedAuction.getAuctionMaxPrice());
                                selectedAuction.setAuctionCurrentPrice(selectedAuction.getAuctionMaxPrice());
                                selectedAuction.setAuctionStatus(1);
                                selectedAuction.setAuctionWinner(userNode.getPublicKey());
                                Transaction transaction = new Transaction(userNode.getPublicKey(), selectedAuction.getAuctionOwner(),selectedAuction.getAuctionMaxPrice());
                                transaction.generateSignature(userNode.getPrivateKey());
                                Block selectedBlock = null;
                                for (Block a : blockchain.getblockchainBlocks()) {
                                    if (a.getId() == selectedAuction.getAuctionID()) {
                                        selectedBlock = a;
                                        break;
                                    }
                                }
                                if(selectedBlock.addTransaction(transaction)) {
                                    transactionPool.addTransaction(transaction);
                                }
                            }else if(bid==selectedAuction.getAuctionMaxPrice()){
                                System.out.println("Leilão terminado com licitação de " + bid);
                                selectedAuction.setAuctionCurrentPrice(bid);
                                selectedAuction.setAuctionStatus(1);
                                selectedAuction.setAuctionWinner(userNode.getPublicKey());
                                Transaction transaction = new Transaction(userNode.getPublicKey(), selectedAuction.getAuctionOwner(), bid);
                                transaction.generateSignature(userNode.getPrivateKey());
                                Block selectedBlock = null;
                                for (Block a : blockchain.getblockchainBlocks()) {
                                    if (a.getId() == selectedAuction.getAuctionID()) {
                                        selectedBlock = a;
                                        break;
                                    }
                                }
                                if(selectedBlock.addTransaction(transaction)) {
                                    transactionPool.addTransaction(transaction);
                                }
                            }else{
                                System.out.println("Valor Licitado insuficiente.");
                            }
                        }else{
                            System.out.println("Status do Leilão: Fechado");
                        }
                    } else {
                        System.out.println("Auction with ID " + auctionID + " not found.");
                    }
                    break;
                case 3:
                    System.out.println("Ver leilões");
                    for (Auction a : auctions) {
                        if(a.getAuctionStatus()==0){
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                        }else{
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Leilão acabado. Preço final: " + a.getAuctionCurrentPrice() + ") ");
                        }
                    }
                    break;
                case 4:
                    System.out.println("Subscrever leilão");
                    for (Auction a : auctions) {
                        System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                    }
                    System.out.println("Insira o ID do leilão que pretende subscrever:");
                    int auct = stdin.nextInt();
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
                case 5:
                    System.out.println("Leilões Subscritos");
                    for (Auction a : SubAuctions.values()) {
                        if(a.getAuctionStatus()==0){
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                        }else{
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Leilão acabado. Preço final: " + a.getAuctionCurrentPrice() + ") ");
                        }
                    }
                    break;
                case 6:
                    System.out.println("Leilões a Vencer");
                    for (Auction a : auctions) {
                        if (a.getAuctionCurrentWinner() == userNode.getPublicKey()) {
                            if(a.getAuctionStatus()==0){
                                System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                            }else{
                                System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Leilão acabado. Preço final: " + a.getAuctionCurrentPrice() + ") ");
                            }
                        }
                    }
                    break;
                case 7:
                    System.out.println("Meus leilões");
                    for (Auction a : MyAuctions.values()) {
                        if(a.getAuctionStatus()==0){
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Preço atual do leilão: " + a.getAuctionCurrentPrice() + ") ");
                        }else{
                            System.out.println(a.getAuctionID() + " - " + a.getAuctionName() + " (Leilão acabado. Preço final: " + a.getAuctionCurrentPrice() + ") ");
                        }
                    }
                    break;
                case 8:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
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
                        ownerWallet,
                        auctionDTO.auctionStartPrice,
                        auctionDTO.auctionMaxPrice
                );
                auctions.add(auctionInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

