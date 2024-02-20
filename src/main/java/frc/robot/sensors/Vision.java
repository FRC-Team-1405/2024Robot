package frc.robot.sensors;

import java.lang.annotation.Target;

import frc.robot.Constants;

public class Vision {
    private Limelight limelight = new Limelight();

    public Vision() {

    }

    public void setAmpTarget() {
        limelight.setPipeline(Constants.Limelight.Pipeline.AMP);
    }

    public void setSpeakerStart() {
        limelight.setPipeline(Constants.Limelight.Pipeline.SPEAKER);
    }

    public boolean hasTarget() {
        return limelight.hasTarget();
    }

    public double getAngleToTarget() {
        if (limelight.getTA() > 0.0) {
            return limelight.getTX();
        }

        // for now return 0 but we'll to calculate from bot position
        return 0;
    }
}
