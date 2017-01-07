package com.stt.portfolio;

public class Stock {
	
	public static final String DEFAULT_COUNTRY="Suomi";
	
	String sector;
	String ticker;
	String name;
	String type;
	String ccy = "EUR"; // Currency code
	String country = DEFAULT_COUNTRY;
	int priceDivider = 1;
	
	public int getPriceDivider() {
		return priceDivider;
	}
	public void setPriceDivider(int priceDivider) {
		this.priceDivider = priceDivider;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCcy() {
		return ccy;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public boolean isOption() {
		
		return type.equals("O");
	}
	

}
