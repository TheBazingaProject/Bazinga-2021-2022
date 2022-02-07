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

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardwaremap;

/**
 * This file provides basic Telop driving for a Pushbot robot.
 * The code is structured as an Iterative OpMode
 *
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 *
 * This particular OpMode executes a basic Tank Drive Teleop for a PushBot
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Teleop", group="Pushbot")
public class Teleop extends OpMode{

    /* Declare OpMode members. */
    Hardwaremap robot       = new Hardwaremap(); // use the class created to define a Pushbot's hardware
    boolean         reverse     = false ;
    int             liftPos, liftInch, liftMax      = 0 ;
    boolean         dpad_right_n1  = false, dpad_left_n1  = false, dpad_up_n1  = false, dpad_down_n1  = false  ;
    static final double     COUNTS_PER_MOTOR_REV    = 537.6 ;
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     LIFT_WHEEL_DIAMETER_IN  = 1.0 ;
    static final double     LIFT_SPEED              = 1.0 ;
    static final double     LIFT_COUNTS_PER_INCH    = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (LIFT_WHEEL_DIAMETER_IN * 3.1415);



    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);
        robot.dump.setPosition(0.8);
        robot.fright.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.fleft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.bright.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.bleft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Initialized");

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData("dump", robot.dump.getPosition());

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        robot.tapeUpDown.setPosition(0);
        robot.tapeWrist.setPosition(1);

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // joystick controls for both controllers
        // Run wheels in tank mode (note: The joystick goes negative when pushed forwards, so negate it)
        double ly = gamepad1.left_stick_y; // Remember, this is reversed!
        double lx = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        if (gamepad1.left_bumper && reverse == true) {
            reverse = false;
        } else if (gamepad1.right_bumper && reverse == false) {
            reverse = true;
        }

        if (reverse == false) {
            robot.fleft.setPower(-ly + lx + rx);
            robot.bleft.setPower(-ly - lx + rx);
            robot.fright.setPower(-ly - lx - rx);
            robot.bright.setPower(-ly + lx - rx);
        }
        if (reverse == true) {
            robot.fleft.setPower(ly - lx - rx);
            robot.bleft.setPower(ly + lx - rx);
            robot.fright.setPower(ly + lx + rx);
            robot.bright.setPower(ly - lx + rx);
        }

        // Use gamepad left & right Bumpers to open and close the claw
//        if (gamepad1.right_bumper)
//            clawOffset += CLAW_SPEED;
//        else if (gamepad1.left_bumper)
//            clawOffset -= CLAW_SPEED;

        // Move both servos to new position.  Assume servos are mirror image of each other.
//        clawOffset = Range.clip(clawOffset, -0.5, 0.5);
//        robot.leftClaw.setPosition(robot.MID_SERVO + clawOffset);
//        robot.rightClaw.setPosition(robot.MID_SERVO - clawOffset);

        // Use gamepad buttons to move the arm up (Y) and down (A)
        // gamepad 1 - driving and intake + spinnerR
        if (gamepad1.x) {
            robot.spinner.setPower(0.9);
        } else if (gamepad1.y) {
            robot.spinner.setPower(-0.9);
        } else {
            robot.spinner.setPower(0);
        }

        if (-gamepad1.right_stick_y > 0) {
            robot.intake.setPower(-0.9);
        } else if (-gamepad1.right_stick_y < 0) {
            robot.intake.setPower(0.9);
        } else {
            robot.intake.setPower(0);
        }

        // gamepad 2 - accessories
        if (gamepad2.x) {
            robot.dump.setPosition(0.17);
        } else {
            robot.dump.setPosition(0.8);
        }

//        while (gamepad2.y) {
//            robot.dump.setPosition(robot.dump.getPosition() - 0.0008);
//        }
//
//        while (gamepad2.a) {
//            robot.camera.setPosition(robot.camera.getPosition() - 0.0003);
//            if (!gamepad2.a) {
//                robot.camera.setPosition(robot.camera.getPosition());
//            }
//        }
//
//        while (gamepad2.b) {
//            robot.camera.setPosition(robot.camera.getPosition() + 0.0003);
//            if (!gamepad2.b) {
//                robot.camera.setPosition(robot.camera.getPosition());
//            }
//        }

        if (gamepad2.right_stick_y > 0) {
            robot.lift.setPower(1);
        } else if (gamepad2.right_stick_y < 0) {
            robot.lift.setPower(-1);
        } else {
            robot.lift.setPower(0);
        }

