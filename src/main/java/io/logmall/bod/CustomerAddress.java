package io.logmall.bod;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;


public class CustomerAddress implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement(name="Street")
	private String street;
	@XmlElement(name="Number")
	private String number;
	@XmlElement(name="PostalCode")
	private String postalCode;
	@XmlElement(name="City")
	private String city;
	@XmlElement(name="CountryCode")
	private String countryCode;
	
	@XmlElement(name = "Email")
	private String email;
	
	@XmlElement(name = "Phone")
	private String phone;
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}	
}
