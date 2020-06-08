package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import exceptions.MultipleStartingPositionsFoundException;
import exceptions.NonExistingEnemyException;
import exceptions.StartingPositionNotFoundException;
import gameEngine.FeatherEngine;
import individuals.Enemy;
import individuals.Player;
import individuals.Individual.Direction;
import map.Map;

public final class Main
{
	/*********************************************************************/
	/* Feather Engine Settings                                           */
	/*********************************************************************/
	
	private final int WIDTH = 1600;
	private final int HEIGHT = 900;
	private final String TITLE = "Super Plagio";
	private final int FPS_CAP = 60;
	private final boolean SHOW_FPS = true;
	private final boolean FULLSCREEN = false;
	private final boolean USE_BUFFER = false;
	
	/*********************************************************************/
	/* Engine variables                                                  */
	/*********************************************************************/
	
	private final static int N_VERTICAL_TILES = 18;
	
	private int tileSize;
	
	private int visibleX;
	private int visibleY;
	
	private float cameraX;
	private float cameraY;
	
	private boolean showDebugData = false;
	
	private int fontSize = HEIGHT / 45;
	
	private final static Color DARK_YELLOW = new Color(51, 51, 0);	
	
	private final static String MAP_PATH = "maps/";
	
	/*********************************************************************/
	/* Game variables                                                    */
	/*********************************************************************/
	
	private int scene = 1;
	
	private Player player;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	
	private int points = 0;	
	
	/*********************************************************************/
	/* Misc                                                              */
	/*********************************************************************/
	
	private Map map;
	private BufferedImage gameOverSprite;
	
	/*********************************************************************/
	/* Constructor                                                       */
	/*********************************************************************/
	
