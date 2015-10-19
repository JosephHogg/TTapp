package uowtt.ttapplication;

import android.text.Html;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joseph on 15/08/2015.
 */
public class Match{

    public Player challenger;
    public Player opponent;
    public boolean winner;
    public String score;
    public String date;

    Match(Player p1, Player p2, boolean win, String score, String date) throws Exception {

        challenger = p1;
        opponent = p2;
        winner = win;
        this.score = score;
        if(date != null)
            this.date = date;
        else
            this.date = new Date().toString();

        if(challenger.standing < opponent.standing)
            throw new Exception("Player cannot challenge somebody below them");
    }

    public Match(JSONObject jMatch) {

        try {
            this.date = jMatch.getString("date");
            this.challenger = new Player(-1, jMatch.getString("challenger"), false);
            this.opponent = new Player(-1, jMatch.getString("opponent"), false);
            this.score = jMatch.getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (score.equals("2-0") || score.equals("2-1")) {
            winner = true;
        }
        else{
            winner = false;
        }
    }

    public JSONObject toJSONObject() {

        JSONObject jMatch = new JSONObject();

        try {
            jMatch.put("date", date);
            jMatch.put("challenger", challenger.name);
            jMatch.put("opponent", opponent.name);
            jMatch.put("result", score);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jMatch;
    }

    @Override
    public String toString() {

        String outcome;

        if (score.equals("2-0") || score.equals("2-1")) {

            outcome = "<font color=#03E103>" + challenger.name + "</font>" + "<font color=black>" +
                    " beats " + "</font>" + "<font color=red>" + opponent.name + " " + "</font>" + "<font color=black>" + score + "</font>";
        } else {

            //Reverse score so that it makes sense

            String tmpScore = "";

            tmpScore += score.charAt(2);
            tmpScore += score.charAt(1);
            tmpScore += score.charAt(0);

            outcome = "<font color=#03E103>" + opponent.name + "</font>" + "<font color=black>" +
                    " beats " + "</font>" + "<font color=red>" + challenger.name + "</font>" + " " + "<font color=black>" + tmpScore + "</font>";
        }

        return outcome;
    }
}
