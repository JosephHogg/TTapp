package uowtt.ttapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joseph on 16/08/2015.
 */
public class Player {

    int jsonIndex;
    String name;
    int streak;
    int wins;
    int losses;
    int standing;
    boolean beginner;
    int[] change;

    public Player(int jsonIndex, String player_name, int standing, int streak, int wins, int losses, boolean beginner, int[] change){

        this.jsonIndex = jsonIndex;
        this.name = player_name;
        this.streak = streak;
        this.wins = wins;
        this.losses = losses;
        this.change = change;
        this.beginner = beginner;
        this.standing = standing;
    }

    public Player(int jsonIndex, String playerName, boolean beginner) {
        this.jsonIndex = jsonIndex;
        this.name = playerName;
        this.streak = 0;
        this.wins = 0;
        this.losses = 0;
        this.change = new int[3];
        this.beginner = beginner;
        this.standing = jsonIndex;
    }

    public void update_stats(boolean win, int new_standing){

        int diff = standing - new_standing;

        update_change(diff);

        standing = new_standing;

        if(win){
            streak++;
            wins++;
        }
        else{
            streak = 0;
            losses++;
        }
    }

    public int get_change(){

        return change[0]+change[1]+change[2];
    }

    private void update_change(int diff) {

        change[0] = change[1];
        change[1] = change [2];
        change[2] = diff;

    }

    public JSONObject toJSONObject(){

        JSONObject json = new JSONObject();
        JSONArray jChange = new JSONArray();

        for(int i = 0; i<3; i++)
            jChange.put(this.change[i]);

        try {
            json.put("name", this.name);
            json.put("standing", this.standing);
            json.put("wins", this.wins);
            json.put("losses", this.losses);
            json.put("currentStreak", this.streak);
            json.put("beginner", this.beginner);
            json.put("change", jChange);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public int totalGames() {
        return wins+losses;
    }
}
