package com.sanroxcode.accountswitcher.updater.util;

import java.io.InputStream;
import java.net.URLConnection;

import org.update4j.FileMetadata;
import org.update4j.service.DefaultUpdateHandler;



public class MyUpdateHandler extends DefaultUpdateHandler {
	private int timeOut = 30;

	@Override
	public InputStream openDownloadStream(FileMetadata file) throws Throwable {
		URLConnection connection = file.getUri().toURL().openConnection();

		// Some downloads may fail with HTTP/403, this may solve it
		connection.addRequestProperty("User-Agent", "Mozilla/5.0");
		// Set a connection timeout of 30 seconds, if default
		connection.setConnectTimeout(timeOut * 1000);
		// Set a read timeout of 30 seconds, if default
		connection.setReadTimeout(timeOut * 1000);

		return connection.getInputStream();
	}
}
