package testo;

import javax.swing.*; 

import java.awt.*;
import java.awt.event.*;


public class OthelloGame {

	private static boolean isBlackTurn = true;
	private OthelloBoard logicalBoard;
	Player humanw;
	Player humanb;
	private boolean noclick = true;
	private boolean isBlackHuman = false;

	public OthelloGame() {
		class colorListener implements MouseListener {
			boolean color;

			public colorListener(boolean isBlack) {
				color = isBlack;
			}

			public void mouseClicked(MouseEvent e) {
				if (color)
					isBlackHuman = true;
				else
					isBlackHuman = false;
				noclick = false;
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}
		}// colorListener

		JFrame frame = new JFrame();
		JPanel black = new JPanel();
		JPanel white = new JPanel();
		JLabel textb = new JLabel("O");
		JLabel textw = new JLabel("R");
		textb.setFont(new java.awt.Font("Dialog", 1, 40));
		textw.setFont(new java.awt.Font("Dialog", 1, 35));
		textb.setForeground(Color.white);
		textw.setForeground(Color.BLACK);

		black.setBackground(Color.black);
		black.setLayout(new BorderLayout());
		black.setPreferredSize(new Dimension(200, 400));
		black.addMouseListener(new colorListener(true));
		black.add(textb,BorderLayout.EAST);
		
		white.addMouseListener(new colorListener(false));

		white.setBackground(Color.white);
		white.setLayout(new BorderLayout());
		white.add(textw,BorderLayout.WEST);
		white.setPreferredSize(new Dimension(200, 400));
		JPanel big = new JPanel(new FlowLayout());
		big.add(black);
		big.add(white);
		frame.add(big);
		frame.setResizable(false);
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		while (noclick) {
			System.out.println("");
		}// waiting

		humanw = new Player(false);
		humanb = new Player(true);
		frame.dispose();
		logicalBoard = new OthelloBoard(humanw, true);

	}// constructor

	public void nextMove(boolean isBlackTurn) {
		if (isBlackTurn == isBlackHuman)
			humanw.play(logicalBoard, 0);
		else
			humanb.play(logicalBoard, 0);
	}

	public int playedSquares() {
		return logicalBoard.getNumberOfTiles();
	}// playedSquares

	public void updateScore() {
		logicalBoard.updateScore();
	}

	public boolean canPlay() {
		return logicalBoard.canPlay();
	}

	public int getBlacks() {
		return logicalBoard.getBlacks();
	}

	public int getWhites() {
		return logicalBoard.getWhites();
	}

	public static void main(String[] args) {
		OthelloGame game = new OthelloGame();

		int i = 0;
		while (game.canPlay() && game.playedSquares() < 64
				&& game.getBlacks() != 0 && game.getWhites() != 0) {
			// System.out.println(" This is turn  " + i);

			game.nextMove(isBlackTurn);
			isBlackTurn = !isBlackTurn;
			game.updateScore();
			i++;
		}// while

		System.out.println("Game Over.");
		int blacks = game.getBlacks();
		int whites = game.getWhites();
		if (blacks > whites)
			System.out.print("Black wins.");
		else if (whites > blacks)
			System.out.print("White wins.");
		else
			System.out.print("Tie.");

	}// main
} //OthelloGame