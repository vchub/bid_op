package dao.squschema;


public class Banner {
    private Campaign campaign;
    private String text;
    
    public Banner() {
    	
    }
    
    public void setCampaign(Campaign inputCampaign) {
    	campaign = inputCampaign;
    }
    
    public Campaign getCampaign() {
    	return campaign;
    }
    
    public String getText() {
    	return text;
    }
    
    public void setText(String inputText) {
    	text = inputText;
    }
}