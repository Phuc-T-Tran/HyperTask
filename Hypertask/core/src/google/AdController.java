package google;

public interface AdController {
	public void showInterstitialAd(final Runnable then);
	//public void hideInterstitialAd();
	
	public boolean isWifiConnected();
}
