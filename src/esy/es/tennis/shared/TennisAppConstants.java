package esy.es.tennis.shared;

public interface TennisAppConstants
{
    int PORT_NUMBER = 11223;
    int UDP_PORT_NUMBER = 11224;
    String separator = "|";
    String notification = "NOTIF";
    String disconnect = "DISCONNECT";
    String updateBoard = "UPDATE_BOARD";
    String movePaletteLeft = "MOVE_P_LEFT";
    String movePaletteRight = "MOVE_P_RIGHT";
    String ballMove = "BALL";
    int boardWidth = 550;
    int boardHeight = 600;
    int paletteWidth = 70;
    int paletteHeight = 10;
    int ballDiameter = 10;
    int moveSpeed = 15;
    int ballSpeed = 1;
    int[] hitPlaces = { 5, 15, 25, 35, 45, 55, 65, 75, 85, 95, 100 };
    int[] stepX = { -3, -2, -2, -1, -1, 0, 1, 1, 2, 2, 3 };
}
