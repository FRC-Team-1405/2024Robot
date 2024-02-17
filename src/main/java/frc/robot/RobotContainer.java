// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.ClimbCommand;
import frc.robot.commands.CloseIntake;
import frc.robot.commands.CommandFlySwatter;
import frc.robot.commands.ControlIntake;
import frc.robot.commands.IntakeNote;
import frc.robot.commands.OpenIntake;
import frc.robot.commands.OutputNote;
import frc.robot.commands.ShootNoteAmp;
import frc.robot.commands.ShootNoteSpeaker;
import frc.robot.commands.ShooterCommand;
import frc.robot.commands.LEDManager;
import frc.robot.commands.SwerveDriveCommand;

import java.util.concurrent.locks.Condition;

import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
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

  private enum Target { Speaker, Amp };
  private Target currentTarget = Target.Speaker;

  private void configureBindings() {
    // control intake deploy/retract
    driver.rightBumper()
          .onTrue( new ConditionalCommand(new OpenIntake(intake, flySwatter), 
                                          new CloseIntake(intake, flySwatter), 
                                          () -> { return intake.getPosition() == Intake.Position.LOWER; }) );

    
    driver.back().whileTrue( Commands.startEnd( () -> { SwerveDrive.useStopAngle(true);},
                                                () -> { SwerveDrive.useStopAngle(false);}));  

     driver.leftBumper()
           .onTrue( new ConditionalCommand(new ShootNoteAmp(intake, shooter, flySwatter), 
                                           new ShootNoteSpeaker( intake, shooter), 
                                           () -> { return currentTarget == Target.Amp; }) );

    operator.a().onTrue( new InstantCommand( () -> { currentTarget = Target.Speaker; } ));

    operator.b().onTrue( new SequentialCommandGroup( 
                              new InstantCommand( () -> { currentTarget = Target.Amp; } ),
                              new CommandFlySwatter(flySwatter, FlySwatter.Position.HIGH)));

    operator.x().onTrue( new SequentialCommandGroup(
                              new CommandFlySwatter(flySwatter, FlySwatter.Position.MEDIUM),
                              new ControlIntake(intake, Intake.Position.EJECT),
                              new OutputNote(intake),
                              new CloseIntake(intake, flySwatter)) );


    operator.y()
      .onTrue(new ConditionalCommand(new CommandFlySwatter(flySwatter, FlySwatter.Position.HIGH), 
                                     new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW), 
                                     () -> { return flySwatter.getPosition() == FlySwatter.Position.LOW; }));

    operator.back().onTrue( new InstantCommand( driveBase::resetGyro ) {
        public boolean runsWhenDisabled() {
          return true;
        }    
      });

    operator.leftBumper()
      .and(operator.rightBumper())
      .onTrue( new SequentialCommandGroup( 
                  new CommandFlySwatter(flySwatter, FlySwatter.Position.CLIMB),
                  new ClimbCommand(flySwatter, () -> { return operator.getRightTriggerAxis() - operator.getLeftTriggerAxis() ; } )
                  ) );
  }

  private void configureShuffleboard(){
    Command command;
    
    command = new IntakeNote(intake);
    command.setName("Intake");
    SmartDashboard.putData("Intake/Input", command);

    command = intake.runOnce(() -> { intake.setSpeed(Intake.Speed.STOP); });
    command.setName("Stop");
    SmartDashboard.putData("Intake/Stop", command);

    command = new OutputNote(intake);
    command.setName("Output");
    SmartDashboard.putData("Intake/Output", command);

    command = new InstantCommand(Preferences::removeAll).ignoringDisable(true);
    command.setName("Reset Prefs");
    SmartDashboard.putData("Preferences/Reset", command);
    
    command = new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW);
    command.setName("Low");
    SmartDashboard.putData("FlySwatter/Low", command);

    command = new CommandFlySwatter(flySwatter, FlySwatter.Position.MEDIUM);
    command.setName("Medium");
    SmartDashboard.putData("Flyswatter/Medium", command);

    command = new CommandFlySwatter(flySwatter, FlySwatter.Position.HIGH);
    command.setName("High");
    SmartDashboard.putData("Flyswatter/High", command);

    command = flySwatter.runOnce(() -> { flySwatter.stop(); });
    command.setName("Stop");
    SmartDashboard.putData("Flyswatter/Stop", command);

    command = new ControlIntake(intake, Intake.Position.LOWER);
    command.setName("Lower");
    SmartDashboard.putData("Intake/Position/Lower", command);

    command = new ControlIntake(intake, Intake.Position.RAISED);
    command.setName("Raised");
    SmartDashboard.putData("Intake/Position/Raised", command);

    command = intake.runOnce(() -> { intake.stop(); });
    command.setName("Stop");
    SmartDashboard.putData("Intake/Position/Stop", command);

    SmartDashboard.putNumber("FlySwatter/Climb/Adjust", 0.0);
    command = new SequentialCommandGroup( 
                  new CommandFlySwatter(flySwatter, FlySwatter.Position.CLIMB),
                  new ClimbCommand(flySwatter, () -> { return SmartDashboard.getNumber("FlySwatter/ ", 0.0) ; } )
                  );
    command.setName("Climb");
    SmartDashboard.putData("FlySwatter/Climb/Active", command);

    command = new SequentialCommandGroup(
                  new ControlIntake(intake, Intake.Position.LOWER),
                  new IntakeNote(intake),
                  new ControlIntake(intake, Intake.Position.RAISED)
                  );
    command.setName("Pick Up Note");
    SmartDashboard.putData("Intake/PickUpNote", command);
     
    command = new ShootNoteSpeaker(intake, shooter);
    command.setName("Shoot Speaker");
    SmartDashboard.putData("Shooter/Speaker", command);
    
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
