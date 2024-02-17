// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utils;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicDutyCycle;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.ReverseLimitValue;

import frc.robot.Constants;

/** Add your docs here. */
public class TalonFXHelper {
    public enum State{
        INIT,
        ZEROISING,
        STOPPED,
        RUNNING,
    };

    private State MotorState = State.INIT;
    private TalonFX Motor;
    private TalonFX slaveMotor = null;
    private StatusSignal<ReverseLimitValue> ReverseLimit;
    private double Accuracy;
    private double Target = Double.MAX_VALUE;

    private MotionMagicDutyCycle MagicSetPosition = new MotionMagicDutyCycle(0.0);

    public TalonFXHelper(int motorID, double accuracy ){
        Motor = new TalonFX(motorID);
        Accuracy = accuracy;

        ReverseLimit = Motor.getReverseLimit();  
    }

    public TalonFXHelper(int motorID, int slaveMotorID, double accuracy ){
        this(motorID, accuracy);

        slaveMotor = new TalonFX(slaveMotorID);
        slaveMotor.setControl(new Follower(motorID, false));
    }
 
    public void Stop() {
        // always stop motor
        Motor.set(0);

        switch(MotorState) {
            case INIT:
            case ZEROISING:
                MotorState = State.INIT;
                break;
            case STOPPED:
            case RUNNING:
                MotorState = State.STOPPED;
        }
    }

    public void Zeroise(){
        // always Zeroise when requested
        MotorState = State.ZEROISING;
        Motor.set(-0.1);
    }

    public void SetPosition(double position) {
        Target = position;
        switch(MotorState) {
            case INIT:
                Zeroise();
                break;
            case ZEROISING:
                break;
            case STOPPED:
            case RUNNING:
                Motor.setControl(MagicSetPosition.withPosition(Target));
                MotorState = State.RUNNING;
                break;
        }
    }
 
    public void Execute() {
        switch (MotorState) {
            case INIT:
                break;
            case ZEROISING:
                ReverseLimit.refresh();
                boolean atLowerLimit = ReverseLimit.getValue() == ReverseLimitValue.Open;
                if (atLowerLimit) {
                    Motor.setPosition(0);
                    MotorState = State.STOPPED;
                    Stop();
                    if(Target != Double.MAX_VALUE){
                        SetPosition(Target);
                    }
                }
                break;
            case STOPPED:
            case RUNNING:
                break;
        }
    }
  
    public boolean IsAtPosition() {  
        boolean atPosition = false;
        switch (MotorState) {
            case INIT:
            case ZEROISING:
            case STOPPED:
                atPosition = true;
                return false;
            case RUNNING:
                atPosition = Math.abs(Target - Motor.getPosition().getValue()) < Accuracy;
        }
        return atPosition;
    }

}
