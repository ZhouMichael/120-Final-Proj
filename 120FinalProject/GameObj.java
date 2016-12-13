import java.awt.Graphics;

public class GameObj implements Comparable<GameObj>{
	public int pos_x;
	public int pos_y;

	public int width;
	public int height;

	public int v_x;
	public int v_y;

	public int max_x;
	public int max_y;

	public GameObj(int v_x, int v_y, int pos_x, int pos_y, int width, int height, int court_width, int court_height) {
		this.v_x = v_x;
		this.v_y = v_y;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.width = width;
		this.height = height;
		this.max_x = court_width - width;
		this.max_y = court_height - height;

	}

	public void move() {
		pos_x += v_x;
		pos_y += v_y;

		clip();
	}

	public void clip() {
		if (pos_x < 0)
			pos_x = 0;
		else if (pos_x > max_x)
			pos_x = max_x;

		if (pos_y < 0)
			pos_y = 0;
		else if (pos_y > max_y)
			pos_y = max_y;
	}

	public boolean intersects(GameObj obj) {
		return (pos_x + width >= obj.pos_x && pos_y + height >= obj.pos_y && obj.pos_x + obj.width >= pos_x
				&& obj.pos_y + obj.height >= pos_y);
	}

	public boolean willIntersect(GameObj obj) {
		int next_x = pos_x + v_x;
		int next_y = pos_y + v_y;
		int next_obj_x = obj.pos_x + obj.v_x;
		int next_obj_y = obj.pos_y + obj.v_y;
		return (next_x + width >= next_obj_x && next_y + height >= next_obj_y && next_obj_x + obj.width >= next_x
				&& next_obj_y + obj.height >= next_y);
	}

	public void bounce(Direction d) {
		if (d == null)
			return;
		switch (d) {
		case UP:
			v_y = Math.abs(v_y);
			break;
		case DOWN:
			v_y = -Math.abs(v_y);
			break;
		case LEFT:
			v_x = Math.abs(v_x);
			break;
		case RIGHT:
			v_x = -Math.abs(v_x);
			break;
		}
	}

	public Direction hitWall() {
		if (pos_x + v_x < 0)
			return Direction.LEFT;
		else if (pos_x + v_x > max_x)
			return Direction.RIGHT;
		if (pos_y + v_y < 0)
			return Direction.UP;
		else if (pos_y + v_y > max_y)
			return Direction.DOWN;
		else
			return null;
	}

	public Direction hitObj(GameObj other) {

		if (this.willIntersect(other)) {
			double dx = other.pos_x + other.width / 2 - (pos_x + width / 2);
			double dy = other.pos_y + other.height / 2 - (pos_y + height / 2);

			double theta = Math.acos(dx / (Math.sqrt(dx * dx + dy * dy)));
			double diagTheta = Math.atan2(height / 2, width / 2);

			if (theta <= diagTheta) {
				return Direction.RIGHT;
			} else if (theta > diagTheta && theta <= Math.PI - diagTheta) {
				if (dy > 0) {
					// Coordinate system for GUIs is switched
					return Direction.DOWN;
				} else {
					return Direction.UP;
				}
			} else {
				return Direction.LEFT;
			}
		} else {
			return null;
		}

	}

	public void draw(Graphics g) {
	}
	
	@Override
	public boolean equals(Object obj){
		return false;
	}

	@Override
	public int compareTo(GameObj o) {
		if(pos_y < o.pos_y)
			return -1;
		else 
			return 1;
	}

}
