package io.logmall;

public final class Constants {

	public static final String URL_CONFIGURATION_KEY = "serverURLd";
	public static final String OTC_URL_CONFIGURATION;
	static {
		OTC_URL_CONFIGURATION = "{\"" + Constants.URL_CONFIGURATION_KEY
				+ "\": \"https://otc.logistics-mall.com/instance-repository-resteasy/rest\"}";		
	}

}
