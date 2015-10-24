package uowtt.ttapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Ladder ladder;
    private GoogleApiClient mGoogleApiClient;
    private DriveFile jsonFile;
    private JSONObject ladderJSON;
    private Metadata meta;
    private Player playerAdded = null;
    private boolean reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        //Check for intent to add new player
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        intent.removeExtra("name");
        boolean beginner = intent.getBooleanExtra("isBeginner", false);
        reset = intent.getBooleanExtra("reset", false);

        if (name != null){
            Log.d("", "Creating Added player   "+beginner);
            Player player = new Player(-1, name, beginner);
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

        registerForContextMenu(findViewById(R.id.button3));
        registerForContextMenu(findViewById(R.id.ladderList));

        Log.d("Checks", "onCreate Main Activity");

        this.ladder = new Ladder();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        if(v.getId() == R.id.button3)
            inflater.inflate(R.menu.menu_main, menu);
        else
            inflater.inflate(R.menu.menu_player, menu);
    }

    public void contextMenu(View view){
        openContextMenu(view);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Intent intent;

        Log.d("sett", (String) item.getTitle());

        AdapterView.AdapterContextMenuInfo info;
        Player player = null;
        String name;

        switch (item.getItemId()) {

            case R.id.action_matches:
                //Start match history activity
                intent = new Intent(this, MatchesActivity.class);
                try {
                    intent.putExtra("matchJSON", ladderJSON.getJSONArray("matches").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                return true;
            case R.id.action_settings:
                //Start ladder settings activity
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_pstats:

                info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                name = ((TextView) info.targetView.findViewById(R.id.playername)).getText().toString();

                try {
                    player = ladder.getPlayer(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(player != null){

                    displayStats(player);
                }

                return true;
            case R.id.action_reset:
                info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                name = ((TextView) info.targetView.findViewById(R.id.playername)).getText().toString();

                try {
                    player = ladder.getPlayer(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(player != null){

                    resetPlayer(player);
                }
            case R.id.action_delete:
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

                name = ((TextView) info.targetView.findViewById(R.id.playername)).getText().toString();

                try {
                    player = ladder.getPlayer(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(player != null){

                    deletePlayer(player);
                }
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deletePlayer(final Player player) {

        final EditText edit = new EditText(MainActivity.this);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to reset this player on the ladder?")
                .setView(edit)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with reset

                        Log.d("pass", edit.getText().toString());

                        if (edit.getText().toString().equals("password")) {

                            //RESET PLAYER POSITION
                            ladder.deletePlayer(ladderJSON, player);
                            new UpdateJSONAsyncTask(MainActivity.this.getApplicationContext()).execute();
                            setupList();
                        } else {
                            Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
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

    private void resetPlayer(final Player player){

        final EditText edit = new EditText(MainActivity.this);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to reset this player on the ladder?")
                .setView(edit)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with reset

                        Log.d("pass", edit.getText().toString());

                        if (edit.getText().toString().equals("password")) {

                            //RESET PLAYER POSITION
                            ladder.resetPlayer(ladderJSON, player);
                            new UpdateJSONAsyncTask(MainActivity.this.getApplicationContext()).execute();
                            setupList();
                        } else {
                            Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
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

    private void displayStats(Player player) {

        LayoutInflater inflater = getLayoutInflater();

        View statsView = inflater.inflate(R.layout.dialog_stats, null);

        ((TextView) statsView.findViewById(R.id.streak)).setText(String.valueOf(player.streak));
        ((TextView) statsView.findViewById(R.id.wins)).setText(String.valueOf(player.wins));
        ((TextView) statsView.findViewById(R.id.losses)).setText(String.valueOf(player.losses));


        new AlertDialog.Builder(MainActivity.this)
                .setTitle(player.name)
                .setView(statsView)
                .show();
    }

    public void startStatsActivity(View v){
        //Start the stats activity
        Intent intent = new Intent(this, StatsActivity.class);
        Bundle b = new Bundle();

        //GET CURRENT STREAKS
        Player[] currentStreaks = ladder.getStreaksArray();
        String[] names = new String[4];
        int[] streaks = new int[4];

        try {
            names[0] = currentStreaks[0].name;
            streaks[0] = currentStreaks[0].streak;
            names[1] = currentStreaks[1].name;
            streaks[1] = currentStreaks[1].streak;
            names[2] = currentStreaks[2].name;
            streaks[2] = currentStreaks[2].streak;
            names[3] = currentStreaks[3].name;
            streaks[3] = currentStreaks[3].streak;
        }
        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        b.putStringArray("cStreakNames", names);
        b.putIntArray("cStreakValues", streaks);

        //TOTAL GAMES
        b.putInt("totalGames", ladder.tot_matches);

        //HIGHEST STREAKS
        b.putStringArray("hStreakNames", ladder.highStreaksNames());
        b.putIntArray("hStreakValues", ladder.highStreakValues());

        //MOST GAMES PLAYED

        Player[] mostGamesPlayed = ladder.sortByNumGames();

        String[] names2 = new String[3];
        int[] streaks2 = new int[3];

        try {
            names2[0] = mostGamesPlayed[0].name;
            streaks2[0] = mostGamesPlayed[0].totalGames();
            names2[1] = mostGamesPlayed[1].name;
            streaks2[1] = mostGamesPlayed[1].totalGames();
            names2[2] = mostGamesPlayed[2].name;
            streaks2[2] = mostGamesPlayed[2].totalGames();
        }
        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    match = new Match(chal, oppo, true, score, null);
                }
                catch(Exception ignore){
                }
            }
            else if(score.equals("0-2") || score.equals("1-2")){

                try {
                    match = new Match(chal, oppo, false, score, null);
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

            if(meta == null){
                meta = jsonFile.getMetadata(mGoogleApiClient).await().getMetadata();

            }
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
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        final ResultCallback<DriveFolder.DriveFileResult> createJSONCallback = new ResultCallback<DriveFolder.DriveFileResult>() {
            @Override
            public void onResult(DriveFolder.DriveFileResult driveFileResult) {

                jsonFile = driveFileResult.getDriveFile();

                //Create a new empty ladder json file and update the json file on google drive
                ladderJSON = new JSONObject();

                try {
                    ladderJSON.put("timestamp", new Date().toString());
                    ladderJSON.put("players", new JSONArray());
                    ladderJSON.put("matches", new JSONArray());
                    Log.d("", ladderJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new UpdateJSONAsyncTask(getApplicationContext()).execute();
            }
        };

        final ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new
                ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            // Handle error
                            return;
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("current_ladder.json")
                                .setMimeType("application/json").build();

                        // Create a file in the root folder
                        Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                .createFile(getGoogleApiClient(), changeSet, null).setResultCallback(createJSONCallback);
                    }
                };


        final ResultCallback<DriveApi.DriveContentsResult> jsonCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult contentsResult) {

                InputStream input = contentsResult.getDriveContents().getInputStream();

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

                    boolean unique = true;

                    for(int i=0; i <ladder.num_players; i++){

                        if(ladder.ladderData.get(i).name.equals(playerAdded.name)){
                            unique = false;
                        }
                    }

                    if(unique) {
                        ladder.addPlayer(ladderJSON, playerAdded);
                    }
                    else{

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage("That name is already in use.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_delete)
                                .show();
                    }

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
                }
                else if(reset){
                    meta = result.getMetadataBuffer().get(0);

                    DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, meta.getDriveId());
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("archive_"+ Calendar.getInstance().get(Calendar.YEAR)+".json")
                            .build();

                    file.updateMetadata(mGoogleApiClient, changeSet);
                }
                else {
                    meta = result.getMetadataBuffer().get(0);

                    DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, meta.getDriveId());
                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(jsonCallback);
                }
            }
        };

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "current_ladder.json"))
                .build();
        Drive.DriveApi.requestSync(mGoogleApiClient);
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
