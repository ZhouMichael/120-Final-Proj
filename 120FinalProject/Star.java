import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Star extends GameObj {

	private static BufferedImage star;

	public Star(int courtWidth, int courtHeight, int v_x, int v_y, int pos_x, int pos_y, int width, int height) {
		super(v_x, v_y, pos_x, pos_y, width, height, courtWidth, courtHeight);
		try {
			if (star == null) {
				star = ImageIO.read(new File("star.png"));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(star, pos_x, pos_y, width, height, null);
	}

	@Override
	public void move() {
		pos_x += v_x;
		pos_y += v_y;
	}
}
