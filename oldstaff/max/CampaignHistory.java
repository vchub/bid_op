package dao.squschema;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class CampaignHistory {
	
	private Campaign campaign;
	
	private SortedMap<LocalDate, Curve> curveHistory;
    private SortedMap<DateTime, List<Integer>> permutationHistory;
    private SortedMap<LocalDate, List<Integer>> optimalPermutationHistory;
    private SortedMap<DateTime, Performance> campaignPerformanceHistory;
    
    private SortedMap<LocalDate, List<Performance>> bannerPerformanceHistory;
    private SortedMap<DateTime, List<Double>> actualBidHistory;
    private SortedMap<DateTime, List<List<Double>>> recommendedBidHistory;

    public CampaignHistory(Campaign inputCampaign) {
    	campaign = inputCampaign;
		curveHistory = new TreeMap<LocalDate, Curve>();
		permutationHistory = new TreeMap<DateTime, List<Integer>>();
		optimalPermutationHistory = new TreeMap<LocalDate, List<Integer>>();
		campaignPerformanceHistory = new TreeMap<DateTime, Performance>();
		bannerPerformanceHistory = new TreeMap<LocalDate, List<Performance>>();
	  	actualBidHistory = new TreeMap<DateTime, List<Double>>();
	  	recommendedBidHistory = new TreeMap<DateTime, List<List<Double>>>();
	}
	
    public Campaign getCampaign() {
		return campaign;
	}
	
    public SortedMap<LocalDate, Curve> getCurveHistory() {
		return curveHistory;
	}
	
    public Curve getCurve(LocalDate date) {
		return curveHistory.get(date);
	}
	
    public void setCurveHistory(SortedMap<LocalDate, Curve> inputCurveHistory) {
		curveHistory = inputCurveHistory;
	}
	
    public void setCurve(LocalDate date, Curve curve) {
		curveHistory.put(date, curve);
	}
	
    public SortedMap<DateTime, List<Integer>> getPermutationHistory() {
		return permutationHistory;
	}
	
    public List<Integer> getPermutation(DateTime time) {
		return permutationHistory.get(time);
	}
	
    public void setPermutationHistory(SortedMap<DateTime, List<Integer>> inputPermutationHistory) {
		permutationHistory = inputPermutationHistory;
	}
	
    public void setPermutation(DateTime inputTime,  List<Integer> inputPermutation) {
		permutationHistory.put(inputTime, inputPermutation);
	}
    
    public SortedMap<LocalDate, List<Integer>> getOptimalPermutationHistory() {
		return optimalPermutationHistory;
	}
	
    public List<Integer> getOptimalPermutation(LocalDate date) {
		return optimalPermutationHistory.get(date);
	}
	
    public void setOptimalPermutationHistory(SortedMap<LocalDate, List<Integer>> inputPermutationHistory) {
		optimalPermutationHistory = inputPermutationHistory;
	}
	
    public void setOptimalPermutation(LocalDate inputDate,  List<Integer> inputPermutation) {
    	optimalPermutationHistory.put(inputDate, inputPermutation);
	}
    
    public SortedMap<DateTime, Performance> getCampaignPerformanceHistory() {
		return campaignPerformanceHistory;
	}
	
    public Performance getCampaignPerformance(DateTime inputTime) {
		return campaignPerformanceHistory.get(inputTime);
	}
	
    public void setCampaignPerformanceHistory(SortedMap<DateTime, Performance> inputCampaignPerformanceHistory) {
    	campaignPerformanceHistory = inputCampaignPerformanceHistory;
	}
	
    public void setCampaignPerformance(DateTime inputTime,  Performance inputCampaignPerformance) {
    	campaignPerformanceHistory.put(inputTime, inputCampaignPerformance);
	}
    
    public SortedMap<LocalDate, List<Performance>> getBannerPerformanceHistory() {
  	  return bannerPerformanceHistory;
    }
    
    public void setBannerPerformanceHistory(SortedMap<LocalDate, List<Performance>> inputBannerPerformanceHistory) {
    	bannerPerformanceHistory = inputBannerPerformanceHistory;
    }
    
    public List<Performance> getBannerPerformances(LocalDate inputDate) {
    	  return bannerPerformanceHistory.get(inputDate);
      }

      public void setBannerPerformances(LocalDate inputDate, List<Performance> inputPerformances) {
    	  bannerPerformanceHistory.put(inputDate, inputPerformances);
      }
    
    public SortedMap<DateTime, List<Double>> getActualBidHistory() {
  	  return actualBidHistory;
    }
    
    public void setActualBidHistory(SortedMap<DateTime, List<Double>> inputBidHistory) {
  	  actualBidHistory = inputBidHistory;
    }
    
    public SortedMap<DateTime, List<List<Double>>> getRecommendedBidHistory() {
    	  return recommendedBidHistory;
      }
      
      public void setRecommendedBidHistory(SortedMap<DateTime, List<List<Double>>> inputBidHistory) {
    	  recommendedBidHistory = inputBidHistory;
      }

    
    public List<Double> getActualBids(DateTime inputTime) {
  	  return actualBidHistory.get(inputTime);
    }

    public void setActualBids(DateTime inputTime, List<Double> inputBids) {
  	  actualBidHistory.put(inputTime, inputBids);
    }
    
    public List<List<Double>> getRecommendedBids(DateTime inputTime) {
    	  return recommendedBidHistory.get(inputTime);
      }

    public void setRecommendedBids(DateTime inputTime, List<List<Double>> inputBids) {
    	recommendedBidHistory.put(inputTime, inputBids);
    }
}
