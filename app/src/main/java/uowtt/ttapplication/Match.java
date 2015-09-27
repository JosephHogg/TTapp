package uowtt.ttapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joseph on 15/08/2015.
 */
public class Match implements Serializable{

    public Player challenger;
    public Player opponent;
    public boolean winner;
    public String score;

    Match(Player p1, Player p2, boolean win, String score) throws Exception {

        challenger = p1;
        opponent = p2;
        winner = win;
        this.score = score;

        if(challenger.standing < opponent.standing)
            throw new Exception("Player cannot challenge somebody below them");
    }

    public JSONObject toJSONObject() {

        JSONObject jMatch = new JSONObject();

        try {
            jMatch.put("date", new Date().toString());
            jMatch.put("challenger", challenger.name);
            jMatch.put("opponent", opponent.name);
            jMatch.put("result", score);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jMatch;
    }
}
