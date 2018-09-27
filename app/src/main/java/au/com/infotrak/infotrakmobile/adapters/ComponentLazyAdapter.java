package au.com.infotrak.infotrakmobile.adapters;

/**
 * Created by SamuelC on 13/03/2015.
 */
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.Component;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;

public class ComponentLazyAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;
    private ArrayList<ComponentInspection> data;
    private Application mApp;
    //public ImageLoader imageLoader;

    public ComponentLazyAdapter(Activity a, ArrayList<ComponentInspection> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mApp = a.getApplication();
        //imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Component getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_component, null);

        TextView compartName = (TextView) vi.findViewById(R.id.compName); // Unit No
        TextView compartID = (TextView) vi.findViewById(R.id.compID); // Unit No
        TextView compartPos = (TextView) vi.findViewById(R.id.compPos); // Unit No
        TextView evalCode = (TextView) vi.findViewById(R.id.evalCode); // Unit No
        ImageView imgCamera = (ImageView) vi.findViewById(R.id.imgCamera);
        ImageView imgComment = (ImageView) vi.findViewById(R.id.imgComment);
        ImageView compIcon = (ImageView) vi.findViewById(R.id.compIcon);
        TextView txtReading = (TextView)vi.findViewById(R.id.txtReading);
        TextView txtLastReading = (TextView)vi.findViewById(R.id.compPreviousInspectionReading);
        TextView txtLastWornPercentage = (TextView)vi.findViewById(R.id.compPreviousInspectionWorn);
        TextView txtLastToolSymbol = (TextView)vi.findViewById(R.id.compPreviousInspectionToolSymbol);

        ComponentInspection component = data.get(position);

        // Setting all values in listview
        compartName.setText(component.GetName());
        compartID.setText(component.GetPartNo());
        String posString = "";
        if(component.GetName().contains("Idler")){
            if(component.GetPos().equals("1"))
                posString = "Front";
            else
                posString = "Rear";
        }else
            posString = component.GetPos();
        compartPos.setText(posString);
        byte[] compImage = component.GetImage();

        if (compImage != null)
            compIcon.setImageBitmap(BitmapFactory.decodeByteArray(compImage, 0, compImage.length));

        if(component.GetComments() != null && !component.GetComments().isEmpty())
            imgComment.setVisibility(View.VISIBLE);
        else
            imgComment.setVisibility(View.INVISIBLE);

        //PRN11498
        //if(component.GetInspectionImage() != null)
        if(component.GetInspectionImage() != null && component.GetInspectionImage().equals("true"))
            imgCamera.setVisibility(View.VISIBLE);
        else
            imgCamera.setVisibility(View.INVISIBLE);

        //PRN9036
        if(component.GetReading() != null)
            txtReading.setText(component.GetReading());
        else
            txtReading.setText("");

        if(component.GetLastReading() != 0)
        txtLastReading.setText(Double.toString(component.GetLastReading()));
        else  txtLastReading.setText("");

        if(component.GetLastWornPercentage() != 0)
            txtLastWornPercentage.setText(Integer.toString(component.GetLastWornPercentage())+'%');
        else  txtLastWornPercentage.setText("");

        if(component.getLastToolSymbol() != null)
            txtLastToolSymbol.setText(component.getLastToolSymbol());
        else  txtLastToolSymbol.setText("");

        if(component.GetIsNew() == 0) {
            InfotrakDataContext dbContext = new InfotrakDataContext(activity.getApplicationContext());
            long percWorn = component.GetPercentage(dbContext);

            //PRN11608
            String percString = "";
            if(percWorn > 120)
                percString = "120% >";
            else if (percWorn < 0 && txtReading.getText() != "")
                percString = "< 0%";
            else
                //PRN9036
                percString = (percWorn < 0) ? "0" : Long.toString(percWorn);


            //PRN9036
            //String percString = (percWorn == -1) ? "-" : Long.toString(percWorn);
            //PRN11608 - commented
            //String percString = (percWorn < 0) ? "0" : Long.toString(percWorn);
            if(!percString.equals("< 0%") && !percString.equals("120% >"))
                evalCode.setText(percString + ((!percString.equals("-")) ? "%" : ""));
            else
                evalCode.setText(percString);

            if(percWorn > 120 || percWorn < 0)
                evalCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            evalCode.setBackgroundColor(GetColorFromEval(percWorn));
        }
        else
        {
            evalCode.setText("0%");
            evalCode.setBackgroundColor(Color.GRAY);
        }

        return vi;
    }

    private int GetColorFromEval(long percWorn) {
        if(percWorn < 0)
            return Color.GRAY;
        if (percWorn <= ((InfoTrakApplication)mApp).getALimit())
            return Color.GREEN;
        if (percWorn <= ((InfoTrakApplication)mApp).getBLimit())
            return Color.YELLOW;
        if (percWorn <= ((InfoTrakApplication)mApp).getCLimit())
            return Color.argb(100, 255, 102, 0);

        return Color.RED;
    }
}
