package services;

import java.util.Date;
import java.util.List;

import dao.squschema.BannerPhraseRegion;
import dao.squschema.Performance;

public interface MC_API {	
	public void createCampaign(String name, double budget, List<BannerPhraseRegion> bannerPhraseRegions, Date startTime, Date endTime, List<Double> userBids);
	
	public List<Double> getBids();
	
	public void updateBannerPerformance(List<Performance> bannerPerformance, Date time);
	
	public void updateCampaignPerformance(Performance campaignPerformance, List<Double> actualBids, List<List<Double>> recommendedBids, Date time);

}