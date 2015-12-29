package testo;

import java.awt.event.*;
import java.util.*;


class Player {
	boolean isWhite;
	boolean isBlack;
	boolean isBlackVirtual;
	private boolean click;
	OthelloBoard real;
	OthelloBoard virtual;

	public Player(boolean turn) {
		isWhite = turn;
		isBlack = turn;
	}// no-arg constructor

	public void play(OthelloBoard board, int depth) {
		real = board;
		int present;
		int result;
		int finalresult = -99999;
		int finalindex = -1;
		List<Integer> possible;
		List<Integer> lastResort = new ArrayList<Integer>();

		if (!isWhite) {
			board.setup(isBlack);
			possible = board.countPossible();
			if (!possible.isEmpty()) {

				while (!click)
					System.out.print("");
				board.humanPass(true);
			}// if
			else {
				board.humanPass(false);
				System.out.println("Nothing to play by human. Turn skipped.");
			}
		}else{
			board.setup(isWhite);
			possible = board.countPossible();
			if (!possible.isEmpty()) {

				while (!click)
					System.out.print("");
				board.humanPass(true);
			}// if
			else {
				board.botPass(false);
				System.out.println("Nothing to play by human. Turn skipped.");
			}
		}
	}

	private int minimax(int index, int depth, int prevnode, int prevmoves) {
		int localindex;
		int localfinal = -999;
		int localresult;
		int localpresent;
		isBlackVirtual = !isBlackVirtual;
		if (depth == 0) {
			virtual.play(index);
			return virtual.evaluate(isBlackVirtual, index, prevmoves);
		} else {
			virtual.play(index);
			int[] arrangement = virtual.save();
			virtual.setup(isBlackVirtual);
			List<Integer> possibleLevel = virtual.countPossible();
			if (!possibleLevel.isEmpty()) {
				int size = possibleLevel.size();
				while (!possibleLevel.isEmpty()) {

					localpresent = possibleLevel.remove(0);
					localresult = minimax(localpresent, depth - 1, localfinal,
							size);
					if (localresult * -1 < prevnode)
						possibleLevel.clear();
					if (localresult > localfinal) {
						localfinal = localresult;
						localindex = localpresent;
					}
					virtual.revert(arrangement);
					isBlackVirtual = !isBlackVirtual;
					virtual.setup(isBlackVirtual);
				}
			} else
				return virtual.evaluate(isBlackVirtual, index,
						possibleLevel.size());
			return -1 * localfinal;
		}// else
	}// minimax()

	public void notifyPlayer() {
		click = true;
	}// notify

}//Player


