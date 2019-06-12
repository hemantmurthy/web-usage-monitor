package com.hamy.sample;

/**
 * This interface encapsulates a Customer Describer. It provides a way to get a description of a Customer.
 * @author Hamy
 *
 */
public interface CustomerDescriber {
	/**
	 * Print a description of the Customer.
	 * @param customer The customer object to be printed.
	 */
	public void printDescription(Customer customer);
}
