// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.Supplier;

import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class FlySwatter extends SubsystemBase {
  public enum Position {
    HIGH(0.9),
    MEDIUM(0.45),
    LOW(0.0),
    CLIMB(0.75);

    private Position(double value){
      Preferences.initDouble("FLySwatter/Position/"+this.name(), value);
      this.value = Preferences.getDouble("FLySwatter/Position/"+this.name(), value);
    }

    private double value;
    public double getValue(){
      return value;
    }
  }
  private double MAX_POSITION_CHANGE = 100;

  private boolean climbActive = false;
  private Position targetPosition = Position.LOW;
  private TalonFX primary = new TalonFX(Constants.CanBus.FLYSWATTER_PRIMARY);
  private TalonFX secondary = new TalonFX(Constants.CanBus.FLYSWATTER_SECONDARY);
  private static final double POSITION_ERROR_DELTA = 0.1;

  private Supplier<Double> getPosition = primary.getPosition().asSupplier();
  private MotionMagicVoltage setPosition = new MotionMagicVoltage(targetPosition.getValue());

  /** Creates a new FlySwatter. */
  public FlySwatter() {
   secondary.setControl(new Follower(Constants.CanBus.FLYSWATTER_PRIMARY, false));

   Preferences.initDouble("FlySwatter/MaxPosition/", MAX_POSITION_CHANGE);
   MAX_POSITION_CHANGE = Preferences.getDouble("FlySwatter/MaxPosition/", MAX_POSITION_CHANGE);
}

  public void setPosition(Position target) 
  {
      targetPosition = target;
      primary.setControl( setPosition.withPosition(targetPosition.getValue()) );
      
      climbActive = (target == Position.CLIMB);
  }

  public void adjustPosition(double amount) {
    if (climbActive){
      double target = targetPosition.getValue() + (MAX_POSITION_CHANGE * amount);
      primary.setControl( setPosition.withPosition(target) );
    }
  }

  public boolean isAtPosition()
  { 
    boolean result =Math.abs(targetPosition.getValue() - getPosition.get()) < POSITION_ERROR_DELTA;
    return result;
  }


  public void stop() {
    primary.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
