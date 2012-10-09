package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import services.MC_API;
import services.simpleMC;

import dao.squschema.Banner;
import dao.squschema.BannerPhrase;
import dao.squschema.BannerPhraseRegion;
import dao.squschema.Performance;
import dao.squschema.Phrase;
import dao.squschema.Region;

public class ServicesTestCase {
	private MC_API mc_api;
	
	private List<BannerPhraseRegion> createBannerPhraseRegions(int numberBannerPhraseRegions) {
		//create two sample banners
		Banner banner1 = new Banner();
		banner1.setText("Super cool bike sale!");
		Banner banner2 = new Banner();
		banner2.setText("Super cool bike sale!");
		
		
		//create two sample phrases
		Phrase phrase1 = new Phrase("bike");
		Phrase phrase2 = new Phrase("buy bike");
			
		
		//create two sample bannerPhrases
		BannerPhrase bannerPhrase1 = new BannerPhrase(banner1, phrase1);
		BannerPhrase bannerPhrase2 = new BannerPhrase(banner2, phrase2);
		
		//create two sample regions
		Region region1 = new Region("Moscow");
		Region region2 = new Region("Yaroslavl");
		
		//create two sample bannerPhrasesRegions
		BannerPhraseRegion bannerPhraseRegion1 = new BannerPhraseRegion(bannerPhrase1, region1);
		BannerPhraseRegion bannerPhraseRegion2 = new BannerPhraseRegion(bannerPhrase2, region2);
		
		List<BannerPhraseRegion> bannerPhraseRegions = new ArrayList<BannerPhraseRegion>(); 
		for (int i = 0; i < numberBannerPhraseRegions; i++) {
			if (i % 2 == 0) {
				bannerPhraseRegions.add(bannerPhraseRegion2);
			} else {
				bannerPhraseRegions.add(bannerPhraseRegion1);
			}
		}
		
		return bannerPhraseRegions;
	}
	
	private Performance createPerformance() {
		//create sample performances
		Performance performance = new Performance();
		performance.setImpressions(27);
		performance.setClicks(9);
		
		return performance;
	}
	
	private List<Double> createActualBids(int numberBannerPhraseRegions) {
		List<Double> actualBids = new ArrayList<Double>();
		
		double sampleBid = 1;
		for (int i = 0; i < numberBannerPhraseRegions; i++) {
			actualBids.add(sampleBid);
		}
		
		return actualBids;
	}
	
	private List<List<Double>> createRecommendedBids(int numberBannerPhraseRegions) {
		List<List<Double>> recommendedBids = new ArrayList<List<Double>>();
		
		double sampleBid = 4;
		for (int i = 0; i < numberBannerPhraseRegions; i++) {
			List<Double> bidLevels = new ArrayList<Double>(); 
			for (int j = 0; j < 4; j++) {
				bidLevels.add(sampleBid - j);
			}
			recommendedBids.add(bidLevels);			
		}
		
		return recommendedBids;
	}
	
		
	@Before
	public void setUpCampaign() {
		//create sample campaign
		String campaignName = "Bestbikes";
				
		double budget = 100000;
		
		int numberBannerPhraseRegions = 10;
		List<BannerPhraseRegion> bannerPhraseRegions = createBannerPhraseRegions(numberBannerPhraseRegions);
		
		DateTime startTime = new DateTime();
		int campaignLengthDays = 7;
		DateTime endTime = startTime.plusDays(campaignLengthDays);	
		
		List<Double> userBids = createActualBids(numberBannerPhraseRegions);
				
		mc_api = new simpleMC();
				
		mc_api.createCampaign(campaignName, budget, bannerPhraseRegions, startTime.toDate(), endTime.toDate(), userBids);
				
		int timeIntervalMinutes = 15;
		long numberIntervals = (endTime.getMillis() - startTime.getMillis()) / (timeIntervalMinutes * 60* 1000);
				
		for(int i = 1; i < numberIntervals; i++) {
			Date currentTime = startTime.plusMinutes(i * timeIntervalMinutes).toDate();
			Performance campaignPerformance = createPerformance();
			List<Double> actualBids = createActualBids(numberBannerPhraseRegions);
			List<List<Double>> recommendedBids = createRecommendedBids(numberBannerPhraseRegions);
			
			mc_api.updateCampaignPerformance(campaignPerformance, actualBids, recommendedBids, currentTime);					
		}
	}

	@Test
	public void testGetBids() {
		assertEquals(mc_api.getBids().size(), 10);
	}
}
