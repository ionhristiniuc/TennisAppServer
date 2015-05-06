package esy.es.tennis.shared;

import java.awt.*;

public interface TennisAppConstants
{
    int PORT_NUMBER = 11223;
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
    int moveSpeed = 7;
    int ballSpeed = 1;
}
