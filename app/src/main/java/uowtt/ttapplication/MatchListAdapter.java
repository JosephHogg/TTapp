package uowtt.ttapplication;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Joseph on 07/10/2015.
 */

public class MatchListAdapter extends ArrayAdapter<Match>{

    private List<Match> matchList;

    public MatchListAdapter(Context context, int resource, List<Match> matchList) {
        super(context, resource, matchList);

        this.matchList = matchList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View newView = convertView;

        Match match = matchList.get(matchList.size() - 1 - position);

        String date = match.date;
        Spanned matchText = Html.fromHtml(match.toString());


        if(newView == null){

            LayoutInflater inflater;
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            newView = inflater.inflate(R.layout.match_item, null);
        }

        TextView dateView = (TextView) newView.findViewById(R.id.date);

        dateView.setText(date);

        TextView matchTextView = (TextView) newView.findViewById(R.id.matchText);

        matchTextView.setText(matchText);

        return newView;
    }
}
