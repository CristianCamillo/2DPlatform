package individuals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import map.Map;
import utils.Utils;

public final class Turtle extends Enemy
{
	private final static int INITIAL_LIFE = 1;
	
	private final static float GROUND_ACCELERATION = 25f;
	private final static float AIR_ACCELERATION = 15f;
	private final static float JUMP_SPEED = 20f;
	
	private final static float X_VELOCITY_CAP = 15f;
	private final static float Y_VELOCITY_CAP = 45f;
	
	private final static float WALKING_ANIMATION_PERIOD = 0.05f ;
	private long currTime = 0;
	private long sprite0Time = 0;
	private int currentSprite = 0;
	
	/*********************************************************************/
	/* Constructors                                                      */
	/*********************************************************************/
	
	public Turtle(Map map, int[] position, Direction facingDirection) throws IOException
	{
		super(map, position, facingDirection, INITIAL_LIFE, GROUND_ACCELERATION, AIR_ACCELERATION, JUMP_SPEED, X_VELOCITY_CAP, Y_VELOCITY_CAP);
		loadSprites();
	}
	
	public Turtle(Map map, int[] position) throws IOException
	{
		this(map, position, Direction.RIGHT);
		loadSprites();
	}
	
	/*********************************************************************/
	/* Other methods                                                     */
	/*********************************************************************/

	public void move(float elapsedTime)
	{		
		if(isAlive())
		{
			if(getXVelocity() == 0f)
			{
				if(isFacingLeft())
					moveRight(elapsedTime);
				else
					moveLeft(elapsedTime);
				tryJumping();
			}
			
			if(isFacingLeft())
				moveLeft(elapsedTime);
			else
				moveRight(elapsedTime);
		}
		
		moveVertically(elapsedTime);
	}
	
	public BufferedImage getCurrentSprite()
	{
		if(isAlive())
		{
			currTime = System.nanoTime();
			
			if(sprite0Time == 0)
				sprite0Time = currTime;
			
			if(currTime > sprite0Time + (currentSprite + 1) * WALKING_ANIMATION_PERIOD * 1_000_000_000)
				currentSprite++;
			
			if(currentSprite > 3)
			{
				currentSprite = 0;
				sprite0Time = currTime;
			}
			
			return sprite[currentSprite];
		}
		else if(System.nanoTime() < currTime + 500_000_000)
			return sprite[currentSprite];
		else
			return null;
	}
	
	private void loadSprites() throws IOException
	{
		BufferedImage[] tiles = Utils.loadTileSet(ImageIO.read(new File(SPR_PATH + "turtle.png")), 16, 1);
		sprite = new BufferedImage[] {tiles[0], tiles[1], tiles[2], Utils.reflectHorizontally(tiles[1])};
	}
}