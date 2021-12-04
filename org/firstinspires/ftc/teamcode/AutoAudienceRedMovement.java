package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

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

@Autonomous(name = "AutoAudienceRedMovement", group = "Iterative Opmode")
//@Disabled
public class AutoAudienceRedMovement extends OpMode {

    // Declare OpMode members.
    Hardwaremap robot = new Hardwaremap();
    ElapsedTime runtime = new ElapsedTime();
    Movement movement = new Movement();


    String task = "Start";

    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";


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
        movement.init(robot);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;

        robot.imu.initialize(parameters);
        robot.dump.setPosition(0.7);

        while (!robot.imu.isGyroCalibrated()) {
        }
        telemetry.addData("Imu Calibration Status:", robot.imu.getCalibrationStatus());
        telemetry.update();

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized :)");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

        // make sure the imu gyro is calibrated before continuing.

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (first) {
            //initVuforia();
            //initTfod();

            //if (tfod != null) {
            //    tfod.activate();
            //}

            first = false;
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
        movement.resetEncoder();
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
                movement.encoderStrafe(movement.TURN_SPEED, -7, 7);
                task = "move to carousel";
                break;

            case "move to carousel":
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    movement.encoderDrive(0.15, -18, -18);
                    task = "spin duck off1";
                }
                break;

            case "spin duck off1":
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    runtime.reset();
                    task = "spin duck off2";
                }
                break;

            case "spin duck off2":
                robot.spinner.setPower(-0.7);
                if (runtime.seconds() > 4.5) {
                    robot.spinner.setPower(0);
                    runtime.reset();
                    task = "strafe out more";
                }
                break;

            case "strafe out more":
                movement.encoderStrafe(movement.TURN_SPEED, -3, 3);
                task = "back up to 3-layer thingy";
                break;

            case "back up to 3-layer thingy":
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    movement.encoderDrive(movement.DRIVE_SPEED, 43.5, 43.5);
                    task = "turn to face thingy";
                }
                break;

            case "turn to face thingy":
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    movement.encoderDrive(movement.TURN_SPEED, -22, 22);
                    task = "forward to thingy";
                }
                break;

            case "forward to thingy":
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    movement.encoderDrive(0.3, -15.5, -15.5);
                    movement.lifting(movement.LIFT_SPEED, 18);
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
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    runtime.reset();
                    task = "dumpy2";
                }
                break;

            case "dumpy2":
                robot.dump.setPosition(0.15);
                if (runtime.seconds() > 4) {
                    robot.dump.setPosition(0.7);
                    runtime.reset();
                    task = "back up a little";
                }
                break;

            case "back up a little":
                movement.encoderDrive(movement.DRIVE_SPEED, 5, 5);
                task = "turn to park";

            case "turn to park":
                if (movement.checkEncoderDone()) {
                    movement.encoderDrive(movement.TURN_SPEED, -24, 24);
                    task = "SPEEDY TO PARK";
                }
                break;

            case "SPEEDY TO PARK":
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    movement.encoderDrive(movement.DRIVE_SPEED, 48, 48);
                    task = "stop";
                }
                break;
//            case "turn a little":
//                rotate(45, TURN_SPEED);
//                if (runtime.seconds() > 3 || getAngle() > 45) {
//                    resetAngle();
//                    encoderComplete();
//                    runtime.reset();
//                }
//                task = "stop";
//                break;

//            case "move to carousel":
//                encoderDrive(DRIVE_SPEED, -30, -30);
//                resetAngle();
//                task = "spin carousel";
//                break;
////
//            case "move to carousel2":
//                if (checkEncoderDone()) {
//                    encoderComplete();
//                    runtime.reset();
//                    task = "spin carousel";
//                }
//                break;
//
//            case "spin carousel":
//                robot.spinner.setPower(0.5);
//                if (runtime.seconds() > 1.5){
//                    robot.spinner.setPower(0);
//                    runtime.reset();
//                    task = "shift to hub";
//                }
//                break;

//            case "spin carousel":
//                if (checkEncoderDone()) {
//                    encoderComplete();
//                    runtime.reset();
//                    robot.spinner.setPower(0.5);
//                    if (runtime.seconds() > 1.5) {
//                        robot.spinner.setPower(0);
//                        runtime.reset();
//                        task = "shift to hub";
//                    }
//                }
//
//            case "shift to hub":
//                encoderStrafe(DRIVE_SPEED, 6, 0);
//                if (checkEncoderDone()){
//                    encoderComplete();
//                    runtime.reset();
//                    task = "move to hub";
//                }
//                break;
//
//            case "move to hub":
//                encoderDrive(DRIVE_SPEED,4, 4);
//                if (checkEncoderDone()) {
//                    drive(0, 0);
//                    task = "turn to hub";
//                    runtime.reset();
//                }
//                break;

//            case "deposit payload":
//                //use variable from camera to determine how long the thing lifts up to deposit the payload
//                runtime.reset();
//                task ="turn to parking";
//                break;

//            case "turn to hub":
//                encoderDrive(TURN_SPEED,-4, 4);
//                if (checkEncoderDone() == true) {
//                    encoderComplete();
//                    runtime.reset();
//                    task = "park";
//                }
//                break;
//
//            case "lift to top hub":
//                robot.lift.setPower(1);
//                if (runtime.seconds() > 0.6){
//                    robot.lift.setPower(0);
//                    runtime.reset();
//                    task = "forward";
//                }
//                break;
//
//            case "forward":
//                encoderDrive(DRIVE_SPEED, 5, 5);
//                if (checkEncoderDone() == true){
//                    encoderComplete();
//                    runtime.reset();
//                    task = "strafe to wall";
//                }
//                break;
//
//            case "strafe to wall":
//                encoderStrafe(DRIVE_SPEED, -12, 12);
//                if (checkEncoderDone() == true){
//                    encoderComplete();
//                    runtime.reset();
//                    task = "back into square";
//                }
//                break;
//
//            case "back into square":
//                encoderDrive(DRIVE_SPEED, -3.5, -3.5);
//                if (checkEncoderDone() == true) {
//                    encoderComplete();
//                    runtime.reset();
//                    task = "stop";
//                }
//                break;

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
        telemetry.addData("angle", movement.getAngle());
        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }


    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
//TODO        parameters.cameraName = Hardware.get(WebcamName.class, "Webcam1");

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
}
