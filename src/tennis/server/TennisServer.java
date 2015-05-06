package tennis.server;

import javafx.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static esy.es.tennis.shared.TennisAppConstants.UDP_PORT_NUMBER;

public class TennisServer
{
    private ExecutorService executorService;
    private Player[] players;
    //private ServerSocket server;
    private DatagramSocket serverSocket;
    private Lock lock;
    private Condition playerConnected;
    private Board board;

    public void init()
    {
        executorService = Executors.newFixedThreadPool(2);
        lock = new ReentrantLock();
        playerConnected = lock.newCondition();
        players = new Player[2];
        board = new Board();

        try
        {
            //server = new ServerSocket( PORT_NUMBER, 2 );
            serverSocket = new DatagramSocket( UDP_PORT_NUMBER );
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            System.out.println("Could not create ServerSocket object");
            System.exit(1);
        }
    }

    private Pair<InetAddress, Integer> getPlayerAddress() throws IOException
    {
            byte[] data = new byte[10];
            DatagramPacket packet = new DatagramPacket(data, 0, data.length);
            serverSocket.receive(packet);
            InetAddress host = packet.getAddress();
            return new Pair<InetAddress, Integer>(host, packet.getPort());
    }

    public void runServer()
    {
            try
            {
                for ( int i = 0; i < players.length; ++i)
                {
                    Pair<InetAddress, Integer> playerData = getPlayerAddress();
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
//            }
    }


}
