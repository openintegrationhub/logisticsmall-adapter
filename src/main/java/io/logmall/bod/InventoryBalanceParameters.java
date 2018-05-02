package io.logmall.bod;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryBalanceParameters implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "ItemMaster")
	private String itemMaster;

	public String getItemMaster() {
		return itemMaster;
	}

	public void setItemMaster(String itemMaster) {
		this.itemMaster = itemMaster;
	}
}
