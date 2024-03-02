// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.ClearIntake;
import frc.robot.commands.ClearShooter;
import frc.robot.commands.ClimbCommand;
import frc.robot.commands.CloseIntake;
import frc.robot.commands.CommandFlySwatter;
import frc.robot.commands.ControlIntake;
import frc.robot.commands.IntakeNote;
import frc.robot.commands.OpenIntake;
import frc.robot.commands.OutputNote;
import frc.robot.commands.Rumble;
import frc.robot.commands.ShootNoteAmp;
import frc.robot.commands.ShootNoteSpeaker;
import frc.robot.commands.LEDManager;
import frc.robot.commands.LobNote;
import frc.robot.commands.SwerveDriveCommand;
import frc.robot.commands.SwerveDriveToNote;
import frc.robot.sensors.Vision;

import java.util.Optional;

import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.FlySwatter;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.tools.LEDs.BatteryLED;
import frc.robot.tools.LEDs.IAddressableLEDHelper;
import frc.robot.tools.LEDs.MultiFunctionLED;
import frc.robot.tools.LEDs.ShootLED;

public class RobotContainer {
  private SwerveDrive driveBase = new SwerveDrive(10, 2*Math.PI, "geared upright",  Constants.kinematics, Constants.config);
  private FlySwatter flySwatter = new FlySwatter();
  private Intake intake = new Intake();
  private Shooter shooter = new Shooter();

  private Vision vision = new Vision( driveBase::getPose );
  private Optional<Alliance> alliance = DriverStation.getAlliance();
  
  private final CommandXboxController driver = new CommandXboxController(0);
  private final CommandXboxController operator = new CommandXboxController(1);

  private static final SendableChooser<String> autos = new SendableChooser<>();
  
  public RobotContainer() {
//    driveBase.enableDebugMode();
   configureBindings();
    configureShuffleboard();
    configureLEDs();           
    configurePathPlanner();
    configureShuffleboard();

    vision.setSpeakerStart();

    driveBase.setDefaultCommand(new SwerveDriveCommand(this::getXSpeed, this::getYSpeed, this::getRotationSpeed, this::getSlideValue, driveBase));
  }

  public void teleopInit() {
    alliance = DriverStation.getAlliance();
  }

  private IAddressableLEDHelper[] leds;
  private MultiFunctionLED multifucntion;
  private LEDManager ledManager;
  public void configureLEDs() {
    multifucntion = new MultiFunctionLED(
      new BatteryLED(15),
      new ShootLED(15));

    leds = new IAddressableLEDHelper[]{multifucntion};

    ledManager = new LEDManager(0, leds);
    ledManager.schedule();
  }

  private enum Target { Speaker, Amp };
  private Target currentTarget = Target.Speaker;

