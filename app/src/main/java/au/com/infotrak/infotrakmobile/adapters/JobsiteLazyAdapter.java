package au.com.infotrak.infotrakmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;

/**
 * Created by SamuelC on 18/03/2015.
 */
public class JobsiteLazyAdapter extends ArrayAdapter<Jobsite> {
    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<Jobsite> values;
    private int viewResourceId;

    public JobsiteLazyAdapter(Context context, int textViewResourceId,
                              ArrayList<Jobsite> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
        this.viewResourceId = textViewResourceId;

    }

    public int getCount() {
        return values.size();
    }

    public Jobsite getItem(int position) {
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
        Jobsite jobsite = values.get(position);
        if (jobsite != null) {
            TextView jobsiteNameLabel = (TextView) v.findViewById(R.id.textViewItem);
            if (jobsiteNameLabel != null) {
                jobsiteNameLabel.setText(String.valueOf(jobsite.GetJobsiteName()));
                jobsiteNameLabel.setTag(jobsite.GetJobsiteId());
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
        Jobsite jobsite = values.get(position);
        if (jobsite != null) {
            TextView jobsiteNameLabel = (TextView) v.findViewById(R.id.textViewItem);
            if (jobsiteNameLabel != null) {
                jobsiteNameLabel.setText(String.valueOf(jobsite.GetJobsiteName()));
                jobsiteNameLabel.setTag(jobsite.GetJobsiteId());
            }
        }

        return v;
    }
}