package com.lavanya.game2048;

import java.io.*;
import java.util.*;

public class Board {
    private int size;
    private int[][] grid;
    private int score;
    private int highScore;
    private Random random;

    private static final String HIGHSCORE_FILE = "highscore.txt";

    public Board(int size) {
        this.size = size;
        grid = new int[size][size];
        random = new Random();
        loadHighScore();
        spawnTile();
        spawnTile();
    }

    public int getSize() { return size; }
    public int[][] getGrid() { return grid; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }

    public boolean move(String direction) {
        boolean moved = false;

        switch (direction) {
            case "UP": moved = moveUp(); break;
            case "DOWN": moved = moveDown(); break;
            case "LEFT": moved = moveLeft(); break;
            case "RIGHT": moved = moveRight(); break;
        }

        if (moved) {
            spawnTile();
            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }
        }

        return moved;
    }

    private void spawnTile() {
        int emptyCount = 0;
        for (int[] row : grid)
            for (int val : row)
                if (val == 0) emptyCount++;

        if (emptyCount == 0) return;

        int target = random.nextInt(emptyCount);
        int count = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == 0) {
                    if (count == target) {
                        grid[r][c] = random.nextFloat() < 0.9 ? 2 : 4;
                        return;
                    }
                    count++;
                }
            }
        }
    }

    // ---------------- MOVE HELPERS ----------------
    private boolean moveLeft() {
        boolean moved = false;
        for (int r = 0; r < size; r++) {
            int[] newRow = new int[size];
            int pos = 0;
            boolean mergedLast = false;

            for (int c = 0; c < size; c++) {
                int val = grid[r][c];
                if (val != 0) {
                    if (pos > 0 && newRow[pos - 1] == val && !mergedLast) {
                        newRow[pos - 1] *= 2;
                        score += newRow[pos - 1];
                        mergedLast = true;
                        moved = true;
                    } else {
                        newRow[pos++] = val;
                        mergedLast = false;
                        if (c != pos - 1) moved = true;
                    }
                }
            }
            grid[r] = newRow;
        }
        return moved;
    }

    private boolean moveRight() {
        rotate180();
        boolean moved = moveLeft();
        rotate180();
        return moved;
    }

    private boolean moveUp() {
        rotateLeft();
        boolean moved = moveLeft();
        rotateRight();
        return moved;
    }

    private boolean moveDown() {
        rotateRight();
        boolean moved = moveLeft();
        rotateLeft();
        return moved;
    }

    // Rotations to reuse moveLeft()
    private void rotateLeft() {
        int[][] newGrid = new int[size][size];
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                newGrid[size - c - 1][r] = grid[r][c];
        grid = newGrid;
    }

    private void rotateRight() {
        int[][] newGrid = new int[size][size];
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                newGrid[c][size - r - 1] = grid[r][c];
        grid = newGrid;
    }

    private void rotate180() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size / 2; c++) {
                int tmp = grid[r][c];
                grid[r][c] = grid[r][size - c - 1];
                grid[r][size - c - 1] = tmp;
            }
        for (int r = 0; r < size / 2; r++) {
            int[] tmp = grid[r];
            grid[r] = grid[size - r - 1];
            grid[size - r - 1] = tmp;
        }
    }

    // ---------------- FILE HANDLING ----------------
    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            highScore = Integer.parseInt(reader.readLine());
        } catch (Exception e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isGameOver() {
        // 1. If any empty tile exists → not game over
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == 0) return false;
            }
        }

        // 2. Check horizontal merges
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size - 1; c++) {
                if (grid[r][c] == grid[r][c + 1]) return false;
            }
        }

        // 3. Check vertical merges
        for (int c = 0; c < size; c++) {
            for (int r = 0; r < size - 1; r++) {
                if (grid[r][r] == grid[r + 1][c]) return false;
            }
        }

        return true; // No empty and no merges possible
    }

}
