
public class Drops extends ImageObj{
	
	public int value;
	public int a_y;

	public Drops(String img_file, int courtWidth, int courtHeight, int v_x, int v_y, int pos_x, int pos_y, int width,
			int height, int value, int a_y) {
		super(img_file, courtWidth, courtHeight, v_x, v_y, pos_x, pos_y, width, height);
		this.value = value;
		this.a_y = a_y;
	}
	
	@Override
	public void move() {
		pos_x += v_x;
		pos_y += v_y;
		v_y += a_y;
	}

}
