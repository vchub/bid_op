package services;

//import java.util.SortedMap;
//import java.util.Date;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import dao.squschema.AdaptationModel;
import dao.squschema.Campaign;
import dao.squschema.CampaignHistory;
import dao.squschema.Curve;
import dao.squschema.VariationModel;
import dao.squschema.BidModel;

public interface Math {

  public BidModel doAdaptationStep(Campaign campaign, CampaignHistory campaignHistory, AdaptationModel variationModel, DateTime time);

  public BidModel doInternalStep(Campaign campaign, CampaignHistory campaignHistory, VariationModel variationModel, DateTime time);

  public double computeBid(Integer bannerPhraseRegionIndex, BidModel bidModel, Double bidRecommendation, DateTime time);

}

class mockMath implements Math {
	  public BidModel doAdaptationStep(Campaign campaign, CampaignHistory campaignHistory, AdaptationModel adaptationModel, DateTime time) {
		  LocalDate lastDate = campaignHistory.getCurveHistory().lastKey();
		  Curve currentCurve = campaignHistory.getCurve(lastDate);
		  
		  Curve newCurve = currentCurve;
		  double[] newCoefficients = newCurve.getCoefficients();
		  newCoefficients[0] = newCoefficients[0] + 1;
		  newCurve.setCoefficients(newCoefficients);
		  
		  DateTime lastTime = campaignHistory.getPermutationHistory().lastKey();
		  List<Integer> currentPermutation = campaignHistory.getPermutation(lastTime);
		  List<Integer> newPermutation = currentPermutation;
		  
		  BidModel newBidModel = new BidModel(newPermutation, newCurve, lastTime);
		  
		  return newBidModel;
	  }

	  public BidModel doInternalStep(Campaign campaign, CampaignHistory campaignHistory, VariationModel variationModel, DateTime time) {
		  LocalDate lastDate = campaignHistory.getCurveHistory().lastKey();
		  Curve currentCurve = campaignHistory.getCurve(lastDate);
		  
		  Curve newCurve = currentCurve;
		  double[] newCoefficients = newCurve.getCoefficients();
		  newCoefficients[0] = newCoefficients[0] + 1;
		  newCurve.setCoefficients(newCoefficients);
		  
		  DateTime lastTime = campaignHistory.getPermutationHistory().lastKey();
		  List<Integer> currentPermutation = campaignHistory.getPermutation(lastTime);
		  List<Integer> newPermutation = currentPermutation;
		  
		  BidModel newBidModel = new BidModel(newPermutation, newCurve, lastTime);
		  
		  return newBidModel;
	  }

	  public double computeBid(Integer bannerPhraseRegionIndex, BidModel bidModel, Double bidRecommendation, DateTime time) {
		  double bidCorrection = 1;
		  
		  /*
		  Performance performance = bannerPhraseRegion.performanceHistory.get(time);
		  double externalBid = performance.externalBid;
		  
		  bid = externalBid + curve.coefficients[0] + curve.coefficients[1] * permutation.positions[1];
		  */
		  return (bidCorrection * bidRecommendation); 
	  }
	}