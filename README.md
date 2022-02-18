[![Maven Central](https://img.shields.io/maven-central/v/com.team4924/LedDisplayI2cDriver.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.team4924%22%20AND%20a:%22LedDisplayI2cDriver%22)

# LedDisplayI2cDriver
An easy straightforward way for FTC teams to control a grid of functional LEDs.
## Features
- Supports up to 8 separate 8x8 LED display boards per I2C bus
- Supports printing characters, words, and custom bitmaps
- Supports changing the x and y position of the character/word/bitmap enabling scrolling
- Supports rotation of the display
- Supports changing the brightness of the display
- Supports changing the blink rate of the display
## Setup
1) From your team's project in Android Studio (more information on setting up a project [here](https://github.com/FIRST-Tech-Challenge/FtcRobotController)), 
   go to the Project view on the left > Gradle Scripts > `build.gradle` (for the TeamCode module).
2) Add the following line to your dependencies: 
   ```groovy
   dependencies {
       implementation 'com.team4924:LedDisplayI2cDriver:1.0'
   }
   ```
3) Run a Gradle sync.
4) If Gradle throws a warning saying `Failed to resolve: com.team4924:LedDisplayI2cDriver:1.0`,
   add the Maven Central repository to your `build.dependencies.gradle` file and sync again:
   ```groovy
   repositories {
       mavenCentral()
   }
   ```

5) That's all you need to do for a single display board, but if you want multiple displayBoards on the same I2c bus,
   there is a little extra. By default, the I2c address of each display is 0x70. Since you cannot have two devices with
   the same I2c address hooked up to the same port, you have to change the I2c address. To do this you both change the
   I2c address in the software, as shown in the multiple displays example, and you have to change it manually by
   soldering the switches on the back of the display. Each switch is a digit in binary, that gets added to the 0x70.
   The switches are labeled with A0, A1, and A2.
## Usage and Examples
The following examples are used in [DisplayTest16x16](LedMatrix/src/main/java/com/team4924/LedDisplayI2cDriver/DisplayTest16x16.java)
##### Scrolling
Scrolls "Hello World" across a single display board
```java
private static final int CHARACTER_WIDTH = 5;

HT16K33 display;  

display = hardwareMap.get(HT16K33.class, "display8x8");

String message = "Hello World";
for(int i  = 0; i < message.length() * CHARACTER_LENGTH; i++) {
    display.print(i, 0, 0, word);
}
```
##### Multiple Display Boards custom Bitmap Display
Displays a custom bitmap display to multiple displays
```java
private static final byte[][] pandaBitmap = {{
            (byte)0b00000011, (byte)0b11000011},{
            (byte)0b00000011, (byte)0b11000111},{
            (byte)0b00100010, (byte)0b00000001},{
            (byte)0b01000100, (byte)0b00000000},{
            (byte)0b10001100, (byte)0b00000000},{
            (byte)0b10001010, (byte)0b11000000},{
            (byte)0b10001001, (byte)0b11001100},{
            (byte)0b10001001, (byte)0b10001100},{
            (byte)0b11001100, (byte)0b00000100},{
            (byte)0b11001110, (byte)0b01110001},{
            (byte)0b01101111, (byte)0b00100011},{
            (byte)0b01111111, (byte)0b10001111},{
            (byte)0b01111111, (byte)0b11111111},{
            (byte)0b00111011, (byte)0b11000111},{
            (byte)0b00011011, (byte)0b11001111},{
            (byte)0b00000001, (byte)0b11101110}};
            
    HT16K33 displayBoard0 = hardwareMap.get(HT16K33.class, "displayBoard0");
    HT16K33 displayBoard1 = hardwareMap.get(HT16K33.class, "displayBoard1");
    HT16K33 displayBoard2 = hardwareMap.get(HT16K33.class, "displayBoard2");
    HT16K33 displayBoard3 = hardwareMap.get(HT16K33.class, "displayBoard3");
    displayBoard1.setI2cAddress(I2cAddr.create7bit(0x71));
    displayBoard2.setI2cAddress(I2cAddr.create7bit(0x72));
    displayBoard3.setI2cAddress(I2cAddr.create7bit(0x73));
    Display fullDisplay = new Display(2, 2, new ArrayList<>(Arrays.asList(
            displayBoard0,
            displayBoard1,
            displayBoard2,
            displayBoard3
    )));
    
    fullDisplay.clear();
    fullDisplay.drawBitmap(0, 0, pandaBitmap);
    Display.writeDisplay();
```
## Ways you can contribute
We are always looking for help in improving are code, and are very open to suggestions. This was the easiest way we found for teams to set this up, 
though, we looked into how to use maven artifacts, but don't have any experience with them. If anyone could help us do that, it would be much appreciated. Please let us know
if you find any mistakes, or run in to any problems, we are happy to help.
Contact us at:
[nrrobotics@gmail.com](mailto:nrrobotics@gmail.com)
