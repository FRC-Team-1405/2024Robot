// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.subsystems.FlySwatter;
import frc.robot.subsystems.Intake;

public class OpenIntake extends SequentialCommandGroup {
  /** Creates a new PickupNote. */
  public OpenIntake(Intake intake, FlySwatter flySwatter) {
    addRequirements(intake, flySwatter);

    this.addCommands( 
      // new CommandFlySwatter(flySwatter, FlySwatter.Position.MEDIUM),
                      new ControlIntake(intake, Intake.Position.LOWER),
                      new IntakeNote(intake),
                      new ControlIntake(intake, Intake.Position.RAISED)
                     
                      // new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW)
     );
  }
}
