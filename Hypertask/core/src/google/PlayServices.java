package google;

public interface PlayServices {	
    public void signIn();
    public void signOut();
    public void rateGame();
    public void unlockAchievement(String achievementID);
    public void submitScore(int score);
    public void showAchievement();
    public void showLeaderboard();
    public boolean isSignedIn();
}
