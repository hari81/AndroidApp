package au.com.infotrak.infotrakmobile.adapters.WRES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.entityclasses.Customer;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.SERVER_TABLES;

public class WRESMakeLazyAdapter extends ArrayAdapter<SERVER_TABLES.SERVER_MAKE_TABLE> {
    private final String MY_DEBUG_TAG = "MakeAdapter";
    private ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> items;
    private ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> itemsAll;
    private ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> suggestions;
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((SERVER_TABLES.SERVER_MAKE_TABLE) (resultValue)).getMakedesc();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (SERVER_TABLES.SERVER_MAKE_TABLE make : itemsAll) {
                    if (make.getMakedesc().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(make);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> filteredList = (ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (SERVER_TABLES.SERVER_MAKE_TABLE c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    private int viewResourceId;

    public WRESMakeLazyAdapter(Context context, int viewResourceId, ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE> items) {

        super(context, viewResourceId, items);

        this.viewResourceId = viewResourceId;
        this.items = items;
        this.itemsAll = (ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE>) items.clone();
        this.suggestions = new ArrayList<SERVER_TABLES.SERVER_MAKE_TABLE>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        SERVER_TABLES.SERVER_MAKE_TABLE make = items.get(position);
        if (make != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.textViewItem);
            if (customerNameLabel != null) {
                customerNameLabel.setText(String.valueOf(make.getMakedesc()));
                customerNameLabel.setTag(make.getMake_auto());
            }
        }
        return v;

    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}
