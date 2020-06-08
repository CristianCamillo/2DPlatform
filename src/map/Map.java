package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import exceptions.IllegalPositionException;
import exceptions.IllegalTileException;
import exceptions.MultipleStartingPositionsFoundException;
import exceptions.NonExistingEnemyException;
import exceptions.StartingPositionNotFoundException;
import individuals.Enemy;
import individuals.Goomba;
import individuals.Turtle;
import utils.Utils;

public final class Map
{
	private final String mapName;	
	private final int width;
	private final int height;
	private final int gravity;
	private final String backgroundFileName;
	private final BufferedImage background;	
	private String mapString = "";
	
	private int[] startingPosition = null;
	private ArrayList<Enemy> enemies = null;
	
	private final static String MAP_PATH = "maps/";
	private final static String SPR_PATH = "sprites/";
	private final static String BG_PATH = "backgrounds/";
	
	final static String START_POSITION_ID = "ST";
	final static String FINISH_POSITION_ID = "FI";	
	final static String NULL_ID = "..";
	final static String COIN_ID = "OO";
	final static String BRICK_ID = "BR";
	final static String ground_ID = "G";
	final static String rock_ID = "R";
	final static String WOODEN_BLOCK_ID = "WB";
	
	final static String ENEMY_ID = "E";
	
	final static String OUT_ID = "out";
	
	private BufferedImage start;
	private BufferedImage[] brick;
	private BufferedImage[] coin;
	private BufferedImage[] ground;
	private BufferedImage[] rock;
	private BufferedImage woodenBlock;
	
	private BufferedImage missing;
	
	private BufferedImage goomba;
	private BufferedImage turtle;
	
	private long brick0Time = 0;
	private long coin0Time = 0;
	private int brickCurrentSprite = 0;
	private int coinCurrentSprite = 0;

	/*********************************************************************/
	/* Constructors                                                      */
	/*********************************************************************/	
	
	public Map(String mapName) throws IOException
	{		
		this.mapName = mapName;
		
		Scanner sca = new Scanner(new FileReader(MAP_PATH + mapName + ".map"));
	
		width = sca.nextInt();
		height = sca.nextInt();
		gravity = sca.nextInt();
		backgroundFileName = sca.next();
		
		background = ImageIO.read(new File(BG_PATH + backgroundFileName));
		
		sca.nextLine();
		
		for(int i = 0; i < height; i++)
		{
			String buffer = sca.nextLine();
			if(buffer.length() != width * 2)
			{
				sca.close();
				throw new IOException("Illegal length of line " + i);
			}
			mapString += buffer;
		}

		sca.close();
		
		printMap();
		
		loadTileSprites();
		findStartingPosition();
		gatherEnemies();
	}
	
	public Map(String mapName, int width, int height, int gravity, String backgroundFileName, String mapString) throws IOException
	{		
		this.mapName = mapName;	
		this.width = width;
		this.height = height;
		this.gravity = gravity;
		this.backgroundFileName = backgroundFileName;
		
		background = ImageIO.read(new File(BG_PATH + backgroundFileName));
		
		this.mapString = mapString;
		
		printMap();
		
		loadTileSprites();
		findStartingPosition();
		gatherEnemies();
	}
	
	private void findStartingPosition() throws StartingPositionNotFoundException, MultipleStartingPositionsFoundException
	{
		int position = mapString.indexOf(START_POSITION_ID);
		
		if(position == -1 || position % 2 == 1)
			throw new StartingPositionNotFoundException();
		
		int leftoverPosition = mapString.substring(position + 2).indexOf(START_POSITION_ID);
		
		if(leftoverPosition != -1 && leftoverPosition % 2 == 0)
			throw new MultipleStartingPositionsFoundException();
		
		startingPosition = getCoordinatesFromInt(position);
		
		removeTile(startingPosition[0], startingPosition[1]);
	}
	
	private void gatherEnemies() throws IOException
	{
		enemies = new ArrayList<Enemy>();
		
		int position;
		
		while((position = mapString.indexOf(ENEMY_ID)) != -1 && position % 2 == 0)
		{
			int[] pos = getCoordinatesFromInt(position);
						
			switch(getTileID(pos[0], pos[1]).substring(1))
			{
				case "0":
					enemies.add(new Goomba(this, pos));
					break;
				case "1":
					enemies.add(new Turtle(this, pos));
					break;
				default:
					throw new NonExistingEnemyException(pos[0], pos[1]);
			}
			
			removeTile(pos[0], pos[1]);
		}
	}
	
	/*********************************************************************/
	/* Getters                                                           */
	/*********************************************************************/
	
