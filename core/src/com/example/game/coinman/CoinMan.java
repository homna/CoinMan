package com.example.game.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    Rectangle manRectangle;
    int manState = 0;
    int delayState = 0;
    float gravity = 0.2f;
    float velocity = 0;
    int manY = 0;

    Random random;

    Texture coin;
    ArrayList<Integer> coinX = new ArrayList<Integer>();
    ArrayList<Integer> coinY = new ArrayList<Integer>();
    int coinCount = 0;
    ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();

    Texture bomb;
    ArrayList<Integer> bombX = new ArrayList<Integer>();
    ArrayList<Integer> bombY = new ArrayList<Integer>();
    int bombCount = 0;
    ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();

    int score = 0;
    int gameState = 0;
    BitmapFont bitmapFont;
    Texture dizzyMan;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");
        manY = 0;
        random = new Random();
        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().scale(10); //size of the score on the screen
        dizzyMan = new Texture("dizzy-1.png");
    }

    public void makeCoin() {
        coinX.add(Gdx.graphics.getWidth());
        coinY.add((int) (random.nextFloat() * Gdx.graphics.getHeight()));
    }

    public void makeBomb() {
        bombX.add(Gdx.graphics.getWidth());
        bombY.add((int) (random.nextFloat() * Gdx.graphics.getHeight()));
    }

    /**
     * render function executes continuously
     */
    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (gameState == 0) {
            //Wait for user to touch the screen then game will start
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 1) {
            //Game is live now
            /**Make Bomb */
            if (bombCount < 250) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }
            bombRectangle.clear();
            for (int i = 0; i < bombX.size(); i++) {
                batch.draw(bomb, bombX.get(i), bombY.get(i));
                bombX.set(i, bombX.get(i) - 8);
                bombRectangle.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(), bomb.getHeight()));
            }
            /**Make Coin */
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }
            coinRectangle.clear();
            for (int i = 0; i < coinX.size(); i++) {
                batch.draw(coin, coinX.get(i), coinY.get(i));
                coinX.set(i, coinX.get(i) - 4);
                coinRectangle.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(), coin.getHeight()));
            }

            /**Move man down in y direction */
            velocity += gravity;
            //  System.out.println("Output_velocity: " + velocity);
            manY -= velocity;
            if (manY <= 0) {
                manY = 0;
            }
            /**Change to different man states to show that he is running */
            if (delayState < 8) {
                delayState++;
            } else {
                delayState = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }
            /**If user touch the screen -> map would jump*/
            if (Gdx.input.justTouched()) {
                if (manY + man[0].getHeight() + 123 < Gdx.graphics.getHeight()) {
                    // System.out.println("Output: " + manY + " , " + man[0].getHeight() + " , " + Gdx.graphics.getHeight());
                    //  System.out.println("Output: "+velocity);
                    velocity = -10;
                }
            }
        } else if (gameState == 2) {
            //Game is over -> After user touched the screen ->reset everything
            if (Gdx.input.isTouched()) {
                gameState = 0;
                velocity = 0;
                manY = 0;
                score = 0;
                coinX.clear();
                coinY.clear();
                coinRectangle.clear();
                coinCount = 0;
                bombX.clear();
                bombY.clear();
                bombRectangle.clear();
                bombCount = 0;
            }
        }

        if (gameState == 2) { //Game is over -> show dizzy man
            batch.draw(dizzyMan, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        } else {
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        }
        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());
        for (int i = 0; i < coinRectangle.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangle.get(i))) {
                score++;
                coinRectangle.remove(i);
                /**Man takes the coin -> Coin  disappears from the screen.*/
                coinX.remove(i);
                coinY.remove(i);
                break;
            }
        }
        for (int j = 0; j < bombRectangle.size(); j++) {
            if (Intersector.overlaps(manRectangle, bombRectangle.get(j))) {
                gameState = 2;
            }
        }
        /**Show score on the bottom of the screen*/
        bitmapFont.draw(batch, String.valueOf(score), 100, 200);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
