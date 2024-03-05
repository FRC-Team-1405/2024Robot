// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utils;

import java.util.function.BooleanSupplier;

/** Add your docs here. */
public class ToggleFilter implements BooleanSupplier{
    public enum Type { Rising, Falling };

    private BooleanSupplier input;
    private Type type;
    private boolean lastValue;
    public ToggleFilter(BooleanSupplier input){
        this(input, Type.Rising);
    }
    public ToggleFilter(BooleanSupplier input, Type type){
        this.input = input;
        this.type = type;
        this.lastValue = input.getAsBoolean();
    }

    public boolean getAsBoolean() {
        boolean currentValue = input.getAsBoolean();
        boolean result = (type == Type.Rising)
                       ? (lastValue == false && currentValue == true )
                       : (lastValue == true  && currentValue == false);
        return result;
    }
}
