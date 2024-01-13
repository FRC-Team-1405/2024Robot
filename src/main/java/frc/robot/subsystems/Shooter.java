// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
  /** Creates a new Shooter. */
  private TalonFX primary;
  private TalonFX secondary;
  public Shooter() {
    primary = new TalonFX(5);
    secondary = new TalonFX(6);

    secondary.setControl(new Follower(5, true));

  }


public void setWheelSpeed(double speed)
{
    primary.set(speed);
} 


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
