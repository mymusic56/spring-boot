package com.nineton.recorder.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class TestTasks {
	private static final Log logger = LogFactory.getLog(TestTasks.class);

	@Scheduled(initialDelay = 1000, fixedDelay = 2000)
	@Async
	public void fileDownload() {
		for (int i = 0; i < 10; i++) {

			try {
				System.out.println(Thread.currentThread().getName()+"++++++++++++ download sleep["+i+"]+++++++++++++");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(" .+++++++++++++. download ++++++++++");
//		logger.info(" .+++++++++++++. download ++++++++++");
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 2000)
	@Async
	public void fileUpload() {
		for (int i = 0; i < 5; i++) {

			try {
				System.out.println(Thread.currentThread().getName()+" ..............upload. sleep ["+i+"]...............");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(" ............... upload ...............");
//		logger.info(" ............... upload ...............");
	}

	@Scheduled(initialDelay = 3000, fixedDelay = 2000)
	@Async
	public void fileResult() {
		try {
			System.out.println(" -----------  result ...sleep----------- ");
//			logger.info(" -----------  result ...sleep----------- ");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(" ----------- result -----------");
//		logger.info(" ----------- result -----------");
	}
}