	public String getMapName()
	{
		return mapName;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getGravity()
	{
		return gravity;
	}
	
	public String getBackgroundFileName()
	{
		return backgroundFileName;
	}
	
	public BufferedImage getBackground()
	{
		return background;
	}
	
	String getTileID(int x, int y)
	{
		if(x >= 0 && x < width && y >= 0 && y < height)
			return mapString.substring(y * width * 2 + x * 2, y * width * 2 + x * 2 + 2);
		else
			return OUT_ID;
	}
	
	public BufferedImage getTileSprite(String tileID)
	{
		long currTime = System.nanoTime();
		
		switch(tileID)
		{
			case START_POSITION_ID:
				return start;
			case NULL_ID:
				return null;
			case COIN_ID:					
				if(currTime > coin0Time + 130_000_000l * (coinCurrentSprite + 1))
					coinCurrentSprite++;
				
				if(coinCurrentSprite > 7)
				{
					coinCurrentSprite = 0;
					coin0Time = currTime;
				}
				
				return coin[(coinCurrentSprite <= 4 ? coinCurrentSprite : 8 - coinCurrentSprite)];
			case BRICK_ID:						
				if(currTime > brick0Time + 120_000_000l * (brickCurrentSprite + 1))
					brickCurrentSprite++;
				
				if(brickCurrentSprite > 3)
				{
					brickCurrentSprite = 0;
					brick0Time = currTime;
				}	
				
				return brick[brickCurrentSprite];
			case ground_ID + "0":
				return ground[0];
			case ground_ID + "1":
				return ground[1];
			case ground_ID + "2":
				return ground[2];
			case ground_ID + "3":
				return ground[3];
			case ground_ID + "4":
				return ground[4];
			case ground_ID + "5":
				return ground[5];
			case ground_ID + "6":
				return ground[6];
			case ground_ID + "7":
				return ground[7];
			case ground_ID + "8":
				return ground[8];
			case ground_ID + "9":
				return ground[9];
			case ground_ID + "A":
				return ground[10];
			case ground_ID + "B":
				return ground[11];
			case ground_ID + "C":
				return ground[12];
			case rock_ID + "0":
				return rock[0];
			case rock_ID + "1":
				return rock[1];
			case rock_ID + "2":
				return rock[2];
			case rock_ID + "3":
				return rock[3];
			case rock_ID + "4":
				return rock[4];
			case rock_ID + "5":
				return rock[5];
			case rock_ID + "6":						
				return rock[6];
			case rock_ID + "7":						
				return rock[7];
			case rock_ID + "8":
				return rock[8];
			case WOODEN_BLOCK_ID:
				return woodenBlock;
			case ENEMY_ID + "0":
				return goomba;
			case ENEMY_ID + "1":
				return turtle;
			default:
				return missing;					
		}
	}
	
	public BufferedImage getTileSprite(int x, int y)
	{
		return getTileSprite(getTileID(x, y));
	}
	
	public int[] getStartingPosition()
	{				
		return new int[] {startingPosition[0], startingPosition[1]};
	}
	
	public ArrayList<Enemy> getEnemies()
	{
		ArrayList<Enemy> clones = new ArrayList<Enemy>();
		
		for(int i = 0; i < enemies.size(); i++)
			clones.add(enemies.get(i).clone());
		
		return clones;
	}
	
	/*********************************************************************/
	/* Setters                                                           */
	/*********************************************************************/
	
	void setTile(int x, int y, String tile) throws IllegalTileException, IllegalPositionException
	{
		if(tile.length() != 2)
			throw new IllegalTileException();
		
		if(x >= 0 && x < width && y >= 0 && y < height)
			mapString = mapString.substring(0, y * width * 2 + x * 2) + tile + mapString.substring(y * width * 2 + x * 2 + 2);
		else
			throw new IllegalPositionException(x, y);
	}
	
	/*********************************************************************/
	/* Other methods                                                     */
	/*********************************************************************/
	
	public void printMap()
	{
		System.out.println(width + " " + height + " " + gravity + " " + backgroundFileName);
		for(int i = 0; i < height; i++)
			System.out.println(mapString.substring(width * 2 * i, width * 2 * (i + 1)));
	}
	
	void saveOnFile() throws IOException
	{
		File f = new File(MAP_PATH + mapName);
		FileWriter fw = new FileWriter(f);
		
		fw.append(width + " " + height + " " + gravity + " " + backgroundFileName + "\n");
		for(int j = 0 ; j < height; j++)
			fw.append(mapString.substring(j * width * 2, (j + 1) * width * 2) + "\n");
		
		fw.close();
	}
	
	public boolean checkSolidBlock(int x, int y)
	{
		String tileID = getTileID(x, y);
		if(tileID.equals(NULL_ID) ||
		   tileID.equals(COIN_ID) ||
		   tileID.equals(OUT_ID))
			return false;
		else
			return true;
	}
	
	public boolean tryPickUpCoin(int x, int y)
	{
		if(getTileID(x, y).equals(COIN_ID))
		{
			removeTile(x, y);
			return true;
		}
		else
			return false;
	}
	
	private void removeTile(int x, int y)
	{
		setTile(x, y, NULL_ID);
	}
	
	private int[] getCoordinatesFromInt(int position)
	{
		int[] pos = new int[2];
		pos[0] = ((position % (width * 2)) / 2);
		pos[1] = (position / (width * 2));
		
		return pos;
	}
	
	private void loadTileSprites() throws IOException
	{
		start = ImageIO.read(new File(SPR_PATH + "start.png"));

		brick = Utils.loadTileSet(ImageIO.read(new File(SPR_PATH + "brick.png")), 16, 1);		
		coin = Utils.loadTileSet(ImageIO.read(new File(SPR_PATH + "coin.png")), 16, 1);		
		ground = Utils.loadTileSet(ImageIO.read(new File(SPR_PATH + "ground.png")), 16, 1);		
		rock = Utils.loadTileSet(ImageIO.read(new File(SPR_PATH + "rock.png")), 16, 1);		
		woodenBlock = ImageIO.read(new File(SPR_PATH + "woodenBlock.png"));
		
		missing = ImageIO.read(new File(SPR_PATH + "missing.png"));
		
		goomba = ImageIO.read(new File(SPR_PATH + "goomba.png")).getSubimage(1, 1, 16, 16);
		turtle = ImageIO.read(new File(SPR_PATH + "turtle.png")).getSubimage(1, 1, 16, 16);
	}
}