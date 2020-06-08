package individuals;

import interfaces.AutoMovement;
import map.Map;

public abstract class Enemy extends Individual implements AutoMovement, Cloneable
{
	/*********************************************************************/
	/* Constructors                                                      */
	/*********************************************************************/
	
	protected Enemy(Map map, int[] position, Direction facingDirection, int INITIAL_HP, float GROUND_ACCELERATION, float AIR_ACCELERATION, float JUMP_SPEED, float X_VELOCITY_CAP, float Y_VELOCITY_CAP)
	{
		super(map, position, facingDirection, INITIAL_HP, GROUND_ACCELERATION, AIR_ACCELERATION, JUMP_SPEED, X_VELOCITY_CAP, Y_VELOCITY_CAP);
	}
	
	public void move(float elapsedTime){}
	
	public Enemy clone()
	{
		try
		{
			return (Enemy) super.clone();
		}
		catch(Exception e) { return null; }
	}
}