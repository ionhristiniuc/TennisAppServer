package esy.es.tennis.shared;

import java.awt.*;

/**
 * abstract tennis ball
 */
public class Ball
{
    private int x;
    private int y;
    private int diameter;
    private boolean isMoving = false;

    public Ball(int x, int y, int diameter)
    {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getDiameter()
    {
        return diameter;
    }

    public void setDiameter(int diameter)
    {
        this.diameter = diameter;
    }

    public void setIsMoving(boolean isMoving)
    {
        this.isMoving = isMoving;
    }

    public boolean isMoving()
    {
        return isMoving;

    }
}
