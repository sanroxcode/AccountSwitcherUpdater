package com.sanroxcode.accountswitcher.updater.main;

import java.io.IOException;

import com.sanroxcode.accountswitcher.updater.util.AppUpdater;

public class Updater {

	public static void main(String[] args) {
		AppUpdater appUpdater = null;

		try {

			if (args.length > 0)
				appUpdater = new AppUpdater(args);
			else
				appUpdater = new AppUpdater();

			System.out.println("Starting updater and waiting 200 milliseconds...");
			Thread.sleep(200);
			System.out.println("try update");
			appUpdater.tryUpdate();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
