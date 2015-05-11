package esy.es.tennis.shared;

public interface TennisAppConstants
{
    int PORT_NUMBER = 11223;
    int UDP_PORT_NUMBER = 11224;
    String separator = "|";
    String separator2 = ":";
    String notification = "NOTIF";
    String connect = "CONNECT";
    String disconnect = "DISCONNECT";
    String invalidNick = "INV_NICK";
    String updateBoard = "UPDATE_BOARD";
    String movePaletteLeft = "MOVE_P_LEFT";
    String movePaletteRight = "MOVE_P_RIGHT";
    String ballMove = "BALL";
    String playersList = "PLAYERS_LIST";
    String askPlay = "ASK_PLAY";
    String respPlay = "RESP_PLAY";
    String occupied = "OCCUPIED";
    String createdBoard = "CREATED_BOARD";
    String gameStopped = "GAME_STOPPED";
    String yes = "YES";
    String no = "NO";
    String error = "ERROR";
    String startGame = "START_GAME";
    String startGameSec = "START_GAME_SEC";
    int FREE_ST = 0;
    int PLAYING_ST = 1;
    int boardWidth = 550;
    int boardHeight = 600;
    int paletteWidth = 70;
    int paletteHeight = 10;
    int ballDiameter = 10;
    int moveSpeed = 13;
    int ballSpeed = 2;
    int[] hitPlaces = { 5, 15, 25, 35, 45, 55, 65, 75, 85, 95, 100 };
    int[] stepX = { -3, -2, -2, -1, -1, 0, 1, 1, 2, 2, 3 };
    int maxPacketLength = 300;
    int maxGames = 200;
}
