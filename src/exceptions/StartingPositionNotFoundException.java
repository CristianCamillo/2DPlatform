package exceptions;

public class StartingPositionNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public StartingPositionNotFoundException()
	{
		super("Starting position not found.");
	}
}