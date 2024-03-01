// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.utils.TalonFXHelper;

public class FlySwatter extends SubsystemBase {
  public enum Position {
    HIGH(37),
    MEDIUM(17),
    LOW(1),
    CLIMB(30);

    private Position(double value){
      Preferences.initDouble("FLySwatter/Position/"+this.name(), value);
      this.value = Preferences.getDouble("FLySwatter/Position/"+this.name(), value);
    }

    private double value;
    public double getValue(){
      return value;
    }
  }
  private double MAX_POSITION_CHANGE = 1;

  private boolean climbActive = false;
  private Position targetPosition = Position.LOW;
  private TalonFXHelper primary = new TalonFXHelper(Constants.CanBus.FLYSWATTER_PRIMARY, Constants.CanBus.FLYSWATTER_SECONDARY, Constants.POSITION_ERROR_DELTA);

  /** Creates a new FlySwatter. */
  public FlySwatter() {
    Preferences.initDouble("FlySwatter/MaxPosition/", MAX_POSITION_CHANGE);
    MAX_POSITION_CHANGE = Preferences.getDouble("FlySwatter/MaxPosition/", MAX_POSITION_CHANGE);

    primary.manualZeroize();
  }
  
  public void setPosition(Position target)  {
    targetPosition = target;
    primary.SetPosition(targetPosition.getValue());
    
    climbActive = (target == Position.CLIMB);
  }

  public void adjustPosition(double amount) {
    if (climbActive){
      double target = primary.GetPosition() + (MAX_POSITION_CHANGE * amount);
      primary.SetPosition(target);
    }
  }

  public boolean isAtPosition() {
    return primary.IsAtPosition();
  }

  public Position getPosition(){
    return targetPosition;
  }


  public void stop() {
    primary.Stop();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
