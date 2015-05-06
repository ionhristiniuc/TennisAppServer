import tennis.server.TennisServer;

public class TennisServerTest
{

    public static void main(String[] args)
    {
        TennisServer server = new TennisServer();
        server.init();
        server.runServer();
    }
}
