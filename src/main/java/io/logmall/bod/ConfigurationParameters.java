package io.logmall.bod;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationParameters implements Serializable{

	
	
	public static final String OTC_URL_CONFIGURATION_VALUE = "https://otc.logistics-mall.com/instance-repository-resteasy/rest";
	
	public static final String LOGATA_DEV_URL_CONFIGURATION_VALUE = "https://logata-dev.logistics-mall.com/instance-repository-resteasy/rest";

	
	private static final long serialVersionUID = 1L;
	
	private String itemMaster;
	
	private String serverURLd;

	public String getItemMaster() {
		return itemMaster;
	}

	public void setItemMaster(String itemMaster) {
		this.itemMaster = itemMaster;
	}

	public String getServerURLd() {
		return serverURLd;
	}

	public void setServerURLd(String serverURLd) {
		this.serverURLd = serverURLd;
	}

	
	

}
