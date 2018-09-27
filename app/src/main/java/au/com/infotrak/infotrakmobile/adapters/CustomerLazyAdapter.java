package au.com.infotrak.infotrakmobile.adapters;

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

/**
 * Created by SamuelC on 17/03/2015.
 */
public class CustomerLazyAdapter extends ArrayAdapter<Customer> {
    private final String MY_DEBUG_TAG = "CustomerAdapter";
    private ArrayList<Customer> items;
    private ArrayList<Customer> itemsAll;
    private ArrayList<Customer> suggestions;
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Customer) (resultValue)).GetCustomerName();
            return str;
        }

        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Customer customer : itemsAll) {
                    if (customer.GetCustomerName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
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
                ArrayList<Customer> filteredList = (ArrayList<Customer>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (Customer c : filteredList) {
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

    public CustomerLazyAdapter(Context context, int viewResourceId, ArrayList<Customer> items) {

        super(context, viewResourceId, items);

        this.viewResourceId = viewResourceId;
        this.items = items;
        this.itemsAll = (ArrayList<Customer>) items.clone();
        this.suggestions = new ArrayList<Customer>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        Customer customer = items.get(position);
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.textViewItem);
            if (customerNameLabel != null) {
                customerNameLabel.setText(String.valueOf(customer.GetCustomerName()));
                customerNameLabel.setTag(customer.GetCustomerId());
            }
        }
        return v;

    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}
