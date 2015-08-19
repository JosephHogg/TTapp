package uowtt.ttapplication;

import android.util.Log;
import java.io.Serializable;

/**
 * Created by Joseph on 15/08/2015.
 */
public class Match implements Serializable{

    public Player challenger;
    public Player opponent;
    public boolean winner;

    Match(Player p1, Player p2, boolean win) throws Exception {

        challenger = p1;
        opponent = p2;
        winner = win;

        if(challenger.standing < opponent.standing)
            throw new Exception("Player cannot challenge somebody below them");
    }

}
