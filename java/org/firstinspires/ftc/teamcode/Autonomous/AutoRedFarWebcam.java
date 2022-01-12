package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Hardwaremap;

// TFOD things
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import java.util.List;

// angle things
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 * <p>
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name = "AutoRedFarWebcam", group = "Iterative Opmode")
//@Disabled
public class AutoRedFarWebcam extends OpMode {

    // Declare OpMode members.
    Hardwaremap robot = new Hardwaremap();
    private ElapsedTime runtime = new ElapsedTime();

    String task = "Start";

    Orientation lastAngles = new Orientation();
    double globalAngle, power = .30, correction;
    double position = 0;
    int DRIVING_LENGTH_CHANGE = 250;

    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck"
    };
    public int BarcodePosition = 1;
    private WebcamName webcamName       = null;

    static final double     COUNTS_PER_MOTOR_REV    = 537.6 ;
    static final double     MAX_REV                 = 300 ;
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     LIFT_WHEEL_DIAMETER_IN  = 1.0 ;
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     LIFT_COUNTS_PER_INCH    = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (LIFT_WHEEL_DIAMETER_IN * 3.1415);
    static final double     DRIVE_SPEED             = 1;
    static final double     TURN_SPEED              = 0.8;
    static final double     LIFT_SPEED              = 0.8;

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "AatjoPH/////AAABmUVCe9GkqkgonwUZ+sukJtU/jOe4gIlnzYqC/xWax9QPT/IvgEVwcy5TfmzM8mkSdpIUv+NSgcQlgFNtErVYWldcLjo8JzSGR7zLO7JH/cu1/OY/S4JzokvXRk4Kg4TvbsvuU7mskkmYqLDZ8F1TYgLbEDHqy57yCy/umU/CXAwS5OGioN6WC5P8x7btlNt8Vitmlcp1aQ1Ru8bdQo0742/0WBNp0Si3MEwJzRQUCFBOhNz+jzWoRxg6GKiQW5452gjMBl/guKVYtCuoZdiAkHjy8+/CzS3yF+9c6eLe8JAIgL0/Wq568IQO/Q4lS3K/k6GZP6jh/cjvZOlnWOscm9kZ2F4R5a1Q6/eoBvzWUk2l";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    boolean first = true;

    /*
     * Code to run ONCE when the driver hits INIT
     */

    @Override
    public void init() {

        robot.init(hardwareMap);

//        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
//
//        parameters.mode = BNO055IMU.SensorMode.IMU;
//        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
//        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
//        parameters.loggingEnabled = false;
//
//        robot.imu.initialize(parameters);
        robot.dump.setPosition(0.8);
        // camera should see the last two dots
        // robot.camera.setPosition(0.5);

        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();
            // no zoom to see everything
            //tfod.setZoom(2.5, 16.0/9.0);
        }

//        while (!robot.imu.isGyroCalibrated()) {
//        }
//        telemetry.addData("Imu Calibration Status:", robot.imu.getCalibrationStatus());
//        telemetry.update();

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized :)");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

        // make sure the imu gyro is calibrated before continuing.
//        if (first) {
//
//
//            first = false;
//        }
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                if (updatedRecognitions.size() == 0) {
                    BarcodePosition = 1;
                    telemetry.addData("Barcode Position", BarcodePosition);
                }
                // step through the list of recognitions and display boundary info.
                int i = 0;
                boolean isDuckDetected = false;
                for (Recognition recognition : updatedRecognitions) {
                    telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                    telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                            recognition.getLeft(), recognition.getTop());
                    telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                            recognition.getRight(), recognition.getBottom());
                    i++;

                    // check label to see if the camera sees a Duck
                    if (recognition.getLabel().equals("Ball")) {
                        isDuckDetected = true;
                        telemetry.addData("Object Detected", "Capstone");
                        if (recognition.getLeft() > 355 && recognition.getRight() > 400) {
                            BarcodePosition = 3;
                        } else if (recognition.getLeft() > 0 && recognition.getLeft() < 350 && recognition.getRight() > 180 && recognition.getRight() < 400) {
                            BarcodePosition = 2;
                        }
                        telemetry.addData("Barcode Position", BarcodePosition);
                    } else if (recognition.getLabel().equals("Duck")) {
                        isDuckDetected = true;
                        telemetry.addData("Object Detected", "Duck");
                        if (recognition.getLeft() > 355 && recognition.getRight() > 400) {
                            BarcodePosition = 3;
                        } else if (recognition.getLeft() > 0 && recognition.getLeft() < 350 && recognition.getRight() > 180 && recognition.getRight() < 400) {
                            BarcodePosition = 2;
                        }
                        telemetry.addData("Barcode Position", BarcodePosition);
                    } else if (updatedRecognitions.size() == 0) {
                        BarcodePosition = 1;
                        telemetry.addData("Barcode Position", BarcodePosition);
                    } else {
                        isDuckDetected = false;
                        telemetry.addData("Barcode Position", 1);
                    }
                }
                telemetry.update();
            }
        }
        double cutOff = 0;
