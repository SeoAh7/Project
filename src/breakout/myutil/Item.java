package breakout.myutil;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Item {
	
	//사각좌표 관리하는 자료형
	public Rectangle pos = new Rectangle();
	
	//추상메소드
	public abstract void draw(Graphics g);

}
