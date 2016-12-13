import java.awt.*;
import javax.swing.*;

public class Game implements Runnable {
	public void run() {
		final JFrame frame = new JFrame("Generic Vertical Shooter");
		frame.setLocation(10, 10);

		final GameScreen court = new GameScreen();
		court.setBackground(Color.black);
		frame.add(court, BorderLayout.CENTER);

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}
