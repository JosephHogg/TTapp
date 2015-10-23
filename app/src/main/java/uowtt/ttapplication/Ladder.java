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

    public String[] topStreaksNames = new String[3];
    public int[] topStreaksValues = new int[3];


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

    public void update(JSONObject ladderJSON, Match match){

        tot_matches++;
        week_matches++;

        JSONArray players = null;
        JSONArray matches = null;

        Player chal = match.challenger;
        Player oppo = match.opponent;

        boolean winner = match.winner;

        int chal_pos = ladderData.indexOf(chal);
        int oppo_pos = ladderData.indexOf(oppo);

        try {
            players = ladderJSON.getJSONArray("players");
            matches = ladderJSON.getJSONArray("matches");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Match:", chal.name + " plays " + oppo.name);

        if(winner == false){

            chal.update_stats(winner, chal_pos);
            oppo.update_stats(!winner, oppo_pos);

            try {
                players.put(chal.jsonIndex, chal.toJSONObject());
                players.put(oppo.jsonIndex, oppo.toJSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateTopStreaks(ladderJSON, oppo.name, oppo.streak);
        }
        else {

            chal.update_stats(winner, oppo_pos);
            oppo.update_stats(!winner, oppo_pos + 1);

            ladderData.remove(chal_pos);
            ladderData.add(oppo_pos, chal);

            try {
                for(int i = 0; i<players.length(); i++){

                    Player tmp = ladderData.get(i);

                    if(tmp.standing > oppo_pos){
                        tmp.standing++;
                    }

                    players.put(tmp.jsonIndex, tmp.toJSONObject());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateTopStreaks(ladderJSON, chal.name, chal.streak);
        }

        try {
            Log.d("match json", match.toJSONObject().toString());
            matches.put(match.toJSONObject());
            ladderJSON.put("matches", matches);
            ladderJSON.put("players", players);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateTopStreaks(JSONObject ladderJSON, String name, int streak) {

        if(streak > topStreaksValues[2]){

            if(streak > topStreaksValues[1]){

                if(streak > topStreaksValues[0]){

                    topStreaksNames[2] = topStreaksNames[1];
                    topStreaksValues[2] = topStreaksValues[1];
                    topStreaksNames[1] = topStreaksNames[0];
                    topStreaksValues[1] = topStreaksValues[0];
                    topStreaksNames[0] = name;
                    topStreaksValues[0] = streak;
                }
                else{

                    topStreaksNames[2] = topStreaksNames[1];
                    topStreaksValues[2] = topStreaksValues[1];
                    topStreaksNames[1] = name;
                    topStreaksValues[1] = streak;
                }
            }
            else{

                topStreaksValues[2] = streak;
                topStreaksNames[2] = name;
            }
        }

        try {
            JSONArray topNamesJSON = new JSONArray();
            for(int i=0; i<3; i++)
                topNamesJSON.put(i, topStreaksNames[i]);

            JSONArray topValuesJSON = new JSONArray();
            for(int i=0; i<3; i++)
                topValuesJSON.put(i, topStreaksValues[i]);

            ladderJSON.put("topStreakNames", topNamesJSON);
            ladderJSON.put("topStreakValues", topValuesJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void load(JSONObject json) throws JSONException {

        ladderData = new ArrayList<>();

        JSONArray playerArray = json.getJSONArray("players");

        for(int i =0; i<playerArray.length(); i++){
            num_players++;
            addJSONPlayer(i, playerArray.getJSONObject(i));
        }

        //Sort player list into order of player position

        Collections.sort(ladderData, new Comparator<Player>() {
            @Override
            public int compare(Player lhs, Player rhs) {
                return lhs.standing > rhs.standing ? 1 : -1;
            }
        });

        JSONArray matchesArray = json.getJSONArray("matches");

        this.tot_matches = matchesArray.length();

        JSONArray names = json.getJSONArray("topStreakNames");

        for(int i=0; i<3; i++)
            topStreaksNames[i] = names.getString(i);

        JSONArray values = json.getJSONArray("topStreakValues");

        for(int i=0; i<3; i++)
            topStreaksValues[i] = values.getInt(i);
    }

    private void addJSONPlayer(int index, JSONObject jsonObject) {

        int[] change = new int[3];

        try {
            JSONArray jChange = jsonObject.getJSONArray("change");

            for (int i = 0; i < 3; i++)
                change[i] = jChange.getInt(i);

            Player player = new Player(index, jsonObject.getString("name"), jsonObject.getInt("standing"),
                    jsonObject.getInt("currentStreak"), jsonObject.getInt("wins"),
                    jsonObject.getInt("losses"), jsonObject.getBoolean("beginner"), change);
            ladderData.add(player);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void addPlayer(JSONObject ladderJSON, Player player){

        if(player.standing == -1){

            player.standing = num_players;
            player.jsonIndex = num_players;
        }

        JSONArray players = null;

        try{
            players = ladderJSON.getJSONArray("players");
            players.put(player.toJSONObject());
            ladderJSON.put("players", players);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ladderData.add(player);
        num_players++;
    }

    public String[] highStreaksNames() {
        //to be implemented
        return topStreaksNames;
    }

    public int[] highStreakValues() {
        //to be implemented
        return topStreaksValues;
    }

    public Player[] sortByNumGames() {

        Player[] sorted = ladderData.toArray(new Player[0]);

        Arrays.sort(sorted, new Comparator<Player>() {
            @Override
            public int compare(Player player, Player t1) {
                if ((player.wins + player.losses) < (t1.wins + t1.losses)) return 1;
                else return -1;
            }
        });

        return sorted;
    }
}
