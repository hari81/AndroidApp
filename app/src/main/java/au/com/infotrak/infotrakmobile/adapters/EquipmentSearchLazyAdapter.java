package au.com.infotrak.infotrakmobile.adapters;

/**
 * Created by SamuelC on 12/03/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.EquipmentSearchActivity;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.controls.CustomCheckBox;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;

public class EquipmentSearchLazyAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context context;
    private ArrayList<Equipment> data;
    private ArrayList<Equipment> selected;


    public EquipmentSearchLazyAdapter(Context context, ArrayList<Equipment> d, ArrayList<Equipment> s) {
        this.context = context;
        data = d;
        selected = s;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_equipment_search, null);

        TextView serialno = (TextView) vi.findViewById(R.id.txtSerialNo); // Serial No
        TextView unitno = (TextView) vi.findViewById(R.id.txtUnitNo); // Unit No
        CustomCheckBox chkEquipment = (CustomCheckBox) vi.findViewById(R.id.chkEquipment); // Select CheckBox


        final Equipment equipment = data.get(position);

        // Setting all values in listview
        serialno.setText(equipment.GetSerialNo());
        unitno.setText(equipment.GetUnitNo());
        if (Equipment.EquipmentInList(equipment, selected))
            chkEquipment.setChecked(true, false);
        else
            chkEquipment.setChecked(false, false);


        chkEquipment.setOnCheckedChangeListener((EquipmentSearchActivity) context);


        return vi;
    }


}
