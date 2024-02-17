// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.FlySwatter;
import frc.robot.subsystems.Intake;

public class CloseIntake extends SequentialCommandGroup {
  /** Creates a new PickupNote. */
  public CloseIntake(Intake intake, FlySwatter flySwatter) {
    addRequirements(intake, flySwatter);

    this.addCommands( new CommandFlySwatter(flySwatter, FlySwatter.Position.MEDIUM),
                      intake.run(intake::stop),
                      new ControlIntake(intake, Intake.Position.RAISED),
                      new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW)
     );
  }
}
