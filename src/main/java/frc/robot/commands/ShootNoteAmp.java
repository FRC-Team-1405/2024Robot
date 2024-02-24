// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.FlySwatter;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ShootNoteAmp extends SequentialCommandGroup {
  /** Creates a new ShootNote. */
  public ShootNoteAmp(Intake intake, Shooter shooter, FlySwatter flySwatter) {
     addRequirements(intake, shooter, flySwatter);

    this.addCommands( //new CommandFlySwatter(flySwatter, FlySwatter.Position.HIGH),
                      new ShooterCommand(shooter, Shooter.ShooterSpeed.AMP),
                      new OutputNote(intake),
                      shooter.runOnce( shooter::stop ),
                      new WaitCommand(0.25),
                      new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW));
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands();
  }
}
