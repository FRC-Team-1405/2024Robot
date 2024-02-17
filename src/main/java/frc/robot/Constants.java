package frc.robot;
    
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Preferences;

public final class Constants {
    public final class Limelight {
        public static class Pipeline {
            public static final byte AMP = 1;
            public static final byte SPEAKER = 2;
        }
    }

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

    public static class CanBus {
        public final static int DRIVEFRONTLEFT = 1;
        public final static int DRIVEFRONTRIGHT = 2;
        public final static int DRIVEBACKLEFT = 3;
        public final static int DRIVEBACKRIGHT = 4;
    
        public final static int ROTATIONFRONTLEFT = 21;
        public final static int ROTATIONFRONTRIGHT = 22;
        public final static int ROTATIONBACKLEFT = 23;
        public final static int ROTATIONBACKRIGHT = 24;
    
        public final static int ENCODERFRONTLEFT = 31;
        public final static int ENCODERFRONTRIGHT = 32;
        public final static int ENCODERBACKLEFT = 33;
        public final static int ENCODERBACKRIGHT = 34;

        public final static int SHOOTER_PRIMARY = 5;
        public final static int SHOOTER_SECONDARY = 6;
        public final static int FLYSWATTER_PRIMARY = 7;
        public final static int FLYSWATTER_SECONDARY = 8;
        public final static int MOTER_INTAKE = 9;
        public final static int MOTER_SPEED = 10;

        public final static int LIDAR = 17;
    }

    public final class Intake {
        static {
            Preferences.initDouble("Intake/Lidar distance in tolerance", 100.0);
            DISTANCE_IN_TOLERANCE = Preferences.getDouble("Intake/Lidar distance in tolerance", 40.0);

            Preferences.initDouble("Intake/Lidar distance out tolerance", 150.0);
            DISTANCE_OUT_TOLERANCE = Preferences.getDouble("Intake/Lidar distance out tolerance", 40.0);
        }
        public final static double DISTANCE_IN_TOLERANCE;  
        public final static double DISTANCE_OUT_TOLERANCE;  
        
    }

    public final class BatteryMonitor {
        public final static double MAXVOLTAGE = 12;
        public final static double MINVOLTAGE = 9;
        // LEDCOUNT has to be a multiple of 3
        public final static int LEDCOUNT = 15;
    
        public final static double BRIGHTNESS = 0.2;
      };

}
