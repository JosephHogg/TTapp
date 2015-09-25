package uowtt.ttapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Joseph on 15/08/2015.
 */
public class Ladder{

    private List<Player> ladderData;
    public int tot_matches;
    public int num_players;
    public int week_matches;
    public int last_week_reset_day;


    Ladder(){

        /*num_players = 0;

        String[] names = new String[] {"Peco", "Tsukimoto", "Kong", "Kazama", "Joseph",
                                      "Elliott", "William", "Megan", "Player A", "Player B"};

        Player[] players = new Player[names.length];

        for(int i=0; i < names.length; i++){

            players[i] = new Player(names[i], i, i);
            num_players++;
        }

        ladderData = Arrays.asList(players);*/

        ladderData = new ArrayList();

        num_players = 0;
        tot_matches = 0;

        Calendar c = Calendar.getInstance();

        week_matches = 0;
        last_week_reset_day = c.get(Calendar.DATE);
    }

    public Player[] getLadderData(){


        Player[] data = ladderData.toArray(new Player[1]);

        return data;
    }

    public List<Player> getLadderList(){

        return ladderData;
    }

    public Player[] getStreaksArray(){

        List<Player> streak = new ArrayList<Player>(ladderData);

        Collections.sort(streak, new Comparator<Player>() {
            @Override
            public int compare(Player lhs, Player rhs) {
                return lhs.streak < rhs.streak ? 1:-1;
            }
        });

        return streak.toArray(new Player[1]);
    }

    public String[] getPlayerList(){

        String[] players = new String[num_players];

        for(int i=0; i<num_players; i++){

            players[i] = ladderData.get(i).name;
        }

        return players;
    }

    public Player getPlayer(String p_name) throws Exception {

        Player player = null;

        for(int i=0; i<num_players; i++){

            Player check_player = ladderData.get(i);

            if(check_player.name.equals(p_name)){

                if(player == null){
                    player = check_player;
                }
                else throw new Exception("Duplicate player name found");

            }
        }

        return player;
    }

    public void check_week(){

        Calendar c = Calendar.getInstance();

        Log.d("date", new Integer(c.get(Calendar.DAY_OF_YEAR)).toString());

        // <0 for case of crossing the new year

        if((c.get(Calendar.DAY_OF_YEAR) - last_week_reset_day) > 7 || (c.get(Calendar.DAY_OF_YEAR) - last_week_reset_day) < 0){

            last_week_reset_day = c.get(Calendar.DAY_OF_YEAR);
            week_matches = 0;

        }

    }

    public void update(Match match){

        tot_matches++;
        week_matches++;

        Player chal = match.challenger;
        Player oppo = match.opponent;

        boolean winner = match.winner;

        int chal_pos = ladderData.indexOf(chal);
        int oppo_pos = ladderData.indexOf(oppo);


        Log.d("Match:", chal.name + " plays " + oppo.name);

        if(winner == false){

            chal.update_stats(winner, chal_pos);
            oppo.update_stats(!winner, oppo_pos);

            return;
        }
        else {

            chal.update_stats(winner, oppo_pos);
            oppo.update_stats(!winner, chal_pos);

            ladderData.set(chal_pos, oppo);
            ladderData.set(oppo_pos, chal);

            return;
        }
    }

    public void load(JSONObject json) throws JSONException {

        JSONArray playerArray = json.getJSONArray("players");

        for(int i =0; i<playerArray.length(); i++){
            num_players++;
            addPlayer(playerArray.getJSONObject(i));
        }
    }

    private void addPlayer(JSONObject jsonObject) {

        int[] change = new int[3];

        try {
            JSONArray jChange = jsonObject.getJSONArray("change");

            for (int i = 0; i < 3; i++)
                change[i] = jChange.getInt(i);

            Player player = new Player(jsonObject.getString("name"), jsonObject.getInt("standing"),
                    jsonObject.getInt("currentStreak"), jsonObject.getInt("wins"),
                    jsonObject.getInt("losses"), change);
            ladderData.add(player);
        }
        catch(JSONException e){

        }

    }
}
