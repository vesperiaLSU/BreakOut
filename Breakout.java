/* 
 * File: Breakout.java 
 * ------------------- 
 * Name: Chen Guo
 * Date: 4/6/2013
 *  
 * This file will eventually implement the game of Breakout. 
 */ 
 
import java.awt.*; 
import java.awt.event.*; 
import java.applet.*; 
 
import acm.graphics.*; 
import acm.program.*; 
import acm.util.*; 
 
public class breakOutv2 extends GraphicsProgram
{ 
 
	/** Width and height of application window in pixels */ 
		public static final int APPLICATION_WIDTH = 400; 
		public static final int APPLICATION_HEIGHT = 600; 
	
	/** Do not use, for system only*/ 
		private static final int WIDTH = APPLICATION_WIDTH; 
		private static final int HEIGHT = APPLICATION_HEIGHT; 
	
	/** Dimensions of the paddle */ 
		private static final int PADDLE_WIDTH = 60; 
		private static final int PADDLE_HEIGHT = 10; 
	
	/** Offset of the paddle up from the bottom */ 
		private static final int PADDLE_Y_OFFSET = 30; 
	
	/** Number of bricks per row */ 
		private static final int NBRICKS_PER_ROW = 10; 
	
	/** Number of rows of bricks */ 
		private static final int NBRICK_ROWS = 10; 
	
	/** Separation between bricks */ 
		private static final int BRICK_SEP = 4; 
	
	/** Width of a brick */ 
		private static final int BRICK_WIDTH =(WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW; 
	
	/** Height of a brick */ 
		private static final int BRICK_HEIGHT = 8; 
 
	/** Radius of the ball in pixels */ 
		private static final int BALL_RADIUS = 10; 
 
	/** Offset of the top brick row from the top */ 
		private static final int BRICK_Y_OFFSET = 70; 
	
	/** Number of turns */ 
		private static final int NTURNS = 3; 
	
	/** Milliseconds of Delay*/ 
		private static final int DELAY = 10; 
	
	/** Ball Default Game Speed*/ 
		private static final int GAME_SPEED = -10; 
	
	/** AI sensor starting height*/ 
		private static final int AI_startHEIGHT = 285; 
	
	/** AI sensor ending height*/ 
		private static final int AI_endHEIGHT = 300; 
	
	
	public void run()
	{ 
		last = new GPoint((getWidth()-PADDLE_WIDTH)/2, (getHeight()-PADDLE_Y_OFFSET)); 
		GCanvas Canvas = new GCanvas(); 
		Canvas.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT); 
		setup(); 
		while(turnsLeft != 0)
		{ 
			play(); 
			pause(1000); 
		} 
		loseClip.play(); 
		gameOverLabel = new GLabel("UNFORTUNATELY, YOU LOSE!!"); 
		gameOverLabel.setFont("Arial-18"); 
		add(gameOverLabel, 50, 300); 
	} 
	
	private void play()
	{ 
		gameOverLabel = new GLabel(""); 
		gameOverLabel.setFont("Arial-18"); 
		add(gameOverLabel, 50, 300); 
	
		ball.setLocation(getWidth()/2-BALL_RADIUS, getHeight()/2-BALL_RADIUS); 
		x_vel = rgen.nextDouble(1.0, 3.0); 
		if (rgen.nextBoolean(0.5)) x_vel = -x_vel; 
		getReady = new GLabel("Get Ready"); 
		getReady.setFont("Arial-18"); 
		readyClip.play(); 
		add(getReady, 50, 300); 
		pause(4000); 
		remove(getReady); 
		while (!gameOver())
		{ 
			moveBall(); 
			checkForCollision(); 
			if (ball.getY()>=AI_startHEIGHT && ball.getY()<=AI_endHEIGHT && y_vel>0)
			{ 
				agentAcuator();
			} 
		pause(DELAY); 
		} 
	} 
	
