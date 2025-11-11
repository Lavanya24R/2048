package com.lavanya.game2048;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen, InputProcessor {

    private MainGame game;
    private Texture background;
    private Board board;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private float boardSize;
    private float tilePadding;

    private int gridSize = 4;
    private int startX, startY;

    public GameScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        background = new Texture("grass2.jpg");
        font = new BitmapFont();
        board = new Board(gridSize);
        Gdx.input.setInputProcessor(this);

        boardSize = Gdx.graphics.getWidth() - 200;
        tilePadding = 10;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        float tileSize = (boardSize - tilePadding * (gridSize + 1)) / gridSize;
        float xOffset = (Gdx.graphics.getWidth() - boardSize) / 2;
        float yOffset = 100;

        // Board background
        shapeRenderer.setColor(Color.valueOf("6e2b01"));
        shapeRenderer.rect(xOffset, yOffset, boardSize, boardSize);

        // Draw tiles
        int[][] grid = board.getGrid();
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                float x = xOffset + tilePadding + c * (tileSize + tilePadding);
                float y = yOffset + tilePadding + r * (tileSize + tilePadding);
                int value = grid[r][c];
                shapeRenderer.setColor(getTileColor(value));
                shapeRenderer.rect(x, y, tileSize, tileSize);
            }
        }

        shapeRenderer.end();

        // Draw numbers and score
        batch.begin();
        int[][] g = board.getGrid();
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                int val = g[r][c];
                if (val > 0) {
                    tileSize = (boardSize - tilePadding * (gridSize + 1)) / gridSize;
                    float x = xOffset + tilePadding + c * (tileSize + tilePadding);
                    float y = yOffset + tilePadding + r * (tileSize + tilePadding);
                    font.setColor(Color.BLACK);
                    font.getData().setScale(2);
                    font.draw(batch, String.valueOf(val), x + tileSize / 2f - 15, y + tileSize / 2f + 10);
                }
            }
        }
        font.getData().setScale(1.5f);
        font.draw(batch, "Score: " + board.getScore(), 50, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "High Score: " + board.getHighScore(), 50, Gdx.graphics.getHeight() - 100);
        batch.end();
    }

    private Color getTileColor(int value) {
        if (value == 0) return Color.valueOf("cdc1b4"); // empty tile
        switch (value) {
            case 2: return Color.valueOf("ffffcc");
            case 4: return Color.valueOf("ffff80");
            case 8: return Color.valueOf("ffff00");
            case 16: return Color.valueOf("ffb366");
            case 32: return Color.valueOf("ff9933");
            case 64: return Color.valueOf("ff6600");
            case 128: return Color.valueOf("ff8080");
            case 256: return Color.valueOf("ff5050");
            case 512: return Color.valueOf("ff1a1a");
            case 1024: return Color.valueOf("ff0000");
            case 2048: return Color.valueOf("a83252");
            case 4096: return Color.valueOf("ff66a3");
            case 8192: return Color.valueOf("ff0066");
            default: return Color.valueOf("ff3399");
        }
    }

    // ----------- INPUT HANDLING -----------
    private int startTouchX, startTouchY;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        startTouchX = screenX;
        startTouchY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Convert LibGDX Y-coordinates (top-left origin) to bottom-left
        screenY = Gdx.graphics.getHeight() - screenY;
        startTouchY = Gdx.graphics.getHeight() - startTouchY;

        int dx = screenX - startTouchX;
        int dy = screenY - startTouchY;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 50) board.move("RIGHT");
            else if (dx < -50) board.move("LEFT");
        } else {
            if (dy > 50) board.move("DOWN");
            else if (dy < -50) board.move("UP");
        }
        return true;
    }

    @Override public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) board.move("DOWN");
        if (keycode == Input.Keys.DOWN) board.move("UP");
        if (keycode == Input.Keys.LEFT) board.move("LEFT");
        if (keycode == Input.Keys.RIGHT) board.move("RIGHT");
        return true;
    }


    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }


    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { shapeRenderer.dispose(); batch.dispose(); font.dispose(); }
}
