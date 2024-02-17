// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.Supplier;

import com.ctre.phoenix6.controls.MotionMagicDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.sensors.FusionTimeofFlight;

public class Intake extends SubsystemBase {

// Motor names and Id
  private TalonFX moterIntake = new TalonFX(Constants.CanBus.MOTER_INTAKE); 
  private TalonFX moterSpeed = new TalonFX(Constants.CanBus.MOTER_SPEED);

  private static final double POSITION_ERROR_DELTA = 0.1;


  public enum Position { 
    RAISED(2),
    LOWER(34);

    private Position(double value){
      Preferences.initDouble("Intake/Position/"+this.name(), value);
      this.value = Preferences.getDouble("Intake/Position/"+this.name(), value);
    }

    private double value;
    public double getValue(){
      return value;
    }
  } 

  private Position activeTarget = Position.RAISED;

  private Supplier<Double> intakePosition = moterIntake.getPosition().asSupplier();//  private MotionMagicVoltage intakeSetPosition = new MotionMagicVoltage(activeTarget.getValue());
  private MotionMagicDutyCycle intakeSetPosition = new MotionMagicDutyCycle(activeTarget.getValue());

  public enum Speed { 
    OUT(5),
    IN(-0.25),
    STOP(0);

    private Speed(double value){
      Preferences.initDouble("Intake/Speed/"+this.name(), value);
      this.value = Preferences.getDouble("Intake/Speed/"+this.name(), value);
    }

    private double value;
    public double getValue(){
      return value;
    }
  }
  private Speed activeSpeed = Speed.STOP;

  private FusionTimeofFlight lidar = new FusionTimeofFlight(Constants.CanBus.LIDAR);
  private double distance = 0.0;
  private boolean hasNote = false;

  public Intake() {
  }
  
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    distance = lidar.Measure();
  }

  public void setPosition(Position target) {
    activeTarget = target;
    moterIntake.setControl( intakeSetPosition.withPosition(activeTarget.getValue()) );
  }

  public void setSpeed(Speed target) {
    activeSpeed = target;
    moterSpeed.set( activeSpeed.getValue());
  } 

  public boolean isAtPosition(){
    return Math.abs(activeTarget.getValue() - intakePosition.get())  < POSITION_ERROR_DELTA  ;
  }

  public Position getPosition() {
    return activeTarget;
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
    boolean noteClose = distance < Constants.Intake.DISTANCE_IN_TOLERANCE;
    boolean noteFar = distance > Constants.Intake.DISTANCE_OUT_TOLERANCE;
    if (hasNote && noteFar ) {
      hasNote = false;
    } else if (!hasNote && noteClose) {
      hasNote = true;
    }
    return hasNote;
  }
}
