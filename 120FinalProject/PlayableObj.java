
public class PlayableObj extends ImageObj{
	
	public int damage;

	public PlayableObj(String img_file, int courtWidth, int courtHeight, int v_x, int v_y, int pos_x, int pos_y,
			int width, int height, int damage) {
		super(img_file, courtWidth, courtHeight, v_x, v_y, pos_x, pos_y, width, height);
		this.damage = damage;
	}

}
