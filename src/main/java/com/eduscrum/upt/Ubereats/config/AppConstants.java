package com.eduscrum.upt.Ubereats.config;

/**
 * Application-wide constants for EduScrum platform.
 *
 * @author Joao
 * @author Ana
 * @version 0.3.0
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // Story Points
    public static final int MIN_STORY_POINTS_FOR_CONSISTENCY = 3;
    public static final double CONSISTENCY_RATIO = 0.75;

    // Roles
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";

    // Scheduler
    public static final String CRON_DAILY_MIDNIGHT = "0 0 0 * * ?";
}
