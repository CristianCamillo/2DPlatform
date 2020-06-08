package individuals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import map.Map;
import utils.Utils;

public final class Goomba extends Enemy
{
	private final static int INITIAL_LIFE = 1;
	
	private final static float GROUND_ACCELERATION = 9f;
	private final static float AIR_ACCELERATION = 4.5f;
	private final static float JUMP_SPEED = 12f;
	
	private final static float X_VELOCITY_CAP = 3f;
	private final static float Y_VELOCITY_CAP = 45f;
	
	private final static float WALKING_ANIMATION_PERIOD = 0.2f;
	private long currTime = 0;
	private long sprite0Time = 0;
	private int currentSprite = 0;
	
	/*********************************************************************/
	/* Constructors                                                      */
	/*********************************************************************/
	
	public Goomba(Map map, int[] position, Direction facingDirection) throws IOException
	{
		super(map, position, facingDirection, INITIAL_LIFE, GROUND_ACCELERATION, AIR_ACCELERATION, JUMP_SPEED, X_VELOCITY_CAP, Y_VELOCITY_CAP);
		loadSprites();
	}
	
	public Goomba(Map map, int[] position) throws IOException
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
		
			if(currTime > sprite0Time + WALKING_ANIMATION_PERIOD * 1_000_000_000)
			{
				currentSprite = 0;
				sprite0Time = currTime;
			}
			else if(currTime > sprite0Time + WALKING_ANIMATION_PERIOD  / 2 * 1_000_000_000)
				currentSprite = 1;
			
			return sprite[currentSprite];
		}
		else if(System.nanoTime() < currTime + 500_000_000)
			return sprite[2];
		else
			return null;
	}
	
	private void loadSprites() throws IOException
	{
		BufferedImage src = ImageIO.read(new File(SPR_PATH + "goomba.png"));
		
		BufferedImage walking0 = src.getSubimage(1, 1, 16, 16);
		BufferedImage walking1 = Utils.reflectHorizontally(walking0);
		BufferedImage dead = src.getSubimage(18, 1, 16, 16);
		
		sprite = new BufferedImage[] {walking0, walking1, dead};
	}
}