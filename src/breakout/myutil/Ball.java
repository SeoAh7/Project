package breakout.myutil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import breakout.mymain.MyMain_º®µ¹±ú±â;

public class Ball extends Item{

	static Image img_ball;
	static {
		img_ball = Toolkit.getDefaultToolkit().getImage("images/ball.png");
	}
	
	@Override
	public void draw(Graphics g) {
		
		//g.setColor(Color.white);
		//g.fillOval(pos.x, pos.y, pos.width, pos.height);
		
		g.drawImage(img_ball, pos.x, pos.y, null);
		
	}
		
}
