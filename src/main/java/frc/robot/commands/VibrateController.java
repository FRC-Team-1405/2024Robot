// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class VibrateController extends Command {
  private CommandXboxController controller;
  private int counter;
  private boolean rumbleOn;
  private int rumbleCount = 0 ;
  /** Creates a new IntakeNote. */
  public VibrateController(CommandXboxController controller) {
    this.controller = controller;
    counter = 0;
    rumbleCount = 0;
    rumbleOn = false;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    controller.getHID().setRumble(RumbleType.kBothRumble, 1);
    counter = 0;
    rumbleCount = 0;
    rumbleOn = true;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    counter += 1;
    if (counter > 10) {
      controller.getHID().setRumble(RumbleType.kBothRumble, rumbleOn ? 0 : 1);
      rumbleCount += rumbleOn ? 1 : 0;
      rumbleOn = !rumbleOn;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    controller.getHID().setRumble(RumbleType.kBothRumble, 0);
    counter = 0;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return rumbleCount >= 3; 
  }
}
