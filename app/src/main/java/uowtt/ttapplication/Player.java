package uowtt.ttapplication;

/**
 * Created by Joseph on 16/08/2015.
 */
public class Player {

    String name;
    int streak;
    int wins;
    int losses;
    int standing;
    int[] change;

    Player(String player_name, int standing, int streak, int wins, int losses, int[] change){

        this.name = player_name;
        this.streak = streak;
        this.wins = wins;
        this.losses = losses;
        this.change = change;
        this.standing = standing;
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
