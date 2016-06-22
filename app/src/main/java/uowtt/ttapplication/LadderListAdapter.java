package uowtt.ttapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 17/08/2015.
 */
public class LadderListAdapter extends ArrayAdapter<Player> {

    private final List<String> activePlayers;
    List<Player> playerList;
    Drawable grup;
    Drawable redown;
    Drawable nochange;
    Drawable streak;

    public LadderListAdapter(Context context, int resource, List<Player> playerList, List<String> activePlayers) {
        super(context, resource, playerList);

        this.playerList = playerList;

        this.grup = context.getResources().getDrawable(R.drawable.grup);
        this.redown = context.getResources().getDrawable(R.drawable.redown);
        this.nochange = context.getResources().getDrawable(R.drawable.nochange);
        this.streak = context.getResources().getDrawable(R.drawable.streak);

        this.activePlayers = activePlayers;

        this.grup.setBounds(0,0,25,25);
        this.redown.setBounds(0, 0, 25, 25);
        this.nochange.setBounds(0, 0, 25, 25);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View newView = convertView;

        Player player = playerList.get(position);
        int change = player.get_change();

        if(newView == null){

            LayoutInflater inflater;
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            newView = inflater.inflate(R.layout.ladder_item, null);
        }

        TextView name_view = (TextView) newView.findViewById(R.id.playername);
        TextView pos_view = (TextView) newView.findViewById(R.id.ladderposition);
        TextView change_view = (TextView) newView.findViewById(R.id.change);

        pos_view.setText(new Integer(position+1).toString());
        if(player.beginner) {
            name_view.setText(Html.fromHtml("<font color=blue>"+player.name+"</font>"));
        }
        else{
            name_view.setText(player.name);
        }

        if(activePlayers !=null && !activePlayers.contains(player.name)){

            name_view.setBackgroundColor(Color.GRAY);
        }
        else{
            name_view.setBackgroundResource(android.R.color.transparent);
        }


        if(change > 0) {
            change_view.setCompoundDrawables(grup, null, null, null);
            change_view.setText(new Integer(change).toString() + "  ");
        }
        if(change == 0) {
            change_view.setCompoundDrawables(nochange, null, null, null);
            change_view.setText("" + "  ");
        }
        if(change < 0) {
            change_view.setCompoundDrawables(redown, null, null, null);
            change_view.setText(new Integer(Math.abs(change)).toString() + "  ");
        }


        return newView;
    }
}
