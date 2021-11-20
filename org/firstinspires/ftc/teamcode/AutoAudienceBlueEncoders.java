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

@Autonomous(name = "AutoAudienceBlueEncoders", group = "Iterative Opmode")
//@Disabled
public class AutoAudienceBlueEncoders extends OpMode {

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
                task = "strafe to carousel";
                break;

            case "strafe to carousel":
                runtime.reset();
                movement.encoderStrafe(movement.TURN_SPEED,5, -5);
                movement.resetEncoder();
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    runtime.reset();
                }
                task = "spin carousel";
                break;

            case "spin carousel":
                robot.spinner.setPower(0.7);
                if (runtime.seconds() > 4.5) {
                    robot.spinner.setPower(0);
                    runtime.reset();
                    task = "turn to hub";
                }
                break;

            case "turn to hub":
                movement.encoderDrive(movement.TURN_SPEED, -5,5);
                movement.resetEncoder();
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    runtime.reset();
                }
                task = "move to hub";
                break;

            case "move to hub":
                movement.encoderDrive(movement.DRIVE_SPEED, 5,5);
                movement.resetEncoder();
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    runtime.reset();
                }
                task = "deposit payload";
                break;

            case "deposit payload":
                movement.lifting(movement.LIFT_SPEED, 6.5);
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    runtime.reset();
                }
                robot.dump.setPosition(0.15);
                if (runtime.seconds() > 6) {
                    robot.dump.setPosition(0.7);
                    runtime.reset();
                    task = "turn to park";
                }

            case "turn to park":
                movement.encoderDrive(movement.TURN_SPEED, -24, 24);
                if (movement.checkEncoderDone()) {
                    movement.encoderComplete();
                    task = "park";
                }
                break;

            case "park":
                movement.encoderDrive(movement.DRIVE_SPEED, 44, 44);
                task = "stop";
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
