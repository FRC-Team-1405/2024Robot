// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.FlySwatter;
import frc.robot.subsystems.Intake;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ClearIntake extends SequentialCommandGroup {
  /** Creates a new ClearIntake. */
  public ClearIntake(FlySwatter flySwatter, Intake intake) {
    // Add your commands in the addCommands() call, e.g.
    addCommands(new CommandFlySwatter(flySwatter, FlySwatter.Position.MEDIUM),
                new ControlIntake(intake, Intake.Position.EJECT),
                new OutputNote(intake),
                new CloseIntake(intake, flySwatter));
  }
}
