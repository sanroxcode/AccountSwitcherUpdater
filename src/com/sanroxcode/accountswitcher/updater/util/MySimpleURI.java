package com.sanroxcode.accountswitcher.updater.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class MySimpleURI {
	private URI uri;

	MySimpleURI(String baseUrl) throws URISyntaxException {
		this.uri = new URI(baseUrl);
	}

	void setParameter(String key, String value) {
		StringBuilder result = new StringBuilder();
		try {
			result.append(URLEncoder.encode(key, "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(value, "UTF-8"));

			appendQuery(result.toString());

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void appendQuery(String query) throws Exception {

		try {
			String newQuery;

			newQuery = this.getUri().getQuery();

			if (newQuery == null)
				newQuery = query;
			else
				newQuery += "&" + query;

			this.uri = new URI(this.getUri().getScheme(), this.getUri().getAuthority(), this.getUri().getPath(),
					newQuery, this.getUri().getFragment());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new Exception("Could not resolve URI");
		}
	}

	public URI getUri() {
		return uri;
	}

	public URL toURL() {
		try {
			System.out.println(uri.toURL().toString());
			return uri.toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
