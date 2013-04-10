package dao.squschema;

import java.util.ArrayList;
import java.util.Date;
//import java.util.SortedMap;
import java.util.List;

import org.joda.time.DateTime;


public class Campaign {

	private String name;
	
	private double budget;
	
	private DateTime campaignStartTime;
	private DateTime campaignEndTime;

	private List<BannerPhraseRegion> bannerPhraseRegions;
	
	private List<Double> userBids;
	
	//private SortedMap<DateTime, Double> expectedBudgetCurve;
	
	private CampaignHistory campaignHistory;
	
	private BidModel currentBidModel;
	
	public Campaign(String inputName) {
		name = inputName;
		bannerPhraseRegions = new ArrayList<BannerPhraseRegion>();
	}
	
	public String getName() {
		return name;
	}
	
	public List<BannerPhraseRegion> getBannerPhraseRegions() {
		return bannerPhraseRegions;
	}
	
	public void setBannerPhraseRegions(List<BannerPhraseRegion> inputBannerPhraseRegions) {
		bannerPhraseRegions = inputBannerPhraseRegions;
	}
	
	public void addBannerPhraseRegion(BannerPhraseRegion inputBannerPhraseRegion) {
		bannerPhraseRegions.add(inputBannerPhraseRegion);
	}
	
	public CampaignHistory getCampaignHistory() {
		return campaignHistory;
	}
	
	public void setCampaignHistory(CampaignHistory inputCampaignHistory) {
		campaignHistory = inputCampaignHistory;
	}
	
	public BidModel getCurrentBidModel() {
		return currentBidModel;
	}
	
	public void setCurrentBidModel(BidModel inputCurrentBidModel) {
		currentBidModel = inputCurrentBidModel;
	}
	
	public double getBudget() {
		return budget;
	}
	
	public void setBudget(double inputBudget) {
		budget = inputBudget;
	}
	
	public void setCampaignStartTime(Date inputStartTime) {
		campaignStartTime = new DateTime(inputStartTime);		
	}
	
	public DateTime getCampaignStartTime() {
		return campaignStartTime;		
	}
	
	public void setCampaignEndTime(Date inputEndTime) {
		campaignEndTime = new DateTime(inputEndTime);		
	}
	
	public DateTime getCampaignEndTime() {
		return campaignEndTime;		
	}
	
	public List<Double> getUserBids() {
	  	  return userBids;
	    }

	    public void setUserBids(List<Double> inputUserBids) {
	  	  userBids = inputUserBids;
	    }
	
	/*
	public SortedMap<DateTime, Double> getExpectedBudgetCurve() {
		return expectedBudgetCurve;
	}
	
	public void setBannerCurve(SortedMap<DateTime, Double> inputExpectedBudgetCurve) {
		expectedBudgetCurve = inputExpectedBudgetCurve;
	}
	*/
}