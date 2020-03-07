package brickPackage;

import java.awt.event.KeyListener;

import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

public class GamePlay extends JPanel implements KeyListener, ActionListener{
	private boolean play = false;
	private int score = 0;
	
	private int totalBricks = 21;
	
	private Timer timer;
	private int delay = 8;
	
	private int playerX = 310;
	
	private int ballPosX = 120;
	private int ballPosY = 350;
	private int ballXDir = -1;
	private int ballYDir = -2;
	
	private BricksGenerator bricks;
	
	public GamePlay() {
		bricks = new BricksGenerator(3,7);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(delay,this);
		timer.start();
	}
	
	public void paint(Graphics g) {
		//background
		g.setColor(Color.black);
		g.fillRect(1, 1, 692, 592);
		
		//drawing bricks
		bricks.draw((Graphics2D)g);
		
		//borders
		g.setColor(Color.yellow);
		g.fillRect(0, 0, 3, 592);
		g.fillRect(0, 0, 692, 3);
		g.fillRect(691 , 0, 3, 592);
		
		//scores
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString("Score:" + score, 570 , 30);
		
		//the paddle
		g.setColor(Color.green);
		g.fillRect(playerX, 550, 100, 8);
		
		//ball
		g.setColor(Color.yellow);
		g.fillOval(ballPosX,ballPosY,20,20);
		
		//endgame
		
		if(totalBricks <= 0) {
			play = false;
			ballXDir = 0;
			ballYDir = 0;
			g.setColor(Color.green);
			g.setFont(new Font("serif",Font.BOLD,30));
			g.drawString("You Won", 280, 300);
			
			g.setFont(new Font("serif",Font.BOLD,20));
			g.drawString("Press Enter for Restart", 250, 350);
			
		}
		
		//game over
		if(ballPosY > 570) {
			play = false;
			ballXDir = 0;
			ballYDir = 0;
			g.setColor(Color.red);
			g.setFont(new Font("serif",Font.BOLD,30));
			g.drawString("Game Over! Your score is:" + score, 180, 300);
			
			g.setFont(new Font("serif",Font.BOLD,20));
			g.drawString("Press Enter for Restart", 250, 350);
		}
		
		g.dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		timer.start();
		if(play) {
			if(new Rectangle(ballPosX, ballPosY,20,20).intersects(new Rectangle(playerX,550,100,8))) {
				ballYDir = -ballYDir;
			}
			
			A: for(int i = 0; i < bricks.map.length; i++) {
				for(int j = 0; j < bricks.map[0].length; j++) {
					if(bricks.map[i][j] > 0) {
						int brickX = j*bricks.brickWidth + 80;
						int brickY = i*bricks.brickHeight + 50;
						int brickWidth = bricks.brickWidth;
						int brickHeight = bricks.brickHeight;
						
						Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
						Rectangle ballRect = new Rectangle(ballPosX,ballPosY,20,20);
						
						if(ballRect.intersects(brickRect)) {
							bricks.setBrickValue(0, i, j);
							totalBricks--;
							score += 5;
							
							if(ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
								ballXDir = -ballXDir;
							} else {
								ballYDir = -ballYDir;
							}
							break A;
						}
					}
				}
			}
			
			ballPosX += ballXDir;
			ballPosY += ballYDir;
			if(ballPosX < 0 || ballPosX > 670) {
				ballXDir = -ballXDir;
			}
			if(ballPosY < 0) {
				ballYDir = -ballYDir;
			}
			
		}
		repaint();
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if(playerX < 570) 
				moveRight();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			if(playerX > 10) 
				moveLeft();
		}	
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(!play) {
				play = true;
				playerX = 310;
				ballPosX = 120;
				ballPosY = 350;
				ballXDir = -1;
				ballYDir = -2;
				score = 0;
				totalBricks = 21;
				bricks = new BricksGenerator(3,7);
				repaint();
			}
		}
	}
	
	public void moveRight() {
		play = true;
		playerX += 20;
	}
	
	public void moveLeft() {
		play = true;
		playerX -= 20;
	}

	
}
