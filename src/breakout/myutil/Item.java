package breakout.myutil;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Item {
	
	//�簢��ǥ �����ϴ� �ڷ���
	public Rectangle pos = new Rectangle();
	
	//�߻�޼ҵ�
	public abstract void draw(Graphics g);

}
