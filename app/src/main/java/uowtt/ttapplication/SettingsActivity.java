package uowtt.ttapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
        boolean beginner = beginnerCheckBox.isChecked();

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("name", playerName);
        intent.putExtra("isBeginner", beginner);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    public void newLadder(View v){

        final EditText edit = new EditText(SettingsActivity.this);

        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you're ready to reset the ladder (The current ladder will be archived).")
                .setView(edit)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with reset

                        Log.d("pass", edit.getText().toString());

                        if (edit.getText().toString().equals("password")) {

                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);

                            intent.putExtra("reset", true);

                            startActivity(intent);
                        } else {
                            Toast.makeText(SettingsActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int width) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.stat_sys_warning)
                .show();
    }
}
