package exceptions;

public class IllegalTileException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public IllegalTileException()
	{
		super("Illegal tile.");
	}
}