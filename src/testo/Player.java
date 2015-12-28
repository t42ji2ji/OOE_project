package testo;

import java.awt.event.*;
import java.util.*;


class Player {
	boolean isAI;
	boolean isBlack;
	boolean isBlackVirtual;
	private boolean click;
	OthelloBoard real;
	OthelloBoard virtual;

	public Player(boolean type, boolean turn) {
		isAI = type;
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

		if (!isAI) {
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

		}// if

		else {
			virtual = new OthelloBoard(board, false);
			isBlackVirtual = isBlack;
			int[] arrangement = virtual.save();
			virtual.setup(isBlackVirtual);
			possible = virtual.countPossible();

			if (!possible.isEmpty()) {
				int size = possible.size();
				while (!possible.isEmpty()) {
					present = possible.remove(0);
					result = minimax(present, depth, finalresult, size);

					if (result > finalresult) {
						if (board.cornerCases(present, finalindex)) {
							finalindex = present;
							finalresult = result;
						} else {
							lastResort.add(present);
						}
					} // if new move is better
					if (present == 0 || present == 7 || present == 56
							|| present == 63) {
						finalindex = present;
						finalresult = result;
					}
					virtual.revert(arrangement);
					isBlackVirtual = !isBlackVirtual;
					virtual.setup(isBlackVirtual);
				}// while
				real.setup(isBlack);
				if (finalindex != -1) {
					System.out.print("Computer: " + finalindex);
					real.play(finalindex);
				} else if (!lastResort.isEmpty()) {
					int resort = lastResort.remove(0);
					System.out.print("Computer: " + resort);
					real.play(resort);
				}
				lastResort.clear();
				real.botPass(true);
			}// if possible to play
			else {
				System.out.println("Nothing to play. Turn is skipped.");
				real.botPass(false);
			}
		}// else AI turn

		click = false;

	}// play

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


