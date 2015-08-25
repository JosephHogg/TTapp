package uowtt.ttapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private Ladder ladder;
    private LadderDataFragment ladderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();

        ladderFragment = (LadderDataFragment) fm.findFragmentByTag("ladder");

        if(ladderFragment == null){
            testLadderSetup();

            ladderFragment = new LadderDataFragment();

            fm.beginTransaction().add(ladderFragment, "ladder").commit();

            ladderFragment.setData(ladder);
        }
        else
            ladder = ladderFragment.getData();

        ladder.check_week();

        Log.d("Checks", "onCreate Main Activity");
    }

    @Override
    protected void onResume(){
        super.onResume();

        List<Player> playerList = ladder.getLadderList();

        LadderListAdapter lad_adapter = new LadderListAdapter(this, R.layout.ladder_item,
                                                playerList);

        populateStreaks();

        ListView ladView = (ListView) findViewById(R.id.ladderList);
        ladView.setAdapter(lad_adapter);

        TextView w_matches = (TextView) findViewById(R.id.textView3);
        w_matches.setText(new Integer(ladder.week_matches).toString());

        Log.d("Checks", "onResume Main Activity");
    }

    private void populateStreaks() {

        Player[] streaks = ladder.getStreaksArray();

        TextView streak1 = (TextView) findViewById(R.id.streak1);
        TextView streak2 = (TextView) findViewById(R.id.streak2);
        TextView streak3 = (TextView) findViewById(R.id.streak3);

        streak1.setText(streaks[0].streak+", "+streaks[0].name);
        streak2.setText(streaks[1].streak + ", " + streaks[1].name);
        streak3.setText(streaks[2].streak + ", " + streaks[2].name);
    }

    private void testLadderSetup(){
        ladder = new Ladder();

        List<Player> playerList = ladder.getLadderList();

        Player p0 = playerList.get(0);
        Player p1 = playerList.get(1);
        Player p2 = playerList.get(2);
        Player p3 = playerList.get(3);
        Player p4 = playerList.get(4);

        try {
            ladder.update(new Match(p1, p2, true));
        }
        catch(Exception e){
            Log.d("Except:", e.getMessage());
        }
        try {
            ladder.update(new Match(p2, p1, true));
        }
        catch(Exception e){

        }
        try {
            ladder.update(new Match(p4, p1, false));
        }
        catch(Exception e){

        }
        try {
            ladder.update(new Match(p3, p2, true));
        }
        catch(Exception e){

        }
        try {
            ladder.update(new Match(p1, p0, false));
        }
        catch(Exception e){

        }
        try {
            ladder.update(new Match(p2, p0, false));
        }
        catch(Exception e){

        }
        try {
            ladder.update(new Match(p3, p0, false));
        }
        catch(Exception e){

        }
        try {
            ladder.update(new Match(p4, p0, false));
        }
        catch(Exception e){

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        ladderFragment.setData(ladder);
    }

    public void newGame(View view) {

        Intent intent = new Intent(this, NewGameActivity.class);
        Bundle b = new Bundle();
        b.putStringArray("playerNamesList", ladder.getPlayerList());

        intent.putExtra("bundle", b);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 & data != null)
        {
            Player chal, oppo;

            String chal_name = data.getStringExtra("chal");
            String oppo_name = data.getStringExtra("oppo");
            String score = data.getStringExtra("score");

            Log.d("caveman", "Got result " + chal_name + oppo_name + score);

            try {
                chal = ladder.getPlayer(chal_name);
                oppo = ladder.getPlayer(oppo_name);
            }
            catch(Exception e){
                Log.d("Conflict", e.getMessage());
                chal = null;
                oppo = null;
            }

            if(score.equals("2-0") || score.equals("2-1")){
                try {
                    ladder.update(new Match(chal, oppo, true));
                }
                catch(Exception ignore){
                }
            }
            else if(score.equals("0-2") || score.equals("1-2")){

                try {
                    ladder.update(new Match(chal, oppo, false));
                }
                catch(Exception ignore){
                }
            }
            else{
                Log.d("Conflict", "Invalid score... "+score);
                return;
            }
        }
    }

}

class LadderDataFragment extends Fragment{

    // data object we want to retain
    private Ladder ladder;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(Ladder ladder) {
        this.ladder = ladder;
    }

    public Ladder getData() {
        return ladder;
    }
}
