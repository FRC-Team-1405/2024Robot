// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.FlySwatter;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ShootNoteSpeaker extends SequentialCommandGroup {
  /** Creates a new ShootNote. */
  public ShootNoteSpeaker(Intake intake, Shooter shooter) {
     addRequirements(intake, shooter);

    this.addCommands( new ShooterCommand(shooter, Shooter.ShooterSpeed.SPEAKER),
                      new OutputNote(intake),
                      shooter.runOnce( shooter::stop ));
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands();
  }

  public ShootNoteSpeaker(Intake intake, FlySwatter flySwatter) {
      //TODO Auto-generated constructor stub
  }
}
