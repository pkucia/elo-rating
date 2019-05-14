package com.elorating.match;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by pokor on 16.05.2017.
 */
@Component
public class QueueListScheduler {

    private static final Logger logger = LoggerFactory.getLogger(QueueListScheduler.class);

    private final MatchService matchService;

    @Autowired
    public QueueListScheduler(MatchService matchService) {
        this.matchService = matchService;
    }

    @Scheduled(cron = "0 0 23 * * *")
    public void removeNotPlayedMatches() {
        logger.info("Remove not finished matches: start");
        List<Match> matches = matchService.findByCompletedIsFalse();
        for (Match match : matches) {
            matchService.delete(match.getId());
        }
        logger.info("Queues scheduler: stop");
    }
}
