package dao.squschema;

public class BannerPhraseRegion {
    
  public Region region;
  public BannerPhrase bannerPhrase;
  
  
  public BannerPhraseRegion(BannerPhrase inputBannerPhrase, Region inputRegion) {
  	bannerPhrase = inputBannerPhrase;
  	region = inputRegion;
  }
  
  
  public Region getRegion() {
	  return region; 
  }
  
  public BannerPhrase getBannerPhrase() {
	  return bannerPhrase;
  }			
}