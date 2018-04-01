package io.logmall.bod;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryBalanceMinimal implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "Items")
	private List<InventoryBalanceLineMinimal> items;

	public List<InventoryBalanceLineMinimal> getItems() {
		return items;
	}

	public void setItems(List<InventoryBalanceLineMinimal> data) {
		this.items = data;
	}

}
