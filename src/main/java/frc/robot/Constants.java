package frc.robot;

import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;

public final class Constants {
     public static final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
    new Translation2d(Units.inchesToMeters(-10), Units.inchesToMeters(-7)), // Front Left
    new Translation2d(Units.inchesToMeters(-10), Units.inchesToMeters(7)), // Front Right
    new Translation2d(Units.inchesToMeters(10), Units.inchesToMeters(-15)), // Back Left
    new Translation2d(Units.inchesToMeters(10), Units.inchesToMeters(15))); // Back Right
     
     public static final HolonomicPathFollowerConfig config = new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your Constants class
    new PIDConstants(2.5, .75, 0.0), // Translation PID constants
    //new PIDConstants(0.0, 0.0, 0.0), // Translation PID constants
    new PIDConstants(10, 0.0, 0.0), // Rotation PID constants.
    4.0, // Max module speed, in m/s
    Units.inchesToMeters(16), // Drive base radius in meters. Distance from robot center to furthest module.
    new ReplanningConfig() // Default path replanning config. See the API for the options here
    );
}