//        if (gamepad2.dpad_up && liftPos == 0) {
//            robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            lifting(LIFT_SPEED, 5);
//            if (checkEncoderDone()) {
//                encoderComplete();
//                robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                liftPos = 1;
//                liftInch = 5;
//            }
//        } else if (gamepad2.dpad_up && liftPos == 1) {
//            robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            lifting(LIFT_SPEED, 2);
//            if (checkEncoderDone()) {
//                encoderComplete();
//                robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                liftPos = 2;
//                liftInch = 7;
//            }
//        } else if (gamepad2.dpad_up && liftPos == 2) {
//            robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            lifting(LIFT_SPEED, 9);
//            if (checkEncoderDone()) {
//                encoderComplete();
//                robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                liftPos = 3;
//                liftInch = 16;
//            }
//        }
//
//        if (gamepad2.dpad_down && liftPos == 3) {
//            robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            lifting(LIFT_SPEED, -9);
//            if (checkEncoderDone()) {
//                encoderComplete();
//                robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                liftPos = 2;
//                liftInch = 7;
//            }
//        } else if (gamepad2.dpad_down && liftPos == 2) {
//            robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            lifting(LIFT_SPEED, -2);
//            if (checkEncoderDone()) {
//                encoderComplete();
//                robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                liftPos = 1;
//                liftInch = 5;
//            }
//        } else if (gamepad2.dpad_down && liftPos == 1) {
//            robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            lifting(LIFT_SPEED, -5);
//            if (checkEncoderDone()) {
//                encoderComplete();
//                robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                liftPos = 0;
//                liftInch = 0;
//            }
//        }
//
//        if (gamepad2.dpad_right) {
//            liftMax = 16 - liftInch;
//            robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            lifting(LIFT_SPEED, liftMax);
//            if (checkEncoderDone()) {
//                encoderComplete();
//                robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            }
//        } else if (gamepad2.dpad_left) {
//            liftMax = liftInch - 16;
//            robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            lifting(LIFT_SPEED, liftMax);
//            if (checkEncoderDone()) {
//                encoderComplete();
//                robot.lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            }
//        }

        // close
//        if (gamepad2.right_bumper){
//            robot.claw.setPosition(150);
//        } else if (gamepad2.left_bumper) {
//            robot.claw.setPosition(-10);
//        }

        // tape capstone controls
        if (gamepad2.dpad_right && !dpad_right_n1){
            robot.tapeWrist.setPosition(robot.tapeWrist.getPosition() + 0.03);
        } else if (gamepad2.dpad_left && !dpad_left_n1) {
            robot.tapeWrist.setPosition(robot.tapeWrist.getPosition() - 0.03);
        }

        if (gamepad2.dpad_up && !dpad_up_n1){
            robot.tapeUpDown.setPosition(robot.tapeUpDown.getPosition() - 0.03);
        } else if (gamepad2.dpad_down && !dpad_down_n1) {
            robot.tapeUpDown.setPosition(robot.tapeUpDown.getPosition() + 0.03);
        }

        dpad_right_n1 = gamepad2.dpad_right;
        dpad_left_n1 = gamepad2.dpad_left;
        dpad_up_n1 = gamepad2.dpad_up;
        dpad_down_n1 = gamepad2.dpad_down;

        if (gamepad2.right_bumper) {
            robot.tapeLaunch.setPower(0.5);
        } else if (gamepad2.left_bumper) {
            robot.tapeLaunch.setPower(-0.5);
        } else {
            robot.tapeLaunch.setPower(0);
        }

        // Send telemetry message to signify robot running;
//        telemetry.addData("claw",  "Offset = %.2f", clawOffset);
        telemetry.addData("ly1",  "%.2f", ly);
        telemetry.addData("lx1", "%.2f", lx);
        telemetry.addData("dump", robot.dump.getPosition());
        telemetry.addData("camera", robot.camera.getPortNumber());
        telemetry.addData("trigger", gamepad1.left_trigger);
        telemetry.addData("reverse?", reverse);
        telemetry.addData("tapeWrist", robot.tapeWrist.getPosition());
        telemetry.addData("tapeUpDown", robot.tapeUpDown.getPosition());
        telemetry.addData("tapeLaunch", robot.tapeLaunch.getPower());
        telemetry.addData("lift position", liftPos);
        telemetry.addData("lift height", liftInch);
        telemetry.addData("power", "Running at %3f :%3f :%3f :%3f", robot.fleft.getPower(), robot.fright.getPower(), robot.bright.getPower(), robot.bleft.getPower());
        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    public void lifting(double speed, double liftInches) {
        int liftTarget;

        liftTarget = robot.lift.getCurrentPosition() + (int)(liftInches * LIFT_COUNTS_PER_INCH);
        robot.lift.setTargetPosition(liftTarget);

        robot.lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.lift.setPower(Math.abs(speed));
    }
    public boolean checkEncoderDone() {
        return !(robot.lift.isBusy());
    }

    public void encoderComplete(){
        robot.lift.setPower(0);
        robot.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Turn off RUN_TO_POSITION
        robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
