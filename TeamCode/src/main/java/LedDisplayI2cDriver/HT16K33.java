package LedDisplayI2cDriver;

import java.util.Arrays;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cAddrConfig;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

import org.jetbrains.annotations.NotNull;

import static LedDisplayI2cDriver.Constants.*;

@I2cDeviceType
@DeviceProperties(name = "HT16K33 8x8 LED Display", xmlTag = "HT16K33")
public class HT16K33 extends I2cDeviceSynchDevice<I2cDeviceSynch> implements I2cAddrConfig {

    private final byte[] displayBuffer;

    private boolean displayOn;
    private int blinkRate;

    private int currentRotation;
    private int rotationOffset;

    private boolean fontColor;

    private int lineLength;

    /**
     * Converts a boolean to a byte. True = 1, False = 0.
     * @param b the boolean to convert
     * @return the corresponding byte
     */
    private static byte boolToByte(boolean b) {
        return b ? (byte)1 : (byte)0;
    }

    /**
     * Returns an indication of the manufacturer of this device.
     * @return the device's manufacturer
     */
    @Override
    public Manufacturer getManufacturer()
    {
        return Manufacturer.Adafruit;
    }

    /**
     * Returns a string suitable for display to the user as to the type of device.
     * @return device name
     */
    @Override
    public String getDeviceName()
    {
        return "HT16K33 8x8 LED Display";
    }

    public HT16K33(I2cDeviceSynch deviceClient) {
        super(deviceClient, true);

        this.deviceClient.setI2cAddress(ADDRESS_I2C_DEFAULT);

        super.registerArmingStateCallback(false);
        this.deviceClient.engage();

        displayBuffer = new byte[8];

        displayOn = false;

        currentRotation = 0;
        rotationOffset = 0;

        blinkRate = 0;

        lineLength = 0;
    }

    /**
     * Initializes the backpack for the 8x8 matrix: turning on the system oscillator,
     * setting everything to default, clearing the display, and turning the display on.
     * @return Whether the initialization was successful or not
     */
    @Override
    protected synchronized boolean doInitialize() {

        systemStart();

        setFontColor(FONT_COLOR_DEFAULT);
        setBrightness(BRIGHTNESS_DEFAULT);
        setBlinkRate(BLINK_RATE_DEFAULT);
        clearDisplayBuffer();
        writeDisplay();
        displayOn();

        return true;

    }

    /**
     * Configures a new I2C address to use.
     * @param newAddress the new I2C address to use
     */
    @Override
    public void setI2cAddress(I2cAddr newAddress) {
        this.deviceClient.setI2cAddress(newAddress);
    }

    /**
     * Returns the I2C address currently in use to communicate with an I2C hardware device.
     * @return the I2C address currently in use
     */
    @Override
    public I2cAddr getI2cAddress() {
        return this.deviceClient.getI2cAddress();
    }

    /**
     * Writes a byte to the indicated register.
     * @param command The first nybble of the command address used for indicating the register
     * @param setting The second nybble of the command address used for indicating settings like blinkRate
     * @param value The byte of data sent to the register. Only used when writing to the display
     */
    private void write8(@NotNull Command command, byte setting, byte value) {
        this.deviceClient.write8(command.bVal * 16 + setting, value);
    }

    /**
     * Turns the system oscillator on.
     */
    public void systemStart() {
        write8(Command.SYSTEM_SETUP, (byte)1, (byte)0);
    }

    /**
     * Turns off the system oscillator.
     */
    public void systemStop() {
        write8(Command.SYSTEM_SETUP, (byte)0, (byte)0);
    }

    /**
     * turns the display on.
     */
    public void displayOn() {
        displayOn = true;
        write8(Command.DISPLAY_SETUP, (byte)(blinkRate * 2 + boolToByte(true)), (byte)0);
    }
    /**
     * turns the display off.
     */
    public void displayOff() {
        displayOn = false;
        write8(Command.DISPLAY_SETUP, (byte)(blinkRate * 2 + boolToByte(true)), (byte)0);
    }

