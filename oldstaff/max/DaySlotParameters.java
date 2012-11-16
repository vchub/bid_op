package dao.squschema;

public class DaySlotParameters {
	private double factor;
	
	private int dayType;
	
	DaySlotParameters() {
		factor = -1;
		dayType = -1;
	}
	
	public DaySlotParameters(double inputFactor, int inputDayType) {
		factor = inputFactor;
		dayType = inputDayType;
	}
	
	public double getFactor() {
		return factor;
	}
	
	public void setFactor(double inputFactor) {
		factor = inputFactor;
	}
	
	public double getDayType() {
		return dayType;
	}
	
	public void setDayType(int inputDayType) {
		dayType = inputDayType;
	}
}
