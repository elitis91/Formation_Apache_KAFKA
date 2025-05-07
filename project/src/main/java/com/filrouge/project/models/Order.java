package com.filrouge.project.models;

public class Order {
	
	  private String orderId;
	  private String productName;
	  private int   number;
	  private double totalPrice;
	  
	  public Order() {
		  
	  }
	  
	public Order(String orderId, String productName, int number, double totalPrice) {
		super();
		this.orderId = orderId;
		this.productName = productName;
		this.number = number;
		this.totalPrice = totalPrice;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", productName=" + productName + ", number=" + number + ", totalPrice="
				+ totalPrice + "]";
	}

	
	
	
	
}
