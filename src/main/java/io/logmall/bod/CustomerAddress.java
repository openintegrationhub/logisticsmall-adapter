package io.logmall.bod;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;


public class CustomerAddress implements Serializable {

	private static final long serialVersionUID = 1L;
	@XmlElement(name="Street")
	private String Street;
	@XmlElement(name="Number")
	private String Number;
	@XmlElement(name="PostalCode")
	private String PostalCode;
	@XmlElement(name="City")
	private String City;
	@XmlElement(name="CountryCode")
	private String CountryCode;
	
	public String getStreet() {
		return Street;
	}
	public void setStreet(String street) {
		Street = street;
	}
	public String getNumber() {
		return Number;
	}
	public void setNumber(String number) {
		Number = number;
	}
	public String getPostalCode() {
		return PostalCode;
	}
	public void setPostalCode(String postalCode) {
		PostalCode = postalCode;
	}
	public String getCity() {
		return City;
	}
	public void setCity(String city) {
		City = city;
	}
	public String getCountryCode() {
		return CountryCode;
	}
	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}
}
