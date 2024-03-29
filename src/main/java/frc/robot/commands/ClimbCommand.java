// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FlySwatter;

public class ClimbCommand extends Command {
  /** Creates a new ClimbCommand. */
  private FlySwatter subSystem; 
  private DoubleSupplier positionChange;
  public ClimbCommand(FlySwatter subSystem, DoubleSupplier positionChange) {
    this.subSystem = subSystem;
    this.positionChange = positionChange;

    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    subSystem.adjustPosition(positionChange.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subSystem.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
