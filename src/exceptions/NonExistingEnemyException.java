package exceptions;

public class NonExistingEnemyException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NonExistingEnemyException()
	{
		super("Non-existing enemy.");
	}
	
	public NonExistingEnemyException(int x, int y)
	{
		super("Non-existing enemy at: " + x + ", " + y + ".");
	}
}