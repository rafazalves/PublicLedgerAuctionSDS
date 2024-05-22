package org.gRPC;

public class clientManager {

    private static clientManager instance;


    public clientManager() {
        instance = this;
    }

    public static clientManager getInstance() {
        return instance;
    }

    public static void main(String[] args){
        System.out.println("---------------------------------");
        System.out.println("-- Cliente -- ");



    }
}
