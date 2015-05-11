package tennis.server;

public class Game implements Runnable
{
    private Player firstPlayer;
    private Player secondPlayer;
    private Board board;
    private Integer gameNumber;
    private final TennisServer server;
    private boolean gameStarted = false;

    public Game(Player firstPlayer, Player secondPlayer, int gameNumber, TennisServer server)
    {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.gameNumber = gameNumber;
        this.server = server;
        board = new Board();
    }

    public boolean isGameStarted()
    {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted)
    {
        this.gameStarted = gameStarted;
    }

    public Player getFirstPlayer()
    {
        return firstPlayer;
    }

    public Player getSecondPlayer()
    {
        return secondPlayer;
    }

    @Override
    public void run()
    {
        board.getBall().setIsMoving(true);
        board.moveBall(this);
    }

    public void pause()
    {
        board.getBall().setIsMoving(false);
    }

    public Board getBoard()
    {
        return board;
    }

    public Integer getGameNumber()
    {
        return gameNumber;
    }

    public void updateClients()
    {
        server.updateClients(this.gameNumber);
    }
}
