
public class EnemyObj extends ImageObj {

	public int hp;
	public int lvl;

	public EnemyObj(String img_file, int courtWidth, int courtHeight, int v_x, int v_y, int pos_x, int pos_y, int width,
			int height, int lvl) {
		super(img_file, courtWidth, courtHeight, v_x, v_y, pos_x, pos_y, width, height);
		this.lvl = lvl;
		hp = (lvl - 1) * 10 + 50; // linearly scaling HP, might make polynomial
									// or exponential growth, depending on
									// damage scaling.
	}

	@Override
	public void move() {
		pos_x += v_x;
		pos_y += v_y;
	}

}
