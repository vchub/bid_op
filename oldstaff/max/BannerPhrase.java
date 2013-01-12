package dao.squschema;

public class BannerPhrase {

    private Banner banner;
    private Phrase phrase;

    public BannerPhrase(Banner bannerObject, Phrase phraseObject) {
    	banner = bannerObject;
    	phrase = phraseObject;
    }
    
    public Banner getBanner() {
    	return banner;
    }
    
    public Phrase getPhrase() {
    	return phrase;
    }
}