    /**
     * clears the display.
     */
    public void clear() {
        if(fontColor) {
            for (byte i = 0; i < DISPLAY_WIDTH * 2; i += 2) {
                write8(Command.DISPLAY_ADDRESS_POINTER, i, (byte)0);
            }
        } else {
            for (byte i = 0; i < DISPLAY_WIDTH * 2; i += 2) {
                write8(Command.DISPLAY_ADDRESS_POINTER, i, (byte)0xFF);
            }
        }
    }

    /**
     * Clears the display buffer.
     */
    public void clearDisplayBuffer() {
        Arrays.fill(displayBuffer, (byte)0);
    }

    /**
     * Configures the offset of the displays rotation.
     * @param rotation indicated rotation; accepts 0-3 otherwise defaults to 0
     */
    public void setRotationOffset(int rotation) {
        if(rotation >= 0 && rotation <= 3) {
            rotationOffset = rotation;
        } else {
            rotationOffset = 0;
        }
        this.currentRotation = (currentRotation + rotationOffset) % 4;
    }

    /**
     * Configures the rotation of the display.
     * @param rotation indicated rotation; accepts 0-3 otherwise defaults to 0
     */
    public void setRotation(int rotation) {
        if(rotation >= 0 && rotation <= 3) {
            currentRotation = (rotation + rotationOffset) % 4;
        } else {
            this.currentRotation = rotationOffset;
        }
    }

    /**
     * Configures the brightness of the entire display.
     * @param brightness indicated brightness; accepts 0-15 otherwise defaults to 15
     *                   15 = maximum brightness, 0 = minimum brightness
     */
    public void setBrightness(int brightness) {
        if(brightness >= 0 && brightness <= 15) {
            write8(Command.SET_BRIGHTNESS, (byte)brightness, (byte)0);
        } else {
            write8(Command.SET_BRIGHTNESS, (byte)0xF, (byte)0);
        }
    }

    /**
     * Configures the blink rate of the entire display.
     * @param blinkRate Indicated blink rate; accepts 0-3 otherwise defaults to 0
     *                  0 = off, 1 = 2HZ, 2 = 1HZ, 3 = 0.5HZ
     */
    public void setBlinkRate(int blinkRate) {
        if(blinkRate >= 0 && blinkRate <= 3) {
            this.blinkRate = blinkRate;
            write8(Command.DISPLAY_SETUP, (byte)(blinkRate * 2 + boolToByte(displayOn)), (byte)0);
        } else {
            this.blinkRate = 0;
            write8(Command.DISPLAY_SETUP, (byte)(boolToByte(displayOn)), (byte)0);
        }
    }

    /**
     * Configures the color of what is drawn on the display
     * @param color Indicated color
     *              true = on, false = off
     */
    public void setFontColor(boolean color) {
        fontColor = color;
    }

    /**
     * Writes the data in displayBuffer to the display.
     */
    public void writeDisplay() {
        if(fontColor) {
            for (byte i = 0; i < DISPLAY_WIDTH * 2; i += 2) {
                write8(Command.DISPLAY_ADDRESS_POINTER, i, displayBuffer[i / 2]);
            }
        } else {
            for (byte i = 0; i < DISPLAY_WIDTH * 2; i += 2) {
                write8(Command.DISPLAY_ADDRESS_POINTER, i, (byte)~displayBuffer[i / 2]);
            }
        }
    }

