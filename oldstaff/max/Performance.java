package dao.squschema;


public class Performance {

	private int impressions;

	private int clicks;

	private int conversions;

	private double cost;
  
	public Performance() {
		impressions = -1;

		clicks = -1;

		conversions = -1;

		cost = -1;
	}
	
	public int getImpressions() {
		return impressions;
	}
	
	public void setImpressions(int inputImpressions) {
		impressions = inputImpressions;
	}
	
	public int getClicks() {
		return clicks;
	}
	
	public void setClicks(int inputClicks) {
		clicks = inputClicks;
	}
	
	public int getConversions() {
		return conversions;
	}
	
	public void setConversions(int inputConversions) {
		conversions = inputConversions;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setCost(double inputCost) {
		cost = inputCost;
	}
}