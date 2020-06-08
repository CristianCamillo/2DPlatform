package individuals;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import map.Map;
import utils.Utils;

public final class Player extends Individual
{	
	private final static byte INITIAL_HP = 1;
	
	private final static float GROUND_ACCELERATION = 30f;
	private final static float AIR_ACCELERATION = 20f;
	private final static float JUMP_SPEED = 21f;
	
	private final static float X_VELOCITY_CAP = 13f;
	private final static float Y_VELOCITY_CAP = 45f;
	
	private final static int WALKING_ANIMATION_PERIOD = 100;
	
	private long lastStepTime = 0;
	private int counter = 0;
	
	/*********************************************************************/
	/* Constructors                                                      */
	/*********************************************************************/
	
	public Player(Map map, Direction facingDirection) throws IOException
	{
		super(map, map.getStartingPosition(), facingDirection, INITIAL_HP, GROUND_ACCELERATION, AIR_ACCELERATION, JUMP_SPEED, X_VELOCITY_CAP, Y_VELOCITY_CAP);
		loadSprites();
	}
	
	public Player(Map map) throws IOException
	{
		this(map, Direction.RIGHT);
		loadSprites();
	}
	
	/*********************************************************************/
	/* Methods                                                           */
	/*********************************************************************/
	
	public void move(Direction direction, boolean tryJumping, float elapsedTime)
	{
		if(isAlive())
		{
			if(direction == Direction.LEFT)
				moveLeft(elapsedTime);	
			else if(direction == Direction.RIGHT)
				moveRight(elapsedTime);
			else
				slowDown(elapsedTime);
			
			if(tryJumping)
				tryJumping();
		}
		else
			slowDown(elapsedTime);
		
		moveVertically(elapsedTime);
	}
	
	public Image getCurrentSprite()
	{		
		if(!isAlive())				// is dead
			return sprite[8];
		
		if(!isOnGround())		    // is jumping/falling
			if(isFacingLeft())
				return sprite[2];
			else
				return sprite[6];		
		
		if(getXVelocity() == 0)     // is standing still
			if(isFacingLeft())
				return sprite[0];
			else
				return sprite[4];		
			
		if(isFacingLeft() && getXVelocity() > 0 || isFacingRight() && getXVelocity() < 0) // is drifting
			if(isFacingLeft())
				return sprite[3];
			else
				return sprite[7];

		if(isSlowing())				// is slowing
			if(isFacingLeft()) 
				return sprite[1];
			else
				return sprite[5];		
		
		counter += (System.nanoTime() - lastStepTime) / 1_000_000;
		lastStepTime = System.nanoTime();
		
		int span = WALKING_ANIMATION_PERIOD - (int)(Math.abs(getXVelocity() * 0.1f) + 0.5f);
		
		if(counter > span)
		{				
			if(counter > span * 2)
				counter = 0;
				
			if(isFacingLeft())
				return sprite[0];
			else
				return sprite[4];
		}
		else
		{					
			if(isFacingLeft())
				return sprite[1];
			else
				return sprite[5];
		}		
	}
	
	public boolean checkForCoins()
	{
		if(isAlive())
			if(map.tryPickUpCoin((int)(getX() + 0.2f), (int)(getY() + 0.2f)) ||
			   map.tryPickUpCoin((int)(getX() + 0.2f), (int)(getY() + 0.8f)) ||
			   map.tryPickUpCoin((int)(getX() + 0.8f), (int)(getY() + 0.2f)) ||
			   map.tryPickUpCoin((int)(getX() + 0.8f), (int)(getY() + 0.8f)))
				return true;
		
		return false;
	}
	
	public void checkForEnemies(ArrayList<Enemy> enemies)
	{
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy currentEnemy = enemies.get(i);
			
			if(currentEnemy.isAlive())
			{	
				float x = getX();
				float y = getY();
				
				float enemyX = currentEnemy.getX();
				float enemyY = currentEnemy.getY();
		
				if(enemyX <= x + 0.1 && x + 0.1 <= enemyX + 1.0 && enemyY <= y && y <= enemyY + 1.0)
					gotHit();
				else if(enemyX <= x + 0.9 && x + 0.9 <= enemyX + 1.0 && enemyY <= y && y <= enemyY + 1.0)
					gotHit();
				else if(x >= enemyX - 0.9 && x <= enemyX + 0.9 && enemyY - 1 < y && y < enemyY)
				{
					currentEnemy.gotHit();
					float jumpSpeed = getJumpSpeed();
					setJumpSpeed(jumpSpeed / 1.5f);
					tryJumping();
					setJumpSpeed(jumpSpeed);
				}
			}
		}
	}
	
	private void loadSprites() throws IOException
	{
		BufferedImage src = ImageIO.read(new File(SPR_PATH + "player.png"));
		
		BufferedImage standing0 = src.getSubimage(1, 1, 16, 16);
		BufferedImage standing1 = Utils.reflectHorizontally(standing0);
		
		BufferedImage walking0 = src.getSubimage(18, 1, 16, 16);
		BufferedImage walking1 = Utils.reflectHorizontally(walking0);
		
		BufferedImage jumping0 = src.getSubimage(35, 1, 16, 16);
		BufferedImage jumping1 = Utils.reflectHorizontally(jumping0);
		
		BufferedImage slowing0 = src.getSubimage(52, 1, 16, 16);
		BufferedImage slowing1 = Utils.reflectHorizontally(slowing0);
		
		BufferedImage lost = src.getSubimage(69, 1, 16, 16);
		
		sprite = new BufferedImage[] {standing0, walking0, jumping0, slowing0, standing1, walking1, jumping1, slowing1, lost};
	}
}