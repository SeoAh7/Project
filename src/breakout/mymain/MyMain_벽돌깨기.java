package breakout.mymain;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import breakout.myutil.Ball;
import breakout.myutil.Bar;
import breakout.myutil.Brick;
import breakout.myutil.Message;
import breakout.myutil.MyConstant;

//AWT(Abstract Window Toolkit) : 윈도우 기능을 구현해놓은 클래스 (현업에서는 잘 안씀)
//  └ Swing : AWT를 좀 더 UI 또는 기능적으로 향상시킨 윈도우 클래스

public class MyMain_벽돌깨기 extends JFrame {
	
	static Image img_background;
	static {
		img_background = Toolkit.getDefaultToolkit().getImage("images/background.png");
	}
	
	Clip clip;
	JPanel gamePan;
	Timer timer = null;
	
	Ball ball = new Ball();
	Bar bar   = new Bar();
	
	Message msg_upper = new Message();
	Message msg_lower = new Message();
	
	ArrayList<Brick> bricks = new ArrayList<Brick>();
	ArrayList<Ball> lives 	= new ArrayList<Ball>();
	
	//Game 정보
	int game_state  = MyConstant.START;
	boolean isPause = false;
	boolean bRight  = true;
	boolean bUp     = true;
	int score = 0;
	int stage = 1;
	int speed     = MyConstant.SPEED;
	int key_state = MyConstant.Key.NONE;

	
	//생성자의 기능 : 초기화
	public MyMain_벽돌깨기() {

		//제목 지정
		super("벽돌깨기");
		
		//게임판 초기화
		init_gamepan();
		
		//타이머 초기화
		init_timer();
		
		//Ball 초기화
		init_ball();
		
		//Bar 초기화
		init_bar();
		
		//Bricks 초기화
		init_bricks();
		
		//lives 초기화
		init_lives();
		
		//키보드이벤트 초기화
		init_key_event();
		
		//메시지 초기화
		init_message();
		
		//위치 지정			x    y
		super.setLocation(700, 300);

		//크기 지정
		setResizable(false);
		pack();

		//보여주기
		super.setVisible(true);

		//종료기능 구현
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}//end 초기화

