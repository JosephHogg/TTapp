package uowtt.ttapplication;

/**
 * Created by Joseph on 16/08/2015.
 */
public class Player {

    String name;
    int id;
    int streak;
    int wins;
    int losses;
    int standing;
    int[] change;

    Player(String player_name, int p_id, int p_standing){

        name = player_name;
        id = p_id;
        streak = 0;
        wins = 0;
        losses = 0;
        change = new int[3];
        standing = p_standing;
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
}
