// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVelocityDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {
  /** Creates a new Shooter. */
  private TalonFX primary;
  private TalonFX secondary;

  private static final double SPEED_ERROR_DELTA = 3;
  private ShooterSpeed targetSpeed; 

  public enum ShooterSpeed {
    AMP(25),
    SPEAKER(50);

    private ShooterSpeed(double value) {
      Preferences.initDouble("Shooter/Speed/"+this.name(), value);
      this.value = Preferences.getDouble("Shooter/Speed/"+this.name(), value);
    }
    
    private double value;
    public double getValue(){
      return value;
    }
  }

  public Shooter() {
    primary = new TalonFX(Constants.CanBus.SHOOTER_PRIMARY);
    secondary = new TalonFX(Constants.CanBus.SHOOTER_SECONDARY);
    secondary.setControl(new Follower(5, false));
  }


public void setWheelSpeed(ShooterSpeed speed)
 {
   targetSpeed = speed;

   primary.setControl(new MotionMagicVelocityDutyCycle(targetSpeed.getValue()));

 } 

public boolean atSpeed(){
    boolean atSpeed = Math.abs(primary.getRotorVelocity().getValueAsDouble() - targetSpeed.getValue()) <= SPEED_ERROR_DELTA;
    return atSpeed;
  }

public void stop(){
    primary.set(0.0);
  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