    /**
     * Writes a pixel to the display buffer.
     * @param y The y-coordinate of the pixel
     * @param x The x-coordinate of the pixel
     */
    public void drawPixel(byte y, byte x) {
        if(x >= DISPLAY_WIDTH || y >= DISPLAY_HEIGHT || x < 0 || y < 0) { return; }
        switch(currentRotation) {
            case 1:
                x += y - (y = x);
                x = (byte)(DISPLAY_WIDTH - x - 1);
                break;
            case 2:
                x = (byte)(DISPLAY_WIDTH - x - 1);
                y = (byte)(DISPLAY_HEIGHT - y - 1);
                break;
            case 3:
                x += y - (y = x);
                y = (byte)(DISPLAY_HEIGHT - y - 1);
                break;
        }
        x += DISPLAY_WIDTH - 1;
        x %= DISPLAY_WIDTH;

        displayBuffer[y] |= (byte)(1 << x);
    }

    /**
     * writes a custom bitmap to the displayBuffer.
     * @param x The x-coordinate of the top left pixel of the bitmap
     * @param y The y-coordinate of the top left pixel of the bitmap
     * @param bitmap The bitmap to be written to the displayBuffer
     */
    public void drawBitmap(int x, int y, byte[][] bitmap) {
        for(int r = Math.max(0, y); r < DISPLAY_HEIGHT && r < y + bitmap.length; r++) {
            for(int c = Math.max(0, x); c < DISPLAY_WIDTH && c < x + bitmap[0].length * BYTE_LENGTH; c++) {
                if((bitmap[r - y][(c - x) / BYTE_LENGTH] >> BYTE_LENGTH - 1 - (c - x) % BYTE_LENGTH & 1) == 1) {
                    drawPixel((byte)(r), (byte)(c));
                }
            }
        }
    }

    /**
     * Writes a character to the displayBuffer.
     * @param x The x-coordinate of the top left pixel of the character
     * @param y The y-coordinate of the top left pixel of the character
     * @param character The character to be written to the displayBuffer.
     */
    public void drawCharacter(int x, int y, char character) {
        for(int r = Math.max(0, y); r < DISPLAY_HEIGHT && r < y + CHARACTER_HEIGHT; r++) {
            for(int c = Math.max(0, x); c < DISPLAY_WIDTH && c < x + CHARACTER_WIDTH; c++) {
                if((CHARACTERS[((int)character - 32) * CHARACTER_WIDTH + (c - x)] >> r - y & 1) == 1) {
                    drawPixel((byte)(r), (byte)(c));
                }
            }
        }
    }

    /**
     * Configures the amount of characters per line when printing a string.
     * @param lineLength The amount of characters per line. When set to 0 or lower, string will
     *                        not wrap.
     */
    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
    }

    /**
     * Writes a String to the displayBuffer.
     * @param x The x-coordinate of the top left pixel of the String
     * @param y The y-coordinate of the top left pixel of the String
     * @param message The String to be written to the displayBuffer
     */
    public void print(int x, int y, String message) {

        int msgWidth;
        int msgHeight;
        if(lineLength > 0) {
            msgWidth = Math.min(lineLength, message.length());
            msgHeight = (int) Math.ceil((double) message.length() / lineLength);
        } else {
            msgWidth = message.length();
            msgHeight = 1;
        }

        char[] characters = message.toCharArray();

        for(int r = Math.max(0, -y/(CHARACTER_HEIGHT + LEADING)); r < msgHeight && r < Math.ceil((double)-y/(CHARACTER_HEIGHT + LEADING)) + Math.ceil((double)DISPLAY_HEIGHT/(CHARACTER_HEIGHT + LEADING)); r++) {
            for(int c = Math.max(0, -x/(CHARACTER_WIDTH + TRACKING)); c < msgWidth && c < Math.ceil((double)-x/(CHARACTER_WIDTH + TRACKING)) + Math.ceil((double)DISPLAY_WIDTH/(CHARACTER_WIDTH + TRACKING)); c++) {
                if(r * msgWidth + c < message.length()) {
                    drawCharacter(x + c * (CHARACTER_WIDTH + TRACKING), y + r * (CHARACTER_HEIGHT + LEADING), characters[r * msgWidth + c]);
                }
            }
        }
    }
}