import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Comet4 extends GameObj {

	private static BufferedImage comet;

	public Comet4(int courtWidth, int courtHeight, int v_x, int v_y, int pos_x, int pos_y, int width, int height) {
		super(v_x, v_y, pos_x, pos_y, width, height, courtWidth, courtHeight);
		try {
			if (comet == null) {
				comet = ImageIO.read(new File("Comet4.png"));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(comet, pos_x, pos_y, width, height, null);
	}

	@Override
	public void move() {
		pos_x += v_x;
		pos_y += v_y;
	}
}
