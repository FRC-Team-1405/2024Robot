// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.tools.LEDs;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

/** Add your docs here. */
public class ShootLED extends AddressableLEDHelper {
    private int offset;

    public ShootLED(int numLEDs) {
        super(numLEDs);
    }

    @Override
    public void initialize(int offset) {
        this.offset = offset;
    }

    private int callCount = 0;
    private final int delayCount = 1;
    @Override
    public AddressableLEDBuffer writeData(AddressableLEDBuffer buffer) {
        if(callCount >= numLEDs * delayCount)
            callCount = 0;
        int racerLEDPos = callCount / delayCount;
        
        for (int i = offset; i < numLEDs + offset; i++) {
            buffer.setLED(i, racerLEDPos == i ? Color.kYellow : Color.kBlack);
        }

        callCount++;
        return buffer;
    }
}
