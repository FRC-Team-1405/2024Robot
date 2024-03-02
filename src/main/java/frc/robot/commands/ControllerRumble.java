// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.util.function.BooleanSupplier;

import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class ControllerRumble extends Command {
  private static final int TickCount = 25;
  private CommandXboxController driver;
  private BooleanSupplier driverRumble;
  private CommandXboxController operator;
  private BooleanSupplier operatorRumble; 
  private int driverTickCount = 1;
  /** Creates a new ControllerRumble. */
  public ControllerRumble(CommandXboxController driver, BooleanSupplier driverRumble, CommandXboxController operator, BooleanSupplier operatorRumble) {
    this.driver = driver;
    this.driverRumble = driverRumble;
    this.driverRumble = operatorRumble;
    this.driver = operator;
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (driverRumble.getAsBoolean() && driverTickCount < 0){
      driverTickCount = TickCount;
      driver.getHID().setRumble(RumbleType.kBothRumble, 1);
    }
    driver.getHID().setRumble(RumbleType.kBothRumble, driverRumble.getAsBoolean() ? 1 : 0);
    operator.getHID().setRumble(RumbleType.kBothRumble, operatorRumble.getAsBoolean() ? 1 : 0);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // stop rumble
    driver.getHID().setRumble(RumbleType.kBothRumble, 0);
    operator.getHID().setRumble(RumbleType.kBothRumble, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
