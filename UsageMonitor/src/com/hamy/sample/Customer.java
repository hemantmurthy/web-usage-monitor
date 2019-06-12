package com.hamy.sample;

/**
 * This class encapsulates a <b>Customer</b>.
 * @author Hamy
 * 
 */
public class Customer {
	public static final String TYPE_PERSON = "P";
	public static final String TYPE_BUSINESS = "B";
	
	private String name;
	private int age;
	private String type;
	
	public Customer(String name, int age, String type) {
		this.name = name;
		this.age = age;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public String getType() {
		return this.type;
	}
	
	/**
	 * Creates a customer of type "P".
	 * @param name Name of this Customer.
	 * @param age Age of this Customer.
	 */
	public Customer(String name, int age) {
		this(name, age, "P");
	}
	
	/**
	 * Prints a description of this Customer to the console.
	 */
	public void printDesc() {
		System.out.println(getDescription(name, age, type));
	}
	
	/**
	 * Get a description of this customer based on name, age and type.<br/>
	 * This method is called by printDesc() to get the description of this customer to be printed to console.
	 * @param name The name of this customer
	 * @param age The age of this customer
	 * @param type The type of this customer
	 * @return A String description of this customer.
	 */
	protected String getDescription(String name, int age, String type) {
		return "Name: " + name + " of type " + type + ", age: " + age;
	}
}

