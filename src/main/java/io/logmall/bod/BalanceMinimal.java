package io.logmall.bod;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BalanceMinimal implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "Data")
	private List<BalanceMinimalItem> data;

	public List<BalanceMinimalItem> getData() {
		return data;
	}

	public void setData(List<BalanceMinimalItem> data) {
		this.data = data;
	}

}
