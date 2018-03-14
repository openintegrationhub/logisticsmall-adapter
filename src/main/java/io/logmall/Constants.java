package io.logmall;

public final class Constants {

	public static final String URL_CONFIGURATION_KEY = "serverURLd";
	public static final String OTC_URL_CONFIGURATION;
	public static final String LOGATA_DEV_CONFIGURATION;

	static {
		LOGATA_DEV_CONFIGURATION = "{\"" + Constants.URL_CONFIGURATION_KEY
				+ "\": \"https://logata-dev.logistics-mall.com/instance-repository-resteasy/rest\"}";	
		OTC_URL_CONFIGURATION = "{\"" + Constants.URL_CONFIGURATION_KEY
				+ "\": \"https://otc.logistics-mall.com/instance-repository-resteasy/rest\"}";
	}

}
