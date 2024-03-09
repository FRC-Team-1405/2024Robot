// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.sensors.Vision;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.tools.MathTools;

public class SwerveDriveToNote extends Command {
  private SwerveDrive driveBase;
  private DoubleSupplier getXSpeed, getYSpeed, getRotationSpeed; 
  private Vision vision;
  private static double ROTATION_SPEED = 0.4; 

  static {
      Preferences.initDouble("SwerveDriveToNote/Rotation Speed", ROTATION_SPEED);
      ROTATION_SPEED = Preferences.getDouble("SwerveDriveToNote/Rotation Speed", ROTATION_SPEED);
  }  

  public SwerveDriveToNote(DoubleSupplier getXSpeed, 
                            DoubleSupplier getYSpeed, 
                            DoubleSupplier getRotationSpeed, 
                            Vision vision,
                            SwerveDrive driveBase) {
    this.getXSpeed = getXSpeed; 
    this.getYSpeed = getYSpeed; 
    this.getRotationSpeed = getRotationSpeed; 
    this.driveBase = driveBase; 
    this.vision = vision;
    addRequirements(driveBase);
  }

  @Override
  public void initialize() {
    vision.setNoteTarget();
  }

  @Override
  public void execute() {
    if (!vision.hasTarget()){
      driveBase.drive(getXSpeed.getAsDouble(), getYSpeed.getAsDouble(), getRotationSpeed.getAsDouble(), true);
    } else {
      double angle = vision.getVisionAngleToTarget();
      double rotationSpeed = MathTools.map(angle, -25.0, 25.0, -ROTATION_SPEED, ROTATION_SPEED  );
      driveBase.drive(getXSpeed.getAsDouble(), getYSpeed.getAsDouble(), rotationSpeed, true);
    }
  }

  @Override
  public void end(boolean interrupted) {
    driveBase.stop();
  }
}