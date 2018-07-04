package com.nineton.recorder.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class VoiceToWordsTasks {
	private static final Log logger = LogFactory.getLog(VoiceToWordsTasks.class);

	@Scheduled(initialDelay = 1000, fixedDelay = 2000)
	@Async
	public void fileDownload() {
		logger.info(" .+++++++++++++. download ++++++++++");
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 2000)
	@Async
	public void fileUpload() {
		logger.info(" ............... upload ...............");
	}

	@Scheduled(initialDelay = 3000, fixedDelay = 2000)
	@Async
	public void fileResult() {
		logger.info(" ----------- result -----------");
	}
}
