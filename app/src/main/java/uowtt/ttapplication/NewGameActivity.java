package uowtt.ttapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class NewGameActivity extends Activity {

    String[] playerA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.scores_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        final AutoCompleteTextView chal_text = (AutoCompleteTextView) findViewById(R.id.chal_autotext);
        final AutoCompleteTextView oppo_text = (AutoCompleteTextView) findViewById(R.id.oppo_autotext);

        Intent intent = getIntent();

        Bundle b = intent.getExtras();

        b = b.getBundle("bundle");

        final String[] playerList = b.getStringArray("playerNamesList");

        playerA = playerList;

        ArrayAdapter<String> auto_adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, playerList);
        chal_text.setAdapter(auto_adapter);
        oppo_text.setAdapter(auto_adapter);

        TextWatcher tw_c = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2 & !Arrays.asList(playerList).contains(s.toString())) {
                    chal_text.setText(s.subSequence(0, 2));
                    chal_text.setSelection(chal_text.getText().length());
                }
                attemptDisplayMatch(spinner, chal_text, oppo_text);
            }
        };

        TextWatcher tw_o = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2 & !Arrays.asList(playerList).contains(s.toString())) {
                    oppo_text.setText(s.subSequence(0, 2));
                    oppo_text.setSelection(chal_text.getText().length());
                }
                attemptDisplayMatch(spinner, chal_text, oppo_text);
            }
        };

        chal_text.addTextChangedListener(tw_c);
        oppo_text.addTextChangedListener(tw_o);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                attemptDisplayMatch(spinner, chal_text, oppo_text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    private void attemptDisplayMatch(Spinner spinner, AutoCompleteTextView chal_text, AutoCompleteTextView oppo_text) {

        String chal_name = chal_text.getText().toString();
        String oppo_name = oppo_text.getText().toString();
        String score = spinner.getSelectedItem().toString();

        TextView outcome = (TextView) findViewById(R.id.outcome_text);

        if(validate_match(chal_name, oppo_name, score, false)){

            if(score.equals("2-0") || score.equals("2-1")){

                outcome.setText(Html.fromHtml("<font color=green>"+chal_name+"<font color=black>"+
                        " beats "+"<font color=red>"+oppo_name+" "+"<font color=black>"+score));
            }
            else{
                outcome.setText(Html.fromHtml("<font color=green>"+oppo_name+"<font color=black>"+
                        " beats "+"<font color=red>"+chal_name+" "+"<font color=black>"+score));
            }
        }
        else{
            outcome.setText("");
        }

    }

    public void submit(View view){

        AutoCompleteTextView chal_text = (AutoCompleteTextView) findViewById(R.id.chal_autotext);
        AutoCompleteTextView oppo_text = (AutoCompleteTextView) findViewById(R.id.oppo_autotext);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        String chal_name = chal_text.getText().toString();
        String oppo_name = oppo_text.getText().toString();
        String score = spinner.getSelectedItem().toString();

        if(validate_match(chal_name, oppo_name, score, true)) {
            Intent intent = new Intent();

            intent.putExtra("chal", chal_name);
            intent.putExtra("oppo", oppo_name);
            intent.putExtra("score", score);

            setResult(1, intent);

            finish();
        }

    }

    private boolean validate_match(String chal_name, String oppo_name, String score, boolean toasts) {

        Context context = getApplicationContext();
        List<String> playerL = Arrays.asList(playerA);

        if(score == "---"){

            Toast toast = Toast.makeText(context, "Please select valid score", Toast.LENGTH_LONG);
            toast.show();

            return false;
        }

        int c_pos = playerL.indexOf(chal_name);
        int o_pos = playerL.indexOf(oppo_name);

        if(c_pos < o_pos){

            Toast toast = Toast.makeText(context, "A player cannot challenge somebody below them on the ladder", Toast.LENGTH_LONG);
            toast.show();

            return false;
        }

        return true;
    }
}
