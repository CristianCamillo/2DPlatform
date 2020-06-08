package map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Scanner;

import gameEngine.FeatherEngine;

public class LevelEditor
{
	private final static int WIDTH = 1600;
	private final static int HEIGHT = 900;
	private final static String TITLE = "Plagio Editor";
	private final static int FPS_CAP = 0;
	private final static boolean SHOW_FPS = true;
	private final static boolean FULLSCREEN = false;
	
	private float x = 16;
	private float y = 9;
	private final short tileWidth = (short) (HEIGHT / 18);
	private final short tileHeight = (short) (HEIGHT / 18);	
	private short visibleX = (short) (WIDTH / tileWidth);
	private short visibleY = (short) (HEIGHT / tileHeight);	
	private float cameraX = 0f;
	private float cameraY = 0f;
	
	private String mapName;
	private short width;
	private short height;
	private short gravity;
	private String backgroundFileName;
	private String mapString = "";
	
	private short selectedTile = 0;
	
	private Map map;
	
	public LevelEditor() throws Exception
	{
		mapName = "testingMap2.map";

		map = new Map(mapName);
		
		map.getEnemies();
		/*Scanner in = new Scanner(System.in);
		
		System.out.print("Open existing map? (y/n) ");
		
		String opt;
		
		do
			opt = in.next();
		while(!opt.equals("y") && !opt.equals("n"));
		
		if(opt.equals("y"))
		{
			System.out.print("Enter map name: ");
			mapName = in.next() + ".map";

			map = new Map(mapName);
			
			map.getEnemies();
		}
		else
		{
			System.out.print("Map name: ");
			do
				mapName = in.next();
			while(mapName.isBlank());
			mapName += ".map";
			
			System.out.print("Width (>= 32): ");
			do
				width = in.nextShort();
			while(width < 32);
			
			System.out.print("Height (>= 18): ");
			do
				height = in.nextShort();
			while(height < 18);
			
			System.out.print("Gravity (>= 0): ");
			do
				gravity = in.nextShort();
			while(gravity < 0);
			
			System.out.print("Background name: ");
			do
				backgroundFileName = in.next();
			while(backgroundFileName.isBlank());
			backgroundFileName += ".png";
			
			for(int i = 0; i < width * height; i++)
					mapString += "..";
			
			System.out.println();
			
			map = new Map(mapName, width, height, gravity, backgroundFileName, mapString);
		}
		
		in.close();*/
		
		FeatherEngine fe = new FeatherEngine(WIDTH, HEIGHT,TITLE, FPS_CAP, SHOW_FPS, FULLSCREEN)
		{
			public void update(float elapsedTime)
			{
				if(key(KeyEvent.VK_ESCAPE))
					stop();
				
				int mouseX = getMouseX();
				int mouseY = getMouseY();
				
				if(key(KeyEvent.VK_UP) || getMouseY() < 0)
					y -= 20 * elapsedTime;
				if(key(KeyEvent.VK_DOWN) || getMouseY() >= HEIGHT)
					y += 20 * elapsedTime;
				if(key(KeyEvent.VK_LEFT) || getMouseX() < 0)
					x -= 20 * elapsedTime;
				if(key(KeyEvent.VK_RIGHT) || getMouseX() >= WIDTH)
					x += 20 * elapsedTime;
				
				if(keyToggle(KeyEvent.VK_A))
					if(selectedTile > 0)
						selectedTile--;
				if(keyToggle(KeyEvent.VK_D))
					if(selectedTile < 28)
						selectedTile++;
				
				if(x < 16)
					x = 16;					
				if(x > map.getWidth() - 16)
					x = map.getWidth() - 16;
				if(y < 9)
					y = 9;
				if(y > map.getHeight() - 9)
					y = map.getHeight() - 9;
				
				if(mouseX >= 0 && mouseY >= 0 && mouseX < WIDTH && mouseY < HEIGHT && (mouse(MouseEvent.BUTTON1) || mouse(MouseEvent.BUTTON2)))
				{
					cameraX = x;
				    cameraY = y;
				    
					float offsetX = cameraX - visibleX / 2f;
					float offsetY = cameraY - visibleY / 2f;
					
					if(offsetX < 0f) offsetX = 0f;
					if(offsetY < 0f) offsetY = 0f;
					if(offsetX > map.getWidth() - visibleX) offsetX = map.getWidth() - visibleX;
					if(offsetY > map.getHeight() - visibleY) offsetY = map.getHeight() - visibleY;
					
					float tileOffsetX = (offsetX - (int)offsetX) * tileWidth;
					float tileOffsetY = (offsetY - (int)offsetY) * tileHeight;
					
					//short x = (short)(mouseX / (short)(tileWidth - tileOffsetX));
					//short y = (short)(mouseY / (short)(tileHeight - tileOffsetY));
					
					for(int y = -1; y <= visibleY; y++)
						for(int x = -1; x <= visibleX; x++)
						{
							short baseX = (short)(x * tileWidth - tileOffsetX);
							short baseY = (short)(y * tileHeight - tileOffsetY);
							if(baseX <= mouseX && mouseX < baseX + tileWidth && baseY <= mouseY && mouseY < baseY + tileHeight)
							{
								short tileX = (short)(x + offsetX);
								short tileY = (short)(y + offsetY);
								
								if(mouse(MouseEvent.BUTTON1))
									map.setTile(tileX, tileY, getSelectedTile());
								else
									map.setTile(tileX, tileY, Map.NULL_ID);										
							}
						}
				}
			}
			
			public void render(Graphics2D g)
			{
			    cameraX = x;
			    cameraY = y;
			    
				float offsetX = cameraX - visibleX / 2f;
				float offsetY = cameraY - visibleY / 2f;
				
				if(offsetX < 0f) offsetX = 0f;
				if(offsetY < 0f) offsetY = 0f;
				if(offsetX > map.getWidth() - visibleX) offsetX = map.getWidth() - visibleX;
				if(offsetY > map.getHeight() - visibleY) offsetY = map.getHeight() - visibleY;
				
				g.drawImage(map.getBackground(), - (int)(offsetX * 30), - (int)(offsetY * 4), WIDTH, (int)(HEIGHT * 1.07), null);
				g.drawImage(map.getBackground(), - (int)(offsetX * 30) + WIDTH, - (int)(offsetY * 4), WIDTH, (int)(HEIGHT * 1.07), null);
				
				float tileOffsetX = (offsetX - (int)offsetX) * tileWidth;
				float tileOffsetY = (offsetY - (int)offsetY) * tileHeight;
				
				for(int y = -1; y <= visibleY; y++)
					for(int x = -1; x <= visibleX; x++)
						g.drawImage(map.getTileSprite((short)(x + offsetX), (short)(y + offsetY)), (int)(x * tileWidth - tileOffsetX), (int)(y * tileHeight - tileOffsetY), tileWidth, tileHeight, null);
				
				g.drawImage(map.getTileSprite(getSelectedTile()), (int)(visibleX * 1.5), (int)(visibleY * 1.5), tileWidth, tileHeight, null);
			}
		};
		
		fe.start();
		
		map.saveOnFile();
	}
	
	private String getSelectedTile()
	{
		if(selectedTile == 0)
			return Map.START_POSITION_ID;
		if(selectedTile == 1)
			return Map.FINISH_POSITION_ID;
		if(selectedTile == 2)
			return Map.COIN_ID;
		if(selectedTile == 3)
			return Map.BRICK_ID;
		if(selectedTile == 4)
			return Map.WOODEN_BLOCK_ID;
		if(selectedTile < 15)
			return Map.GROUND_ID + (selectedTile - 5);
		if(selectedTile < 18)
			return Map.GROUND_ID + (char)('A' + selectedTile - 15);
		if(selectedTile < 27)
			return Map.ROCK_ID + (selectedTile - 18);
		if(selectedTile < 29)
			return Map.ENEMY_ID + (selectedTile - 27);
		return Map.NULL_ID;
	}
}