package io.logmall.bod;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BalanceMinimalItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "ItemMaster")
	private String itemMaster;

	@XmlElement(name = "Quantity")
	private BigDecimal quantity;

	@XmlElement(name = "Unit")
	private String unit;

	@Override
	public String toString() {
		return "" + itemMaster + ": " + quantity + " " + unit;
	}

	public String getItemMaster() {
		return itemMaster;
	}

	public void setItemMaster(String itemMaster) {
		this.itemMaster = itemMaster;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
