package com.matthew0x40.gameoflife;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

public class Game implements MouseListener, MouseMotionListener,
		KeyListener {
	Main main;
	final int maxX = 100;
	final int maxY = 100;
	final int cellwidth = 8;
	boolean[][] grid = new boolean[maxX][maxY];
	boolean[][] gridCopy = new boolean[maxX][maxY];
	public boolean gamePaused = false;
	long generations = 0;
	long nextGenTime = 0;
	long lastGenTime = System.currentTimeMillis();
	
	public Game(Main main) {
		this.main = main;
		grid[50][50] = true;
		grid[51][50] = true;
		grid[52][50] = true;
	}

	int countAround(int i, int j) {
		int count = 0;
		for (int x = i - 1; x <= i + 1; x++)
			for (int y = j - 1; y <= j + 1; y++) {
				if (x == i && y == j)
					continue;
				count += eval(x, y);
			}
		return count;
	}

	int eval(int i, int j) {
		if (i < 0 || j < 0 || i == maxX || j == maxY)
			return 0;
		return grid[i][j] ? 1 : 0;
	}

	public void update(int width, int height) {
		if (gamePaused)
			return;
		
		nextGenTime = lastGenTime + main.speedSlider.getValue();
		
		if (System.currentTimeMillis() < nextGenTime) {
			return;
		}
		
		generations++;
		main.genTextField.setText(Long.toString(generations));
		copyGrid(grid, gridCopy);
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				int n = countAround(x,y);
				if (grid[x][y]) {
					if (n < 2 || n > 3) {
						gridCopy[x][y] = false;
					}
				} else {
					if (n == 3) {
						gridCopy[x][y] = true;
					}
				}
			}
		}
		copyGrid(gridCopy, grid);
		
		lastGenTime = System.currentTimeMillis();
	}
	
	public void initGrid(boolean[][] source) {
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				source[x][y] = false;
			}
		}
	}
	
	public void copyGrid(boolean[][] from, boolean[][] to) {
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				to[x][y] = from[x][y];
			}
		}
	}
	
	public void render(Graphics2D g, int width, int height) {
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				if (grid[x][y]) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.BLACK);
				}

				g.fillRect(x * cellwidth, y * cellwidth, cellwidth, cellwidth);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			int x = (int) (arg0.getX()/cellwidth);
			int y = (int) (arg0.getY()/cellwidth);
			if (x < 0 || y < 0 || x >= maxX || y >= maxY)
				return;
			grid[x][y] = true;
		} else if (arg0.getButton() == MouseEvent.BUTTON3) {
			int x = (int) (arg0.getX()/cellwidth);
			int y = (int) (arg0.getY()/cellwidth);
			if (x < 0 || y < 0 || x >= maxX || y >= maxY)
				return;
			grid[x][y] = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (SwingUtilities.isLeftMouseButton(arg0)) {
			int x = (int) (arg0.getX()/cellwidth);
			int y = (int) (arg0.getY()/cellwidth);
			if (x < 0 || y < 0 || x >= maxX || y >= maxY)
				return;
			grid[x][y] = true;
		} else if (SwingUtilities.isRightMouseButton(arg0)) {
			int x = (int) (arg0.getX()/cellwidth);
			int y = (int) (arg0.getY()/cellwidth);
			if (x < 0 || y < 0 || x >= maxX || y >= maxY)
				return;
			grid[x][y] = false;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			togglePaused();
		}
	}
	
	public void togglePaused() {
		if (this.gamePaused) {
			this.gamePaused = false;
			this.main.frame.setTitle(Main.title);
			this.main.pauseButton.setText("Pause");
		} else {
			this.gamePaused = true;
			this.main.frame.setTitle(Main.title + " (paused)");
			this.main.pauseButton.setText("Unpause");
		}
	}
}
