package testo;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

class OthelloBoard {
	private int blacks = 0;
	private int whites = 0;
	public static final int BLACK = 10;
	public static final int WHITE = 11;
	public static final int BLANK = 99;
	public static final int CORNER = 1;
	public static final int EDGE = 2;
	public static final int CORNERGUARD = 3;
	public static final int EDGECORNERGUARD = 5;
	public static final int SQUARE = 4;
	public static final int NORTH = 20;
	public static final int SOUTH = 21;
	public static final int EAST = 22;
	public static final int WEST = 23;
	Player observer;
	public static final int POSSIBLE = 12;
	public boolean isReal;
	OthelloTile[] position = new OthelloTile[64];
	private boolean isBlackTurn = false;
	private boolean botTurn = true;
	private boolean humanTurn = true;
	private java.util.List<Integer> pool = new ArrayList();
	private Board board;

	public OthelloBoard(Player human, boolean reality) {
		buildPosition();
		observer = human;
		isReal = reality;
		board = new Board(this);
		position[27].setColor(WHITE);
		position[28].setColor(BLACK);
		position[35].setColor(BLACK);
		position[36].setColor(WHITE);
	}// initial board constructor

	public OthelloBoard(OthelloBoard original, boolean reality) {
		isReal = reality;
		buildPosition();
		for (int i = 0; i < 64; i++)
			position[i].modify(original.getTile(i));
	}// copy constructor

	private void buildPosition() {
		OthelloTile[] compass2;
		for (int i = 0; i < 64; i++) {
			compass2 = new OthelloTile[8];
			compass2[0] = (i % 8 == 0 || i < 8) ? null : position[i - 9];
			compass2[1] = (i < 8) ? null : position[i - 8];
			compass2[2] = (i % 8 == 7 || i < 8) ? null : position[i - 7];
			compass2[3] = (i % 8 == 0) ? null : position[i - 1];
			compass2[4] = (i % 8 == 7) ? null : position[i + 1];
			compass2[5] = (i > 55 || i % 8 == 0) ? null : position[i + 7];
			compass2[6] = (i > 55) ? null : position[i + 8];
			compass2[7] = (i > 55 || i % 8 == 7) ? null : position[i + 9];
			position[i] = new OthelloTile(i, compass2);
			if (i > 0 && i % 8 != 0)
				position[i - 1].setRight(position[i]);
			if (i > 8) {
				if ((i - 9) % 8 == 0)
					position[i - 9].setBottomTiles(null, position[i - 1],
							position[i]);
				else if ((i - 9) % 8 == 7)
					position[i - 9].setBottomTiles(position[i - 2],
							position[i - 1], null);
				else
					position[i - 9].setBottomTiles(position[i - 2],
							position[i - 1], position[i]);
			}
		}// for i
		position[55].setBottomTiles(position[62], position[63], null);
	}// this builds position[]

	public void botPass(boolean bot) {
		botTurn = bot;
	}

	public void humanPass(boolean human) {
		humanTurn = human;
	}

	public boolean canPlay() {
		return botTurn | humanTurn;
	}

	public int cornerTaken() {
		int color;
		if (isBlackTurn)
			color = WHITE;
		else
			color = BLACK;
		if (position[0].getColor() == color || position[7].getColor() == color
				|| position[56].getColor() == color
				|| position[63].getColor() == color)
			return -1000;
		else
			return 0;
	}// cornerTaken

	public boolean cornerCases(int i, int possibleCorner) {
		if (possibleCorner != -1
				&& position[possibleCorner].getType() == CORNER)
			return false;
		if (position[i].getType() == CORNERGUARD
				|| position[i].getType() == EDGECORNERGUARD) {
			if (position[position[i].whichCorner()].getColor() == BLANK)
				return false;
		}

		return true;
	}// getType

	public OthelloTile[] getPosition() {
		return position;
	}

	public OthelloTile getTile(int index) {

		return position[index];

	}// getTile

	public void setup(boolean turn) {
		this.isBlackTurn = turn;
		clearPossible();
		survey();
		calculateMoves();

	}// setup()

	public int[] save() {
		int[] state = new int[64];
		for (int i = 0; i < state.length; i++)
			state[i] = position[i].getColor();
		return state;
	}// save()

	public void revert(int[] rebound) {
		blacks = 0;
		whites = 0;

		for (int i = 0; i < 64; i++) {
			position[i].setColor(rebound[i]);
		}// rebound table

	}// revert()

	public int evaluate(boolean turn, int move, int pastmoves) {

		// greedy evaluation, simply count the number of tiles
		setup(turn);
		int numberOfMyMoves = countPossible().size();
		// setup(!turn);
		int numberOfYourMoves = pastmoves;
		int colorDifference;
		{
			if (turn == true)
				colorDifference = blacks / (blacks + whites);
			else
				colorDifference = whites / (whites + blacks);
		}
		int evaluation = numberOfMyMoves - 5 * numberOfYourMoves
				+ colorDifference + cornerTaken();
		return -1 * evaluation;
	}// evaluate

