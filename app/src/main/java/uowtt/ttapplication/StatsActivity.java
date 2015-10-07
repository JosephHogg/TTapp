package uowtt.ttapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        Intent intent = getIntent();

        Bundle b = intent.getExtras();

        b = b.getBundle("bundle");

        //CURRENT STREAKS
        String[] cStreakNames = b.getStringArray("cStreakNames");
        int[] cStreakValues = b.getIntArray("cStreakValues");

        TextView s1 = (TextView) findViewById(R.id.cStreak1);
        s1.setText(cStreakNames[0] + "  -  "+cStreakValues[0]);
        TextView s2 = (TextView) findViewById(R.id.cStreak2);
        s2.setText(cStreakNames[1] + "  -  "+cStreakValues[1]);
        TextView s3 = (TextView) findViewById(R.id.cStreak3);
        s3.setText(cStreakNames[2] + "  -  "+cStreakValues[2]);
        TextView s4 = (TextView) findViewById(R.id.cStreak4);
        s4.setText(cStreakNames[3] + "  -  "+cStreakValues[3]);

        //TOTAL GAMES
        TextView total = (TextView) findViewById(R.id.totalGames);
        total.setText(Integer.toString(b.getInt("totalGames")));

        //HIGHEST STREAKS
        String[] hStreakNames = b.getStringArray("hStreakNames");
        int[] hStreakValues = b.getIntArray("hStreakValues");

        TextView h1 = (TextView) findViewById(R.id.hStreak1);
        h1.setText(hStreakNames[0] + "  -  "+hStreakValues[0]);
        TextView h2 = (TextView) findViewById(R.id.hStreak2);
        h2.setText(hStreakNames[1] + "  -  "+hStreakValues[1]);
        TextView h3 = (TextView) findViewById(R.id.hStreak3);
        h3.setText(hStreakNames[2] + "  -  "+hStreakValues[2]);

        //MOST GAMES PLAYED
        String[] mGamesNames = b.getStringArray("mGamesNames");
        int[] mGamesValues = b.getIntArray("mGamesValues");

        TextView m1 = (TextView) findViewById(R.id.mostGames1);
        m1.setText(mGamesNames[0] + "  -  " + mGamesValues[0]);
        TextView m2 = (TextView) findViewById(R.id.mostGames2);
        m2.setText(mGamesNames[1] + "  -  " + mGamesValues[1]);
        TextView m3 = (TextView) findViewById(R.id.mostGames3);
        m3.setText(mGamesNames[2] + "  -  " + mGamesValues[2]);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
