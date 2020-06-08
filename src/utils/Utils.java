package utils;

import java.awt.image.BufferedImage;

public class Utils
{	
	public static float cap(float value, float cap)
	{
		if(Math.abs(value) > Math.abs(cap))
			return cap;
		else
			return value;
	}
	
	public static BufferedImage[] loadTileSet(BufferedImage src, int tileSize, int space)
	{
		int nTile = src.getWidth() / (tileSize + space);
		BufferedImage[] tileSet = new BufferedImage[nTile];
		
		for(int i = 0; i < nTile; i++)
			tileSet[i] = src.getSubimage(space + (tileSize + space) * i, space, tileSize, tileSize);
		
		return tileSet;		
	}
	
	public static BufferedImage reflectHorizontally(BufferedImage image)
	{
		BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		out.createGraphics().drawImage(image, image.getWidth(), 0, - image.getWidth(), image.getHeight(), null);
		return out;
	}
}