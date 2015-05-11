package tennis.server;

import esy.es.tennis.net.SenderReceiverUDP;
import esy.es.tennis.shared.Palette;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import static esy.es.tennis.shared.TennisAppConstants.*;

public class Player implements Runnable
{
    //private Lock lock;
    //private Condition playerConnected;
    private boolean enabled = false;
    //private Board board;
    private SenderReceiverUDP handler;
    private String nickName;
    private final TennisServer server;
    private int status = FREE_ST;
    private Integer gameNumber = null;

    public Player(SenderReceiverUDP handler, String nickName, TennisServer server)
    {
        this.handler = handler;
        this.nickName = nickName;
        //this.lock = lock;
//        this.playerConnected = playerConnected;
//        this.board = board;
        //this.players = players;
        //this.datagramSocket = ds;
        this.server = server;
    }

    /**
     * Send a list with all available players to the client
     */
    public void refreshPlayers()
    {
        StringBuilder message = new StringBuilder(playersList);
        for ( Player p : server.getPlayers().values())
            message.append(separator).append(p.getNickName()).append(separator2).append(p.getStatus());
        handler.send(message.toString());
    }

    @Override
    public void run()
    {
        listenPlayer();
    }

//        if ( playerNumber == 0 )    // wait until second player connects
//        {
//            lock.lock();
//
//            try
//            {
//                String s = notification + separator + "Waiting for second player";
//                sendData(s.getBytes());
//
//                while (!enabled)
//                    playerConnected.await();    //  wait for second player to connect
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//            finally
//            {
//                lock.unlock();
//            }
//        }

        // play the game
//        sendMessage("Welcome player " + playerNumber);

//        try
//        {

//            try
//            {
//                lock.lock();
//                board.getBall().setIsMoving(false);
//            }
//            finally
//            {
//                lock.unlock();
//            }

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

//    private void sendData( byte[] data )
//    {
//        try
//        {
//            DatagramPacket toSend = new DatagramPacket(data, data.length, playerAddress, port);
//            datagramSocket.send(toSend);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

//    private Pair<String, Sender> receiveMessage() throws IOException
//    {
//        byte[] buffer = new byte[100];
//        DatagramPacket receivePacket = new DatagramPacket( buffer, 0, buffer.length );
//        datagramSocket.receive(receivePacket);
//        return new Pair<>(new String(receivePacket.getData(), 0, receivePacket.getLength()),
//                new Sender(receivePacket.getAddress(), receivePacket.getPort()));
//    }

    private void listenPlayer()
    {
        String message = null;

        do
        {
            try
            {
                message = handler.receive();
                processMessage(message);
            }
            catch (IOException e)
            {
                displayMessage("An error occurred while reading from stream (player " + nickName + ")");
            }

        } while (message == null || !message.equals(disconnect));
    }

    private void processMessage(String message)
    {
        String[] data = message.split(Pattern.quote(separator));

        if (data.length > 0)
        {
            switch (data[0])
            {
                case movePaletteLeft:
                    movePalette(movePaletteLeft);
                    break;
                case movePaletteRight:
                    movePalette(movePaletteRight);
                    break;
                case playersList:
                    refreshPlayers();
                    break;
                case askPlay:
                    server.sendMessageToPlayer(data[1], askPlay + separator + getNickName());
                    break;
                case respPlay:
                    if (data[1].equals(yes))    // if player responded yes, notify another player
                        server.sendMessageToPlayer( data[2], respPlay + separator + yes + separator + getNickName() );
                    else if (data[1].equals(no))                   // if player responded no
                        server.sendMessageToPlayer(data[2], respPlay + separator + no + separator + getNickName());
                    break;
                case disconnect:
                    server.disconnectPlayer(getNickName());
                    server.refreshPlayers();
                    break;
                case occupied:  // if player is already occupied
                    server.sendMessageToPlayer( data[1], occupied + separator + getNickName() );
                    break;
                case createdBoard:
                    try
                    {
                        server.createNewGame( data[1], getNickName());
                        server.sendMessageToPlayer(data[1], createdBoard + separator + getNickName());
                    }
                    catch (Exception e)
                    {
                        sendMessage(error + separator + data[0]);
                        server.sendMessageToPlayer(data[1], error + separator + getNickName());
                    }
                    break;
                case startGame:
                    server.sendMessageToPlayer(data[1], startGame + separator + getNickName());
                    break;
                case startGameSec:  // start the game
                    server.startGame(gameNumber);
                    break;
                case gameStopped:
                    server.sendMessageToPlayer(data[1], gameStopped + separator + getNickName());
                    server.removeGame(gameNumber);
                    break;
            }
        }
    }


    public Integer getGameNumber()
    {
        return gameNumber;
    }

    public void setGameNumber(Integer gameNumber)
    {
        this.gameNumber = gameNumber;
    }

    private void movePalette(String direction)
    {
        Game game = server.getGames().get(gameNumber);
        Palette palette = (Objects.equals(this, game.getFirstPlayer())) ? game.getBoard().getFirstPalette() : game.getBoard().getSecondPalette();

        if (direction.equals(movePaletteLeft))
        {
            for ( int i = 0; i < moveSpeed; i += 2)
            {
                palette.setX(palette.getX() - 2);
                delay(2);
                server.updateClients(gameNumber);
            }
        }
        else
        {
                for (int i = 0; i < moveSpeed; i += 2)
                {
                    palette.setX( palette.getX() + 2 );
                    delay(2);
                    server.updateClients(gameNumber);
                }
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

//    public void updateClientsBall()
//    {
//        lock.lock();
//
//        try
//        {
//            String message = ballMove + separator + board.getBall().getX() + separator + board.getBall().getY();
//            byte[] bytes = message.getBytes();
//
//            players[0].sendData(bytes);
//            players[1].sendData(bytes);
//        }
//        finally
//        {
//            lock.unlock();
//        }
//    }


    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getNickName()
    {
        return nickName;
    }

    private void displayMessage( String message )
    {
        System.out.println(message);
    }

    public void sendMessage(String message)
    {
        handler.send(message);
    }

//    private class Sender
//    {
//        private final InetAddress address;
//        private final int portNumber;
//
//        public Sender( InetAddress address, int portNumber )
//        {
//            this.address = address;
//            this.portNumber = portNumber;
//        }
//
//        public InetAddress getAddress()
//        {
//            return address;
//        }
//
//        public int getPortNumber()
//        {
//            return portNumber;
//        }
//    }
}