	private void init_key_event() {
		
		KeyAdapter adapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//super.keyPressed(e);
				int key = e.getKeyCode();
				
				//START 키 지정
				if(game_state!=MyConstant.PROCEED) { 			//게임 시작 전
					if(key==KeyEvent.VK_SPACE) {
						if(game_state==MyConstant.START || game_state==MyConstant.TRY_AGAIN) {	//게임시작 시 목숨 차감
							Ball remain_ball = lives.get(lives.size()-1);
							lives.remove(remain_ball);
							reset_ball_bar();
							game_state=MyConstant.PROCEED;
							timer.start();
						}else if(game_state==MyConstant.Stage.CLEAR_1 || game_state==MyConstant.Stage.CLEAR_2) {
							stage++;
							speed++;
							reset_ball_bar();
							init_bricks();
							game_state=MyConstant.PROCEED;
						}else if(game_state==MyConstant.GAME_OVER || game_state==MyConstant.Stage.CLEAR_3) { 
							reset_all();
						}
					}
				}else if(game_state==MyConstant.PROCEED) {		//게임 시작 후
					if(key==KeyEvent.VK_ESCAPE) {
						isPause = !isPause;
						if(isPause) {
							timer.stop();
							draw_message(getGraphics());
						}else {
							timer.restart();
						}
					}else if(isPause && key==KeyEvent.VK_Q) {
						System.exit(0);
					}
				}
				
				//Ball Speed 지정
				if(key==KeyEvent.VK_ADD) {
					if(speed<9) {
						speed++;
						playSound("audio/speed_up.wav", false);
					}else if(speed>=9) {
						speed = 9;
						playSound("audio/speed_max.wav", false);
					}
				}else if(key==KeyEvent.VK_SUBTRACT) {
					if(speed>4) {
						speed--;
						playSound("audio/speed_down.wav", false);
					}else if(speed<=4) {
						speed = 4;
						playSound("audio/speed_min.wav", false);
					}
				}
				
				if(key==KeyEvent.VK_LEFT) {
					key_state = key_state | MyConstant.Key.LEFT;
					
				}else if(key==KeyEvent.VK_RIGHT) {
					key_state = key_state | MyConstant.Key.RIGHT;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				//super.keyReleased(e);
				int key = e.getKeyCode();
				
				if(key==KeyEvent.VK_LEFT) {
					key_state = key_state ^ MyConstant.Key.LEFT;
					
				}else if(key==KeyEvent.VK_RIGHT) {
					key_state = key_state ^ MyConstant.Key.RIGHT;
				}
			}
		};
		this.addKeyListener(adapter);
	}

	private void init_bar() {

		bar.pos.x = MyConstant.GAMEPAN_W/2 - MyConstant.BAR_W/2;
		bar.pos.y = MyConstant.GAMEPAN_H - 70;
		
		bar.pos.width  = MyConstant.BAR_W;
		bar.pos.height = MyConstant.BAR_H;
	}

	private void init_ball() {
		
		ball.pos.x = MyConstant.GAMEPAN_W/2 - MyConstant.BALL_W/2;
		ball.pos.y = MyConstant.GAMEPAN_H - 70 - MyConstant.BALL_H;
		
		ball.pos.width  = MyConstant.BALL_W;
		ball.pos.height = MyConstant.BALL_H;
	}
	
	private void init_bricks() {
		
		if(stage==1) {
			for(int i=0; i<MyConstant.BRICK_ROW+1; i++) {
				for(int j=0; j<MyConstant.BRICK_COL; j++) {
			
					int n = MyConstant.BRICK_ROW;
					if(j+1<i) {
						continue;
					}else if(j<=n+(n-i-2)) {
						Brick brick = new Brick();
						brick.pos.x = MyConstant.BRICK_W*j + MyConstant.BRICK_GAP*j;
						brick.pos.y = 50 + MyConstant.BRICK_H*i + MyConstant.BRICK_GAP*i;
						brick.pos.width  = MyConstant.BRICK_W;
						brick.pos.height = MyConstant.BRICK_H;
						brick.index = i+1;
						bricks.add(brick);
					}
				}
			}
			
		}else if(stage==2) {
			for(int i=0; i<MyConstant.BRICK_ROW+1; i++) {
				for(int j=0; j<MyConstant.BRICK_COL; j++) {
					
					int n = MyConstant.BRICK_ROW;
					if(j+2<i) {
						continue;
					}else if(j<=n+(n-i-1)) {
						Brick brick = new Brick();
						brick.pos.x = MyConstant.BRICK_W*j + MyConstant.BRICK_GAP*j;
						brick.pos.y = 50 + MyConstant.BRICK_H*i + MyConstant.BRICK_GAP*i;
						brick.pos.width  = MyConstant.BRICK_W;
						brick.pos.height = MyConstant.BRICK_H;
						brick.index = i;
						bricks.add(brick);
					}
				}
			}
		}else if(stage==3) {
			for(int i=0; i<MyConstant.BRICK_ROW+1; i++) {
				for(int j=0; j<MyConstant.BRICK_COL; j++) {
					
					Brick brick = new Brick();
					brick.pos.x = MyConstant.BRICK_W*j + MyConstant.BRICK_GAP*j;
					brick.pos.y = 50 + MyConstant.BRICK_H*i + MyConstant.BRICK_GAP*i;
					brick.pos.width  = MyConstant.BRICK_W;
					brick.pos.height = MyConstant.BRICK_H;
					brick.index = i;
					bricks.add(brick);
				}
			}
		}
	}
	
	private void reset_ball_bar() {
		init_ball();
		init_bar();
		gamePan.repaint();
	}
	
	private void reset_all() {
		game_state=0;	//0:시작하기
		score = 0;
		stage = 1;
		bricks.removeAll(bricks);
		lives.removeAll(lives);
		reset_ball_bar();
		init_bricks();
		init_lives();

		speed = MyConstant.SPEED;
		gamePan.repaint();
	}
	
	
	private void init_lives() {
		
		for(int i=0; i<MyConstant.BALL_LIVES; i++) {
			Ball remain_ball = new Ball();
			remain_ball.pos.x = MyConstant.GAMEPAN_W - 30 - 30*i;
			remain_ball.pos.y = 15;
			remain_ball.pos.width  = MyConstant.BALL_W;
			remain_ball.pos.height = MyConstant.BALL_H;
			lives.add(remain_ball);
		}
	}

	private void init_message() {
		
		msg_upper.pos.x = 10;
		msg_upper.pos.y = MyConstant.GAMEPAN_H/2 + 20;
		
		msg_lower.pos.x = 50;
		msg_lower.pos.y = MyConstant.GAMEPAN_H/2 + 80;
	}

	private void init_timer() {
		
		ActionListener timer_listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(game_state==MyConstant.PROCEED) {
					process();
					gamePan.repaint(); //paintComponent(g) call
				}  
			}
		};
		timer = new Timer(10, timer_listener);
		//timer.start();
	}

	protected void process() {
		
		move_ball();
		move_bar();
		collision_wall_check();
		collision_bar_check();
		collision_bricks_check();
	}

	private void move_bar() {
		
		if((key_state & MyConstant.Key.LEFT) == MyConstant.Key.LEFT) {
			bar.pos.x -= speed + 2;
		}else if((key_state & MyConstant.Key.RIGHT) == MyConstant.Key.RIGHT) {
			bar.pos.x += speed + 2;
		}
		
		if(bar.pos.x + MyConstant.BAR_W > MyConstant.GAMEPAN_W) {
			bar.pos.x = MyConstant.GAMEPAN_W - MyConstant.BAR_W;
		}else if(bar.pos.x < 0) {
			bar.pos.x = 0;
		}
	}
	
	private void move_ball() {
		
		if(bRight)
			ball.pos.x += speed;
		else if(bRight==false)
			ball.pos.x -= speed;
		if(bUp)
			ball.pos.y -= speed;
		else if(bUp==false)
			ball.pos.y += speed;
	}
	
	private void collision_wall_check() {
		
		if((ball.pos.x + MyConstant.BALL_W) > MyConstant.GAMEPAN_W) {		//오른쪽 벽
			ball.pos.x = MyConstant.GAMEPAN_W - MyConstant.BALL_W; 
			bRight = false;
		}else if((ball.pos.x) < 0) { 										//왼쪽 벽
			ball.pos.x = 0;
			bRight = true;
		}else if((ball.pos.y) < 0 ) {										//위쪽 벽
			ball.pos.y = 0;
			bUp = false;
			
		}else if((ball.pos.y + MyConstant.BALL_H) > MyConstant.GAMEPAN_H) { //화면 아래로 내려가면
			if((lives.size()-1) >= 0) {
				playSound("audio/ball_out.wav", false);
				game_state=MyConstant.TRY_AGAIN;
			}else {
				playSound("audio/game_over.wav", false);
				game_state=MyConstant.GAME_OVER;
			}
		}
	}
		
	private void collision_bar_check() {
		
		if(ball.pos.intersects(bar.pos)) {
			playSound("audio/collision_bar.wav", false);
			bUp = true;
		}
	}

	private void collision_bricks_check() {
		
		for(int i=0; i<bricks.size(); i++) {
			
			Brick brick = bricks.get(i);
			
			if(ball.pos.intersects(brick.pos)) {
				
				playSound("audio/collision_brick.wav", false);
				score += 20;
				if(bUp==true) {
					bUp=false;
				}else if(bUp==false) {
					bUp=true;
				}
				bricks.remove(brick);
				
			}
			if(bricks.size()==0) { 						//Brick 다 깨면
				playSound("audio/clear.wav", false);
				if(stage == 1) {
					game_state = MyConstant.Stage.CLEAR_1;
				}else if(stage == 2) {
					game_state = MyConstant.Stage.CLEAR_2;
				}else if(stage == 3){
					game_state = MyConstant.Stage.CLEAR_3;
				}
			}
		}
	}
	
	private void playSound(String pathName, boolean isLoop) {
		
		try {
			clip = AudioSystem.getClip();
			File audioFile = new File(pathName);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			clip.open(audioStream);
			clip.start();
			if(isLoop)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init_gamepan() {
		
		gamePan = new JPanel() {
			
			@Override
			protected void paintComponent(Graphics g) {
				// TODO Auto-generated method stub
				//super.paintComponent(g);
				
				//이전화면 지우기
				//g.clearRect(0, 0, MyConstant.GAMEPAN_W, MyConstant.GAMEPAN_H);
				
				//g.setColor(Color.darkGray);
				//g.fillRect(0, 0, MyConstant.GAMEPAN_W, MyConstant.GAMEPAN_H);
				
				g.drawImage(img_background, 0, 0, gamePan);
				
				ball.draw(g);
				bar.draw(g);
				
				draw_bricks(g);
				draw_message(g);
				draw_speed(g);
				draw_lives(g);
				draw_score(g);
				draw_stage(g);
			}
		};
		gamePan.setPreferredSize(new Dimension(MyConstant.GAMEPAN_W, MyConstant.GAMEPAN_H));
		this.add(gamePan);
		playSound("audio/bgm.wav", true);
	}

	protected void draw_bricks(Graphics g) {
		
		for(Brick brick : bricks) {
			brick.draw(g);
		}
	}

	protected void draw_message(Graphics g) {

		if(game_state==MyConstant.START) {					//시작화면
			msg_upper.setMsg_upper("  BREAK OUT  ");
			msg_upper.draw(g);
			msg_lower.setMsg_lower("START : SPACE KEY ");
			msg_lower.draw2(g);
		}else if(game_state==MyConstant.TRY_AGAIN) {		//목숨-1
			msg_upper.setMsg_upper("  TRY AGAIN  ");
			msg_upper.draw(g);
			msg_lower.setMsg_lower("START : SPACE KEY ");
			msg_lower.draw2(g);
		}else if(game_state==MyConstant.GAME_OVER) {		//게임오버(목숨0)
			msg_upper.setMsg_upper("  GAME OVER  ");
			msg_upper.draw(g);
			msg_lower.setMsg_lower("RESET : SPACE KEY ");
			msg_lower.draw2(g);
		}else if(game_state==MyConstant.Stage.CLEAR_1) {	//Stage1 Clear
			msg_upper.setMsg_upper("STAGE 1 CLEAR");
			msg_upper.draw(g);
			msg_lower.setMsg_lower("NEXT STAGE : SPACE ");
			msg_lower.draw2(g);
		}else if(game_state==MyConstant.Stage.CLEAR_2) {	//Stage2 Clear
			msg_upper.setMsg_upper("STAGE 2 CLEAR");
			msg_upper.draw(g);
			msg_lower.setMsg_lower("NEXT STAGE : SPACE ");
			msg_lower.draw2(g);
		}else if(game_state==MyConstant.Stage.CLEAR_3) {	//Stage3 Clear
			msg_upper.setMsg_upper("STAGE 3 CLEAR");
			msg_upper.draw(g);
			msg_lower.setMsg_lower("RESET : SPACE KEY ");
			msg_lower.draw2(g);
		}else if(game_state==MyConstant.PROCEED) {			//게임시작
			if(isPause) {									//일시정지
				msg_upper.setMsg_upper("    PAUSE    ");
				msg_upper.draw(g);	
				msg_lower.setMsg_lower("RESUME:ESC  QUIT:Q");
				msg_lower.draw2(g);
			}else {
				msg_upper.setMsg_upper("");
				msg_upper.draw(g);
				msg_lower.setMsg_lower("");
				msg_lower.draw2(g);
			}
		}
		
	}
	
	Font font1 = new Font("Courier New",Font.BOLD,17);
	protected void draw_speed(Graphics g) {
	
		g.setFont(font1);
		String str_ball_speed = String.format("SPEED:%2d", speed);
		g.setColor(Color.white);
		g.drawString(str_ball_speed, 5 , 40);
	}

	protected void draw_lives(Graphics g) {
		
		for(Ball ball : lives) {
			ball.draw(g);
		}
	}

	protected void draw_score(Graphics g) {
		
		String str_score = String.format("SCORE: %d", score);
		g.drawString(str_score, 5 , 20);
	}
	
	Font font2 = new Font("Courier New",Font.BOLD,25);
	protected void draw_stage(Graphics g) {
		
		g.setFont(font2);
		String str_stage = String.format("STAGE %d", stage);
		g.setColor(Color.white);
		g.drawString(str_stage, 152 , 30);
	}

	public static void main(String[] args) {

		new MyMain_벽돌깨기();
	}

}
