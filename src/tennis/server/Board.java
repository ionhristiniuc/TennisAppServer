package tennis.server;

import esy.es.tennis.shared.Ball;
import esy.es.tennis.shared.Palette;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static esy.es.tennis.shared.TennisAppConstants.*;

public class Board
{
    private Palette firstPalette;
    private Palette secondPalette;
    private Ball ball;
    private int width;
    private int height;
    private ExecutorService executorService;

    public Board()
    {
        this.executorService = Executors.newFixedThreadPool(1);
        width = boardWidth;
        height = boardHeight;
        firstPalette = new Palette(boardWidth / 2 - paletteWidth / 2, boardHeight - paletteHeight * 2, paletteWidth, paletteHeight);
        secondPalette = new Palette(boardWidth / 2 - paletteWidth / 2, 0, paletteWidth, paletteHeight);
        ball = new Ball(boardWidth / 2 - ballDiameter / 2, boardHeight / 2 - ballDiameter / 2, ballDiameter, ballSpeed, ballSpeed);
    }

    public Ball getBall()
    {
        return ball;
    }

    public Palette getFirstPalette()
    {
        return firstPalette;
    }

    public void setFirstPalette(Palette firstPalette)
    {
        this.firstPalette = firstPalette;
    }

    public Palette getSecondPalette()
    {
        return secondPalette;
    }

    public void setSecondPalette(Palette secondPalette)
    {
        this.secondPalette = secondPalette;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setBall(Ball ball)
    {
        this.ball = ball;
    }

    public void moveBall( Game game )
    {

        executorService.execute(() -> {
            final int centerX = boardWidth / 2 - ballDiameter / 2;
            final int centerY = boardHeight / 2 - ballDiameter / 2;

            while ( getBall().isMoving() )
            {
                int stepX = getBall().getSpeedX();
                int stepY = getBall().getSpeedY();

                try
                {
                    Thread.sleep(7);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (getBall().getX() + stepX < 0 || getBall().getX() + stepX > getWidth() - getBall().getDiameter())    // if wall
                    stepX = -stepX;

                // if the ball will bump with upper (second) palette
                if (getBall().getY() + stepY - paletteHeight < 0 && getBall().getX() >= getSecondPalette().getX() - getBall().getDiameter() + 1 &&
                        getBall().getX() <= getSecondPalette().getX() + getSecondPalette().getWidth() - 1 )
                {
                    stepY = -stepY;
                    double ballCenter = getBall().getX() + getBall().getDiameter() / 2;
                    double hitPlace = percentage( getSecondPalette().getX(), getSecondPalette().getX() + getSecondPalette().getWidth(), ballCenter );
                    stepX = getStepX( hitPlace );

                }
                else
                    if ( getBall().getY() + stepY - paletteHeight < 0 )     // if the ball went off
                    {
                        getBall().setX(centerX);
                        getBall().setY(centerY);
                        stepX = ballSpeed;
                        stepY = ballSpeed;
                    }

                // if the ball will bump with lower (first) palette
                if (getBall().getY() + getBall().getDiameter() + getFirstPalette().getHeight() >= getHeight() &&
                      getBall().getX() >= getFirstPalette().getX() - getBall().getDiameter() + 1
                        && getBall().getX() <= getFirstPalette().getX() + getFirstPalette().getWidth())
                {
                    stepY = -stepY;
                    double ballCenter = getBall().getX() + getBall().getDiameter() / 2;
                    double hitPlace = percentage( getFirstPalette().getX(), getFirstPalette().getX() + getFirstPalette().getWidth(), ballCenter );
                    stepX = getStepX( hitPlace );
                }
                else
                    if ( getBall().getY() + getBall().getDiameter() + getFirstPalette().getHeight() >= getHeight() )
                    {
                        getBall().setX(centerX);
                        getBall().setY(centerY);
                        stepX = ballSpeed;
                        stepY = ballSpeed;
                    }

                getBall().setX(getBall().getX() + stepX);
                getBall().setY(getBall().getY() + stepY);

                //player.updateClients();     // send message to clients about ball position
                getBall().setSpeedX(stepX);
                getBall().setSpeedY(stepY);

               game.updateClients();
            }
        });
    }

    private double percentage( double from, double to, double number )          // ex: 10, 30, 20 - returns 50
    {
        double length = to - from;
        double one = length / 100;
        return (number - from) / one;
    }

    private int getStepX(double hitPl)
    {
        for ( int i = 0; i < stepX.length; ++i )
            if ( hitPl <= hitPlaces[i] )
                return stepX[i];

        return 0;
    }
}
