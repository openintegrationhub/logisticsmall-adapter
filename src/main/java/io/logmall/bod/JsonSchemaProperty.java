package io.logmall.bod;

import java.io.Serializable;

public class JsonSchemaProperty implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String name;
	private TYPE type;
	private String title;
	private boolean required;
	
	public enum TYPE{
		STRING;
		
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
		
	}
	
	public JsonSchemaProperty() {
		
	}
	
	public JsonSchemaProperty(String name, TYPE type, String title, boolean required) {
		this.name = name;
		this.type = type;
		this.title = title;
		this.required = required;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
	

}
