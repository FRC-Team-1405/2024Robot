// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.CloseIntake;
import frc.robot.commands.CommandFlySwatter;
import frc.robot.commands.IntakeNote;
import frc.robot.commands.OpenIntake;
import frc.robot.commands.OutputNote;
import frc.robot.commands.ShooterCommand;
import frc.robot.commands.LEDManager;
import frc.robot.commands.SwerveDriveCommand;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.FlySwatter;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.subsystems.Shooter.ShooterSpeed;
import frc.robot.tools.LEDs.BatteryLED;
import frc.robot.tools.LEDs.IAddressableLEDHelper;
import frc.robot.tools.LEDs.MultiFunctionLED;
import frc.robot.tools.LEDs.ShootLED;

public class RobotContainer {
  private SwerveDrive driveBase = new SwerveDrive(4, 2*Math.PI, "geared upright",  Constants.kinematics, Constants.config);
  private FlySwatter flySwatter = new FlySwatter();
  private Intake intake = new Intake();
  private Shooter shooter = new Shooter();
  
  private final CommandXboxController driver = new CommandXboxController(0);
  private final CommandXboxController operator = new CommandXboxController(1);
  
  public RobotContainer() {
    driveBase.enableDebugMode();
    driveBase.setHeadingAdjustment(180);
    configureBindings();
    configureShuffleboard();

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
    driver.rightBumper()
      .onTrue( new OpenIntake(intake, flySwatter))
      .onFalse( new CloseIntake(intake, flySwatter));

    driver.back().whileTrue( Commands.startEnd( () -> { SwerveDrive.useStopAngle(true);},
      () -> { SwerveDrive.useStopAngle(false);}));  
    driver.a().onTrue(new ShooterCommand(shooter, ShooterSpeed.AMP));
    driver.b().onTrue(new ShooterCommand(shooter, ShooterSpeed.SPEAKER));

    //if(operator.leftBumper().onTrue(Commands.startEnd(() -> {FlySwatter.climbingMode(true);}, () -> {FlySwatter.climbingMode(false);}, flySwatter)) && operator.rightBumper().onTrue(Commands.startEnd(() -> {FlySwatter.climbingMode(true);}, () -> {FlySwatter.climbingMode(false);}, flySwatter)))){
        //I Wrote this for the climbing i couldnt figure it out so i just commented it out
  

    operator.y()
      .onTrue(new CommandFlySwatter(flySwatter, FlySwatter.Position.HIGH))
      .onFalse(new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW));
    operator.back().onTrue( new InstantCommand( driveBase::resetGyro ) {
        public boolean runsWhenDisabled() {
          return true;
        }    
      });
  }

  private void configureShuffleboard(){
    SmartDashboard.putData("Intake", new IntakeNote(intake));
    SmartDashboard.putData("StopIntake", intake.run(() -> { intake.setSpeed(Intake.Speed.STOP); }));
    SmartDashboard.putData("Output", new OutputNote(intake));

    SmartDashboard.putData("Reset Preferences", new InstantCommand(Preferences::removeAll));
  }

  private void resetPrefs(){
    Preferences.removeAll();;
  }

  double getXSpeed() { 
    int pov = driver.getHID().getPOV();
    double finalX;
    if (Math.abs(driver.getLeftY()) <= 0.1)
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