//        for (Recognition r : tfod.getRecognitions()) {
//            telemetry.addData("height", Math.abs(r.getBottom() - r.getTop()) + 5);
//        }
        telemetry.update();
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        resetEncoder();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        switch (task) {
            case "Start":
                runtime.reset();
                //turn camera to face barcode (before match?)
                //scan barcode using camera, save value as a variable so we can recall it later

                task = "strafe out of wall";
                break;

            case "strafe out of wall":
                encoderStrafe(TURN_SPEED, -7, 7);
                task = "move to shipping hub";
                break;

            case "move to shipping hub":
                if (checkEncoderDone()) {
                    encoderComplete();
                    encoderDrive(0.15, -17.5, -17.5);
                    task = "turn to face thingy";
                }
                break;

            case "turn to face thingy":
                if (checkEncoderDone()) {
                    encoderComplete();
                    encoderDrive(TURN_SPEED, -22, 22);
                    task = "forward to thingy";
                }
                break;

            case "forward to thingy":
                if (checkEncoderDone()) {
                    encoderComplete();
                    if (BarcodePosition == 1) {
                        encoderDrive(0.25, -12.4, -12.4);
                        lifting(LIFT_SPEED, 5);
                    } else if (BarcodePosition == 2) {
                        encoderDrive(0.3, -13.2, -13.2);
                        lifting(LIFT_SPEED, 7);
                    } else if (BarcodePosition == 3) {
                        encoderDrive(0.3,-15, -15);
                        lifting(LIFT_SPEED, 16);
                    }
                    runtime.reset();
                    task = "dumpy";
                }
                break;

//            case "lift up to drop":
//                if (checkEncoderDone()) {
//                    encoderComplete();
//                    lifting(LIFT_SPEED, 6.5);
//                    task = "dumpy";
//                }
//                break;

            case "dumpy":
                if (checkEncoderDone()) {
                    encoderComplete();
                    runtime.reset();
                    task = "dumpy2";
                }
                break;

            case "dumpy2":
                if (BarcodePosition == 1) {
                    robot.dump.setPosition(0.2);
                } else if (BarcodePosition == 2) {
                    robot.dump.setPosition(0.25);
                } else {
                    robot.dump.setPosition(0.2);
                }
                if (runtime.seconds() > 4) {
                    robot.dump.setPosition(0.7);
                    runtime.reset();
                    task = "back up a little";
                }
                break;

            case "back up a little":
                encoderDrive(DRIVE_SPEED, 5, 5);
                task = "turn to park";

            case "turn to park":
                if (checkEncoderDone()) {
                    encoderDrive(TURN_SPEED, 24, -24);
                    task = "SPEEDY TO PARK";
                }
                break;

            case "SPEEDY TO PARK":
                if (checkEncoderDone()) {
                    encoderComplete();
                    encoderDrive(DRIVE_SPEED, 52, 52);
                    task = "stop";
                }
                break;

            case "stop":
                if (tfod != null) {
                    tfod.shutdown();
                }
                runtime.reset();
                stop();
                break;
        }

        telemetry.addData("task: ", task);
        telemetry.addData("sec:  ", runtime.seconds());
        telemetry.addData("angle", getAngle());
        telemetry.addLine("fleft   fright   bright   bleft");
        telemetry.addData("Target",  "Running to %7d :%7d :%7d :%7d", robot.fleft.getTargetPosition(), robot.fright.getTargetPosition(), robot.bright.getTargetPosition(), robot.bleft.getTargetPosition());
        telemetry.addData("Current",  "Running at %7d :%7d :%7d :%7d", robot.fleft.getCurrentPosition(), robot.fright.getCurrentPosition(), robot.bright.getCurrentPosition(), robot.bleft.getCurrentPosition());
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

    public void encoderDrive(double speed, double rightInches, double leftInches) {
        int frightTarget;
        int fleftTarget;
        int brightTarget;
        int bleftTarget;


        // Determine new target position, and pass to motor controller
        fleftTarget = robot.fleft.getCurrentPosition() - (int)(leftInches * COUNTS_PER_INCH);
        frightTarget = robot.fright.getCurrentPosition() - (int)(rightInches * COUNTS_PER_INCH);
        bleftTarget = robot.bleft.getCurrentPosition() - (int)(leftInches * COUNTS_PER_INCH);
        brightTarget = robot.bright.getCurrentPosition() - (int)(rightInches * COUNTS_PER_INCH);
        robot.fright.setTargetPosition(frightTarget);
        robot.fleft.setTargetPosition(fleftTarget);
        robot.bright.setTargetPosition(brightTarget);
        robot.bleft.setTargetPosition(bleftTarget);

        // Turn On RUN_TO_POSITION
        robot.fright.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.fleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.bright.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.bleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        //runtime.reset();
        robot.fright.setPower(Math.abs(speed));
        robot.fleft.setPower(Math.abs(speed));
        robot.bright.setPower(Math.abs(speed));
        robot.bleft.setPower(Math.abs(speed));
    }

    // frightBleft is positive and fleftBright is negative to strafe left
    public void encoderStrafe(double speed,
                              double frightBleftInches, double fleftBrightInches) {
        int newFleftTarget;
        int newFrightTarget;
        int newBleftTarget;
        int newBrightTarget;

        // Determine new target position, and pass to motor controller
        newFleftTarget = robot.fleft.getCurrentPosition() + (int)(fleftBrightInches * COUNTS_PER_INCH);
        newFrightTarget = robot.fright.getCurrentPosition() + (int)(frightBleftInches * COUNTS_PER_INCH);
        newBleftTarget = robot.bleft.getCurrentPosition() + (int)(frightBleftInches * COUNTS_PER_INCH);
        newBrightTarget = robot.bright.getCurrentPosition() + (int)(fleftBrightInches * COUNTS_PER_INCH);

        robot.fright.setTargetPosition(newFrightTarget);
        robot.fleft.setTargetPosition(newFleftTarget);
        robot.bright.setTargetPosition(newBrightTarget);
        robot.bleft.setTargetPosition(newBleftTarget);

        // Turn On RUN_TO_POSITION
        robot.fright.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.fleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.bright.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.bleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        runtime.reset();
        robot.fright.setPower(Math.abs(speed));
        robot.fleft.setPower(Math.abs(speed));
        robot.bright.setPower(Math.abs(speed));
        robot.bleft.setPower(Math.abs(speed));
    }

    public boolean checkEncoderDone() {
        return !(robot.fright.isBusy() && robot.fleft.isBusy() && robot.bright.isBusy() && robot.bleft.isBusy() || robot.lift.isBusy());
    }

    public void encoderComplete(){
        robot.fright.setPower(0);
        robot.fleft.setPower(0);
        robot.bright.setPower(0);
        robot.bleft.setPower(0);
        robot.lift.setPower(0);

        robot.fright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.fleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.bright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.bleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Turn off RUN_TO_POSITION
        robot.fright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.fleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.bright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.bleft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    private void resetEncoder() {

        position = robot.fleft.getCurrentPosition();
    }

    private double getPosition() {

        return robot.fleft.getCurrentPosition() - position;
    }

    public void drive(double leftPower, double rightPower) {
        robot.bleft.setPower(leftPower);
        robot.bright.setPower(rightPower);
        robot.fleft.setPower(leftPower);
        robot.fright.setPower(rightPower);
    }

    private void resetAngle() {
        lastAngles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }

    /**
     * Get current cumulative angle rotation from last reset.
     *
     * @return Angle in degrees. + = left, - = right.
     */
    private double getAngle() {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    /**
     * See if we are moving in a straight line and if not return a power correction value.
     *
     * @return Power adjustment, + is adjust left - is adjust right.
     */
    private double checkDirection() {
        // The gain value determines how sensitive the correction is to direction changes.
        // You will have to experiment with your robot to get small smooth direction changes
        // to stay on a straight line.
        double correction, angle, gain = .10;

        angle = getAngle();

        if (angle == 0)
            correction = 0;             // no adjustment.
        else
            correction = -angle;        // reverse sign of angle for correction.

        correction = correction * gain;

        return correction;
    }

    /**
     * Rotate left or right the number of degrees. Does not support turning more than 180 degrees.
     *
     * @param degrees Degrees to turn, + is left - is right
     */
    public void rotate(int degrees, double power) {
        double leftPower, rightPower;

        // restart imu movement tracking.
        // resetAngle();

        // getAngle() returns + when rotating counter clockwise (left) and - when rotating
        // clockwise (right).

        // slow as we get closer
        if (Math.abs(getAngle() - degrees) < 0.5) {
            power = power * 0.0;
        }
        else if (Math.abs(getAngle() - degrees) < 5) {
            power = power * 0.25;
        }
        else if (Math.abs(getAngle() - degrees) < 10) {
            power = power * 0.5;
        }

        if (degrees < 0) {   // turn right.
            leftPower = -power;
            rightPower = power;
        } else if (degrees > 0) {   // turn left.
            leftPower = power;
            rightPower = -power;
        } else return;

        // set power to rotate.
        drive(leftPower, rightPower);

        // turn the motors off.
        //drive(0, 0);

        // wait for rotation to stop.

        // reset angle tracking on new heading.
        // resetAngle();
    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     private void initTfod() {
     int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
     "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
     TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
     tfodParameters.minimumConfidence = 0.8;
     tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
     tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
     }
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.useObjectTracker = true;
        tfodParameters.minResultConfidence = 0.85f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}
