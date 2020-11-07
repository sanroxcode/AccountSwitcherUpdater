package com.sanroxcode.accountswitcher.updater.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.Property;
import org.update4j.Update;

public class AppUpdater {
	public final String APP_XML_CONFIG_URI;
	private final String API_KEY;
	private int timeOut = 0;

	public AppUpdater() throws IOException {
		Properties prop = new Properties();
		InputStream isReader = getClass().getClassLoader().getResourceAsStream("config.properties");

		if (isReader != null) {
			prop.load(isReader);
		} else {
			throw new FileNotFoundException("config.properties file not found.");
		}

		APP_XML_CONFIG_URI = prop.getProperty("configFileURI");
		API_KEY = prop.getProperty("apiKey");
		System.out.println("Default configuration loaded...");
	}

	public AppUpdater(String[] props) throws IOException {

		Properties prop = new Properties();
		InputStream isReader = getClass().getClassLoader().getResourceAsStream("config.properties");

		boolean nextIsURI = false;
		boolean nextIsKey = false;
		boolean nextIsTimeOut = false;
		String key = "";
		String uri = "";

		for (String argProp : props) {
			if (argProp.toLowerCase().equals("-uri")) {
				nextIsURI = true;
				continue;
			}
			if (nextIsURI) {
				uri = argProp.trim();
				nextIsURI = false;
				continue;
			}
			if (argProp.toLowerCase().equals("-key")) {
				nextIsKey = true;
				continue;
			}
			if (nextIsKey) {
				key = argProp.trim();
				nextIsKey = false;
				continue;
			}
			if (argProp.toLowerCase().equals("-timeout")) {
				nextIsTimeOut = true;
				continue;
			}
			if (nextIsTimeOut) {
				timeOut = Integer.parseInt(argProp.trim());
				nextIsTimeOut = false;
				continue;
			}
		}

		if (isReader != null) {
			prop.load(isReader);
		} else if (uri.equals("") || key.equals("")) {
			throw new FileNotFoundException("config.properties file not found.");
		}

		APP_XML_CONFIG_URI = uri.equals("") ? prop.getProperty("configFileURI") : uri;
		API_KEY = key.equals("") ? prop.getProperty("apiKey") : key;

		System.out.println("Custom configuration loaded...");
	}

	public void tryUpdate() {
		MyUpdateHandler myUpdateHandler = new MyUpdateHandler();
		try {

			Configuration cfg = getConfig();
			if (cfg == null) {
				return;
			}

			configToString(cfg);
			Configuration config = cfg;

			Path temp = Paths.get(config.getResolvedProperty("app.location") + "updateTemp\\");

			if (Update.containsUpdate(temp)) {
				Update.finalizeUpdate(temp);
			}

			new Thread(() -> config.launch()).start();

			configToString(config);

			if (config.requiresUpdate()) {
				config.updateTemp(temp, myUpdateHandler);				
				System.exit(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("Unable to update..." + "\n" + e.getMessage());
		}
	}

	private Configuration getConfig() throws IOException {
		Configuration config = null;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("alt", "media");
		parameters.put("prettyPrint", "true");
		parameters.put("key", API_KEY);

		try (InputStream in = simpleRequest(APP_XML_CONFIG_URI, parameters)) {
			config = Configuration.read(new InputStreamReader(in));
		} catch (Exception e) {
			throw new IOException(e);
		}

		return config;
	}

	private void configToString(Configuration cb) {
		System.out.println("base path = " + cb.getBasePath());
		System.out.println("base uri = " + cb.getBaseUri());
		for (Property prop : cb.getProperties()) {
			System.out.println(prop.getKey() + " = " + prop.getValue());
		}

		for (FileMetadata filemeta : cb.getFiles()) {
			System.out.println("URI = " + filemeta.getUri());
		}
	}

	private InputStream simpleRequest(String strURI, HashMap<String, String> parameters) throws IOException {
		try {

			MySimpleURI uri = new MySimpleURI(strURI);

			parameters.entrySet().forEach((entry) -> {
				uri.setParameter(entry.getKey(), entry.getValue());
			});

			URL url = uri.toURL();

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			int status = con.getResponseCode();
			if (status < 300)
				return con.getInputStream();

		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Could not connect to the configuration file");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException("Invalid URI Syntax");
		}

		return null;
	}
}
