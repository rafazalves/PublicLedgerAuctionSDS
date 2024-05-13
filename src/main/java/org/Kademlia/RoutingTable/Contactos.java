package org.Kademlia.RoutingTable;
import org.Kademlia.*;

public class Contactos {
    private final Node n;
    private long lastSeen;
    private int failedTries;

    public Contactos(Node n) {
        this.n = n;
        this.lastSeen = System.currentTimeMillis() / 1000L; // transform to seconds
        this.failedTries = 0;
    }

    public Node getN() {
        return n;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getFailedTries() {
        return failedTries;
    }

    public void setFailedTries(int failedTries) {
        this.failedTries = failedTries;
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }

    public void incrementFailedTries() {
        this.failedTries++;
    }

    public int compare(Contactos o) {
        if (this.getN().equals(o.getN())) // mesmo node
            return 0;
        if (this.getLastSeen() > o.getLastSeen()) { // this.lastSeen é mais recente
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Contactos{" +
                "n=" + n +
                ", lastSeen=" + lastSeen +
                ", failedTries=" + failedTries +
                '}';
    }
}
