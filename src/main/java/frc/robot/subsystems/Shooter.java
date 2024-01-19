// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.Map;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVelocityDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {
  /** Creates a new Shooter. */
  private TalonFX primary;
  private TalonFX secondary;

  private double targetSpeed = 0; 

  public enum ShooterSpeed {
    AMP,
    SPEAKER
  }

  private Map<ShooterSpeed,Double> shooterMap = Map.of(
     ShooterSpeed.AMP, 15.0,
     ShooterSpeed.SPEAKER, 30.0
  );

  public Shooter() {
    primary = new TalonFX(Constants.ShooterConstants.shooterPrimary);
    secondary = new TalonFX(Constants.ShooterConstants.shooterSecondary);
    secondary.setControl(new Follower(5, true));
  }


public void setWheelSpeed(ShooterSpeed speed)
 {
   targetSpeed = shooterMap.get(speed);

   primary.setControl(new MotionMagicVelocityDutyCycle(targetSpeed));

 } 

public boolean atSpeed(){
    if(Math.abs(primary.getRotorVelocity().getValueAsDouble() - targetSpeed) <= 3) {
      return true; 
    }
    else{
      return false; 
    }
  }

public void stop(){
    primary.set(0.0);
  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
