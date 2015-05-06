package tennis.server;

import esy.es.tennis.shared.Ball;
import esy.es.tennis.shared.Palette;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

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
        ball = new Ball(boardWidth / 2 - ballDiameter / 2, boardHeight / 2 - ballDiameter / 2, ballDiameter);
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

    public void moveBall( Player player )
    {

        executorService.execute(() -> {
            int stepX = ballSpeed;
            int stepY = ballSpeed;
            int centerX = boardWidth / 2 - ballDiameter / 2;
            int centerY = boardHeight / 2 - ballDiameter / 2;

            while ( getBall().isMoving() )
            {
                try
                {
                    Thread.sleep(8);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (getBall().getX() + stepX < 0 || getBall().getX() + stepX > getWidth() - getBall().getDiameter())
                    stepX = -stepX;

                if (getBall().getY() + stepY - paletteHeight < 0 && getBall().getX() >= getSecondPalette().getX() &&
                        getBall().getX() <= getSecondPalette().getX() + getSecondPalette().getWidth() )
                    stepY = -stepY;
                else
                    if ( getBall().getY() + stepY - paletteHeight < 0 )
                    {
                        getBall().setX(centerX);
                        getBall().setY(centerY);
                    }

                if (getBall().getY() + getBall().getDiameter() + getFirstPalette().getHeight() >= getHeight() &&
                      getBall().getX() >= getFirstPalette().getX() && getBall().getX() <= getFirstPalette().getX() + getFirstPalette().getWidth())
                    stepY = -stepY;
                else
                    if ( getBall().getY() + getBall().getDiameter() + getFirstPalette().getHeight() >= getHeight() )
                    {
                        getBall().setX(centerX);
                        getBall().setY(centerY);
                    }

                getBall().setX( getBall().getX() + stepX );
                getBall().setY( getBall().getY() + stepY );

                player.updateClientsBall();
            }

        });

    }
}
