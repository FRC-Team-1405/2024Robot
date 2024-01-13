package frc.robot.sensors;

import java.lang.annotation.Target;

import frc.robot.Constants;

public class Targets {
    private Limelight limelight = new Limelight();

    public Targets() {

    }

    public void setAmpTarget() {
        limelight.setPipeline(Constants.Limelight.Pipeline.AMP);
    }

    public void setSpeakerStart() {
        limelight.setPipeline(Constants.Limelight.Pipeline.SPEAKER);
    }

    public double getAngleToTarget() {
        if (limelight.getTA() > 0.0) {
            return limelight.getTX();
        }

        // for now return 0 but we'll to calculate from bot position
        return 0;
    }
}
