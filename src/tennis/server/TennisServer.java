package tennis.server;

import esy.es.tennis.net.ReceiverUDP;
import esy.es.tennis.net.SenderReceiverUDP;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static esy.es.tennis.shared.TennisAppConstants.*;

public class TennisServer
{
    private ExecutorService executorService = Executors.newCachedThreadPool();;
    private TreeMap<String, Player> players;
    private TreeMap<Integer, Game> games;
    private ReceiverUDP connectionHandler;
   // private Lock lock;
   // private Condition playerConnected;
   // private Board board;
    static final Object lock = new Object();

    public void init()
    {
        //lock = new ReentrantLock();
        //playerConnected = lock.newCondition();
        //players = new Player[2];
        //board = new Board();

        try
        {
            players = new TreeMap<>();
            games = new TreeMap<>();
            //server = new ServerSocket( PORT_NUMBER, 2 );
            //serverSocket = new DatagramSocket( UDP_PORT_NUMBER );
            connectionHandler = new ReceiverUDP(UDP_PORT_NUMBER);
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            System.exit(1);
        }
    }

//    private Pair<InetAddress, Integer> getPlayerAddress() throws IOException
//    {
//            byte[] data = new byte[10];
//            DatagramPacket packet = new DatagramPacket(data, 0, data.length);
//            serverSocket.receive(packet);
//            InetAddress host = packet.getAddress();
//            return new Pair<InetAddress, Integer>(host, packet.getPort());
//    }


    public Map<String, Player> getPlayers()
    {
        return players;
    }

    public void runServer()
    {
        while (true)
        {
            try
            {
                String mess = connectionHandler.receive();
                System.out.print(mess);
                SenderReceiverUDP handler = new SenderReceiverUDP(connectionHandler.getSenderAddress(), connectionHandler.getSenderPort());
                System.out.println("Sender address: " + connectionHandler.getSenderAddress());
                System.out.println("Sender port: " + connectionHandler.getSenderPort());
                String[] data = mess.split(Pattern.quote(separator));
                if (data.length == 2 && data[0].equals(connect) && !data[1].isEmpty() && !players.containsKey(data[1]))
                {
                    System.out.print( "valid message:" + mess);

                    String nickName = data[1];
                    Player player = new Player( handler, nickName, this);
                    players.put(nickName, player);
                    executorService.execute(player);    // receive/send messages from/to player
                    //handler.setDestinationAddress(InetAddress.getByName("192.168.1.2"));
                    handler.send(connect);
                    refreshPlayers();
                }
                else
                    handler.send(invalidNick);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

            /*try
            {
                for ( int i = 0; i < players.length; ++i)
                {
                    Pair<InetAddress, Integer> playerData = getPlayerAddress();
                    //if ( !tennisPlayers.values().stream().anyMatch( player -> playe ) )
                    players[i] = new Player(playerData.getKey(), playerData.getValue(), i, lock, playerConnected, board, players, serverSocket);
                    executorService.execute(players[i]);
                }


                lock.lock();

                try
                {
                    players[0].setEnabled(true);    // wake-up first player
                    players[1].setEnabled(true);
                    playerConnected.signalAll();
                }
                finally
                {
                    lock.unlock();
                }

                board.getBall().setIsMoving(true);
                board.moveBall( players[0] );
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
//            finally
//            {
//                try
//                {
//                    server.close();
//                }
//                catch (IOException e)
//                {
//                    System.out.println("An error occurred while closing the ServerSocket object");
//                }
//            }*/
    }

    public void sendMessageToPlayer( String playerNickName, String message )
    {
        Player toSend = players.get(playerNickName);
        if (toSend != null)
            toSend.sendMessage(message);
    }

    public boolean disconnectPlayer( String playerNickName )
    {
        if (players.containsKey(playerNickName))
        {
            players.remove(playerNickName);
            return true;
        }

        return false;
    }

    public void refreshPlayers()
    {
        StringBuilder message = new StringBuilder(playersList);
        for ( Player p : getPlayers().values())
            message.append(separator).append(p.getNickName()).append(separator2).append(p.getStatus());
//            handler.send(message.toString());
        final String mess = message.toString();

        synchronized (lock)
        {
            for ( Player p : getPlayers().values())
                p.sendMessage(mess);
        }

    }

    public void createNewGame(String firstPlayerName, String secondPlayerName) throws Exception
    {
        Player first = players.get(firstPlayerName);
        Player second = players.get(secondPlayerName);

        if (first != null && second != null )
        {
            int gameNr = getNewGameNumber();

            if (gameNr != -1)
            {
                Game game = new Game(first, second, gameNr, this);
                games.put( gameNr, game );
                first.setGameNumber(gameNr);
                second.setGameNumber(gameNr);
            }
            else
                throw new Exception("No more space for new games");
        }
    }

    private int getNewGameNumber()
    {
        for ( int i = 1; i <= maxGames; ++i)
        {
            if (!games.containsKey(i))
                return i;
        }

        return -1;
    }

    public void startGame(Integer gameNumber)
    {
        Game game = games.get(gameNumber);
        if (game != null)
        {
            executorService.execute( game );
            game.setGameStarted(true);
        }
    }

    public void removeGame(Integer gameNumber)
    {
        Game game = games.get(gameNumber);
        if (game != null)
        {
            game.setGameStarted(false);
            game.getFirstPlayer().setGameNumber(null);
            game.getSecondPlayer().setGameNumber(null);
            games.remove(gameNumber);
        }
    }

    public void updateClients( Integer gameNumber )
    {
        Game game = games.get(gameNumber);
        if (game != null)
        {
            String message = updateBoard + separator + game.getBoard().getBall().getX() + separator + game.getBoard().getBall().getY() + separator +
                    game.getBoard().getFirstPalette().getX() + separator + game.getBoard().getSecondPalette().getX();
            game.getFirstPlayer().sendMessage(message);
            game.getSecondPlayer().sendMessage(message);

            //System.out.println("Sent message to clients: " + message);
        }
    }

    public TreeMap<Integer, Game> getGames()
    {
        return games;
    }
}
