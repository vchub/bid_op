package dao.squschema;

public class VariationModel {

	private double parameters[];
	
	public VariationModel(double[] inputParameters) {
		parameters = inputParameters;
	}
	
	public double[] getParameters() {
		return parameters;
	}
	
	public void setParameters(double[] inputParameters) {
		parameters = inputParameters;
	}
}