package frc.robot.subsystems;

import frc.robot.SwerveModule;
import frc.robot.Constants;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Swerve extends SubsystemBase {
    public SwerveDriveOdometry swerveOdometry;
    public SwerveModule[] mSwerveMods;
    public AHRS gyro;

    DoublePublisher mod0AnglePublisher;
    DoublePublisher mod1AnglePublisher;
    DoublePublisher mod2AnglePublisher;
    DoublePublisher mod3AnglePublisher;

    final private boolean invertGyro = true;

    StructArrayPublisher<SwerveModuleState> desiredStatesPublisher = NetworkTableInstance.getDefault().getStructArrayTopic("swerve/advantagescope/desiredStates", SwerveModuleState.struct).publish();


    public Swerve() {
        gyro = new AHRS(NavXComType.kMXP_SPI);
        gyro.zeroYaw();

        mSwerveMods = new SwerveModule[] {
            new SwerveModule(0, Constants.Swerve.Mod0.constants),
            new SwerveModule(1, Constants.Swerve.Mod1.constants),
            new SwerveModule(2, Constants.Swerve.Mod2.constants),
            new SwerveModule(3, Constants.Swerve.Mod3.constants)
        };

        mod0AnglePublisher = NetworkTableInstance.getDefault().getTable("SmartDashboard").getDoubleTopic("swerve/modules/0/Angle").publish();
        mod1AnglePublisher = NetworkTableInstance.getDefault().getTable("SmartDashboard").getDoubleTopic("swerve/modules/1/Angle").publish();
        mod2AnglePublisher = NetworkTableInstance.getDefault().getTable("SmartDashboard").getDoubleTopic("swerve/modules/2/Angle").publish();
        mod3AnglePublisher = NetworkTableInstance.getDefault().getTable("SmartDashboard").getDoubleTopic("swerve/modules/3/Angle").publish();

        swerveOdometry = new SwerveDriveOdometry(Constants.Swerve.swerveKinematics, getGyroYaw(), getModulePositions());
    }

    public void drive(Translation2d translation, double rotation, boolean fieldRelative, boolean isOpenLoop) {
        SwerveModuleState[] swerveModuleStates =
            Constants.Swerve.swerveKinematics.toSwerveModuleStates(
                fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation, 
                                    getHeading()
                                )
                                : new ChassisSpeeds(
                                    translation.getX(), 
                                    translation.getY(), 
                                    rotation)
                                );
             
        this.mod0AnglePublisher.set(swerveModuleStates[0].angle.getDegrees());
        this.mod1AnglePublisher.set(swerveModuleStates[1].angle.getDegrees());
        this.mod2AnglePublisher.set(swerveModuleStates[2].angle.getDegrees());
        this.mod3AnglePublisher.set(swerveModuleStates[3].angle.getDegrees());

        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Constants.Swerve.maxSpeed);

        desiredStatesPublisher.set(swerveModuleStates);

        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(swerveModuleStates[mod.moduleNumber], isOpenLoop);
        }
    }    

    /* Used by SwerveControllerCommand in Auto */
    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, Constants.Swerve.maxSpeed);

        desiredStatesPublisher.set(desiredStates);
        
        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(desiredStates[mod.moduleNumber], false); // autonomouse closed loop / open loop
        }
    }

    public SwerveModuleState[] getModuleStates(){
        SwerveModuleState[] states = new SwerveModuleState[4];
        for(SwerveModule mod : mSwerveMods){
            states[mod.moduleNumber] = mod.getState();
        }
        return states;
    }

    public SwerveModulePosition[] getModulePositions(){
        SwerveModulePosition[] positions = new SwerveModulePosition[4];
        for(SwerveModule mod : mSwerveMods){
            positions[mod.moduleNumber] = mod.getPosition();
        }
        return positions;
    }

    public Pose2d getPose() {
        return swerveOdometry.getPoseMeters();
    }

    public void setPose(Pose2d pose) {
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), pose);
    }

    public Rotation2d getHeading(){
        return getPose().getRotation();
    }

    public void setHeading(Rotation2d heading){
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), new Pose2d(getPose().getTranslation(), heading));
    }

    public void zeroHeading(){
        swerveOdometry.resetPosition(getGyroYaw(), getModulePositions(), new Pose2d(getPose().getTranslation(), new Rotation2d()));
    }

    public Rotation2d getGyroYaw() {
        if (invertGyro){
            return Rotation2d.kZero.minus(Rotation2d.fromDegrees(gyro.getYaw()));
        }
        
        return Rotation2d.fromDegrees(gyro.getYaw());
    }

    public void resetModulesToAbsolute(){
        for(SwerveModule mod : mSwerveMods){
            mod.resetToAbsolute();
        }
    }

    @Override
    public void periodic(){
        swerveOdometry.update(getGyroYaw(), getModulePositions());

        SmartDashboard.putNumber("swerve/imu", getGyroYaw().getDegrees());

        for(SwerveModule mod : mSwerveMods){
            SmartDashboard.putNumber("swerve/modules/Mod " + mod.moduleNumber + " CANcoder", mod.getCANcoder().getDegrees());
            SmartDashboard.putNumber("swerve/modules/Mod " + mod.moduleNumber + " Angle", mod.getPosition().angle.getDegrees());
            SmartDashboard.putNumber("swerve/modules/Mod " + mod.moduleNumber + " Velocity", mod.getState().speedMetersPerSecond); 
        }
    }
}