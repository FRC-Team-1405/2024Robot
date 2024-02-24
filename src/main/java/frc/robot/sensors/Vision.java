package frc.robot.sensors;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Preferences;
import frc.robot.Constants;

public class Vision {
    private Limelight limelight = new Limelight();

        public static class Pipeline {
            public static final byte NOTE = 0;
            public static final byte AMP = 1;
            public static final byte SPEAKER = 2;
        };

    public Translation2d SPEAKER_POS = Constants.VisionTargets.BLUE_SPEAKER_POS;
    private Supplier<Pose2d> botPose; 
    private byte activeTarget;

    public Vision(Supplier<Pose2d> botPose) {
        this.botPose = botPose;
        activeTarget = limelight.getPipeline();
    }

    public void setAlliance(Alliance alliance){
        switch (alliance) {
            case Red:
                SPEAKER_POS = Constants.VisionTargets.RED_SPEAKER_POS;
                break;
            case Blue:
                SPEAKER_POS = Constants.VisionTargets.BLUE_SPEAKER_POS;
                break;        
            default:
                break;
        }
    }

    public void setNoteTarget() {
        activeTarget = Pipeline.NOTE;
        limelight.setPipeline(activeTarget);
    }

    public void setAmpTarget() {
        activeTarget = Pipeline.AMP;
        limelight.setPipeline(activeTarget);
    }

    public void setSpeakerStart() {
        activeTarget = Pipeline.SPEAKER;
        limelight.setPipeline(activeTarget);
    }

    public boolean hasTarget() {
        return limelight.hasTarget();
    }

    public double getVisionAngleToTarget() {
        if (limelight.getTA() > 0.0) {
            return limelight.getTX();
        }

        return 0.0;
    }

    public double getPoseAngleToTarget() {
        Pose2d pose = botPose.get();
        Translation2d target = SPEAKER_POS;

        switch (activeTarget){
            case Pipeline.NOTE:
                return 0;
            case Pipeline.AMP:
                return 0;
            case Pipeline.SPEAKER:
             target = SPEAKER_POS;
        }

        double theta = Math.atan2(pose.getX(), target.getY() - pose.getY()) / Math.PI;
        return (pose.getRotation().getDegrees() / 180) - theta;
    }
}
