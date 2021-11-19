package breakout.myutil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

public class Bar extends Item{
	
	static Image img_bar;
	static {
		img_bar = Toolkit.getDefaultToolkit().getImage("images/bar.png");
	}

	@Override
	public void draw(Graphics g) {
		
		//g.setColor(Color.white);
		//g.fillRect(pos.x, pos.y, pos.width, pos.height);
		
		g.drawImage(img_bar, pos.x, pos.y, null);
		
	}

}
