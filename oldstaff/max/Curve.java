package dao.squschema;


public class Curve {

	private double coefficients[];

	private Campaign campaign;
  
	public Curve(double[] inputCoefficients, Campaign inputCampaign) {
		coefficients = inputCoefficients;
		campaign = inputCampaign;
	}
	
	public Campaign getCampaign() {
		return campaign;
	}

	public double[] getCoefficients() {
		return coefficients;
	}
	
	public void setCoefficients(double[] inputCoefficients) {
		coefficients = inputCoefficients;
	}
}