	private void setup()
	{ 
		//Create the bricks 
		double width = getWidth(); 
		double height = getHeight(); 
		double startPoint = width/2 - (NBRICKS_PER_ROW*BRICK_WIDTH + (NBRICKS_PER_ROW-1)*BRICK_SEP)/2; 
	
		for (int i=0; i<NBRICK_ROWS; i++)
		{ 
			for (int j=0; j<NBRICKS_PER_ROW; j++)
			{ 
				brick = new GRect(startPoint + j*(BRICK_WIDTH+BRICK_SEP), BRICK_Y_OFFSET + i*(BRICK_HEIGHT+BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true); 
				if (i==0 || i==1) brick.setFillColor(Color.RED); 
				else if (i==2 || i==3) brick.setFillColor(Color.ORANGE); 
				else if (i==4 || i==5) brick.setFillColor(Color.YELLOW); 
				else if (i==6 || i==7) brick.setFillColor(Color.GREEN); 
				else if (i==8 || i==9) brick.setFillColor(Color.CYAN); 
				add(brick); 
			} 
		} 
	
		//create the paddle 
		paddle = new GRect((width-PADDLE_WIDTH)/2, (height-PADDLE_Y_OFFSET), PADDLE_WIDTH, PADDLE_HEIGHT); 
		paddle.setFilled(true); 
		add(paddle); 
	
		//create the ball 
		ball = new GOval(width/2-BALL_RADIUS, height/2-BALL_RADIUS, BALL_RADIUS*2, BALL_RADIUS*2); 
		ball.setFilled(true); 
		ball.setColor(Color.MAGENTA); 
		add(ball); 
	
		//create the score board 
		scoreBoard = new GLabel("Scores: " + score); 
		scoreBoard.setFont("Arial-20"); 
		add(scoreBoard, 50, 50); 
	
		//create game over lable 
		gameOverLabel = new GLabel(""); 
		gameOverLabel.setFont("Arial-18"); 
		add(gameOverLabel, 50, 300); 
	
		//mouseListener 
		//addMouseListeners(); 
	} 
	
	//create mouse controller 
		/*public void mouseMoved(MouseEvent e){ 
		double width=getWidth(); 
		double height=getHeight(); 
		if (e.getX()<width-PADDLE_WIDTH)
		{ 
			paddle.move(e.getX()-last.getX(), 0); 
			last = new GPoint(e.getPoint()); 
		} 
	}*/ 
	
	private double aim_calc(double vel_x, double vel_y, double ball_x, double ball_y, double target_y)
		{
			double target_x;
			target_x = ball_x + vel_x * ((target_y - ball_y)/(vel_y * -1));
			while(target_x > getWidth()-2*BALL_RADIUS || target_x < 0)
			{
				if (target_x > getWidth()-2*BALL_RADIUS)
				{ 
					target_x = 2*getWidth()-4*BALL_RADIUS-target_x;
				} 
				else if (target_x < 0)
				{ 
					target_x = -target_x;
				} 
			}
			return target_x;
		}
	private double agentSensor()
	{ 
		double ballPosiX; 
		double ballPosiY; 
		double vel_x;
		double vel_y;
		double paddlePosiY = PADDLE_Y_OFFSET + 2*BALL_RADIUS; 
		int bounces;//~need to keep track of bounces for targeting
		bounces = 0;
		ballPosiX = ball.getX(); 
		ballPosiY = getHeight()-ball.getY();
		vel_x = x_vel;//~Saving the x and y velocities, not certain if game keeps 
		vel_y = y_vel;//~playing or not while calculations are done
		paddlePosiX = ballPosiX + vel_x*((ballPosiY - paddlePosiY)/vel_y); 
		//~placed in a while loop.. I know it shouldn't be needed but it bothered me
		while(paddlePosiX > getWidth()-2*BALL_RADIUS || paddlePosiX < 0)
		{
			if (paddlePosiX > getWidth()-2*BALL_RADIUS)
			{ 
				paddlePosiX = 2*getWidth()-4*BALL_RADIUS-paddlePosiX;
				bounces = bounces +1;
			} 
			else if (paddlePosiX < 0)
			{ 
				paddlePosiX = -paddlePosiX;
				bounces = bounces +1;
			} 
		}
		
		//paddleRandomX = rgen.nextDouble((paddlePosiX-PADDLE_WIDTH), (paddlePosiX + 2*BALL_RADIUS)); 
		//return paddleRandomX;
		//~creates a "target line" with a min and max x value and a y value
		Points target = environment.getTarget();
		double target_x_min = target.getX() - BRICK_WIDTH/2;//~min of target range
		double target_x_max= target.getX() - BRICK_WIDTH/2;;//~max of target range
		double target_y =  getHeight() - target.getY();//~y value of target range
		//~giving a manual target for bug testing, feel free to delete when target find algorithm is ready
		//target_y = 285;
		//target_x_min = 150;
		//target_x_max = 250;
		//~end manual targeting
		/*
		~insert target find algorithm here
		~would fill target_x_min, target_x_max, and target_y
		please account for ball radius with target_x_max ^_^
		*/
		
		if(bounces% 2 == 1)//~if odd number of bounces
		{
			//~flip the x velocity as it would be flipped once the ball hits the paddle
			vel_x = vel_x * -1; 
		}
		double target_x;
		double paddle_aim;
		/*
			~If the calculation can find a paddle position that will cause the ball
				to hit the target it will return that position to the actuator.  If 
				it cannot it will return a random position.
			if (xOnPaddle < 12) x_vel = -3; 6
			else if (xOnPaddle >= 12 && xOnPaddle < 24) x_vel = -2; 18
			else if (xOnPaddle >= 24 && xOnPaddle < 36) x_vel += 0; 30
			else if (xOnPaddle >= 36 && xOnPaddle < 48) x_vel = 2; 42
			else if (xOnPaddle >= 48) x_vel = 3; 54
		*/
		//first try middle of paddle +=0
		target_x = aim_calc(vel_x, vel_y, paddlePosiX, paddlePosiY, target_y);
		
		if(target_x < target_x_min || target_x > target_x_max)
		{
			//~if middle doesn't work try middle left
			target_x = aim_calc(-2, vel_y, paddlePosiX, paddlePosiY, target_y);
			if(target_x < target_x_min || target_x > target_x_max)
			{
				//~if above didn't work try middle right
				target_x = aim_calc(+2, vel_y, paddlePosiX, paddlePosiY, target_y);
				if(target_x < target_x_min || target_x > target_x_max)
				{
					//~if above didn't work try left edge
					target_x = aim_calc(-3, vel_y, paddlePosiX, paddlePosiY, target_y);
					if(target_x < target_x_min || target_x > target_x_max)
					{
						//~if above didn't work try right edge
						target_x = aim_calc(+3, vel_y, paddlePosiX, paddlePosiY, target_y);
						if(target_x < target_x_min || target_x > target_x_max)
						{
							//if all fail pick at random
							paddle_aim = rgen.nextDouble((paddlePosiX-PADDLE_WIDTH), (paddlePosiX + 2*BALL_RADIUS));
						}
						else
						{
							//if right edge works set to right edge
							paddle_aim = paddlePosiX - 54 /*- BALL_RADIUS*/;
						}
						
					}
					else
					{
						//if left edge works set to left edge
						paddle_aim = paddlePosiX - 6 /*- BALL_RADIUS*/;
					}	
				}
				else
				{
					//set to middle right
					paddle_aim = paddlePosiX - 42 /*- BALL_RADIUS*/;
				}	
			}
			else
			{
				//set to middle left
				paddle_aim = paddlePosiX - 18 /*- BALL_RADIUS*/;
			}	
		}
		else
		{
			//set to middle
			paddle_aim = paddlePosiX - 30 /*+ BALL_RADIUS*/;
		}
		return paddle_aim;
	} 
	
	private void agentAcuator()
	{ 
		double paddlePosition = agentSensor(); 
		paddle.move(paddlePosition-last.getX(), 0); 
		last = new GPoint(paddle.getX(), paddle.getY()); 
	} 
	
	//move the ball 
	private void moveBall()
	{ 
		ball.move(x_vel, y_vel); 
	} 
	
	//Check for all the possible collisions within the world 
	private void checkForCollision()	
	{ 
		checkForCollisionWithBrick(); 
		checkForCollisionWithPaddle(); 
		checkForCollisionWithUp(); 
		checkForCollisionWithLeft(); 
	checkForCollisionWithRight(); 	
	} 	
  
	//Get the element the ball first touched from from parameters 
	private GObject getCollidingObject()
	{ 
		gobj = getElementAt(ball.getX(),ball.getY());  //top-left parameter 
		if (gobj != null) return gobj; 
		else
		{ 
			gobj = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY());  //bottom-left parameter 
			if (gobj != null) return gobj; 
			else
			{ 
				gobj = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);  //top-right parameter 
				if (gobj != null) return gobj; 
				else
				{ 
					gobj = getElementAt(ball.getX()+BALL_RADIUS*2, ball.getY()+BALL_RADIUS*2);   
					//bottom-right parameter 
					return gobj; 
				}  
			} 
		} 
	} 
	
	private void checkForCollisionWithPaddle()
	{ 
		gobj = getCollidingObject(); 
		if (gobj == paddle)
		{ 
			y_vel = -y_vel; 
			xOnPaddle = ball.getX()- paddle.getX() + BALL_RADIUS; 
			//change the velocity on X direction depending on the spot the ball hits the paddle 
			if (xOnPaddle < 12) x_vel = -3; 
			else if (xOnPaddle >= 12 && xOnPaddle < 24) x_vel = -2; 
			else if (xOnPaddle >= 24 && xOnPaddle < 36) x_vel += 0; 
			else if (xOnPaddle >= 36 && xOnPaddle < 48) x_vel = 2; 
			else if (xOnPaddle >= 48) x_vel = 3;  
			score -= 3;
			scoreBoard.setLabel("Scores: " + score);
		} 
	} 
	
	private void checkForCollisionWithBrick()
	{ 
		gobj = getCollidingObject();		
		if (gobj != paddle && gobj!=null && gobj!=scoreBoard)
		{ 
			bounceClip.play();
			double gobj_x = gobj.getX();
			double gobj_y = gobj.getY();
			int i = (int)(gobj_x-BRICK_SEP)/(BRICK_WIDTH + BRICK_SEP);
			int j = (int)(gobj_y-BRICK_Y_OFFSET)/(BRICK_HEIGHT+BRICK_SEP);
			environment.brickMap[i][j] = false;
			y_vel = -y_vel; 
			remove(gobj); 
			nBricks -= 1; 
			score += 10; 
			scoreBoard.setLabel("Scores: " + score); 
		} 
	} 
	
	private void checkForCollisionWithUp()
	{ 
		if (ball.getY() < 0)
		{ 
			y_vel = -y_vel; 
			double diff = ball.getY(); 
			ball.move(0, -2*diff); 
		} 
	} 
	
	private void checkForCollisionWithLeft()
	{ 
		if (ball.getX() < 0)
		{ 
			x_vel = -x_vel; 
			double diff = ball.getX(); 
			ball.move(-2*diff, 0); 
		} 
	} 
	
	private void checkForCollisionWithRight()
	{ 
		if (ball.getX() > getWidth()-BALL_RADIUS)
		{ 
			x_vel = -x_vel; 
			double diff = ball.getX()-(getWidth()-2*BALL_RADIUS); 
			ball.move(-2*diff, 0); 
		} 
	} 
	
	private boolean checkForCollisionWithBottom()
	{ 
		if (ball.getY() > getHeight()- BALL_RADIUS)
		{ 
			ball.move(0, 0);  
			return true; 
		} 
		else return false; 
	} 
	
	private boolean gameOver()
	{ 
		if (checkForCollisionWithBottom())
		{ 
			gameOverLabel.setLabel("YOU MISSED! Your current score is " + score); 
			pause(2000); 
			turnsLeft -= 1; 
			gameOverLabel.setLabel("You have " + turnsLeft + " turns left"); 
			pause(2000); 
			remove(gameOverLabel); 
			return true; 
		} 
		else if (nBricks == 0)
		{ 
			winClip.play(); 
			gameOverLabel.setLabel("YOUR WIN! Your final score is " + score); 
			pause(4000); 
			remove(gameOverLabel); 
			return true; 
		} 
		else return false; 
	} 
	private Bricks environment = new Bricks();
	private GRect paddle, brick; 
	private GOval ball; 
	private GPoint last; 
	private GObject gobj; 
	private GLabel scoreBoard, gameOverLabel, getReady; 
	private double xOnPaddle, paddleRandomX; 
	private double paddlePosiX=(getWidth()-PADDLE_WIDTH)/2; 
	private int nBricks = NBRICK_ROWS*NBRICKS_PER_ROW;
	private int score = 0; 
	private int turnsLeft = 3; 
	private double x_vel, y_vel = GAME_SPEED; 
	private RandomGenerator rgen = new RandomGenerator();
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au"); 
	AudioClip winClip = MediaTools.loadAudioClip("win.au"); 
	AudioClip loseClip = MediaTools.loadAudioClip("lose.au"); 
	AudioClip readyClip = MediaTools.loadAudioClip("getReady.au");
	
