package com.team4924.LedDisplayI2cDriver;

import java.util.ArrayList;

import static com.team4924.LedDisplayI2cDriver.Constants.*;

/**
 * A single display made up of multiple {@link HT16K33} displays.
 * Includes methods for working with all the displays.
 *
 * @see HT16K33
 */
public class Display {

    private final int displayWidth;
    private final int displayHeight;
    private int rotation;
    private int lineLength;
    private final ArrayList<HT16K33> displayBoards;

    /**
     * @param height The height of the display in # displays
     * @param width The width of the display in # displays
     * @param displayBoards The displays ordered from left to right top to bottom
     * @see HT16K33
     */
    public Display(int height, int width, ArrayList<HT16K33> displayBoards) {
        this.displayHeight = height;
        this.displayWidth = width;
        this.displayBoards = displayBoards;
        rotation = 0;
        lineLength = 0;
    }

    /**
     * Turns all displays on
     * @see HT16K33#displayOn()
     */
    public void displayOn() {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.displayOn();
        }
    }

    /**
     * Turns all displays off
     * @see HT16K33#displayOff()
     */
    public void displayOff() {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.displayOff();
        }
    }

    /**
     * Clears the displayBuffer of all displays
     * @see HT16K33#clear()
     */
    public void clear() {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.clear();
        }
    }

    /**
     * Rotates the entire display
     * @param rotation Indicated rotation; accepts 0-3 otherwise defaults to 0
     * @see HT16K33#setRotation(int rotation)
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
        for(int i = 0; i < displayBoards.size(); i++) {
            displayBoards.get(i).setRotation(rotation);
        }
    }

    /**
     * Changes the brightness of all displays
     * @param newBrightness Indicated brightness; accepts 0-15 otherwise defaults to 15
     * @see HT16K33#setBrightness(int brightness)
     */
    public void setBrightness(int newBrightness) {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.setBrightness(newBrightness);
        }
    }

    public void setFontColor(boolean newFontColor) {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.setFontColor(newFontColor);
        }
    }

    /**
     * Write the buffers to all displays
     * @see HT16K33#writeDisplay()
     */
    public void writeDisplay() {
        for(int i = 0; i < displayBoards.size(); i++) {
            displayBoards.get(i).writeDisplay();
        }
    }

    /**
     * Write a bitmap across all displays
     * @param x The x position of the bitmap.
     * @param y The y position of the bitmap.
     * @param bitmap The bitmap to display.
     * @see HT16K33#drawBitmap(int x, int y, byte[][] bitmap)
     */
    public void drawBitmap(int x, int y, byte[][] bitmap) {
        switch(rotation) {
            case 0:
                for(int r = Math.max(0, y/8); r < Math.ceil(y/8.0) + Math.ceil(bitmap.length/8.0) && r < displayHeight; r++) {
                    for(int c = Math.max(0, x/8); c < Math.ceil(x/8.0) + bitmap[0].length && c < displayWidth; c++) {
                        if(r * displayWidth + c < displayBoards.size()) {
                            displayBoards.get(r * displayWidth + c).drawBitmap(x - c * 8, y - r * 8, bitmap);
                        }
                    }
                }
                break;
            case 1:
                for(int r = Math.max(0, y/8); r < Math.ceil(y/8.0) + bitmap[0].length && r < displayWidth; r++) {
                    for(int c = Math.max(0, x/8); c < Math.ceil(x/8.0) + Math.ceil(bitmap.length/8.0) && c < displayHeight; c++) {
                        if(c * displayWidth + (displayWidth - 1 - r) < displayBoards.size()) {
                            displayBoards.get(c * displayWidth + (displayWidth - 1 - r)).drawBitmap(x - c * 8, y - r * 8, bitmap);
                        }
                    }
                }
                break;
            case 2:
                for(int r = Math.max(0, y/8); r < Math.ceil(y/8.0) + Math.ceil(bitmap.length/8.0) && r < displayHeight; r++) {
                    for(int c = Math.max(0, x/8); c < Math.ceil(x/8.0) + bitmap[0].length && c < displayWidth; c++) {
                        if((displayHeight - 1 - r) * displayWidth + displayWidth - 1 - c < displayBoards.size()) {
                            displayBoards.get((displayHeight - 1 - r) * displayWidth + displayWidth - 1 - c).drawBitmap(x - c * 8, y - r * 8, bitmap);
                        }
                    }
                }
                break;
            case 3:
                for(int r = Math.max(0, y/8); r < Math.ceil(y/8.0) + bitmap[0].length && r < displayWidth; r++) {
                    for(int c = Math.max(0, x/8); c < Math.ceil(x/8.0) + Math.ceil(bitmap.length/8.0) && c < displayHeight; c++) {
                        if((displayHeight - 1 - c) * displayWidth + r < displayBoards.size()) {
                            displayBoards.get((displayHeight - 1 - c) * displayWidth + r).drawBitmap(x - c * 8, y - r * 8, bitmap);
                        }
                    }
                }
                break;
        }
    }

    /**
     * Draw a character across all displays
     * @param x The x position of the character.
     * @param y The y position of the character.
     * @param character The character to display.
     * @see HT16K33#drawCharacter(int x, int y, char character)
     */
    public void drawCharacter(int x, int y, char character) {
        switch(rotation) {
            case 0:
                for (int r = Math.max(0, y / 8); r < Math.ceil(y / 8.0) + CHARACTER_HEIGHT && r < displayHeight; r++) {
                    for (int c = Math.max(0, x / 8); c < Math.ceil(x / 8.0) + CHARACTER_WIDTH && c < displayWidth; c++) {
                        if (r * displayWidth + c < displayBoards.size()) {
                            displayBoards.get(r * displayWidth + c).drawCharacter(x - c * 8, y - r * 8, character);
                        }
                    }
                }
                break;
            case 1: for (int r = Math.max(0, y / 8); r < Math.ceil(y / 8.0) + CHARACTER_HEIGHT && r < displayWidth; r++) {
                    for (int c = Math.max(0, x / 8); c < Math.ceil(x / 8.0) + CHARACTER_WIDTH && c < displayHeight; c++) {
                        if (c * displayWidth + (displayWidth - 1 - r) < displayBoards.size()) {
                            displayBoards.get(c * displayWidth + (displayWidth - 1 - r)).drawCharacter(x - c * 8, y - r * 8, character);
                        }
                    }
                }
                break;
            case 2: for (int r = Math.max(0, y / 8); r < Math.ceil(y / 8.0) + CHARACTER_HEIGHT && r < displayHeight; r++) {
                    for (int c = Math.max(0, x / 8); c < Math.ceil(x / 8.0) + CHARACTER_WIDTH && c < displayWidth; c++) {
                        if ((displayHeight - 1 - r) * displayWidth + displayWidth - 1 - c < displayBoards.size()) {
                            displayBoards.get((displayHeight - 1 - r) * displayWidth + displayWidth - 1 - c).drawCharacter(x - c * 8, y - r * 8, character);
                        }
                    }
                }
                break;
            case 3: for (int r = Math.max(0, y / 8); r < Math.ceil(y / 8.0) + CHARACTER_HEIGHT && r < displayWidth; r++) {
                    for (int c = Math.max(0, x / 8); c < Math.ceil(x / 8.0) + CHARACTER_WIDTH && c < displayHeight; c++) {
                        if ((displayHeight - 1 - c) * displayWidth + r < displayBoards.size()) {
                            displayBoards.get((displayHeight - 1 - c) * displayWidth + r).drawCharacter(x - c * 8, y - r * 8, character);
                        }
                    }
                }
                break;
        }
    }


    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.setLineLength(lineLength);
        }
    }

    public void print(int x, int y, String message) {

        int msgWidth;
        int msgHeight;
        if(lineLength > 0) {
            msgWidth = Math.min(lineLength, message.length()) * (CHARACTER_WIDTH + TRACKING);
            msgHeight = (int) Math.ceil((double)message.length() / lineLength) * (CHARACTER_HEIGHT + LEADING);
        } else {
            msgWidth = message.length() * (CHARACTER_WIDTH + TRACKING);
            msgHeight = (CHARACTER_HEIGHT + LEADING);
        }

        for(int r = Math.max(0, y/8); r < Math.ceil(y/8.0) + Math.ceil(msgHeight/8.0) && r < displayHeight; r++) {
            for(int c = Math.max(0, x/8); c < Math.ceil(x/8.0) + Math.ceil(msgWidth/8.0) && c < displayWidth; c++) {
                if(r * displayWidth + c < displayBoards.size()) {
                    displayBoards.get(r * displayWidth + c).print(x - c * 8, y - r * 8, message);
                }
            }
        }
    }
}
