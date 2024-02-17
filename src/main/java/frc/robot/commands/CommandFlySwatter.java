// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FlySwatter;

public class CommandFlySwatter extends Command {
  private FlySwatter flySwatter;
  private FlySwatter.Position target;
  /** Creates a new CommandFlySwatter. */
  public CommandFlySwatter(FlySwatter subSystem, FlySwatter.Position position) {
    flySwatter = subSystem;
    target = position;
    addRequirements(flySwatter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    flySwatter.setPosition(target);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if ( interrupted
      || target == FlySwatter.Position.LOW) {
      flySwatter.stop();
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return flySwatter.isAtPosition();
  }
}