//TARGET SELECT CODE
	//Class points contains x,y coordinates
	public class Points{
		int x,y;
		public Points(){
			x = 0;
			y = 0;
		}
		public Points(int X, int Y){
			x = X;
			y = Y;
		}
		public int getX(){ return x; }
		public int getY(){ return y; }
	}
	//Class bricks is implemented while visualizing the brickMap as a [Column]x[Row] array
	public class Bricks{
		public boolean[][] brickMap;
		
		//Default Constructor
		public Bricks(){
			brickMap = new boolean[10][10];
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++)
					brickMap[i][j] = true;
		}
		//Constructor with set sizes
		public Bricks(int n, int m){
			brickMap = new boolean[n][m];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++)
					brickMap[i][j] = true;
		}
		//finds the depth of that column
		public int checkDepth(int column){
			for (int i = 9; i >= 0; i--)
				if (brickMap[column][i])
					return i;
			return -1;
		}
		//returns true if all the columns are equal to each other or 0
		public boolean checkLevel(){
			boolean flag = true;
			int i = 0;
			int depthChecker = checkDepth(i);
			//Finds the first column without a depth of 0
			while (depthChecker != -1 && i < 9){
				i++;
				depthChecker = checkDepth(i);
			}
			//Compares the columns to first non-zero column
			for (int j = i; j < 10; j++)
				if (checkDepth(j) != 0 && checkDepth(j) != depthChecker)
					flag = false;
			return flag;
		}
		//finds the column with the lowest non-zero value depth
		public int lowestColumn(){
			int i = 0;
			int depthChecker = checkDepth(i);
			//Finds the first column without a depth of 0
			while (depthChecker != -1 && i < 9){
				i++;
				depthChecker = checkDepth(i);
			}
			//Finds lowest non-zero column
			for (int j = i; j < 10; j++)
				if (checkDepth(i) > checkDepth(j) && checkDepth(j) != 0)					
					i = j;				
			return i;
		}
		//returns the depth of the column
		public int getDepth(int col){ return checkDepth(col); }
		
		
		//finds the most desirable target
		public Points getTarget(){
			int X,Y = 10;
			RandomGenerator rand = new RandomGenerator();
			//if even, give a random block to target
			if (checkLevel()){
				X = rand.nextInt(10);
				Y = getDepth(X);
			}
			//If column is not an edge column, aim for easiest block that gives multiple hits
			//Else if column is an edge column, aim for the wall that will give multiple hits
			else{
				X = lowestColumn();
				if (X < paddlePosiX){							//ball is left of column
					if (X != 9 && getDepth(X+1) != 0){			//column not edge, column to right not empty
						X++;
						Y = getDepth(X) - 1;
					}else if (X == 9){							//column is edge
						Y = getDepth(X - 1);
					}else 										//default lowest brick target
						Y = getDepth(X);
				}
				else if (X > paddlePosiX){						//ball is right of column
					if (X != 0 && getDepth(X-1) != 0){			//column not edge, column to left not empty
						X--;
						Y = getDepth(X) - 1;
					}else if (X == 0){							//column is edge
						Y = getDepth(X + 1);
					}else										//default lowest brick target
						Y = getDepth(X);
				}else{											//default single brick hit
					X = lowestColumn();
					Y = getDepth(X);
				}
			}
			return new Points(X,Y);
		}
	}
}
