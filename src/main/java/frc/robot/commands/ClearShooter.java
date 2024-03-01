// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Shooter.ShooterSpeed;

public class ClearShooter extends Command {
  private Shooter subsystem;
  private int tickCount;
  /** Creates a new ClearShooter. */
  public ClearShooter(Shooter subsystem) {
    this.subsystem = subsystem;
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    tickCount = 0;
    subsystem.setWheelSpeed(ShooterSpeed.REVERSE);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    tickCount += 1;
    if (tickCount > 25) {
      subsystem.setWheelSpeed(ShooterSpeed.SPEAKER);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subsystem.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return (tickCount >= 50);
  }
}
