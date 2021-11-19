package breakout.myutil;

public class MyConstant {

	//Ball
	public static final int BALL_W = 16;
	public static final int BALL_H = 16;
	public static final int BALL_LIVES = 3;
	
	//Bar
	public static final int BAR_W = 80;
	public static final int BAR_H = 16;
	
	//SPEED
	public static final int SPEED = 4;
	
	//Brick
	public static final int BRICK_W = 40;
	public static final int BRICK_H = 20;
	
	public static final int BRICK_ROW = 6; 
	public static final int BRICK_COL = 10; 
	public static final int BRICK_GAP = 3; 
	
	//Game State
	public static final int START 	    = 0;
	public static final int TRY_AGAIN   = 1;
	public static final int GAME_OVER	= 2;
	public static final int PROCEED		= 3;
		
	//Stage
	public static class Stage{
		public static final int CLEAR_1 = 4;
		public static final int CLEAR_2 = 5;
		public static final int CLEAR_3 = 6;
	}	
	
	//∞‘¿”∆«
	public static final int GAMEPAN_W = 400 + BRICK_GAP*(BRICK_COL-1);
	public static final int GAMEPAN_H = 600;
	
	//Key
	public static class Key{
		public static final int NONE  = 0;
		public static final int LEFT  = 1;
		public static final int RIGHT = 2;
	}
	
	
	
}
