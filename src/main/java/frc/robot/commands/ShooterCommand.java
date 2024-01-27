// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Shooter.ShooterSpeed;

public class ShooterCommand extends Command {
  private Shooter shooter;
  private ShooterSpeed speed; 

  /** Creates a new ShooterCommand. */
  public ShooterCommand(Shooter subsystem, ShooterSpeed speed) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.addRequirements(shooter);
    this.speed = speed;
    this.shooter = subsystem;
    
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    shooter.setWheelSpeed(speed);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
   
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    shooter.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return shooter.atSpeed();
  }
}
