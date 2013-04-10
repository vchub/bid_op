package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import dao.squschema.AdaptationModel;
import dao.squschema.BannerPhraseRegion;
import dao.squschema.BidModel;
import dao.squschema.Campaign;
import dao.squschema.CampaignHistory;
import dao.squschema.Curve;
import dao.squschema.Performance;
import dao.squschema.VariationModel;

public class simpleMC implements MC_API {
	Campaign campaign;
	CampaignHistory campaignHistory;
	VariationModel variationModel;
	AdaptationModel adaptationModel;
		
	Math math;
	
	public simpleMC() {
		math = new mockMath();
	}
	
	public void createCampaign(String name, double budget, List<BannerPhraseRegion> bannerPhraseRegions, Date startTime, Date endTime, List<Double> userBids) {
		campaign = new Campaign(name);
		
		campaign.setBannerPhraseRegions(bannerPhraseRegions);
		campaign.setCampaignStartTime(startTime);
		campaign.setCampaignEndTime(endTime);
		campaign.setBudget(budget);
		campaign.setUserBids(userBids);
		
		//SortedMap<DateTime, Double> expectedBudgetCurve = computeExpectedBudgetCurve(budget, startTime, endTime);
		//campaign.setBannerCurve(expectedBudgetCurve);
		
		campaignHistory = new CampaignHistory(campaign);
		
		campaign.setCampaignHistory(campaignHistory);
		
		variationModel = getVariationModel();
		
		adaptationModel = getAdaptationModel();
		
		BidModel initialBidModel = getInitialBidModel(bannerPhraseRegions, startTime);
		
		campaign.setCurrentBidModel(initialBidModel);
		
		DateTime startDate = new DateTime(startTime);
		LocalDate startDay = startDate.toLocalDate();
		campaignHistory.setCurve(startDay, initialBidModel.getCurve());
		
		campaignHistory.setPermutation(startDate, initialBidModel.getPermutation());
	}
	
	public List<Double> getBids() {
		List<Double> bids = new ArrayList<Double>();
		
		DateTime lastTime = campaignHistory.getActualBidHistory().lastKey();
		
		bids = campaignHistory.getActualBids(lastTime);
		
		return bids;		
	}
	
	public void updateBannerPerformance(List<Performance> bannerPerformances, Date time) {
		DateTime inputTime = new DateTime(time);
		LocalDate date = inputTime.toLocalDate();
			
		campaignHistory.setBannerPerformances(date, bannerPerformances);
		
		DateTime currentTime = new DateTime();
		
		BidModel newBidModel = math.doAdaptationStep(campaign, campaignHistory, adaptationModel, inputTime);
		
		Curve newCurve = newBidModel.getCurve();
		List<Integer> newPermutation = newBidModel.getPermutation();
		
		LocalDate currentDate = currentTime.toLocalDate();
		
		campaignHistory.setCurve(currentDate, newCurve);
		campaignHistory.setPermutation(currentTime, newPermutation);	
		
		campaignHistory.setOptimalPermutation(currentDate, newPermutation);
	}
	
	public void updateCampaignPerformance(Performance campaignPerformance, List<Double> actualBids, List<List<Double>> recommendedBids, Date time) {
		DateTime inputTime = new DateTime(time);
		
		campaignHistory.setCampaignPerformance(inputTime, campaignPerformance);
		
		DateTime currentTime = new DateTime();
		BidModel newBidModel = math.doInternalStep(campaign, campaignHistory, variationModel, currentTime);
		
		List<Integer> newPermutation = newBidModel.getPermutation();
		
		campaignHistory.setPermutation(currentTime, newPermutation);
		
		campaignHistory.setActualBids(currentTime, actualBids);
		campaignHistory.setRecommendedBids(currentTime, recommendedBids);
		
		Curve newCurve = newBidModel.getCurve();
		BidModel currentBidModel = new BidModel(newPermutation, newCurve, currentTime);
		
		List<BannerPhraseRegion> bannerPhraseRegions = campaign.getBannerPhraseRegions();
		int numberBannerPhraseRegions = bannerPhraseRegions.size();
		List<Double> bids = new ArrayList<Double>();
		
		
		for (int bannerIndex = 0; bannerIndex < numberBannerPhraseRegions; bannerIndex++) {	
			double bidRecommendation = getBidRecommendation(recommendedBids.get(bannerIndex));
			double currentBid = math.computeBid(bannerIndex, currentBidModel, bidRecommendation, currentTime);
			
			bids.add(currentBid);
		}
		
		campaignHistory.setActualBids(inputTime, bids);
	}
	
	private Double getBidRecommendation(List<Double> bidLevels) {
		int targetLevel = 1;
		return bidLevels.get(targetLevel);
	}
	
	private VariationModel getVariationModel() {
		//create variation model
		double[] variationParameters = new double[1];
		variationParameters[0] = 0;
		VariationModel outputVariationModel = new VariationModel(variationParameters);
		
		return outputVariationModel;
	}
	
	private AdaptationModel getAdaptationModel() {
		//create adaptation model
		double[] adaptationParameters = new double[1];
		adaptationParameters[0] = 0;
		AdaptationModel outputAdaptationModel = new AdaptationModel(adaptationParameters);
		
		return outputAdaptationModel;
	}
	
	private BidModel getInitialBidModel(List<BannerPhraseRegion> bannerPhraseRegions, Date inputTime) {
		DateTime initialTime = new DateTime(inputTime);
		List<Integer> permutation = new ArrayList<Integer>();
		
		for (int i = 0; i < bannerPhraseRegions.size(); i++) {
			permutation.add(i);
		}
		
		//create sample curve
		double[] coefficients = new double[4];
		for (int i = 0; i < 4; i++) {
			coefficients[i] = i;
		}
		
		Curve curve = new Curve(coefficients, campaign);
				
		//create sample bidModel
		BidModel initialBidModel = new BidModel(permutation, curve, initialTime);
		
		return initialBidModel;
	}
}
