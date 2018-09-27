package au.com.infotrak.infotrakmobile.adapters.WRES;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESRecommendation;

/**
 * Created by PaulN on 4/04/2018.
 */

public class WRESDropDownListAdapter extends BaseAdapter {

    private ArrayList<WRESRecommendation> _list_items = new ArrayList<WRESRecommendation>();
    private LayoutInflater mInflater;
    private Button mSelectedItems;
    private int _selectedCount = 0;
    private String _firstSelected = "";


    public WRESDropDownListAdapter(Context context, ArrayList<WRESRecommendation> items,
                                   Button tv) {
        _list_items.addAll(items);

        mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        mSelectedItems = tv;

        for (int i = 0; i < _list_items.size(); i++) {
            if (_list_items.get(i).selected == true) {

                if (_firstSelected == "") {
                    _firstSelected = _list_items.get(i).descr;
                }

                _selectedCount++;
            }
        }
    }

    public ArrayList<WRESRecommendation> get_list_items() {
        return _list_items;
    }

    public void set_list_items(ArrayList<WRESRecommendation> _list_items) {
        this._list_items = _list_items;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return _list_items.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.wres_recommendation_list_row, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.wres_recommendation);
        final CheckBox chkbox = (CheckBox) convertView.findViewById(R.id.wres_checkbox);

        WRESRecommendation detail = _list_items.get(position);
        tv.setText(detail.descr);
        chkbox.setChecked(detail.selected);

        // Listener
        final View finalConvertView = convertView;
        chkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setText(position);
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                chkbox.setChecked(!chkbox.isChecked());

                setText(position);
            }
        });


        return finalConvertView;
    }


    /*
     * Function which updates the selected values display and information(checkSelected[])
     * */
    private void setText(int position1){

        if (!_list_items.get(position1).selected) {
            _list_items.get(position1).selected = true;
            _selectedCount++;
        } else {
            _list_items.get(position1).selected = false;
            _selectedCount--;
        }

        if (_selectedCount == 0) {
            mSelectedItems.setText("Add recommendations");
        } else if (_selectedCount == 1) {
            for (int i = 0; i < _list_items.size(); i++) {
                if (_list_items.get(i).selected == true) {
                    _firstSelected = _list_items.get(i).descr;
                    break;
                }
            }
            mSelectedItems.setText(_firstSelected);
        } else if (_selectedCount > 1) {
            for (int i = 0; i < _list_items.size(); i++) {
                if (_list_items.get(i).selected == true) {
                    _firstSelected = _list_items.get(i).descr;
                    break;
                }
            }
            mSelectedItems.setText(_firstSelected + "...");
        }
    }
}