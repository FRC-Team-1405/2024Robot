// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveDrive;

public class SwerveDriveCommand extends Command {
  private SwerveDrive driveBase;
  private DoubleSupplier getXSpeed, getYSpeed, getRotationSpeed; 
  private DoubleSupplier getSlide;
  private boolean fieldOriented = true;
  private BooleanSupplier toggleDriveMode;

  public SwerveDriveCommand(DoubleSupplier getXSpeed, 
                            DoubleSupplier getYSpeed, 
                            DoubleSupplier getRotationSpeed, 
                            DoubleSupplier getSlide,
                            BooleanSupplier toggleDriveMode,
                            SwerveDrive driveBase) {
    this.getXSpeed = getXSpeed; 
    this.getYSpeed = getYSpeed; 
    this.getRotationSpeed = getRotationSpeed; 
    this.getSlide = getSlide;
    this.driveBase = driveBase; 
    this.toggleDriveMode = toggleDriveMode;
    addRequirements(driveBase);
  }

  @Override
  public void execute() {
    double slideValue = getSlide.getAsDouble();
    if (toggleDriveMode.getAsBoolean()){
      fieldOriented = !fieldOriented;
    }

    if (slideValue == 0.0) {
      driveBase.drive(getXSpeed.getAsDouble(), getYSpeed.getAsDouble(), getRotationSpeed.getAsDouble(), fieldOriented);
    } else {
      driveBase.drive(slideValue, getYSpeed.getAsDouble(), getRotationSpeed.getAsDouble(), false);
    }
  }

  @Override
  public void end(boolean interrupted) {
    driveBase.stop();
  }
}