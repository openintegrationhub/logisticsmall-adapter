package io.logmall.bod;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemMasterMinimal implements Serializable{

	private static final long serialVersionUID = 1L;
	@XmlElement(name="BaseQuantityClassificationUnit")
	private String baseQuantityClassificationUnit;
	@XmlElement(name="StatusCode")
	private String statusCode;
	@XmlElement(name="Identifier")
	private String identifier;
	@XmlElement(name="Description")
	private String description;
	
	public String getBaseQuantityClassificationUnit() {
		return baseQuantityClassificationUnit;
	}
	public void setBaseQuantityClassificationUnit(String baseQuantityClassificationUnit) {
		this.baseQuantityClassificationUnit = baseQuantityClassificationUnit;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
