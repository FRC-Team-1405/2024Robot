package frc.robot.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import badlog.lib.BadLog;

/** Add your docs here. */
public class LogManager {
    private static List<Logger> logSources = Collections.synchronizedList(new ArrayList<Logger>());
    private static BadLog logger ;

    public static void register(Logger logger) {
        logSources.add(logger);
    }

    public static void init(String filename) {
        try {
            Files.deleteIfExists( Paths.get(filename) );
        } catch (Exception ex) {
            System.out.println(ex);
        }

        logger = BadLog.init(filename);

        // add all subscribed topics
        logSources.forEach( src -> src.initLogger() );

        logger.finishInitialization();
    }

    public static void logEvents() {
        logSources.forEach( src -> src.logEvents() );
    }

}
