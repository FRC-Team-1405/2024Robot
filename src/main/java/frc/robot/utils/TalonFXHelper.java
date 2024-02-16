// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utils;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.ReverseLimitValue;

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
    private StatusSignal<ReverseLimitValue> ReverseLimit;
    private double Accuracy;
    private double Target = Double.MAX_VALUE;

    private MotionMagicVoltage MagicSetPosition = new MotionMagicVoltage(0);

    public TalonFXHelper(int motorID, double accuracy ){
        Motor = new TalonFX(motorID);
        Accuracy = accuracy;

        ReverseLimit = Motor.getReverseLimit();  
    }
 
    public void Stop() {
        Motor.set(0);
        MotorState = State.STOPPED;
    }

    public void Zeroise(){
        MotorState = State.ZEROISING;
        Motor.set(-0.05);
    }

    public void SetPosition(double position) {
        Target = position;
        if(MotorState == State.INIT){
            Zeroise();
        }
        else if(MotorState != State.ZEROISING) {
            Motor.setControl(MagicSetPosition.withPosition(Target));
            MotorState = State.RUNNING;
        }
    }
 
    public void Execute() {
        if(MotorState == State.ZEROISING && ReverseLimit.getValue() == ReverseLimitValue.Open){
            Motor.setPosition(0);
            MotorState = State.STOPPED;
            if(Target != Double.MAX_VALUE){
                SetPosition(Target);
            }
        }
    }
  
    public boolean IsAtPosition() {  
        return Math.abs(Target - Motor.getPosition().getValue()) < Accuracy;
    }

}
