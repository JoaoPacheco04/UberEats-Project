package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.config.AppConstants;
import com.eduscrum.upt.Ubereats.entity.Sprint;
import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import com.eduscrum.upt.Ubereats.repository.SprintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for scheduled tasks in the EduScrum platform.
 * Handles automated operations like checking for overdue sprints.
 *
 * @author
 * @version 1.0 (2025-12-10)
 */
@Service
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private final SprintRepository sprintRepository;

    /**
     * Constructs a new SchedulerService with required dependencies.
     *
     * @param sprintRepository Repository for sprint data access
     */
    public SchedulerService(SprintRepository sprintRepository) {
        this.sprintRepository = sprintRepository;
    }

    /**
     * Daily scheduled task to check for overdue sprints.
     * Runs at midnight and logs any sprints that have passed their end date
     * while still being in progress.
     */
    @Scheduled(cron = AppConstants.CRON_DAILY_MIDNIGHT)
    @Transactional
    public void checkOverdueSprints() {
        logger.info("Running daily scheduled task: checkOverdueSprints");

        LocalDate today = LocalDate.now();
        List<Sprint> activeSprints = sprintRepository.findByStatus(SprintStatus.IN_PROGRESS);

        for (Sprint sprint : activeSprints) {
            if (sprint.getEndDate().isBefore(today)) {
                logger.warn("Sprint overdue detected: ID={}, Name={}, EndDate={}",
                        sprint.getId(), sprint.getName(), sprint.getEndDate());
            }
        }
    }
}
