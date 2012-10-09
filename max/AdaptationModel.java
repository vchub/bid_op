package dao.squschema;

public class AdaptationModel {
	private double parameters[];
	
	public AdaptationModel(double[] inputParameters) {
		parameters = inputParameters;
	}
	
	public double[] getParameters() {
		return parameters;
	}
	
	public void setParameters(double[] inputParameters) {
		parameters = inputParameters;
	}
}
