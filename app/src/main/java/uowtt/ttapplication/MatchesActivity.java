package uowtt.ttapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        ListView list = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();

        String jsonString = intent.getStringExtra("matchJSON");

        JSONArray matches = null;

        try {
            matches = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<Match> matchList = new ArrayList<>();

        for(int i = 0; i < matches.length(); i++){

            JSONObject jMatch = null;

            try {
                jMatch = (JSONObject) matches.get(i);
                matchList.add(new Match(jMatch));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        list.setAdapter(new MatchListAdapter(getApplicationContext(), R.layout.match_item, matchList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matches, menu);
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
