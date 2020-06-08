package exceptions;

public class IllegalPositionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public IllegalPositionException(int x, int y)
	{
		super("Illegal position: " + x + ", " + y + ".");
	}
}