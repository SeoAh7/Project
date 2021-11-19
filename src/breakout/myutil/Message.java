package breakout.myutil;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Message extends Item{

	String msg_upper, msg_lower;
	
	
	public void setMsg_upper(String msg_upper) {
		this.msg_upper = msg_upper;
	}

	public void setMsg_lower(String msg_lower) {
		this.msg_lower = msg_lower;
	}

	@Override
	public void draw(Graphics g) {
		
		Font font = new Font("Courier New", Font.BOLD, 52);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(msg_upper, pos.x, pos.y);
	}

	public void draw2(Graphics g) {
		
		Font font = new Font("Courier New", Font.BOLD, 32);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(msg_lower, pos.x, pos.y);
	}


}
