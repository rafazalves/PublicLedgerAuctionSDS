package org.Kademlia;

import javax.sound.midi.Receiver;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    /* Maximum size of a Datagram Packet */
    private static final int DATAGRAM_BUFFER_SIZE = 64 * 1024;      // 64KB
    /* Server Objects */
    private final DatagramSocket socket;
    private transient boolean isRunning;
    private final Map<Integer, Receiver> receivers;
    private final Timer timer;      // Schedule future tasks
    private final Map<Integer, TimerTask> tasks;    // Keep track of scheduled tasks

    private final Node localNode;

    {
        isRunning = true;
        this.tasks = new HashMap<>();
        this.receivers = new HashMap<>();
        this.timer = new Timer(true);
    }

    public Server(int udpPort, Node localNode) throws SocketException
    {
        this.socket = new DatagramSocket(udpPort);
        this.localNode = localNode;

        /* Start listening for incoming requests in a new thread */
        this.startListener();
    }

    private void startListener() {
    }


}
