package io.logmall.bod;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;

public class PurchaseOrderLineMinimal implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "LineNumber")
	private Integer lineNumber;
	@XmlElement(name = "ItemMasterIdentifier")
	private String itemMasterIdentifier;
	@XmlElement(name = "OrderedQuantity")
	private BigDecimal orderedQuantity;
	@XmlElement(name = "QuantityUnit")
	private String quantityUnit;

	
	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getItemMasterIdentifier() {
		return itemMasterIdentifier;
	}

	public void setItemMasterIdentifier(String itemMasterIdentifier) {
		this.itemMasterIdentifier = itemMasterIdentifier;
	}

	public BigDecimal getOrderedQuantity() {
		return orderedQuantity;
	}

	public void setOrderedQuantity(BigDecimal orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(String quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

}
