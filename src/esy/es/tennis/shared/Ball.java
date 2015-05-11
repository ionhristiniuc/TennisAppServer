package esy.es.tennis.shared;

/**
 * abstract tennis ball
 */
public class Ball
{
    private int x;
    private int y;
    private int diameter;
    private boolean isMoving = false;
    private int speedX;
    private int speedY;

    public Ball(int x, int y, int diameter, int speedX, int speedY)
    {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.speedX = speedX;
        this.speedY = speedY;
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

    public int getSpeedY()
    {
        return speedY;
    }

    public void setSpeedY(int speedY)
    {
        this.speedY = speedY;
    }

    public int getSpeedX()
    {
        return speedX;
    }

    public void setSpeedX(int speedX)
    {
        this.speedX = speedX;
    }
}
