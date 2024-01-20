// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.CloseIntake;
import frc.robot.commands.CommandFlySwatter;
import frc.robot.commands.OpenIntake;
import frc.robot.commands.LEDManager;
import frc.robot.commands.SwerveDriveCommand;

import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.FlySwatter;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.tools.LEDs.BatteryLED;
import frc.robot.tools.LEDs.IAddressableLEDHelper;
import frc.robot.tools.LEDs.MultiFunctionLED;
import frc.robot.tools.LEDs.ShootLED;

public class RobotContainer {
  private SwerveDrive driveBase = new SwerveDrive(4, 2*Math.PI, "geared upright",  Constants.kinematics, Constants.config);
  private FlySwatter flySwatter = new FlySwatter();
  private Intake intake = new Intake();
  
  private final CommandXboxController driver = new CommandXboxController(0);
  
  public RobotContainer() {
    driveBase.enableDebugMode();
    driveBase.setHeadingAdjustment(180);
    configureBindings();
    driveBase.setDefaultCommand(new SwerveDriveCommand(this::getXSpeed, this::getYSpeed, this::getRotationSpeed, driveBase));
  }

  private IAddressableLEDHelper[] leds;
  private MultiFunctionLED multifucntion;
  private LEDManager ledManager;
  public void ConfigureLEDs() {
    multifucntion = new MultiFunctionLED(
      new ShootLED(15),
      new BatteryLED(15));

    leds = new IAddressableLEDHelper[]{multifucntion};

    ledManager = new LEDManager(1, leds);
    ledManager.schedule();
  }

  private void configureBindings() {
    driver.a()
      .onTrue( new OpenIntake(intake, flySwatter))
      .onFalse( new CloseIntake(intake, flySwatter));

    driver.y()
      .onTrue(new CommandFlySwatter(flySwatter, FlySwatter.Position.HIGH))
      .onFalse(new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW));

  }

  double getXSpeed() { 
    int pov = driver.getHID().getPOV();
    double finalX;

    if ( pov == 0 )
      finalX = -0.75;
    else if(pov == 180)
      finalX = 0.75;
    else if (Math.abs(driver.getLeftY()) <= 0.1)
      finalX = 0.0;
    else
      finalX = driver.getLeftY();
    
    return -finalX;
  }

  public double getYSpeed() { 
    int pov = driver.getHID().getPOV();

    double finalY;
    if ( pov == 270 || pov == 315 || pov == 225)
      finalY = -.5;
    else if(pov == 90 || pov == 45 || pov == 135)
      finalY = 0.5;
    else if (Math.abs(driver.getLeftX()) <= 0.1)
      finalY = 0.0;
    else
      finalY = driver.getLeftX();
    
    return -finalY; 
  } 
  
  public double getRotationSpeed() { 
    double finalRotation;

      finalRotation = driver.getRightX();

      if (Math.abs(finalRotation) < 0.1)
        finalRotation = 0.0;
    
    return finalRotation;
  }


  public Command getAutonomousCommand() {
    return new PathPlannerAuto("New Auto");
  }
}
