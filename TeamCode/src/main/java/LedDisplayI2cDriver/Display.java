package LedDisplayI2cDriver;

import java.util.ArrayList;

import static LedDisplayI2cDriver.Constants.*;

public class Display {

    private final int displayWidth;
    private final int displayHeight;
    private int rotation;
    private int lineLength;
    private final ArrayList<HT16K33> displayBoards;

    /**
     * @param height the height of the display in displayBoards
     * @param width the width of the display in displayBoards
     * @param displayBoards the displayBoards ordered from left to right top to bottom
     */
    public Display(int height, int width, ArrayList<HT16K33>displayBoards) {
        this.displayHeight = height;
        this.displayWidth = width;
        this.displayBoards = displayBoards;
        rotation = 0;
        lineLength = 0;
    }

    /**
     * turns each displayBoard on
     */
    public void displayOn() {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.displayOn();
        }
    }

    /**
     * turns each displayBoard off
     */
    public void displayOff() {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.displayOff();
        }
    }

    /**
     * clears each of the displayBoards
     */
    public void clear() {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.clear();
        }
    }

    /**
     * clears each displayBoard's displayBuffer
     */
    public void clearDisplayBuffer() {
        for(HT16K33 displayBoard : displayBoards) {
            displayBoard.clearDisplayBuffer();
        }
    }

    /**
     * rotates the display
     * @param rotation indicated rotation; accepts 0-3 otherwise defaults to 0
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
        for(int i = 0; i < displayBoards.size(); i++) {
            displayBoards.get(i).setRotation(rotation);
        }
    }

    /**
     * changes the brightness of each displayBoard
     * @param newBrightness indicated brightness; accepts 0-15 otherwise defaults to 15
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

    public void writeDisplay() {
        for(int i = 0; i < displayBoards.size(); i++) {
            displayBoards.get(i).writeDisplay();
        }
    }

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
