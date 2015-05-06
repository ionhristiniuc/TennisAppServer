package tennis.server;

import javafx.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import static esy.es.tennis.shared.TennisAppConstants.*;

public class Player implements Runnable
{
//    private Socket connection;
    private DatagramSocket datagramSocket;
    private int playerNumber;
    private Lock lock;
    private Condition playerConnected;
//    private ObjectOutputStream output;
//    private ObjectInputStream input;
    private boolean enabled = false;
    private Board board;
    private Player[] players;
    private final InetAddress playerAddress;
    private final int port;

    public Player(InetAddress playerAddress, int port, int playerNumber, Lock lock, Condition playerConnected, Board board, Player[] players, DatagramSocket ds)
    {
        this.playerAddress = playerAddress;
        this.port = port;
        this.playerNumber = playerNumber;
        this.lock = lock;
        this.playerConnected = playerConnected;
        this.board = board;
        this.players = players;
        this.datagramSocket = ds;
    }

    @Override
    public void run()
    {
//        try
//        {
//            getStreams();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        if ( playerNumber == 0 )    // wait until second player connects
        {
            lock.lock();

            try
            {
                String s = notification + separator + "Waiting for second player";
                sendData(s.getBytes());

                while (!enabled)
                    playerConnected.await();    //  wait for second player to connect
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                lock.unlock();
            }
        }

        // play the game
//        sendMessage("Welcome player " + playerNumber);

//        try
//        {
            listenPlayer();     // not thread-safe

            try
            {
                lock.lock();
                board.getBall().setIsMoving(false);
            }
            finally
            {
                lock.unlock();
            }
        }
//        finally
//        {
//            try
//            {
//                //closeConnection();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }

    //}

//    private void getStreams() throws IOException
//    {
//        output = new ObjectOutputStream( connection.getOutputStream() );
//        output.flush();
//
//        input = new ObjectInputStream(connection.getInputStream());
//    }

//    private void sendMessage( String message )
//    {
//        try
//        {
//            output.writeObject(message);
//            output.flush();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }

    private void sendData( byte[] data )
    {
        try
        {
            DatagramPacket toSend = new DatagramPacket(data, data.length, playerAddress, port);
            datagramSocket.send(toSend);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    private Pair<String, Sender> receiveMessage() throws IOException
    {
        byte[] buffer = new byte[100];
        DatagramPacket receivePacket = new DatagramPacket( buffer, 0, buffer.length );
        datagramSocket.receive(receivePacket);
        return new Pair<>(new String(receivePacket.getData(), 0, receivePacket.getLength()),
                new Sender(receivePacket.getAddress(), receivePacket.getPort()));
    }

    private void listenPlayer()
    {
//        String message = null;
        Pair<String, Sender> messSender = null;

        do
        {
            try
            {
                //message = (String) input.readObject();
                messSender = receiveMessage();
                processMessage(messSender);
            }
            catch (IOException e)
            {
                displayMessage("An error occurred while reading from stream (player " + playerNumber + ")");
            }

        } while (messSender == null || !messSender.getKey().equals(disconnect));
    }

    private void processMessage(Pair<String, Sender> messSender)
    {
        String[] data = messSender.getKey().split(Pattern.quote(separator));

        if (data.length > 0)
        {
            switch (data[0])
            {
                case movePaletteLeft:
                    movePalette(movePaletteLeft, messSender.getValue());
                    updateClients();
                    break;
                case movePaletteRight:
                    movePalette(movePaletteRight, messSender.getValue());
                    updateClients();
                    break;
            }
        }
    }

    private void movePalette(String direction, Sender sender)
    {
        if (direction.equals(movePaletteLeft))
        {
            if (players[0].playerAddress.equals(sender.address) && players[0].port == sender.getPortNumber())   // player 0 is the sender
                for ( int i = 0; i < moveSpeed; ++i)
                {
                    board.getFirstPalette().setX(board.getFirstPalette().getX() - 1);
                    delay(3);
                    updateClients();
                }
            else
                for ( int i = 0; i < moveSpeed; ++i)
                {
                    board.getSecondPalette().setX(board.getSecondPalette().getX() - 1);
                    delay(3);
                    updateClients();
                }
        }
        else
        {
            if (players[0].playerAddress.equals(sender.address) && players[0].port == sender.getPortNumber())   // player 0
                for (int i = 0; i < moveSpeed; ++i)
                {
                    board.getFirstPalette().setX( board.getFirstPalette().getX() + 1 );
                    delay(3);
                    updateClients();
                }
            else
                for (int i = 0; i < moveSpeed; ++i)
                {
                    board.getSecondPalette().setX(board.getSecondPalette().getX() + 1);
                    delay(3);
                    updateClients();
                }
        }
    }

    public void updateClients()
    {
        lock.lock();

        String message = updateBoard + separator + board.getBall().getX() + separator + board.getBall().getY() + separator +
                board.getFirstPalette().getX() + separator + board.getSecondPalette().getX();
        byte[] bytes = message.getBytes();

        try
        {
            players[0].sendData(bytes);
            players[1].sendData(bytes);
        }
        finally
        {
            lock.unlock();
        }
    }

    private void delay( int millis )
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void updateClientsBall()
    {
        lock.lock();

        try
        {
            String message = ballMove + separator + board.getBall().getX() + separator + board.getBall().getY();
            byte[] bytes = message.getBytes();

            players[0].sendData(bytes);
            players[1].sendData(bytes);
        }
        finally
        {
            lock.unlock();
        }
    }

    private void displayMessage( String message )
    {
        System.out.println(message);
    }

    private class Sender
    {
        private final InetAddress address;
        private final int portNumber;

        public Sender( InetAddress address, int portNumber )
        {
            this.address = address;
            this.portNumber = portNumber;
        }

        public InetAddress getAddress()
        {
            return address;
        }

        public int getPortNumber()
        {
            return portNumber;
        }
    }
}
