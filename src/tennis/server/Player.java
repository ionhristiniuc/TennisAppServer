package tennis.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import static esy.es.tennis.shared.TennisAppConstants.*;

public class Player implements Runnable
{
    private Socket connection;
    private int playerNumber;
    private Lock lock;
    private Condition playerConnected;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean enabled = false;
    private Board board;
    private Player[] players;

    public Player(Socket connection, int playerNumber, Lock lock, Condition playerConnected, Board board, Player[] players)
    {
        this.connection = connection;
        this.playerNumber = playerNumber;
        this.lock = lock;
        this.playerConnected = playerConnected;
        this.board = board;
        this.players = players;
    }

    @Override
    public void run()
    {
        try
        {
            getStreams();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if ( playerNumber == 0 )    // wait until second player connects
        {
            lock.lock();

            try
            {
                sendMessage( notification + separator + "Waiting for second player");

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

        try
        {
            listenPlayer();

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
        finally
        {
            try
            {
                closeConnection();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void getStreams() throws IOException
    {
        output = new ObjectOutputStream( connection.getOutputStream() );
        output.flush();

        input = new ObjectInputStream(connection.getInputStream());
    }

    private void sendMessage( String message )
    {
        try
        {
            output.writeObject(message);
            output.flush();
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

    private void listenPlayer()
    {
        String message = null;

        do
        {
            try
            {
                message = (String) input.readObject();
                if (message != null)
                {
                    //displayMessage("Received message from player " + playerNumber + ": " + message );
                    processMessage(message);
                }
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                displayMessage("An error occurred while reading from stream (player " + playerNumber + ")");
            }
            catch (ClassNotFoundException e)
            {
                //e.printStackTrace();
                displayMessage("\nInvalid object received from player " + playerNumber);
            }

        } while (message == null || !message.equals(disconnect));
    }

    public void closeConnection() throws IOException
    {
        output.close();
        input.close();
        connection.close();
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
                    updateClients();
                    break;
                case movePaletteRight:
                    movePalette(movePaletteRight);
                    updateClients();
                    break;
            }
        }
    }

    private void movePalette( String direction )
    {
        if (direction.equals(movePaletteLeft))
        {
            if (playerNumber == 0)
                board.getFirstPalette().setX( board.getFirstPalette().getX() - moveSpeed );
            else
                board.getSecondPalette().setX( board.getSecondPalette().getX() - moveSpeed );
        }
        else
        {
            if (playerNumber == 0)
                board.getFirstPalette().setX( board.getFirstPalette().getX() + moveSpeed );
            else
                board.getSecondPalette().setX( board.getSecondPalette().getX() + moveSpeed );
        }
    }

    public void updateClients()
    {
        lock.lock();

        String message = updateBoard + separator + board.getBall().getX() + separator + board.getBall().getY() + separator +
                board.getFirstPalette().getX() + separator + board.getSecondPalette().getX();

        try
        {
            players[0].sendMessage( message );
            players[1].sendMessage( message );
        }
        finally
        {
            lock.unlock();
        }
    }

    public void updateClientsBall()
    {
        lock.lock();

        String message = ballMove + separator + board.getBall().getX() + separator + board.getBall().getY();
        try
        {
            players[0].sendMessage( message );
            players[1].sendMessage( message );
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
}
