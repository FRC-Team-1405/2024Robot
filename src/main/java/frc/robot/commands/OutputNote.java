// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;

public class OutputNote extends Command {
  private Intake intake;
  /** Creates a new IntakeNote. */
  public OutputNote(Intake subsystem) {
    intake = subsystem;
    addRequirements(intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    intake.setSpeed(Intake.Speed.OUT);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    intake.setSpeed(Intake.Speed.STOP);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return !intake.hasNote();
  }
}
