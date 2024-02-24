// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class Rumble extends Command {
  private CommandXboxController controller;
  private int ticks;
  private int ticksReset;
  /** Creates a new Rumble. */
  public Rumble(CommandXboxController controller, int ticks) {
    this.controller = controller;
    this.ticksReset = ticks;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    ticks = ticksReset;
    controller.getHID().setRumble(RumbleType.kBothRumble, 1);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    ticks -= 1;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    controller.getHID().setRumble(RumbleType.kBothRumble, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return ticks <= 0;
  }
}
