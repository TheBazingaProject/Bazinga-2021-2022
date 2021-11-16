package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Movement {
    double globalAngle, power = .30, correction;
    Orientation lastAngles = new Orientation();
    double position = 0;

    Hardwaremap robot = new Hardwaremap();
    public void resetEncoder() {
        position = robot.fleft.getCurrentPosition();
    }

    public double getPosition() {
        return robot.fleft.getCurrentPosition() - position;
    }

    public void drive(double leftPower, double rightPower) {
        robot.bleft.setPower(leftPower);
        robot.bright.setPower(rightPower);
        robot.fleft.setPower(leftPower);
        robot.fright.setPower(rightPower);
    }

    public void resetAngle() {
        lastAngles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }

    /**
     * Get current cumulative angle rotation from last reset.
     *
     * @return Angle in degrees. + = left, - = right.
     */
    public double getAngle() {
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
    public double checkDirection() {
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
            leftPower = power;
            rightPower = -power;
        } else if (degrees > 0) {   // turn left.
            leftPower = -power;
            rightPower = power;
        } else return;

        // set power to rotate.
        drive(leftPower, rightPower);

        // turn the motors off.
        //drive(0, 0);

        // wait for rotation to stop.

        // reset angle tracking on new heading.
        // resetAngle();
    }
}