	public Main() throws IOException
	{		
		try
		{
			map = new Map("testingMap2");
			player = new Player(map);		
			
			int[] strPos = map.getStartingPosition();
			cameraX = strPos[0];
			cameraY = strPos[1];
		}
		catch(StartingPositionNotFoundException e1)
		{
			JOptionPane.showMessageDialog(null, "No player starting position found into \"" + MAP_PATH + map.getMapName() + ".map\"!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch(MultipleStartingPositionsFoundException e2)
		{
			JOptionPane.showMessageDialog(null, "Multiple player starting positions found into \"" + MAP_PATH + map.getMapName() + ".map\"!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch(NonExistingEnemyException e3)
		{
			JOptionPane.showMessageDialog(null, "Non-existing enemy found into \"" + MAP_PATH + map.getMapName() + ".map\"!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch(IOException e0)
		{
			JOptionPane.showMessageDialog(null, "IOException while loading " + MAP_PATH + map.getMapName() + ".map\"!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		enemies = map.getEnemies();
		
		try
		{
			gameOverSprite = ImageIO.read(new File("sprites/gameOver.png"));
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "IOException while loading HUD sprites!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		FeatherEngine fe = new FeatherEngine(WIDTH, HEIGHT, TITLE, FPS_CAP, SHOW_FPS, FULLSCREEN, USE_BUFFER)
		{
			@Override
			public void update(float elapsedTime)
			{
				if(key(KeyEvent.VK_ESCAPE))
					stop();
				
				if(scene == 0)
				{
					
				}			
				else if(scene == 1)
				{
					if(keyToggle(KeyEvent.VK_F1))
						showDebugData = !showDebugData;
					
					if(keyToggle(KeyEvent.VK_F2))
						setFPSCap(getFPSCap() == FPS_CAP ? 0 : FPS_CAP);
					
					if(keyToggle(KeyEvent.VK_F3))
					{
						setSize(WIDTH, HEIGHT, !isFullscreen());
						
						tileSize = getHeight() / N_VERTICAL_TILES;
						
						visibleX = getWidth() / tileSize;
						visibleY = getHeight() / tileSize;
					}
		
					/*********************************************************************/
					/* Player                                                            */
					/*********************************************************************/
							
					boolean tryJumping = false;
					
					if(keyToggle(KeyEvent.VK_SPACE))
						tryJumping = true;
					
					if(key(KeyEvent.VK_LEFT) && !key(KeyEvent.VK_RIGHT))
						player.move(Direction.LEFT, tryJumping, elapsedTime);
					else if(!key(KeyEvent.VK_LEFT) && key(KeyEvent.VK_RIGHT))
						player.move(Direction.RIGHT, tryJumping, elapsedTime);
					else
						player.move(null, tryJumping, elapsedTime);
					
					if(player.isAlive())
						if(player.checkForCoins())
							points += 100;
					
					/*********************************************************************/
					/* Enemies                                                           */
					/*********************************************************************/
				
					for(Enemy enemy : enemies)
						enemy.move(elapsedTime);
					
					/*********************************************************************/
					/* Resolve                                                           */
					/*********************************************************************/
					
					if(player.isAlive())
						player.checkForEnemies(enemies);
					
					for(int i = 0; i < enemies.size(); i++)
						if(enemies.get(i).getCurrentSprite() == null)
							enemies.remove(i);
					
					/*********************************************************************/
					
					if(key(KeyEvent.VK_ENTER))
						scene = 0;
				}
			}
			
			@Override
			public void render(Graphics g, byte[] frameBuffer)
			{
				g.clearRect(0, 0, getWidth(), getHeight());
				
				if(scene == 0)
				{
					
				}
				else if(scene == 1)
				{
					/*********************************************************************/
					/* Camera view                                                       */
					/*********************************************************************/
				
				    cameraX = player.getX();
				    cameraY = player.getY();
				    
					float offsetX = cameraX - visibleX / 2f;
					float offsetY = cameraY - visibleY / 2f;
					
					if(offsetX < 0f) offsetX = 0f;
					if(offsetY < 0f) offsetY = 0f;
					if(offsetX > map.getWidth() - visibleX) offsetX = map.getWidth() - visibleX;
					if(offsetY > map.getHeight() - visibleY) offsetY = map.getHeight() - visibleY;
					
					g.drawImage(map.getBackground(), - (int)(offsetX * 30), - (int)(offsetY * 4), WIDTH, (int)(HEIGHT * 1.07), null);
					g.drawImage(map.getBackground(), - (int)(offsetX * 30) + WIDTH, - (int)(offsetY * 4), WIDTH, (int)(HEIGHT * 1.07), null);
					
					float tileOffsetX = (offsetX - (int)offsetX) * tileSize;
					float tileOffsetY = (offsetY - (int)offsetY) * tileSize;
					
					for(int y = -1; y <= visibleY; y++)
						for(int x = -1; x <= visibleX; x++)
							g.drawImage(map.getTileSprite((int)(x + offsetX), (int)(y + offsetY)), (int)(x * tileSize - tileOffsetX), (int)(y * tileSize - tileOffsetY), tileSize, tileSize, null);
					
					for(Enemy enemy : enemies)
						g.drawImage(enemy.getCurrentSprite(), (int)((enemy.getX() - offsetX) * tileSize), (int)((enemy.getY() - offsetY) * tileSize), tileSize, tileSize, null);
					
					g.drawImage(player.getCurrentSprite(), (int)((player.getX() - offsetX) * tileSize), (int)((player.getY() - offsetY) * tileSize), tileSize, tileSize, null);
					
					if(!player.isAlive())
						g.drawImage(gameOverSprite, getWidth() / 4, getHeight() / 2 - getHeight() / 10, getWidth() / 2, getHeight() / 5, null);
						
					/*********************************************************************/
					/* HUD                                                               */		
					/*********************************************************************/
					
					g.setFont(new Font("", Font.PLAIN, fontSize));
					
					g.setColor(DARK_YELLOW);
					g.drawString("Points:", getWidth() - (int)(getWidth() / 9.7f) + (int)(tileSize / 1.5), getHeight() / 100 + (int)(tileSize / 1.55));
					g.setColor(Color.YELLOW);
					g.drawString(points + "", getWidth() - getWidth() / 16 + (int)(tileSize / 1.5), getHeight() / 100 + (int)(tileSize / 1.5));
					
					if(showDebugData)
					{
						g.setColor(Color.BLACK);
						g.fillRect(10, 10, fontSize * 15, 10 * fontSize + fontSize / 5);
						g.setColor(Color.GREEN);
						g.drawString("FPS = " + getFPS(), 													   10, 10 + fontSize * 1  - fontSize / 7);
						g.drawString("Player.x = " + player.getX(),                                            10, 10 + fontSize * 3  - fontSize / 7);
						g.drawString("Player.y = " + player.getY(),                                            10, 10 + fontSize * 4  - fontSize / 7);
						g.drawString("Player.xVelocity = " + player.getXVelocity(),                            10, 10 + fontSize * 5  - fontSize / 7);
						g.drawString("Player.yVelocity = " + player.getYVelocity(),                            10, 10 + fontSize * 6  - fontSize / 7);
						g.drawString("Player.onGround = " + player.isOnGround(),                               10, 10 + fontSize * 7  - fontSize / 7);
						g.drawString("Player.facingDirection = " + (player.isFacingLeft() ? "LEFT" : "RIGHT"), 10, 10 + fontSize * 8  - fontSize / 7);
						g.drawString("Player.slowing = " + player.isSlowing(),                                 10, 10 + fontSize * 9  - fontSize / 7);
						g.drawString("Player.hp = " + player.getHP(),                                          10, 10 + fontSize * 10 - fontSize / 7);
					}
				}
			}			
		};
		
		tileSize = fe.getHeight() / N_VERTICAL_TILES;
		
		visibleX = fe.getWidth() / tileSize;
		visibleY = fe.getHeight() / tileSize;
		
		fe.start();
	}
}