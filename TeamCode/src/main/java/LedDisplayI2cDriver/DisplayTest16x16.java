/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package LedDisplayI2cDriver;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.I2cAddr;

import java.util.ArrayList;
import java.util.Arrays;

import LedDisplayI2cDriver.Display;
import LedDisplayI2cDriver.HT16K33;

import static LedDisplayI2cDriver.Constants.*;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */ 

@TeleOp(name="Test")
public class DisplayTest16x16 extends LinearOpMode {

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

    @Override
    public void runOpMode() {

        int x = 0;
        int y = 0;

        int rotation = 0;

        boolean dPadUpPressed = false;
        boolean dPadDownPressed = false;
        boolean dPadLeftPressed = false;
        boolean dPadRightPressed = false;
        boolean leftBumperPressed = false;
        boolean rightBumperPressed = false;

        String message = "Hello Pandas!";

        HT16K33 displayBoard0 = hardwareMap.get(HT16K33.class, "displayBoard0");
        HT16K33 displayBoard1 = hardwareMap.get(HT16K33.class, "displayBoard1");
        HT16K33 displayBoard2 = hardwareMap.get(HT16K33.class, "displayBoard2");
        HT16K33 displayBoard3 = hardwareMap.get(HT16K33.class, "displayBoard3");
        displayBoard1.setI2cAddress(I2cAddr.create7bit(0x71));
        displayBoard2.setI2cAddress(I2cAddr.create7bit(0x72));
        displayBoard3.setI2cAddress(I2cAddr.create7bit(0x74));
        Display fullDisplay = new Display(2, 2, new ArrayList<>(Arrays.asList(
                displayBoard0,
                displayBoard1,
                displayBoard2,
                displayBoard3
        )));

        fullDisplay.setLineLength(2);

        waitForStart();

        //counts down from 100
        for(int i = 100; i > 0; i--) {
            fullDisplay.clear();
            fullDisplay.print(0, 0, String.valueOf(i));
            fullDisplay.writeDisplay();
        }

        fullDisplay.setLineLength(0);

        //scrolls message across the display
        for(int i = 0; i < message.length() * 5; i++) {
            fullDisplay.clear();
            fullDisplay.print(x, y, message);
            fullDisplay.writeDisplay();
            x -= 1;
            sleep(100);
        }

        x = 0;
        y = 0;

        fullDisplay.clear();
        fullDisplay.drawBitmap(0, 0, pandaBitmap);
        fullDisplay.writeDisplay();

        /*
        displays the panda bitmap to the screen letting you rotate with the bumpers and change the
        x and y positions with the dpad.
         */
        while(opModeIsActive()) {
            if(gamepad1.dpad_right) {
                if(!dPadRightPressed) {
                    dPadRightPressed = true;
                    x += 1;
                    fullDisplay.clear();
                    fullDisplay.drawBitmap(x, y, pandaBitmap);
                    fullDisplay.writeDisplay();
                }
            } else if(dPadRightPressed) {
                dPadRightPressed = false;
            }
            if(gamepad1.dpad_down) {
                if(!dPadDownPressed) {
                    dPadDownPressed = true;
                    y += 1;
                    fullDisplay.clear();
                    fullDisplay.drawBitmap(x, y, pandaBitmap);
                    fullDisplay.writeDisplay();
                }
            } else if(dPadDownPressed) {
                dPadDownPressed = false;
            }
            if(gamepad1.dpad_left) {
                if(!dPadLeftPressed) {
                    dPadLeftPressed = true;
                    x -= 1;
                    fullDisplay.clear();
                    fullDisplay.drawBitmap(x, y, pandaBitmap);
                    fullDisplay.writeDisplay();
                }
            } else if(dPadLeftPressed) {
                dPadLeftPressed = false;
            }
            if(gamepad1.dpad_up) {
                if(!dPadUpPressed) {
                    dPadUpPressed = true;
                    y -= 1;
                    fullDisplay.clear();
                    fullDisplay.drawBitmap(x, y, pandaBitmap);
                    fullDisplay.writeDisplay();
                }
            } else if(dPadUpPressed) {
                dPadUpPressed = false;
            }
            if(gamepad1.left_bumper) {
                if(!leftBumperPressed) {
                    leftBumperPressed = true;
                    rotation = (rotation + 3) % 4;
                    fullDisplay.setRotation(rotation);
                    fullDisplay.clear();
                    fullDisplay.drawBitmap(x, y, pandaBitmap);
                    fullDisplay.writeDisplay();
                }
            } else if(leftBumperPressed) {
                leftBumperPressed = false;
            }
            if(gamepad1.right_bumper) {
                if(!rightBumperPressed) {
                    rightBumperPressed = true;
                    rotation = (rotation + 1) % 4;
                    fullDisplay.setRotation(rotation);
                    fullDisplay.clear();
                    fullDisplay.drawBitmap(x, y, pandaBitmap);
                    fullDisplay.writeDisplay();
                }
            } else if(rightBumperPressed) {
                rightBumperPressed = false;
            }
        }
    }
}

