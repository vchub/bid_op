package dao.squschema;

public class TimeSlotParameters {

  private double factor;
  
  	public TimeSlotParameters() {
		factor = -1;
	}
	
	public TimeSlotParameters(double inputFactor) {
		factor = inputFactor;
	}
	
	public double getFactor() {
		return factor;
	}
	
	public void setFactor(double inputFactor) {
		factor = inputFactor;
	}
  
}