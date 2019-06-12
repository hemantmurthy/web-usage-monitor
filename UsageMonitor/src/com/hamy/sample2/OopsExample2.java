package com.hamy.sample2;

import com.hamy.sample.Customer;
import com.hamy.sample.CustomerDescriber;

public class OopsExample2 {
	public static void main(String[] args) {
		Customer c = new MyCustomer("Jane", 26);
		c.printDesc();
		Customer d = new Customer("John Pizzas", 50, Customer.TYPE_BUSINESS);
		d.printDesc();
		
		CustomerDescriber cd = new MyCustomerDescriber();
		cd.printDescription(c);
		
		cd = new CustomerDescriber() {
			@Override
			public void printDescription(Customer customer) {
			}
		};
	}
}

class MyCustomer
extends Customer {
	MyCustomer(String name, int age) {
		super(name, age);
	}
	
	@Override
	protected String getDescription(String name, int age, String type) {
		return "Hey there!!! My name's " + name + " and my age is " + age;
	}
}

class MyCustomerDescriber 
implements CustomerDescriber {
	@Override
	public void printDescription(Customer customer) {
		System.out.println("I'm a customer named " + customer.getName() + " aged " + customer.getAge());
	}
}