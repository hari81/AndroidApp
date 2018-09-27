package au.com.infotrak.infotrakmobile.adapters.WRES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.SERVER_TABLES;

public class ModelLazyAdapter extends ArrayAdapter<SERVER_TABLES.SERVER_MODEL_TABLE> {
    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<SERVER_TABLES.SERVER_MODEL_TABLE> values;
    private int viewResourceId;

    public ModelLazyAdapter(Context context, int textViewResourceId,
                            ArrayList<SERVER_TABLES.SERVER_MODEL_TABLE> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
        this.viewResourceId = textViewResourceId;

    }

    public int getCount() {
        return values.size();
    }

    public SERVER_TABLES.SERVER_MODEL_TABLE getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        SERVER_TABLES.SERVER_MODEL_TABLE model = values.get(position);
        if (model != null) {
            TextView modelNameLabel = (TextView) v.findViewById(R.id.textViewItem);
            if (modelNameLabel != null) {
                modelNameLabel.setText(String.valueOf(model.getModeldesc()));
                modelNameLabel.setTag(model.getModel_auto());
            }
        }

        return v;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        SERVER_TABLES.SERVER_MODEL_TABLE model = values.get(position);
        if (model != null) {
            TextView modelNameLabel = (TextView) v.findViewById(R.id.textViewItem);
            if (modelNameLabel != null) {
                modelNameLabel.setText(String.valueOf(model.getModeldesc()));
                modelNameLabel.setTag(model.getModel_auto());
            }
        }

        return v;
    }
}