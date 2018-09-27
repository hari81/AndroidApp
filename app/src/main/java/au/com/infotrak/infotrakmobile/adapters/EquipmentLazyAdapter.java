package au.com.infotrak.infotrakmobile.adapters;

/**
 * Created by SamuelC on 12/03/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Presenter;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Presenter_Interface;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.controls.CustomCheckBox;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;

public class EquipmentLazyAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private ArrayList<Equipment> data;
    InfotrakDataContext dbContext;
    private MSI_Model_DB_Manager msi_db;
    private MSI_Utilities _utilities = null;
    //public ImageLoader imageLoader;

    public EquipmentLazyAdapter(Activity a, ArrayList<Equipment> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _utilities = new MSI_Utilities(activity);
        //imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }


    public Object getItem(int position) {
        if(data.size() > position )
            return data.get(position);

        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        dbContext = new InfotrakDataContext(parent.getContext());
        msi_db = new MSI_Model_DB_Manager(parent.getContext());

        if (convertView == null)
            vi = inflater.inflate(R.layout.list_equipment, null);

        TextView serialno = (TextView) vi.findViewById(R.id.txtSerialNo); // Serial No
        TextView unitno = (TextView) vi.findViewById(R.id.txtUnitNo); // Unit No
        TextView smu = (TextView) vi.findViewById(R.id.smu); // SMU
        TextView status = (TextView) vi.findViewById(R.id.txtStatus);
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb image
        final CustomCheckBox chkEquipment = (CustomCheckBox)vi.findViewById(R.id.chkEquipment);

        final Equipment equipment = data.get(position);

        // Setting all values in listview
        serialno.setText(equipment.GetSerialNo());
        serialno.setTag(equipment.GetID());
        unitno.setText(equipment.GetUnitNo());
        smu.setText(equipment.GetSMU());
        if(!equipment.GetStatus().equals("NOT STARTED"))
            status.setText(equipment.GetStatus());

        ////////////////////////////
        // MSI
        MSI_Utilities msi_utilities = new MSI_Utilities(null);
        if (msi_utilities.isMSIEquipment(equipment)
            && !equipment.GetStatus().equals(_utilities.inspection_not_started)
        )
        {
            MSI_Model_DB_Manager dbModel = new MSI_Model_DB_Manager(activity);
            long inspectionId = dbModel.selectInspectionId(equipment.GetID());

            MSI_Presenter_Interface presenter = new MSI_Presenter(activity);
            String inspectionStatus = presenter.validateEquipmentScreen(inspectionId);
            status.setText(inspectionStatus);

            presenter.updateInspectionStatus(inspectionId, inspectionStatus);
        }
        // MSI End
        ////////////////////////////////////

        byte[] image = equipment.GetImage();
        if (image != null)
            thumb_image.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

        if(equipment.GetIsChecked() == 1)
            chkEquipment.setChecked(true);
        else
            chkEquipment.setChecked(false);

        chkEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Integer val = 0;
                final boolean isChecked = chkEquipment.isChecked();
                if(isChecked) val =1;

                equipment.SetIsChecked(val);

                ////////////////////////////
                // MSI
                MSI_Utilities msi_utilities = new MSI_Utilities(null);
                if (msi_utilities.isMSIEquipment(equipment))
                {
                    msi_db.UpdateCheckedStatusForEquipment(equipment.GetID(),val);
                } else {
                    dbContext.UpdateCheckedStatusForEquipment(equipment.GetID(),val);
                }
            }
        });

        return vi;
    }
}
