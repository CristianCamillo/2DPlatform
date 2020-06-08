package individuals;

import java.awt.image.BufferedImage;

import interfaces.Drawable;
import map.Map;
import utils.Utils;

public abstract class Individual implements Drawable
{
	public enum Direction {LEFT, RIGHT};
	
	protected Map map;
	
	private float x;
	private float y;
	private float xVelocity = 0f;
	private float yVelocity = 0f;
	private boolean onGround = false;
	private Direction facingDirection;
	private boolean slowing;
	
	private int hp;
	
	private float groundAcceleration;
	private float airAcceleration;
	private float jumpSpeed;
	
	private float xVelocityCap;
	private float yVelocityCap;
	
	protected BufferedImage[] sprite;
	
	protected final static String SPR_PATH = "sprites/";
	
	/*********************************************************************/
	/* Constructors                                                      */
	/*********************************************************************/
	
	public Individual(Map map, int[] position, Direction facingDirection, int hp, float groundAcceleration, float airAcceleration, float jumpSpeed, float xVelocityCap, float yVelocityCap)
	{
		this.map = map;
		
		x = position[0];
		y = position[1];
		this.facingDirection = facingDirection;
		
		this.hp = hp;
		
		this.groundAcceleration = groundAcceleration;
		this.airAcceleration = airAcceleration;
		this.jumpSpeed = jumpSpeed;
		
		this.xVelocityCap = xVelocityCap;
		this.yVelocityCap = yVelocityCap;
	}
	
	/*********************************************************************/
	/* Getters                                                           */
	/*********************************************************************/
	
	public float getX() 		   { return x; }	
	public float getY()			   { return y;	}	
	public float getXVelocity()	   { return xVelocity; }	
	public float getYVelocity()	   { return yVelocity; }	
	public boolean isOnGround()	   { return onGround; }	
	public boolean isFacingLeft()  { return facingDirection == Direction.LEFT; }	
	public boolean isFacingRight() { return facingDirection == Direction.RIGHT; }	
	public boolean isSlowing()     { return slowing; }	
	public int getHP()             { return hp;	}	
	public boolean isAlive()       { return (hp > 0) ? true : false; }	
	public float getJumpSpeed()    { return jumpSpeed; }
	
	/*********************************************************************/
	/* Setters                                                           */
	/*********************************************************************/
	
	public void setJumpSpeed(float jumpSpeed) { this.jumpSpeed = jumpSpeed; }
	
	/*********************************************************************/
	/* Movement                                                          */
	/*********************************************************************/
	
	protected void moveLeft(float elapsedTime)
	{
		moveSideway(elapsedTime, Direction.LEFT);
	}
	
	protected void moveRight(float elapsedTime)
	{		
		moveSideway(elapsedTime, Direction.RIGHT);
	}
	
	private void moveSideway(float elapsedTime, Direction direction)
	{
		int dir = direction == Direction.LEFT ? - 1 : 1;
		
		if(xVelocity == 0)
			x += 0.1f * dir;
		xVelocity += (onGround ? groundAcceleration : airAcceleration) * elapsedTime * dir;
		xVelocity = Utils.cap(xVelocity, xVelocityCap * dir);
		facingDirection = direction;
		
		solveXCollision(elapsedTime);
		
		slowing = false;
	}
	
	protected void moveVertically(float elapsedTime)
	{
		yVelocity += map.getGravity() * elapsedTime;
		yVelocity = Utils.cap(yVelocity, yVelocityCap);

		solveYCollision(elapsedTime);
	}
	
	protected void tryJumping()
	{
		if(onGround)
			yVelocity -= jumpSpeed;
	}
	
	protected void slowDown(float elapsedTime)
	{
		Direction currentDirection = facingDirection;
		
		if(xVelocity > 0)
			moveLeft(elapsedTime);
		else if(xVelocity < 0)
			moveRight(elapsedTime);
		
		if(Math.abs(xVelocity) < 0.3f)
			xVelocity = 0f;
		
		slowing = true;
		facingDirection = currentDirection;
	}
	
	private void solveXCollision(float elapsedTime)
	{
		float newX = x + xVelocity * elapsedTime;
		
		if(newX < 0) // out to left side of the map
		{
			newX = 0;
			xVelocity = 0;
		}
		else if(newX >= map.getWidth() - 1) // out to right side of the map
		{
			newX = map.getWidth() - 1;
			xVelocity = 0;
		}
		else if(xVelocity < 0f) // moving left into tile
		{
			for(int i = (int)x; i >= (int)newX; i--)
				if(map.checkSolidBlock((int)(i + 0.0f), (int)(y + 0.0f)) || map.checkSolidBlock((int)(i + 0.0f), (int)(y + 0.9f)))
				{
					newX = i + 1;
					xVelocity = 0f;
					break;
				}
		}
		else if(xVelocity > 0f) // moving right into tile
		{
			for(int i = (int)x; i <= (int)newX; i++)
				if(map.checkSolidBlock((int)(i + 1.0f), (int)(y + 0.0f)) || map.checkSolidBlock((int)(i + 1.0f), (int)(y + 0.9f)))
				{
					newX = i;
					xVelocity = 0f;
					break;
				}
		}
		
		x = newX;
	}
	
	private void solveYCollision(float elapsedTime)
	{
		float newY = y + yVelocity * elapsedTime;
		
		onGround = false;			
		if(yVelocity < 0f)
		{
			for(int i = (int)y; i >= (int)newY; i--)
				if(map.checkSolidBlock((int)(x + 0.1f), (int)(i + 0.0f)) || map.checkSolidBlock((int)(x + 0.9f), (int)(i + 0.0f)))
				{
					newY = i + 1;
					yVelocity = 0f;
					break;
				}
		}
		else if(yVelocity > 0f)
		{
			for(int i = (int)y; i <= (int)newY; i++)
				if(map.checkSolidBlock((int)(x + 0.1f), (int)(i + 1.0f)) || map.checkSolidBlock((int)(x + 0.9f), (int)(i + 1.0f)))
				{
					newY = i;
					yVelocity = 0f;
					onGround = true;
					break;
				}
		}
		
		y = newY;
	}
	
	/**/
	
	protected void gotHit()
	{
		hp--;
	}	
}