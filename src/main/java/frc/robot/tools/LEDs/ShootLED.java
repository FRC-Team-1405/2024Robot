// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.tools.LEDs;

import java.util.Optional;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;

/** Add your docs here. */
public class ShootLED extends AddressableLEDHelper {
    private int offset;

    private Color shootColor = null;
    private Color getShootColor() {
        if (shootColor == null) {
            Optional<Alliance> alliance = DriverStation.getAlliance() ;
            if (alliance.isPresent()) {
                shootColor = alliance.get() == Alliance.Red ? Color.kRed : Color.kBlue;
            } else {
                return Color.kGreen;
            }
        }
        return shootColor;
    }
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
            buffer.setLED(i, racerLEDPos == i ? getShootColor() : Color.kBlack);
        }

        callCount++;
        return buffer;
    }
}
