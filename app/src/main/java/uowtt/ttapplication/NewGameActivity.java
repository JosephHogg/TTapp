package uowtt.ttapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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

        AutoCompleteAdapter auto_adapter = new AutoCompleteAdapter(this.getApplicationContext(), playerList);
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
                    oppo_text.setSelection(oppo_text.getText().length());
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

                outcome.setText(Html.fromHtml("<font color=green>"+chal_name+"</font>"+"<font color=black>"+
                        " beats "+"</font>"+"<font color=red>"+oppo_name+" "+"</font>"+"<font color=black>"+score+"</font>"));
            }
            else{
                outcome.setText(Html.fromHtml("<font color=green>"+oppo_name+"</font>"+"<font color=black>"+
                        " beats "+"</font>"+"<font color=red>"+chal_name+"</font>"+" "+"<font color=black>"+score+"</font>"));
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

            Spanned matchText = Html.fromHtml(Match.matchToString(score, chal_name, oppo_name));

            DialogFragment dialog = new confirmMatchDialog(matchText, score, chal_name, oppo_name);

            dialog.show(getFragmentManager(), "tag...?");
        }

    }

    public class confirmMatchDialog extends DialogFragment{

        Spanned matchText;
        String score;
        String chal_name;
        String oppo_name;

        public confirmMatchDialog(Spanned matchText, String score, String chal_name, String oppo_name){

            this.matchText = matchText;
            this.score = score;
            this.chal_name = chal_name;
            this.oppo_name = oppo_name;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            // Build the dialog and set up the button click handlers
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View customView = inflater.inflate(R.layout.dialog_match, null);

            ((TextView) customView.findViewById(R.id.matchText)).setText(matchText);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(customView);

            builder.setMessage("Confirm?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent();

                            intent.putExtra("chal", chal_name);
                            intent.putExtra("oppo", oppo_name);
                            intent.putExtra("score", score);

                            setResult(1, intent);

                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            return;
                        }
                    });
            return builder.create();
        }

    }

    private boolean validate_match(String chal_name, String oppo_name, String score, boolean toasts) {

        Context context = getApplicationContext();
        List<String> playerL = Arrays.asList(playerA);

        if(chal_name.equals(oppo_name)){

            if (toasts) {
                Toast toast = Toast.makeText(context, "A player cannot play a match against themselves.", Toast.LENGTH_SHORT);
                toast.show();
            }

            return false;

        }

        if(score.equals("---")){

            if (toasts) {
                Toast toast = Toast.makeText(context, "Please select valid score", Toast.LENGTH_SHORT);
                toast.show();
            }

            return false;
        }

        if(!playerL.contains(chal_name) || !playerL.contains(oppo_name))
            return false;

        int c_pos = playerL.indexOf(chal_name);
        int o_pos = playerL.indexOf(oppo_name);

        if(c_pos < o_pos) {

            if (toasts) {
                Toast toast = Toast.makeText(context, "A player cannot challenge somebody below them on the ladder", Toast.LENGTH_LONG);
                toast.show();
            }

            return false;
        }

        return true;
    }
}
