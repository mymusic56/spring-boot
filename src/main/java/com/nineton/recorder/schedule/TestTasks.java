package com.nineton.recorder.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestTasks {
	private static final Log logger = LogFactory.getLog(TestTasks.class);
	
    @Scheduled(initialDelay=1000, fixedDelay = 5000)
    public void fileDownload(){
    	
    	try {
    		logger.info(" +++++++++++++++++ download ..sleep.++++++++++");
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	logger.info(" .+++++++++++++. download ++++++++++");
    }
    
    @Scheduled(initialDelay=2000, fixedDelay = 5000)
    public void fileUpload() {
    	try {
    		logger.info(" ............... upload ...sleep............");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	logger.info(" ............... upload ...............");
    }
    
    @Scheduled(initialDelay=3000, fixedDelay = 5000)
    public void fileResult(){
    	try {
    		logger.info(" -----------  result ...sleep----------- ");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info(" ----------- result -----------");
    }
}
