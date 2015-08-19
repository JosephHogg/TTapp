package uowtt.ttapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.util.Arrays;


public class NewGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

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
                if(s.length() > 2 & !Arrays.asList(playerList).contains(s.toString())) {
                    chal_text.setText(s.subSequence(0, 2));
                    chal_text.setSelection(chal_text.getText().length());
                }
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
                if(s.length() > 2 & !Arrays.asList(playerList).contains(s.toString())) {
                    oppo_text.setText(s.subSequence(0, 2));
                    oppo_text.setSelection(chal_text.getText().length());
                }
            }
        };

        chal_text.addTextChangedListener(tw_c);
        oppo_text.addTextChangedListener(tw_o);
    }

    public void submit(View view){

        AutoCompleteTextView chal_text = (AutoCompleteTextView) findViewById(R.id.chal_autotext);
        AutoCompleteTextView oppo_text = (AutoCompleteTextView) findViewById(R.id.oppo_autotext);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        String chal_name = chal_text.getText().toString();
        String oppo_name = oppo_text.getText().toString();
        String score = spinner.getSelectedItem().toString();

        Intent intent = new Intent();

        intent.putExtra("chal", chal_name);
        intent.putExtra("oppo", oppo_name);
        intent.putExtra("score", score);

        setResult(1, intent);

        finish();

    }
}
