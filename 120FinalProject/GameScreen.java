import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.*;

@SuppressWarnings("serial")
public class GameScreen extends JPanel {

	public static final int COURT_WIDTH = 460;
	public static final int COURT_HEIGHT = 768;
	public static final int INTERVAL = 35;

	private int player_velocity = 18;
	private int player_damage = 50;
	private int attackDelay = 4;
	private int bulletSize = 25;

	private int shootTick = 0;
	private int generationTick = 0;
	private int enemyLvl = 1; // Enemy Level and Wave # are the same.
	private int startX = 25;
	private int changeX = 75;

	private int coinsTotal = 0;
	private int coinsRound = 0;
	private int enemiesKilled = 0;

	private int totalEnemiesKilled = 0;
	private int totalCoinsCollected = 0;
	private int highestWave = 1;
	private int highscore = 0;

	private int upgradesWidthPx = 460;
	private int upgradesHeightPx = 460;
	private int upgradesWidth = 2;
	private int upgradesHeight = 2;
	private Rectangle[][] upgrades = new Rectangle[upgradesWidth][upgradesHeight];
	private String[] upgradeStrings = { "Damage", "Att Speed", "Speed", "Bull Size" };
	private int[] upgradeCosts = { 20, 50, 20, 30 };

	ImageObj dmgImg;
	ImageObj ASImg;
	ImageObj SpeedImg;
	ImageObj SizeImg;

	private PlayableObj player = new PlayableObj("playerShip2_blue.png", COURT_WIDTH, COURT_HEIGHT, 0, 0,
			COURT_WIDTH / 2, 700, 62, 62, player_damage);
	private Set<PlayerProjectile> projectileSet = new TreeSet<PlayerProjectile>();
	private Set<EnemyObj> enemySet = new TreeSet<EnemyObj>();
	private Set<Drops> drops = new TreeSet<Drops>();
	private Set<Star> stars = new TreeSet<Star>();
	private Set<GameObj> comets = new TreeSet<GameObj>();

	public enum GameMode {
		PLAYING, LOSEMENU, STARTMENU, INSTRUCTIONS, UPGRADES, STATS
	}

	public GameMode mode = GameMode.STARTMENU;

