package io.logmall.bod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import de.fraunhofer.ccl.bo.converter.xml.oagis.adapter.DateTimeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PurchaseOrderMinimal implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement(name = "PurchaseOrderIdentifier")
	private String purchaseOrderIdentifier;
	@XmlElement(name = "OrderDateTime")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private DateTime orderDateTime;
	@XmlElement(name = "DeliveryTypeCode")
	private String deliveryTypeCode;
	@XmlElement(name = "Name")
	private String name;
	@XmlElement(name = "Address")
	private CustomerAddress address;
	@XmlElement(name = "Firstname")
	private String firstname;
	@XmlElement(name = "Lines")
	
	private List<PurchaseOrderLineMinimal> lines;

	public boolean addLine(PurchaseOrderLineMinimal line) {
		if(line == null) {
			throw new IllegalArgumentException("Line must not be null");
		}
		if(lines == null) {
			this.lines = new ArrayList<PurchaseOrderLineMinimal>();
		}
		return this.lines.add(line);
	}

	public String getPurchaseOrderIdentifier() {
		return purchaseOrderIdentifier;
	}

	public void setPurchaseOrderIdentifier(String purchaseOrderIdentifier) {
		this.purchaseOrderIdentifier = purchaseOrderIdentifier;
	}

	public DateTime getOrderDateTime() {
		return orderDateTime;
	}

	public void setOrderDateTime(DateTime orderDateTime) {
		this.orderDateTime = orderDateTime;
	}

	public String getDeliveryTypeCode() {
		return deliveryTypeCode;
	}

	public void setDeliveryTypeCode(String deliveryTypeCode) {
		this.deliveryTypeCode = deliveryTypeCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String careOfName) {
		name = careOfName;
	}

	public CustomerAddress getAddress() {
		return address;
	}

	public void setAddress(CustomerAddress address) {
		this.address = address;
	}

	public List<PurchaseOrderLineMinimal> getLines() {
		return lines;
	}

	public void setLines(List<PurchaseOrderLineMinimal> lines) {
		this.lines = lines;
	}

	public String getFirstName() {
		return firstname;
	}

	public void setFirstName(String firstname) {
		this.firstname = firstname;
	}
	
	
}