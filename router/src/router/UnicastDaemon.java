/**
 * Redes Integradas de Telecomunicacoes
 * MEEC/MERSIM 2026/2027
 *
 * UnicastDaemon.java
 *
 * Thread that handles the Unicast socket events
 *
 * @author  Luis Bernardo
 */
package router;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Class that supports unicast communication
 */
public class UnicastDaemon extends Thread {
    volatile boolean keepRunning= true;
    DatagramSocket ds;
    Router router;
    
    // Constructor
    UnicastDaemon(Router router, DatagramSocket ds) {
        this.router= router;
        this.ds= ds;
    }

    
    // Thread main function
    @Override
    public void run() {
        byte [] buf= new byte[8096];
        DatagramPacket dp= new DatagramPacket(buf, buf.length);
        try {
            while (keepRunning) {
                try {
                    ds.receive(dp);
                    ByteArrayInputStream BAis= 
                        new ByteArrayInputStream(buf, 0, dp.getLength());
                    DataInputStream dis= new DataInputStream(BAis);
                    System.out.println("Received packet ("+dp.getLength()+
                        ") from " + dp.getAddress().getHostAddress() +
                        ":" +dp.getPort());
                    synchronized (this) {
                        router.process_packet(dp, dis);
                    }
                }
                catch (SocketException se) {
                    if (keepRunning) {
                        // Avoid concurrent run of packet reception
                        router.Log("recv UDP SocketException : " + se + "\n");
                    }
                }
            }
        }
        catch(IOException e) {
            if (keepRunning) {
                router.Log("IO exception receiving data from socket : " + e);
            }
        }
    }

    
    // Stop thread
    public void stopRunning() {
        keepRunning= false;
    }
}

