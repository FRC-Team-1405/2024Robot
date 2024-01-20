// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Intake.Speed;

public class ControlIntake extends Command {
  private Intake intake;
  private Intake.Position target; 

  /** Creates a new control_intake. */
  public ControlIntake(Intake subsystem, Intake.Position position) {
    intake = subsystem;
    target = position;
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    intake.setPosition(target);
    if (target == Intake.Position.LOWER ){
      intake.setSpeed(Intake.Speed.IN);
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(intake.hasNote()) {
      intake.setSpeed(Intake.Speed.STOP);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      intake.stop(); 
      intake.setSpeed(Speed.STOP);
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return intake.isAtPosition();
  }
}
