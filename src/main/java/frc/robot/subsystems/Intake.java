// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.Map;

import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

  private TalonFX moterIntake = new TalonFX(1); 
  private TalonFX moterSpeed = new TalonFX(2);
// Motor names and Id

  public enum Position { RAISED, LOWER } ;
  private Position activeTarget = Position.RAISED;

  public enum Speed { IN, OUT, STOP } ;
  private Speed activeSpeed = Speed.STOP;

  private Map<Position,Double> positionValue = Map.of(
      Position.RAISED, .9,
      Position.LOWER, .2
  );
  private Map<Speed,Double> speedValue = Map.of(
    Speed.OUT, 0.5,
    Speed.IN, -0.4,
    Speed.STOP, .0
  );

public Intake() {
  if ( !Preferences.containsKey("Intake/Position/In") ){
      Preferences.setDouble("Intake/Position/In", -0.4);
  }
  intakeValues.put(Intake.IN, Preferences.getDouble("Intake/Position/Out",0) );

}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void setPosition(Position target) {
    activeTarget = target;
    moterIntake.setControl( new MotionMagicVoltage(positionValue.get(activeTarget)) );
  }
  public void setSpeed(Speed target) {
    activeSpeed = target;
    moterSpeed.setControl( new MotionMagicVoltage(speedValue.get(activeSpeed)) );
  }
  public void stop() {
    stopIntake();
    stopSpeed();
  }
  // Stops moters
  public void stopIntake() {
    moterIntake.set(0);   
  }
  public void stopSpeed() {
    moterSpeed.set(0);
  }

}
