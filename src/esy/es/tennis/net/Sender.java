package esy.es.tennis.net;

import java.io.IOException;

public interface Sender
{
    void send(String message) throws IOException;
}
