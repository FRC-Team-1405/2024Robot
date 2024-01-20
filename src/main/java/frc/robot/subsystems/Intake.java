// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.sensors.FusionTimeofFlight;

public class Intake extends SubsystemBase {

// Motor names and Id
  private TalonFX moterIntake = new TalonFX(1); 
  private TalonFX moterSpeed = new TalonFX(2);

  private static final double POSITION_ERROR_DELTA = 0.1;
  private Supplier<Double> intakePosition = moterIntake.getPosition().asSupplier();

  public enum Position { RAISED, LOWER } ;
  private Position activeTarget = Position.RAISED;

  public enum Speed { IN, OUT, STOP } ;
  private Speed activeSpeed = Speed.STOP;

  private Map<Position,Double> positionValue = new HashMap<Position,Double>() ;
  private Map<Speed,Double> speedValue = new HashMap<Speed,Double>();

  private FusionTimeofFlight lidar = new FusionTimeofFlight(Constants.CanBus.LIDAR);
  private double distance = 0.0;

public Intake() {
  Preferences.initDouble("Intake/Position/Raised",9);
  positionValue.put(Position.RAISED, Preferences.getDouble("Intake/Position/Raised",.9) );

  Preferences.initDouble("Intake/Position/Lower",.2); 
  positionValue.put(Position.LOWER, Preferences.getDouble("Intake/Position/Lower",.2) );

  Preferences.initDouble("Intake/Speed/Out", .5);
  speedValue.put(Speed.OUT, Preferences.getDouble("Intake/Speed/Out",.5) );

  Preferences.initDouble("Intake/Speed/In",-.4); 
  speedValue.put(Speed.IN, Preferences.getDouble("Intake/Speed/In",-.4) );

  Preferences.initDouble("Intake/Speed/Stop",0);  
  speedValue.put(Speed.STOP, Preferences.getDouble("Intake/Speed/Stop",.0) );
  }



  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    distance = lidar.Measure();
  }

  public void setPosition(Position target) {
    activeTarget = target;
    moterIntake.setControl( new MotionMagicVoltage(positionValue.get(activeTarget)) );
  }
  public void setSpeed(Speed target) {
    activeSpeed = target;
    moterSpeed.setControl( new MotionMagicVoltage(speedValue.get(activeSpeed)) );
  } 

  public boolean isAtPosition(){
    return Math.abs( positionValue.get(activeTarget) - intakePosition.get())  < POSITION_ERROR_DELTA  ;
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
  public boolean hasNote() {
    return distance < Constants.Intake.DISTANCE_TOLERANCE;
  }
}
