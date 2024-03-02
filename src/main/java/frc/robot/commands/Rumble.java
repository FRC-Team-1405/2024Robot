// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class Rumble extends Command {
  private CommandXboxController controller;
  private int ticksOn; 
  private int ticksOff;
  private int repeat;
  private int ticksReset;
  private int tickCount;
  private int rumbleCount;
  /** Creates a new Rumble. */
  public Rumble(CommandXboxController controller, int ticksOn, int ticksOff, int repeat) {
    this.controller = controller;
    this.ticksOn = ticksOn;
    this.ticksOff = ticksOff;
    this.repeat = repeat;

    this.ticksReset = ticksOn + ticksOff;
    }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    tickCount = 0;
    rumbleCount = 0;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (tickCount == 0) {
      controller.getHID().setRumble(RumbleType.kBothRumble, 1);
      rumbleCount++;
    }
    else if (tickCount > ticksOn + ticksOff) { 
      tickCount = -1;
    }
    else if (tickCount == ticksOn) {
      controller.getHID().setRumble(RumbleType.kBothRumble, 0);
    } 
    tickCount++;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    controller.getHID().setRumble(RumbleType.kBothRumble, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return rumbleCount >= repeat && tickCount >= (ticksOn + ticksOff);
  }
}
