package uowtt.ttapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Joseph on 24/08/2015.
 */
public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable{

    private List <String> listObjects;
    List<String> suggestions = new ArrayList<>();
    private int resource;

    private Filter mFilter = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if(constraint != null) {
                suggestions.clear();
                for(String name : listObjects){
                    if(name.toLowerCase().contains(constraint.toString().toLowerCase())){
                        suggestions.add(name);
                    }
                }

                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            if(results == null){
                return;
            }

            List<String> filteredList = (List<String>) results.values;
            if(results.count > 0) {
                clear();
                for (String filteredObject : filteredList) {
                    add(filteredObject);
                }
                notifyDataSetChanged();
            }
        }
    };

    public AutoCompleteAdapter(Context context, String[] strings) {
        super(context, R.layout.support_simple_spinner_dropdown_item, new ArrayList(Arrays.asList(strings)));
        this.listObjects = new ArrayList<>(Arrays.asList(strings));
        this.resource = android.R.layout.simple_list_item_1;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String listString = getItem(position);

        View newView;

        if(convertView == null){

            LayoutInflater inflater;
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            newView = inflater.inflate(resource, null);
        }
        else
            newView = convertView;

        TextView text = (TextView) newView.findViewById(android.R.id.text1);

        text.setText(listString);
        text.setTextColor(Color.BLACK);

        return newView;
    }
}
