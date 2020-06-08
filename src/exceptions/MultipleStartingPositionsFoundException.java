package exceptions;

public class MultipleStartingPositionsFoundException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public MultipleStartingPositionsFoundException()
	{
		super("Multiple starting positions found.");
	}
}