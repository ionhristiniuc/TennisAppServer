package esy.es.tennis.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static esy.es.tennis.shared.TennisAppConstants.maxPacketLength;

public class ReceiverUDP implements Receiver
{
    private int localPort;
    private DatagramSocket socket;
    private InetAddress senderAddress = null;
    private int senderPort = -1;
    private int maxLength = maxPacketLength;

    public ReceiverUDP(int localPort) throws SocketException
    {
        this.localPort = localPort;
        socket = new DatagramSocket(localPort);
    }

    @Override
    public String receive() throws IOException
    {
        byte[] data = new byte[maxLength];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        socket.receive(receivePacket);
        senderPort = receivePacket.getPort();  // saving new port
        senderAddress = receivePacket.getAddress();
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    public InetAddress getSenderAddress()
    {
        return senderAddress;
    }

    public int getSenderPort()
    {
        return senderPort;
    }
}