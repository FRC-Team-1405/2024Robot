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
    LOW(0.0);

    private Position(double value){
      Preferences.initDouble("FLySwatter/Position/"+this.name(), value);
      this.value = Preferences.getDouble("FLySwatter/Position/"+this.name(), value);
    }

    private double value;
    public double getValue(){
      return value;
    }
  }
  
  private Position targetPosition = Position.LOW;
  private TalonFX primary = new TalonFX(Constants.CanBus.FLYSWATTER_PRIMARY);
  private TalonFX secondary = new TalonFX(Constants.CanBus.FLYSWATTER_SECONDARY);
  private static final double POSITION_ERROR_DELTA = 0.1;

  private Supplier<Double> position = primary.getPosition().asSupplier();

  /** Creates a new FlySwatter. */
  public FlySwatter() {
   secondary.setControl(new Follower(Constants.CanBus.FLYSWATTER_PRIMARY, false));
  }

  public void setPosition(Position target) 
  {
      targetPosition = target;
      primary.setControl( new MotionMagicVoltage(targetPosition.getValue()) );
  }

  public boolean isAtPosition()
  { 
    return Math.abs(targetPosition.getValue() - position.get()) < POSITION_ERROR_DELTA;
  }


  public void stop() {
    primary.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
