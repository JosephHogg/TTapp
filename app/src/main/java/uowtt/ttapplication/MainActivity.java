package uowtt.ttapplication;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Ladder ladder;
    private LadderDataFragment ladderFragment;
    private GoogleApiClient mGoogleApiClient;
    private DriveContents jsonContents;
    private JSONObject ladderJSON;
    private Metadata meta;
    private Player playerAdded = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        //Check for intent to add new player
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        if (name != null){
            Log.d("", "Creating Added player");
            Player player = new Player(-1, name);
            this.playerAdded = player;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();

        ladderFragment = (LadderDataFragment) fm.findFragmentByTag("ladder");

        if (ladderFragment == null) {
            //testLadderSetup();
            ladder = new Ladder();

            ladderFragment = new LadderDataFragment();

            fm.beginTransaction().add(ladderFragment, "ladder").commit();

            ladderFragment.setData(ladder);
        } else
            ladder = ladderFragment.getData();

        ladder.check_week();

        registerForContextMenu(findViewById(R.id.button3));

        Log.d("Checks", "onCreate Main Activity");

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);
    }

    public void contextMenu(View view){
        openContextMenu(view);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_matches:
                //Start match history activity
            case R.id.action_settings:
                //Start ladder settings activity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void startStatsActivity(View v){
        //Start the stats activity
        Intent intent = new Intent(this, StatsActivity.class);
        Bundle b = new Bundle();

        //GET CURRENT STREAKS
        Player[] currentStreaks = ladder.getStreaksArray();
        String[] names = new String[4];
        int[] streaks = new int[4];

        names[0] = currentStreaks[0].name;
        streaks[0] = currentStreaks[0].streak;
        names[1] = currentStreaks[1].name;
        streaks[1] = currentStreaks[1].streak;
        names[2] = currentStreaks[2].name;
        streaks[2] = currentStreaks[2].streak;
        names[3] = currentStreaks[3].name;
        streaks[3] = currentStreaks[3].streak;

        b.putStringArray("cStreakNames", names);
        b.putIntArray("cStreakValues", streaks);

        //TOTAL GAMES
        b.putInt("totalGames", ladder.tot_matches);

        //HIGHEST STREAKS
        // (not yet implemented)

        b.putStringArray("hStreakNames", ladder.highStreaksNames());
        b.putIntArray("hStreakValues", ladder.highStreakValues());

        //MOST GAMES PLAYED

        Player[] mostGamesPlayed = ladder.sortByNumGames();

        String[] names2 = new String[3];
        int[] streaks2 = new int[3];

        names2[0] = mostGamesPlayed[0].name;
        streaks2[0] = mostGamesPlayed[0].streak;
        names2[1] = mostGamesPlayed[1].name;
        streaks2[1] = mostGamesPlayed[1].streak;
        names2[2] = mostGamesPlayed[2].name;
        streaks2[2] = mostGamesPlayed[2].streak;

        b.putStringArray("mGamesNames", names2);
        b.putIntArray("mGamesValues", streaks2);

        intent.putExtra("bundle", b);

        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, 2);
        } catch (IntentSender.SendIntentException e) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.d("Checks", "onResume Main Activity");
    }

    private void populateStreaks() {

        Player[] streaks = ladder.getStreaksArray();

        //TextView streak1 = (TextView) findViewById(R.id.streak1);
        //TextView streak2 = (TextView) findViewById(R.id.streak2);
        //TextView streak3 = (TextView) findViewById(R.id.streak3);

        //streak1.setText(streaks[0].streak+", "+streaks[0].name);
        //streak2.setText(streaks[1].streak + ", " + streaks[1].name);
        //streak3.setText(streaks[2].streak + ", " + streaks[2].name);
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

            Match match = null;

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
                    match = new Match(chal, oppo, true, score);
                }
                catch(Exception ignore){
                }
            }
            else if(score.equals("0-2") || score.equals("1-2")){

                try {
                    match = new Match(chal, oppo, false, score);
                }
                catch(Exception ignore){
                }
            }
            else{
                Log.d("Conflict", "Invalid score... "+score);
                return;
            }

            ladder.update(ladderJSON, match);
            new UpdateJSONAsyncTask(this.getApplicationContext()).execute();
            setupList();
        }
        if(requestCode == 2 & resultCode == RESULT_OK)
            mGoogleApiClient.connect();

    }

    public class UpdateJSONAsyncTask extends ApiClientAsyncTask<JSONObject, Void, Boolean> {

        public UpdateJSONAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(JSONObject... args) {


            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, meta.getDriveId());
            DriveApi.DriveContentsResult contentsResult = file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();

            DriveContents contents = contentsResult.getDriveContents();

            OutputStream out = contents.getOutputStream();

            // Update json timestamp
            try {
                ladderJSON.put("timestamp", new Date().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                out.write(ladderJSON.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            com.google.android.gms.common.api.Status status =
                    contents.commit(mGoogleApiClient, null).await();
            return status.getStatus().isSuccess();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                showMessage("Error while editing contents");
                return;
            }
            showMessage("Successfully edited contents");
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        final ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new
                ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            // Handle error
                            return;
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("samp_ladder.json")
                                .setMimeType("application/json").build();
                        // Create a file in the root folder
                        Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                .createFile(getGoogleApiClient(), changeSet, null);
                    }
                };


        final ResultCallback<DriveApi.DriveContentsResult> jsonCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult contentsResult) {
                jsonContents = contentsResult.getDriveContents();

                InputStream input = jsonContents.getInputStream();

                BufferedReader streamReader = null;
                try {
                    streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                try {
                    while ((inputStr = streamReader.readLine()) != null) {
                        responseStrBuilder.append(inputStr);
                        Log.d("strings", inputStr);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    ladderJSON = new JSONObject(responseStrBuilder.toString());
                    Log.d("JSON", ladderJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    ladder.load(ladderJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(playerAdded != null){
                    Log.d("", "Adding player " + playerAdded.name);
                    ladder.addPlayer(ladderJSON, playerAdded);
                    new UpdateJSONAsyncTask(getApplicationContext()).execute();
                }

                setupList();
            }
        };

        final ResultCallback<DriveApi.MetadataBufferResult> qCallback = new ResultCallback<DriveApi.MetadataBufferResult>() {


            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    showMessage("Cannot find DriveId. Are you authorized to view this file?");
                    return;
                }

                Log.d("metadata", Integer.toString(result.getMetadataBuffer().getCount()));

                if (result.getMetadataBuffer().getCount() == 0) {

                    Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(contentsCallback);
                } else {
                    meta = result.getMetadataBuffer().get(0);

                    DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, meta.getDriveId());
                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(jsonCallback);

                }
            }
        };

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "samp_ladder.json"))
                .build();
        //Drive.DriveApi.requestSync(mGoogleApiClient);
        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(qCallback);
    }

    private void setupList() {

        List<Player> playerList = ladder.getLadderList();

        LadderListAdapter lad_adapter = new LadderListAdapter(this, R.layout.ladder_item,
                playerList);


        ListView ladView = (ListView) findViewById(R.id.ladderList);
        ladView.setAdapter(lad_adapter);

    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnectionSuspended(int i) {

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