	public void survey() {
		pool.clear();
		for (int i = 0; i < 64; i++) {
			position[i].clearTargets();
			int candidate = position[i].getColor();
			if (isBlackTurn && candidate == BLACK)
				pool.add(i);
			else if (!isBlackTurn && candidate == WHITE)
				pool.add(i);
		}// for i
		// System.out.println("Pool is now: " + pool);
	}// survey()

	public void calculateMoves() {
		while (!pool.isEmpty()) {
			int c = pool.remove(0);
			java.util.List<java.util.List<Integer>> moves = position[c]
					.lookaround(isBlackTurn);

			ListIterator<java.util.List<Integer>> iterator = moves
					.listIterator();
			while (iterator.hasNext()) {
				java.util.List<Integer> nexter = iterator.next();
				ListIterator<Integer> subiterator = nexter.listIterator(nexter
						.size());
				int goal = subiterator.previous();
				position[goal].setColor(POSSIBLE);
				position[goal].setTargets(nexter);

			}// while iterator

		}// while pools

	}// calculateMoves()

	public java.util.List<Integer> countPossible() {
		java.util.List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 64; i++)
			if (position[i].getColor() == POSSIBLE)
				list.add(i);
		return list;
	}// countPossible()

	public void play(int index) {
		position[index].play();
	}// play();

	public void clearPossible() {
		for (int i = 0; i < 64; i++)
			if (position[i].getColor() == POSSIBLE) {
				position[i].clearTargets();
				position[i].setColor(BLANK);
			}
	} // clearPossible()

	class OthelloTile implements MouseListener {
		private int indicator;
		private int color;
		private int weight;
		private int type;
		private int edge;
		OthelloTile[] compass;
		java.util.Set<Integer> targets = new HashSet<Integer>();

		public int getType() {
			return type;
		}

		public void modify(OthelloTile original) {
			indicator = original.getIndicator();
			setColor(original.getColor());
		} // modify

		public void setEdge() {
			if (indicator < 7)
				edge = NORTH;
			else if (indicator > 55)
				edge = SOUTH;
			else if (indicator % 8 == 0)
				edge = WEST;
			else if (indicator % 8 == 7)
				edge = EAST;
			else
				edge = 0;
		}// setEdge()

		public OthelloTile(int indicator, OthelloTile[] compass2) {
			this.compass = compass2;
			this.indicator = indicator;
			this.color = BLANK;
			setWeight();
			setEdge();
		}// constructor

		public void setTargets(java.util.List<Integer> update) {
			ListIterator<Integer> iterator = update.listIterator();
			while (iterator.hasNext())
				targets.add(iterator.next());
			targets.remove(indicator);
		}// updateTargets

		public int getWeight() {
			return weight;
		}

		public int whichCorner() {
			switch (indicator) {
			case 1:
			case 8:
			case 9:
				return 0;
			case 6:
			case 14:
			case 15:
				return 7;
			case 48:
			case 49:
			case 57:
				return 56;
			case 54:
			case 55:
			case 62:
				return 63;
			default:
				return 0;
			}// switch
		}// whichCorner()

		public boolean checkEdge() {
			int c = indicator;
			int color;
			if (isBlackTurn)
				color = WHITE;
			else
				color = BLACK;
			switch (c) {
			case 1:
				if (position[c + 1].getColor() == color
						&& checkEdge(3, 4, 5, 6, 7))
					return true;
			case 6:
				if (position[c - 1].getColor() == color
						&& checkEdge(0, 1, 2, 3, 4))
					return true;
			case 8:
				if (position[16].getColor() == color
						&& checkEdge(24, 32, 40, 48, 56))
					return true;
			case 15:
				if (position[23].getColor() == color
						&& checkEdge(31, 39, 47, 55, 63))
					return true;
			case 48:
				if (position[40].getColor() == color
						&& checkEdge(0, 8, 16, 24, 23))
					return true;
			case 57:
				if (position[58].getColor() == color
						&& checkEdge(59, 60, 61, 62, 63))
					return true;
			case 55:
				if (position[47].getColor() == color
						&& checkEdge(7, 15, 23, 31, 39))
					return true;
			case 62:
				if (position[61].getColor() == color
						&& checkEdge(56, 57, 58, 59, 60))
					return true;
			default:
				return false;
			}
		}// checkEdge

		public boolean checkEdge(int a1, int a2, int a3, int a4, int a5) {
			int color;
			if (isBlackTurn)
				color = WHITE;
			else
				color = BLACK;
			if (position[a1].getColor() == color
					|| (position[a2].getColor() == color)
					|| (position[a3].getColor() == color)
					|| (position[a4].getColor() == color)
					|| (position[a5].getColor() == color))
				return false;
			if ((position[a1].getColor() == BLANK)
					& (position[a2].getColor() == BLANK)
					& (position[a3].getColor() == BLANK)
					& (position[a4].getColor() == BLANK)
					& (position[a5].getColor() == BLANK))
				return false;

			return true;
		}

		public void setWeight() {
			int i = indicator;
			if ((i == 0) || (i == 7) || (i == 56) || (i == 63)) {
				weight = 50;
				type = CORNER;
			}// if corners

			else if ((i == 1) || (i == 8) || (i == 6) || (i == 15) || (i == 48)
					|| (i == 57) || (i == 55) || (i == 62)) {
				type = EDGECORNERGUARD;
				weight = -150;
			} else if ((i == 9) || (i == 14) || (i == 49) || (i == 54)) {
				type = CORNERGUARD;
				weight = -99;
			} else if ((i % 8 == 0) || (i % 8 == 7) || (i < 8) || (i > 55)) {
				type = EDGE;
				weight = 30;
			} else if ((i % 8 == 1) || (i % 8 == 6) || (i < 14) || (i > 48)) {
				type = SQUARE;
				weight = 20;
			} else if ((i % 8 == 2) || (i % 8 == 5) || (i < 22) || (i > 41)) {
				type = SQUARE;
				weight = 10;
			} else
				weight = 1;
		}// setWeight

		public void recalcWeight(boolean isBlack) {
			/*
			 * int i=indicator; for (int j=0; j<64;j++) { if
			 * (position[i].getType() == CORNER && position[i].getColor()==BLACK
			 * && isBlack) }
			 * 
			 * switch(type) { case CORNER: break; case CORNERGUARD: case EDGE:
			 * break; default: ; }//switch
			 */
		}// recalcWeight()

		public void play() {
			if (isBlackTurn)
				setColor(BLACK);
			else
				setColor(WHITE);

			for (Integer victim : targets)
				position[victim].invert();

			isBlackTurn = !isBlackTurn;
			java.util.List<Integer> killPossibles = countPossible();
			for (Integer j : killPossibles)
				position[j].setColor(BLANK);
		}// play()

		public void mouseClicked(MouseEvent e) {
			// if (color!=POSSIBLE) System.out.println("Invalid move");

			if (color == POSSIBLE) {
				System.out.print("Human plays:  " + indicator + " | ");
				this.play();
				observer.notifyPlayer();
			}// else
			// board.update(indicator);

		}// mouseClicked

		public void mouseExited(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
			// System.out.println(weight);

		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void invert() {
			if (color == BLACK) {
				setColor(WHITE);
				blacks--;
			} else if (color == WHITE) {
				setColor(BLACK);
				whites--;
			}
		}// invert

		public int getIndicator() {
			return indicator;
		}

		public void clearTargets() {
			targets.clear();
		}

		public int getColor() {
			return color;
		}

		public java.util.List<java.util.List<Integer>> lookaround(boolean turn) {
			int goal;
			java.util.List<Integer> vector;
			java.util.List<java.util.List<Integer>> trajectory = new ArrayList<java.util.List<Integer>>();
			if (color == BLACK)
				goal = WHITE;
			else
				goal = BLACK;
			for (int i = 0; i < compass.length; i++) {
				if (compass[i] != null && compass[i].getColor() == goal) {
					vector = new ArrayList<Integer>();
					vector.add(compass[i].lookextend(vector, goal, i));
					int tail = vector.get(vector.size() - 1);
					if (tail != 99)
						trajectory.add(vector);
				}

			}// for i

			return trajectory;
		}// lookaround

		public Integer lookextend(java.util.List<Integer> traj, int goal, int i) {
			traj.add(indicator);
			if (compass[i] == null)
				return new Integer(99);
			if (compass[i].getColor() == BLANK
					|| compass[i].getColor() == POSSIBLE)
				return compass[i].getIndicator();
			if (compass[i].getColor() == goal)
				return compass[i].lookextend(traj, goal, i);
			return new Integer(99);
		} // lookextend

		public void setRight(OthelloTile tile) {
			compass[4] = tile;
		}

		public void setBottomTiles(OthelloTile tile1, OthelloTile tile2,
				OthelloTile tile3) {
			compass[5] = tile1;
			compass[6] = tile2;
			compass[7] = tile3;

		}// setBottomTiles

		public void setColor(int color) {
			this.color = color;
			if (color == BLACK)
				blacks++;
			else if (color == WHITE)
				whites++;
			if (isReal)
				board.update(indicator);
		}

		public String toString() {
			String value;
			switch (color) {
			case (10):
				value = "black";
				break;
			case (11):
				value = "white";
				break;
			case (12):
				value = "choice";
				break;
			default:
				value = "blank";
				break;
			}// switch

			return "Pool " + indicator + " " + value;
		}

	}// OthelloTile

	public int getNumberOfTiles() {
		return getBlacks() + getWhites();
	}

	public void updateScore() {
		board.updateScore("Blacks: " + blacks, "\n Whites: " + whites);
		System.out.println("SCORE: Blacks: " + blacks + " Whites: " + whites);
	}

	public int getBlacks() {
		return blacks;
	}

	public int getWhites() {
		return whites;
	}

}// OthelloBoard