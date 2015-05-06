package tennis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static esy.es.tennis.shared.TennisAppConstants.*;

public class TennisServer
{
    private ExecutorService executorService;
    private Player[] players;
    private ServerSocket server;
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
            server = new ServerSocket( PORT_NUMBER, 2 );
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            System.out.println("Could not create ServerSocket object");
            System.exit(1);
        }
    }

    public void runServer()
    {
            try
            {
                for ( int i = 0; i < players.length; ++i)
                {
                    players[i] = new Player(server.accept(), i, lock, playerConnected, board, players);
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
