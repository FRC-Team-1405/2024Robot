// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.Map;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class FlySwatter extends SubsystemBase {
  public enum Position {
    HIGH,
    MEDIUM,
    LOW 
  }

  private Position targetPosition = Position.LOW;
  private TalonFX primary = new TalonFX(Constants.CanBus.FLYSWATTER_PRIMARY);
  private TalonFX secondary = new TalonFX(Constants.CanBus.FLYSWATTER_SECONDARY);
  private static final double POSITION_ERROR_DELTA = 0.1;

  private StatusSignal<Double> position = primary.getPosition();

  private Map<Position,Double> positionValues = Map.of(
    Position.LOW, 0.0,
    Position.MEDIUM, 45.0,
    Position.HIGH, 90.0
  );

  /** Creates a new FlySwatter. */
  public FlySwatter() {
    if ( !Preferences.containsKey("FLySwatter/Position/Low") ){
      Preferences.setDouble("FLySwatter/Position/Low", 0);
    }
    if ( !Preferences.containsKey("FLySwatter/Position/Medium") ){
      Preferences.setDouble("FLySwatter/Position/Medium", 0);
    }
    if ( !Preferences.containsKey("FLySwatter/Position/High"));{
      Preferences.setDouble("FLySwatter/Position/High", 0);
    }
    
    positionValues.put(Position.LOW, Preferences.getDouble("FLySwatter/Position/Low", 0));
    positionValues.put(Position.MEDIUM, Preferences.getDouble("FLySwatter/Position/Medium", 0));
    positionValues.put(Position.HIGH, Preferences.getDouble("FLySwatter/Position/High", 0));

    secondary.setControl(new Follower(Constants.CanBus.FLYSWATTER_PRIMARY, false));
  }

  public void setPosition(Position target) 
  {
      targetPosition = target;
      primary.setControl( new MotionMagicVoltage(positionValues.get(targetPosition)) );
  }

  public boolean isAtPosition()
  {
        double current = position.refresh().getValue();
        double target = positionValues.get(targetPosition);

        return Math.abs(target - current) < POSITION_ERROR_DELTA;
  }


  public void stop() {
    primary.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
