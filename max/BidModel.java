package dao.squschema;

import java.util.List;

import org.joda.time.DateTime;

public class BidModel {

    private List<Integer> permutation;
    private Curve curve;
    private DateTime time;
    
    public BidModel(List<Integer> inputPermutation, Curve inputCurve, DateTime inputTime) {
    	time = inputTime;
    	curve = inputCurve;
    	permutation = inputPermutation;
    }
    
    public void setPermutation(List<Integer> inputPermutation) {
    	permutation = inputPermutation;
    }
    
    public List<Integer> getPermutation() {
    	return permutation;
    }
    
    public void setCurve(Curve inputCurve) {
    	curve = inputCurve;
    }
    
    public Curve getCurve() {
    	return curve;
    }
    
    public void setTime(DateTime inputTime) {
    	time = inputTime;
    }
    
    public DateTime getTime() {
    	return time;
    }
}