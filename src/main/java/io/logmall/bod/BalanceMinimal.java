package io.logmall.bod;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BalanceMinimal implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "Items")
	private List<BalanceMinimalItem> items;

	public List<BalanceMinimalItem> getItems() {
		return items;
	}

	public void setItems(List<BalanceMinimalItem> data) {
		this.items = data;
	}

}