  private void configureBindings() {
    // control intake deploy/retract
    driver.rightBumper()
          .onTrue( new ConditionalCommand(new OpenIntake(intake, flySwatter), 
                                          new CloseIntake(intake, flySwatter), 
                                          () -> { return intake.getPosition() == Intake.Position.RETRACTED; }) );

    
//    driver.back().whileTrue( Commands.startEnd( () -> { SwerveDrive.useStopAngle(true);},
//                                                () -> { SwerveDrive.useStopAngle(false);}));  

     driver.leftBumper()
           .onTrue( new ConditionalCommand(new ShootNoteAmp(intake, shooter, flySwatter), 
                                           new ShootNoteSpeaker( intake, shooter), 
                                           () -> { return currentTarget == Target.Amp; }) );

    operator.a().onTrue( new InstantCommand( () -> {
       currentTarget = Target.Speaker; 
       shooter.setWheelSpeed(Shooter.ShooterSpeed.SPEAKER);
      } ));

    operator.b()
            .and( () -> flySwatter.getCurrentCommand() == null)
            .onTrue( new SequentialCommandGroup( 
                              new InstantCommand( () -> { 
                                currentTarget = Target.Amp; 
                                shooter.setWheelSpeed(Shooter.ShooterSpeed.AMP);
                              } ),
                              new CommandFlySwatter(flySwatter, FlySwatter.Position.HIGH)) );

    operator.x().onTrue( new ParallelCommandGroup( 
                            new ClearShooter(shooter),
                            new ClearIntake(flySwatter, intake)));


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
                  new ClimbCommand(flySwatter, () -> { return -operator.getLeftY(); } )
                  ) );

    Trigger driverRumbleTrigger = new Trigger( () -> intake.hasNote() || shooter.atSpeed() );
    Command rumbleDriver = new Rumble(driver, 15, 5, 2);
    driverRumbleTrigger.onTrue( rumbleDriver )
                       .onFalse( rumbleDriver );

    Trigger prepReady = new Trigger( () -> intake.hasNote() && intake.getCurrentCommand() == null);
    Command rumbleOperator = new Rumble(operator, 15, 5, 2);
    prepReady.onTrue( rumbleOperator )
             .onFalse( rumbleOperator );

    driver.rightStick().whileTrue( new SwerveDriveToNote(this::getXSpeed, this::getYSpeed, this::getRotationSpeed, vision, driveBase) );
  } 
  
  private void configureShuffleboard(){
    Command command;
    autos.setDefaultOption("Shuffle1", "Shuffle1");
    autos.addOption("Shuffle2", "Shuffle2");
    SmartDashboard.putData("Auto/Autos", autos);

    command = new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW);
    command.setName("Flyswatter");
    SmartDashboard.putData("Flyswatter/Low", command);
    
    command = new CommandFlySwatter(flySwatter, FlySwatter.Position.MEDIUM);
    command.setName("Flyswatter");
    SmartDashboard.putData("Flyswatter/Medium", command);

    command = new CommandFlySwatter(flySwatter, FlySwatter.Position.HIGH);
    command.setName("Flyswatter");
    SmartDashboard.putData("Flyswatter/High", command);

    command = new IntakeNote(intake);
    command.setName("Intake");
    SmartDashboard.putData("Intake/Input", command);

    command = intake.runOnce(() -> { intake.setSpeed(Intake.Speed.STOP); });
    command.setName("Stop");
    SmartDashboard.putData("Intake/Stop", command);

    command = new OutputNote(intake);
    command.setName("Output");
    SmartDashboard.putData("Intake/Output", command);

    // command = new InstantCommand(Preferences::removeAll).ignoringDisable(true);
    // command.setName("Reset Prefs");
    // SmartDashboard.putData("Preferences/Reset", command);
    
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

    command = new ControlIntake(intake, Intake.Position.EXTENDED);
    command.setName("Lower");
    SmartDashboard.putData("Intake/Position/Lower", command);

    command = new ControlIntake(intake, Intake.Position.RETRACTED);
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
                  new ControlIntake(intake, Intake.Position.EXTENDED),
                  new IntakeNote(intake),
                  new ControlIntake(intake, Intake.Position.RETRACTED)
                  );
    command.setName("Pick Up Note");
    SmartDashboard.putData("Intake/PickUpNote", command);
     
    command = new ShootNoteSpeaker(intake, shooter);
    command.setName("Shoot Speaker");
    SmartDashboard.putData("Shooter/Speaker", command);
    
    command = new Rumble(driver, 15, 5, 2).ignoringDisable(true);
    command.setName("Rumble Driver");
    SmartDashboard.putData("Rumble Driver", command);
  }



  void configurePathPlanner() {
    NamedCommands.registerCommand("Pickup Note", 
        new SequentialCommandGroup( new ControlIntake(intake, Intake.Position.EXTENDED),
                                    new IntakeNote(intake),
                                    new ControlIntake(intake, Intake.Position.RETRACTED))
    );
    NamedCommands.registerCommand("Lower Intake", new ControlIntake(intake, Intake.Position.EXTENDED));
    NamedCommands.registerCommand("Raise Intake", new ControlIntake(intake, Intake.Position.RETRACTED));
    NamedCommands.registerCommand("Shoot Speaker", new ShootNoteSpeaker(intake, shooter));
    NamedCommands.registerCommand("Lob Note", new LobNote(intake, shooter));
    NamedCommands.registerCommand("Shoot Amp", new ShootNoteAmp(intake, shooter, flySwatter));
    NamedCommands.registerCommand("Raise Flyswatter", new CommandFlySwatter(flySwatter, FlySwatter.Position.MEDIUM));
    NamedCommands.registerCommand("Lower Flyswatter", new CommandFlySwatter(flySwatter, FlySwatter.Position.LOW));
    NamedCommands.registerCommand("Eject Note", 
        new SequentialCommandGroup( new ControlIntake(intake, Intake.Position.EXTENDED),
                                    new IntakeNote(intake),
                                    new ControlIntake(intake, Intake.Position.EJECT),
                                    new OutputNote(intake),
                                    new ControlIntake(intake, Intake.Position.RETRACTED))
    );
  }

  double getXSpeed() { 
    double speedMultiplication = 0.6;
    speedMultiplication += (driver.getLeftTriggerAxis() - driver.getRightTriggerAxis()) * 0.4;

    double finalX;
    if (Math.abs(driver.getLeftY()) <= 0.1)
      finalX = 0.0;
    else
      finalX = driver.getLeftY();
    
    return -finalX * speedMultiplication;
  }

  public double getYSpeed() { 
    double speedMultiplication = 0.6;
    speedMultiplication += (driver.getLeftTriggerAxis() - driver.getRightTriggerAxis()) * 0.4;
    
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
    
    return -finalY * speedMultiplication; 
  } 
  
  public double getRotationSpeed() { 
    double finalRotation =  driver.getRightX();

    if (Math.abs(finalRotation) < 0.15)
        finalRotation = 0.0;
    
    return finalRotation;
  }

  public double getVisionRotationSpeed() {
    if (vision.hasTarget()){
      return vision.getVisionAngleToTarget();
    } else {
      return vision.getPoseAngleToTarget();
    }
  }

  public double getSlideValue() {
    int pov = driver.getHID().getPOV();
    if (pov == 45 || pov == 90 || pov == 135) {
      return 0.4 ;
    } else if (pov == 225 || pov == 270 || pov == 315) {
      return -0.4 ;
    }

    return 0.0;
  }
  public Command getAutonomousCommand() {
    // String auto = autos.getSelected();
    // System.out.println(auto);
    // if(auto == "Shuffle1") {
    //   System.out.println("###################### SHUFFLE 1 SELECTED");
    //   return new PathPlannerAuto("1_Shuffle");
    // } else if(auto == "Shuffle2") {
    //   System.out.println("###################### SHUFFLE 2 SELECTED");
    //   return new PathPlannerAuto("2_Shuffle");
    // } else {
    //   System.out.println("###################### SOMETHING WENT WRONG");
    //   return new PathPlannerAuto("today_auto");
    // }
    return new PathPlannerAuto("Feeder_H_G");
  }
}
  