	public GameScreen() {

		Random rand = new Random();
		for (int i = 0; i < 100; i++) {
			stars.add(new Star(COURT_WIDTH, COURT_HEIGHT, 0, 10, rand.nextInt(COURT_WIDTH + 10) - 10,
					rand.nextInt(COURT_HEIGHT + 10) - 10, 25, 25));
		}

		for (int i = 0; i < upgrades.length; i++) {
			for (int j = 0; j < upgrades[i].length; j++) {
				upgrades[i][j] = new Rectangle(5 + i * (upgradesWidthPx / upgradesWidth),
						150 + j * (upgradesHeightPx / upgradesHeight), upgradesWidthPx / upgradesWidth,
						upgradesHeightPx / upgradesHeight);
			}
		}

		dmgImg = new ImageObj("laserBlue10.png", COURT_WIDTH, COURT_HEIGHT, 0, 0,
				upgrades[0][0].x + (upgrades[0][0].width - 50) / 2, upgrades[0][0].y + 80, 50, 50);

		ASImg = new ImageObj("powerupYellow_bolt.png", COURT_WIDTH, COURT_HEIGHT, 0, 0,
				upgrades[0][1].x + (upgrades[0][1].width - 35) / 2, upgrades[0][1].y + 87, 35, 35);

		SpeedImg = new ImageObj("bolt_gold.png", COURT_WIDTH, COURT_HEIGHT, 0, 0,
				upgrades[1][0].x + (upgrades[1][0].width - 30) / 2, upgrades[1][0].y + 85, 30, 40);

		SizeImg = new ImageObj("laserBlue10.png", COURT_WIDTH, COURT_HEIGHT, 0, 0,
				upgrades[1][1].x + (upgrades[1][1].width - bulletSize * 2) / 2,
				upgrades[1][1].y + 80 + (50 - bulletSize * 2) / 2, bulletSize * 2, bulletSize * 2);

		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		Timer timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer.start();

		setFocusable(true);

		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
					player.v_x = -player_velocity;
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
					player.v_x = player_velocity;
				else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
					player.v_y = -player_velocity;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
					player.v_y = player_velocity;
				else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (mode == GameMode.STARTMENU)
						reset();
					else if (mode == GameMode.LOSEMENU || mode == GameMode.STATS || mode == GameMode.INSTRUCTIONS
							|| mode == GameMode.UPGRADES)
						mode = GameMode.STARTMENU;
				}
			}

			public void keyReleased(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
						&& player.v_x == -player_velocity)
					player.v_x = 0;
				else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
						&& player.v_x == player_velocity)
					player.v_x = 0;
				else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
						&& player.v_y == -player_velocity)
					player.v_y = 0;
				else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
						&& player.v_y == player_velocity)
					player.v_y = 0;
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (mode == GameMode.STARTMENU) {
					if (e.getX() >= 68 && e.getX() <= 68 + 334 && e.getY() >= 300 - 24 && e.getY() <= 300 + 5) {
						mode = GameMode.INSTRUCTIONS;
					} else if (e.getX() >= 68 && e.getX() <= 68 + 334 && e.getY() >= 370 - 24
							&& e.getY() <= 370 - 24 + 29) {
						mode = GameMode.STATS;
					} else if (e.getX() >= 68 && e.getX() <= 68 + 334 && e.getY() >= 440 - 24
							&& e.getY() <= 440 - 24 + 29) {
						mode = GameMode.UPGRADES;
					} else if (e.getX() >= 68 && e.getX() <= 68 + 334 && e.getY() >= 510 - 24
							&& e.getY() <= 510 - 24 + 29) {
						try {
							load();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				} else if (mode == GameMode.UPGRADES) {
					for (int i = 0; i < upgrades.length; i++) {
						for (int j = 0; j < upgrades[i].length; j++) {
							if (upgrades[i][j].contains(e.getPoint())) {
								switch (upgrades.length * i + j) {
								case 0: // Damage
									if (coinsTotal >= upgradeCosts[0]) {
										player.damage += 5;
										coinsTotal -= upgradeCosts[0];
										upgradeCosts[0] += 10;
									}
									break;
								case 1: // Att Speed
									if (coinsTotal >= upgradeCosts[1] && attackDelay > 1) {
										attackDelay -= 1;
										coinsTotal -= upgradeCosts[1];
										upgradeCosts[1] += 25;
										if (attackDelay == 1) {
											upgradeStrings[1] = "Done";
											upgradeCosts[1] = 9999;
										}
									}
									break;
								case 2: // Speed
									if (coinsTotal >= upgradeCosts[2] && player_velocity < 36) {
										player_velocity += 1;
										coinsTotal -= upgradeCosts[2];
										upgradeCosts[2] += 10;
										if (player_velocity == 36) {
											upgradeStrings[2] = "Done";
											upgradeCosts[2] = 9999;
										}
									}
									break;
								case 3: // Bullet Size
									if (coinsTotal >= upgradeCosts[3] && bulletSize < 34) {
										bulletSize += 2;
										coinsTotal -= upgradeCosts[3];
										upgradeCosts[3] += 15;
										if (bulletSize == 34) {
											upgradeStrings[3] = "Done";
											upgradeCosts[3] = 9999;
										}
									}
									break;
								default:
									break;
								}
							}
						}
					}
				}
			}

		});
	}

	public void reset() {

		mode = GameMode.PLAYING;

		player.pos_x = COURT_WIDTH / 2;
		player.pos_y = 700;
		projectileSet = new TreeSet<PlayerProjectile>();
		enemySet = new TreeSet<EnemyObj>();
		drops = new TreeSet<Drops>();
		comets = new TreeSet<GameObj>();

		shootTick = 0;
		generationTick = 0;
		enemyLvl = 1;
		enemiesKilled = 0;
		coinsRound = 0;

		requestFocusInWindow();
	}

	void tick() {
		Iterator<Star> iterStars = stars.iterator();
		while (iterStars.hasNext()) {
			Star next = iterStars.next();
			next.move();
			if (next.pos_y > COURT_HEIGHT)
				iterStars.remove();
		}

		Random rand = new Random();
		stars.add(new Star(COURT_WIDTH, COURT_HEIGHT, 0, 10, rand.nextInt(COURT_WIDTH + 10) - 10, -10, 25, 25));

		if (mode == GameMode.PLAYING) {

			player.move();

			if (rand.nextInt(150 - enemyLvl*5) == 0) {
				switch (rand.nextInt(4)) {
				case 0:
					comets.add(new Comet1(COURT_WIDTH, COURT_HEIGHT, 0, 15 + enemyLvl * 3, rand.nextInt(COURT_WIDTH),
							-15, 70, 70));
					break;
				case 1:
					comets.add(new Comet2(COURT_WIDTH, COURT_HEIGHT, 0, 15 + enemyLvl * 3, rand.nextInt(COURT_WIDTH),
							-15, 70, 70));
					break;
				case 2:
					comets.add(new Comet3(COURT_WIDTH, COURT_HEIGHT, 0, 15 + enemyLvl * 3, rand.nextInt(COURT_WIDTH),
							-15, 70, 70));
					break;
				case 3:
					comets.add(new Comet4(COURT_WIDTH, COURT_HEIGHT, 0, 15 + enemyLvl * 3, rand.nextInt(COURT_WIDTH),
							-15, 70, 70));
					break;
				default:
					break;
				}
			}

			if (shootTick == 0) {
				projectileSet.add(new PlayerProjectile("laserBlue10.png", COURT_WIDTH, COURT_HEIGHT, 0, -20,
						player.pos_x + player.width / 2 - 10, player.pos_y - 10, bulletSize, bulletSize,
						player.damage));
			}
			if (shootTick == attackDelay)
				shootTick = 0;
			else
				shootTick++;

			if (generationTick % 42 == 0) {
				randomEnemyWave();
			}

			if (generationTick == 630) {
				generationTick = 1;
				enemyLvl++;
			} else
				generationTick++;

			Iterator<PlayerProjectile> iter = projectileSet.iterator();
			while (iter.hasNext()) {
				PlayerProjectile next = iter.next();
				next.move();
				if (next.pos_y < 0)
					iter.remove();
			}

			Iterator<EnemyObj> iter2 = enemySet.iterator();
			while (iter2.hasNext()) {
				EnemyObj next = iter2.next();
				next.move();
				if (next.pos_y > COURT_HEIGHT)
					iter2.remove();
			}

			Iterator<Drops> iter6 = drops.iterator();
			while (iter6.hasNext()) {
				Drops next = iter6.next();
				next.move();
				if (next.pos_y > COURT_HEIGHT)
					iter6.remove();
			}
			
			Iterator<GameObj> iter8 = comets.iterator();
			while (iter8.hasNext()) {
				GameObj next = iter8.next();
				next.move();
				if (next.pos_y > COURT_HEIGHT)
					iter8.remove();
			}

			// Projectile-Enemy Collision Detection
			Iterator<PlayerProjectile> iter3 = projectileSet.iterator();
			while (iter3.hasNext()) {
				PlayerProjectile proj = iter3.next();
				Iterator<EnemyObj> iter4 = enemySet.iterator();
				while (iter4.hasNext()) {
					EnemyObj enemy = iter4.next();

					if (proj.intersects(enemy)) {
						enemy.hp -= proj.damage;
						iter3.remove();
						if (enemy.hp <= 0) {
							if (enemy.width == 59) {
								for (int i = 0; i < 10; i++)
									addDrop(enemy.pos_x, enemy.pos_y);
							} else if (enemy.width == 58) {
								for (int i = 0; i < 10; i++)
									addDrop(enemy.pos_x, enemy.pos_y);
							} else {
								addDrop(enemy.pos_x, enemy.pos_y);
							}
							iter4.remove();
							enemiesKilled++;
						}
						break;
					}
				}
			}

			// Enemy-Player Collision Detection
			Iterator<EnemyObj> iter5 = enemySet.iterator();
			while (iter5.hasNext()) {
				if (player.intersects(iter5.next())) {
					repaint();
					mode = GameMode.LOSEMENU;
					coinsTotal += coinsRound;
					totalCoinsCollected += coinsRound;
					totalEnemiesKilled += enemiesKilled;
					if (enemyLvl > highestWave)
						highestWave = enemyLvl;
					if (coinsRound > highscore)
						highscore = coinsRound;

					save();
				}
			}

			// Player-Drops Collision Detection
			Iterator<Drops> iter7 = drops.iterator();
			while (iter7.hasNext()) {
				Drops next = iter7.next();
				if (player.intersects(next)) {
					iter7.remove();
					coinsRound += next.value;
				}
			}
			
			// Player-Comets Collision Detection
			Iterator<GameObj> iter9 = comets.iterator();
			while (iter9.hasNext()) {
				if (player.intersects(iter9.next())) {
					repaint();
					mode = GameMode.LOSEMENU;
					coinsTotal += coinsRound;
					totalCoinsCollected += coinsRound;
					totalEnemiesKilled += enemiesKilled;
					if (enemyLvl > highestWave)
						highestWave = enemyLvl;
					if (coinsRound > highscore)
						highscore = coinsRound;

					save();
				}
			}
		}
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		g.setColor(Color.white);

		Iterator<Star> iterStars = stars.iterator();
		while (iterStars.hasNext()) {
			iterStars.next().draw(g);
		}

		Font bitFont = null;
		try {
			// create the font to use. Specify the size!
			bitFont = Font.createFont(Font.TRUETYPE_FONT, new File("8-BIT FONT.TTF")).deriveFont(48f);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}

		g.setFont(bitFont);

		if (mode == GameMode.STARTMENU) {

			g.setFont(g.getFont().deriveFont(32f));
			drawCenteredString(g, "GENERIC VERTICAL", 150);
			drawCenteredString(g, "SHOOTER", 200);

			g.setFont(g.getFont().deriveFont(24f));
			drawCenteredString(g, "Instructions", 300);
			drawCenteredString(g, "STATS", 370);
			drawCenteredString(g, "Upgrades", 440);
			drawCenteredString(g, "Load", 510);
			drawCenteredString(g, "Press Space to Play", 600);

			g.drawRect(68, 300 - 24, 334, 29);
			g.drawRect(68, 370 - 24, 334, 29);
			g.drawRect(68, 440 - 24, 334, 29);
			g.drawRect(68, 510 - 24, 334, 29);

		} else if (mode == GameMode.LOSEMENU) {

			g.setFont(g.getFont().deriveFont(48f));
			drawCenteredString(g, "You Lost", 200);

			g.setFont(g.getFont().deriveFont(24f));
			drawCenteredString(g, "Enemies Killed      " + enemiesKilled, 350);
			drawCenteredString(g, "Coins Collected    " + coinsRound, 400);
			drawCenteredString(g, "Wave                        " + enemyLvl, 300);

			g.setFont(g.getFont().deriveFont(18f));
			drawCenteredString(g, "Progress Auto Saved", 500);

			g.setFont(g.getFont().deriveFont(18f));
			drawCenteredString(g, "Press Space to Exit to Menu", 600);

		} else if (mode == GameMode.PLAYING) {

			player.draw(g);

			Iterator<PlayerProjectile> iter = projectileSet.iterator();
			while (iter.hasNext()) {
				iter.next().draw(g);
			}

			Iterator<EnemyObj> iter2 = enemySet.iterator();
			while (iter2.hasNext()) {
				iter2.next().draw(g);
			}

			Iterator<Drops> iter3 = drops.iterator();
			while (iter3.hasNext()) {
				iter3.next().draw(g);
			}
			
			Iterator<GameObj> iter4 = comets.iterator();
			while (iter4.hasNext()) {
				iter4.next().draw(g);
			}

			g.setFont(g.getFont().deriveFont(24f));
			FontMetrics fontMet = g.getFontMetrics();
			drawString(g, coinsRound + " Coins", COURT_WIDTH - fontMet.stringWidth(coinsRound + " Coins") + 5, 29);
			drawString(g, "Wave " + enemyLvl, 5, 29);

		} else if (mode == GameMode.INSTRUCTIONS) {
			g.setFont(g.getFont().deriveFont(30f));
			drawCenteredString(g, "INSTRUCTIONS", 50);

			g.setFont(g.getFont().deriveFont(18f));
			drawString(g, "GENERIC VERTICAL SHOOTER is", 10, 100);
			drawString(g, "as its name suggests a vert", 10, 130);
			drawString(g, "ical shooting game based ar", 10, 160);
			drawString(g, "ound defeating enemies scro", 10, 190);
			drawString(g, "lling from the top as you t", 10, 220);
			drawString(g, "he player shoot up at them ", 10, 250);
			drawString(g, "while avoiding getting hit ", 10, 280);
			drawString(g, "and picking up their drops ", 10, 310);
			drawString(g, "and upgrading along the way", 10, 340);

			g.setFont(g.getFont().deriveFont(30f));
			drawCenteredString(g, "CONTROLS", 420);

			g.setFont(g.getFont().deriveFont(18f));
			drawCenteredString(g, "USE WASD or ARROWKEYS", 470);
			drawCenteredString(g, "TO MOVE AND SHOOT", 500);

			drawCenteredString(g, "Press Space to Go to Menu", 600);
		} else if (mode == GameMode.UPGRADES) {
			g.setFont(g.getFont().deriveFont(24f));
			drawCenteredString(g, "UPGRADES", 50);
			drawCenteredString(g, coinsTotal + " Coins", 80);

			g.setFont(g.getFont().deriveFont(18f));
			drawCenteredString(g, "Press Space to Go To Menu", 650);

			g.setFont(g.getFont().deriveFont(24f));
			for (int i = 0; i < upgrades.length; i++) {
				for (int j = 0; j < upgrades[i].length; j++) {
					g.drawRect(upgrades[i][j].x, upgrades[i][j].y, upgrades[i][j].width, upgrades[i][j].height);

					centeredStringRect(g, upgradeStrings[i * upgrades.length + j], upgrades[i][j], 5);
					centeredStringRect(g, Integer.toString(upgradeCosts[i * upgrades.length + j]) + " Coins",
							upgrades[i][j], 200);
				}
			}

			dmgImg.draw(g);
			ASImg.draw(g);
			SpeedImg.draw(g);
			SizeImg.draw(g);

		} else if (mode == GameMode.STATS) {
			g.setFont(g.getFont().deriveFont(20f));
			drawCenteredString(g, "Total Enemies Killed   " + totalEnemiesKilled, 100);
			drawCenteredString(g, "Total Gold Collected   " + totalCoinsCollected, 150);
			drawCenteredString(g, "Highest Wave Reached   " + highestWave, 200);
			drawCenteredString(g, "Highest Score          " + highscore, 250);

			g.setFont(g.getFont().deriveFont(18f));
			drawCenteredString(g, "Press Space to Go To Menu", 650);
		}

	}

	public void addDrop(int x, int y) {
		Random rand = new Random();
		int value = 1;
		String file = "star_gold.png";
		int size = 25;
		if (rand.nextInt(10) == 0) {
			value = 10;
			file = "star.png";
			size = 75;
		}

		if (rand.nextInt(2) == 0) {
			drops.add(new Drops(file, COURT_WIDTH, COURT_HEIGHT, rand.nextInt(4) + 1, -5, x, y, size, size, value, 2));
		} else {
			drops.add(new Drops(file, COURT_WIDTH, COURT_HEIGHT, -rand.nextInt(4) + 1, -5, x, y, size, size, value, 2));
		}
	}

	public void addDropChest(int x, int y) {
		Random rand = new Random();
		if (rand.nextInt(2) == 0) {
			drops.add(new Drops("star.png", COURT_WIDTH, COURT_HEIGHT, rand.nextInt(4) + 1, -5, x, y, 75, 75, 10, 2));
		} else {
			drops.add(new Drops("star.png", COURT_WIDTH, COURT_HEIGHT, -rand.nextInt(4) + 1, -5, x, y, 75, 75, 10, 2));
		}
	}

	public void randomEnemyWave() {
		Random rand = new Random();
		String enemyFile = "enemyBlack5.png";
		int size = 60;
		for (int i = 0; i < 6; i++) {
			size = 60;
			switch (rand.nextInt(5)) {
			case 0:
				enemyFile = "enemyBlack5.png";
				break;
			case 1:
				enemyFile = "enemyBlue2.png";
				break;
			case 2:
				enemyFile = "enemyBlue4.png";
				break;
			case 3:
				enemyFile = "enemyRed1.png";
				break;
			case 4:
				enemyFile = "enemyGreen3.png";
				break;
			default:
				break;
			}
			if (rand.nextInt(108) == 0) {
				enemyFile = "ufoYellow.png";
				size = 59;
				if (rand.nextInt(5) == 0) {
					enemyFile = "ufoRed.png";
					size = 58;
				}
			}
			enemySet.add(new EnemyObj(enemyFile, COURT_WIDTH, COURT_HEIGHT, 0, 12 + enemyLvl / 2, startX + i * changeX,
					0, size, size, enemyLvl));
		}
	}

	public void centeredStringRect(Graphics g, String s, Rectangle r, int extraY) {
		FontMetrics fontMet = g.getFontMetrics();
		g.drawString(s, r.x + (r.width - fontMet.stringWidth(s)) / 2, r.y + fontMet.getHeight() + extraY);
	}

	public void drawCenteredString(Graphics g, String s, int y) {
		FontMetrics fontMet = g.getFontMetrics();
		g.drawString(s, (COURT_WIDTH - fontMet.stringWidth(s)) / 2 + fontMet.stringWidth(" ") / 2, y);
	}

	public void drawString(Graphics g, String s, int x, int y) {
		g.drawString(s, x, y);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}

	public void save() {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("PlayerData.txt"), "utf-8"));
			writer.write(player_velocity + "," + player_damage + "," + attackDelay + "," + bulletSize + "," + coinsTotal
					+ "," + totalEnemiesKilled + "," + totalCoinsCollected + "," + highestWave + "," + highscore + ",");
			for (int i = 0; i < upgradeStrings.length; i++)
				writer.write(upgradeStrings[i] + ",");

			for (int i = 0; i < upgradeCosts.length; i++) {
				if (i == upgradeCosts.length - 1) {
					writer.write(upgradeCosts[i] + "");
				} else
					writer.write(upgradeCosts[i] + ",");
			}
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load() throws FileNotFoundException {
		Scanner fileScanner = new Scanner(new File("PlayerData.txt"));
		String line = fileScanner.nextLine();

		String[] lineSplit = line.split(",");

		player_velocity = Integer.parseInt(lineSplit[0]);
		player_damage = Integer.parseInt(lineSplit[1]);
		attackDelay = Integer.parseInt(lineSplit[2]);
		bulletSize = Integer.parseInt(lineSplit[3]);
		coinsTotal = Integer.parseInt(lineSplit[4]);
		totalEnemiesKilled = Integer.parseInt(lineSplit[5]);
		totalCoinsCollected = Integer.parseInt(lineSplit[6]);
		highestWave = Integer.parseInt(lineSplit[7]);
		highscore = Integer.parseInt(lineSplit[8]);

		upgradeStrings[0] = lineSplit[9];
		upgradeStrings[1] = lineSplit[10];
		upgradeStrings[2] = lineSplit[11];
		upgradeStrings[3] = lineSplit[12];

		upgradeCosts[0] = Integer.parseInt(lineSplit[13]);
		upgradeCosts[1] = Integer.parseInt(lineSplit[14]);
		upgradeCosts[2] = Integer.parseInt(lineSplit[15]);
		upgradeCosts[3] = Integer.parseInt(lineSplit[16]);
	}
}
