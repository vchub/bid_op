package dao.squschema;

import java.util.Vector;

public class Region {

	private String name;

	private Region parentRegion;

	private Vector<Region> subRegions;
  
	private double factor;
  
	public Region(String inputName) {
		name = inputName;
	}
	
	public String getName() {
		return name;
	}
	
	public Region getParentRegion() {
		return parentRegion;
	}
	
	public void setParentRegion(Region inputParentRegion) {
		parentRegion = inputParentRegion;
	}
	
	
	public Vector<Region> getSubRegions() {
		return subRegions;
	}
	
	public void setSubRegions(Vector<Region> inputSubRegions) {
		subRegions = inputSubRegions;
	}
	
	public void addSubRegion(Region inputSubRegion) {
		subRegions.add(inputSubRegion);
	}
	
	
	public double getFactor() {
		return factor;
	}
	
	public void setFactor(double inputFactor) {
		factor = inputFactor;
	}
}