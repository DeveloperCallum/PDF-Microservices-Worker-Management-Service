package com.willcocks.callum.workermanagementservice.util;

import com.willcocks.callum.workermanagementservice.listener.OnSendCallbackForExtraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import util.Env;
@Service
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(OnSendCallbackForExtraction.class);
    public static final int PAGES_PER_JOB = Env.getEnvOrDefault("PAGES_PER_JOB", Integer::parseInt, 100);

    static{
        logger.info("Initialising Configuration...");
        logger.info("PAGES_PER_THREAD: " + Configuration.PAGES_PER_JOB);
    }
}

