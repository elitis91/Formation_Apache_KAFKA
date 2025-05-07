package com.filrouge.project.models;

public class ProductRevenue {

	  private long windowStart;   
	  private long windowEnd;
	  private String productName;
	  private double revenue;
	  
	  public ProductRevenue() {} 
	  
	  
	  public ProductRevenue(long windowStart, long windowEnd, String productName, double revenue) {
		super();
		this.windowStart = windowStart;
		this.windowEnd = windowEnd;
		this.productName = productName;
		this.revenue = revenue;
	  	}
	  
		public long getWindowStart() {
			return windowStart;
		}
		public void setWindowStart(long windowStart) {
			this.windowStart = windowStart;
		}
		public long getWindowEnd() {
			return windowEnd;
		}
		public void setWindowEnd(long windowEnd) {
			this.windowEnd = windowEnd;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public double getRevenue() {
			return revenue;
		}
		public void setRevenue(double revenue) {
			this.revenue = revenue;
		}


		@Override
		public String toString() {
			return "ProductRevenue [windowStart=" + windowStart + ", windowEnd=" + windowEnd + ", productName="
					+ productName + ", revenue=" + revenue + "]";
		}
		 
		
	  
}
