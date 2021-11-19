package breakout.myutil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

public class Brick extends Item{
	
	static Image [] img_bricks = new Image[7];
	static {
		for(int i=0; i<img_bricks.length; i++) {
		
		String filename = String.format("images/bricks/brick_%d.png",i);
		img_bricks[i] = Toolkit.getDefaultToolkit().getImage(filename); 
		}
	}
	
	public Color color = Color.red;
	
	public int index = 0;
	@Override
	public void draw(Graphics g) {

		g.drawImage(img_bricks[index], pos.x, pos.y, null);
	}
}
