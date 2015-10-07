package uowtt.ttapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void addPlayer(View v){

        EditText nameView = (EditText) findViewById(R.id.playerName);
        CheckBox beginnerCheckBox = (CheckBox) findViewById(R.id.beginnerCheck);

        String playerName = nameView.getText().toString();
        boolean beginner = beginnerCheckBox.isActivated();

        Intent intent = new Intent(this, MainActivity.class);

        //CHECK NAME UNIQUE HERE

        intent.putExtra("name", playerName);
        intent.putExtra("isBeginner", beginner);

        startActivity(intent);
    }

    public void newLadder(View v){

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("reset", true);

        startActivity(intent);
    }
}
