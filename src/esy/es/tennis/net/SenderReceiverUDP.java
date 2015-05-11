package esy.es.tennis.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SenderReceiverUDP implements Sender, Receiver
{
    private DatagramSocket socket;
    private InetAddress destinationAddress;
    private int destinationPort;
    private int maxLength = 500;

    public SenderReceiverUDP(InetAddress destinationAddress, int destinationPort) throws SocketException
    {
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
        socket = new DatagramSocket();
    }

    public SenderReceiverUDP() throws SocketException
    {
        socket = new DatagramSocket();
        destinationPort = -1;
        destinationAddress = null;
    }

    @Override
    public String receive() throws IOException
    {
        byte[] data = new byte[maxLength];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        socket.receive(receivePacket);
        destinationPort = receivePacket.getPort();  // saving new port
        destinationAddress = receivePacket.getAddress();
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    @Override
    public void send(String message)
    {
        if (destinationAddress != null)
        {
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, 0, data.length, destinationAddress, destinationPort);
            try
            {
                socket.send(packet);
                System.out.println("Packet " + packet + "sent to address: " + destinationAddress + "\nPort:" + destinationPort);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public InetAddress getDestinationAddress()
    {
        return destinationAddress;
    }

    public void setDestinationAddress(InetAddress destinationAddress)
    {
        this.destinationAddress = destinationAddress;
    }

    public int getDestinationPort()
    {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort)
    {
        this.destinationPort = destinationPort;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }
}
