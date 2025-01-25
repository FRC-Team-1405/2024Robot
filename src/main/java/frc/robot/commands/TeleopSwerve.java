package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.SwerveModule;
import frc.robot.subsystems.Swerve;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Command;


public class TeleopSwerve extends Command {    
    private Swerve s_Swerve;    
    private DoubleSupplier translationSup;
    private DoubleSupplier strafeSup;
    private DoubleSupplier rotationSup;
    private BooleanSupplier robotCentricSup;
    private BooleanSupplier test360;
    private double desiredDegree = 0;
    private final double degreeIncrement = 1;

    public TeleopSwerve(Swerve s_Swerve, DoubleSupplier translationSup, DoubleSupplier strafeSup, DoubleSupplier rotationSup, BooleanSupplier robotCentricSup, BooleanSupplier test360) {
        this.s_Swerve = s_Swerve;
        addRequirements(s_Swerve);

        this.translationSup = translationSup;
        this.strafeSup = strafeSup;
        this.rotationSup = rotationSup;
        this.robotCentricSup = robotCentricSup;
        this.test360 = test360;
    }

    @Override
    public void execute() {
        /* Get Values, Deadband*/
        double translationVal = MathUtil.applyDeadband(translationSup.getAsDouble(), Constants.stickDeadband);
        double strafeVal = MathUtil.applyDeadband(strafeSup.getAsDouble(), Constants.stickDeadband);
        double rotationVal = MathUtil.applyDeadband(rotationSup.getAsDouble(), Constants.stickDeadband);

        // /* Drive */
        // s_Swerve.drive(
        //     new Translation2d(translationVal, strafeVal).times(Constants.Swerve.maxSpeed), 
        //     rotationVal * Constants.Swerve.maxAngularVelocity, 
        //     !robotCentricSup.getAsBoolean(), 
        //     true
        // );


        if(test360.getAsBoolean() && desiredDegree < 360){
            for (SwerveModule swerveModule : s_Swerve.mSwerveMods){
                swerveModule.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(desiredDegree)), true);
            }

            desiredDegree += degreeIncrement;
        } else {
            if(!test360.getAsBoolean()){
                desiredDegree = 0;

                s_Swerve.drive(
            new Translation2d(translationVal, strafeVal).times(Constants.Swerve.maxSpeed), 
            rotationVal * Constants.Swerve.maxAngularVelocity, 
            !robotCentricSup.getAsBoolean(), 
            true
        );
            }
        }
    }
}