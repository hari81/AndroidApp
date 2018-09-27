package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_ComponentType;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_GeneralInfo;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;

public class MSI_MandatoryRecordsActivity extends AppCompatActivity {

    // Database context
    MSI_Model_DB_Manager _db = new MSI_Model_DB_Manager(this);
    MSI_Utilities _utilities = new MSI_Utilities(this);
    String _side = _utilities.LEFT;

    // Component info
    long _inspectionId = 0;
    MSI_ComponentType _componentType;

    // Images
    private ArrayList<MSI_Image> _arrImages = new ArrayList<>();

    private void GetInputParameters()
    {
        Bundle b = getIntent().getExtras();
        _inspectionId = b.getLong("inspection_id");
        _componentType = b.getParcelable("componentType");
        _side = b.getString("side");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msi__mandatory_screen);

        // Prevent the keyboard from popping up automatically.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Parse all the input parameters sent to this screen.
        GetInputParameters();

        // Initialize variables
        _db = new MSI_Model_DB_Manager(this);

        // Screen header
        TextView headerText = (TextView) findViewById(R.id.msi_header);
        headerText.setText(_componentType.getComponentName() + " - Mandatory Photos");

        // Images
        Boolean bHasRecord = RenderImageList();

        // Validate Mandatory Screen
        if (!bHasRecord) {

            // Jump to Measurement Point screen
            Intent intent = new Intent(getApplicationContext(), MSI_MeasurementPointsActivity.class);

            Bundle b = new Bundle();
            b.putLong("inspectionId", _inspectionId);
            b.putParcelable("componentType", _componentType);
            b.putString("side", _side);
            intent.putExtras(b);

            startActivity(intent);
            MSI_MandatoryRecordsActivity.this.finish();
        }

        // Navigation buttons
        RenderNavButtons();
    }

    private Boolean RenderImageList()
    {
        // Get images
        ArrayList<MSI_Image> images = _db.selectMandatoryImages(_inspectionId, _componentType.getComparTypeAuto());
        ArrayList<ArrayList<MSI_Image>> arrImages = _utilities.ReArrangeLeftRightImage(images);

        Drawable greenCamera = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.camera_green);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout viewRoot = (LinearLayout) findViewById(R.id.MSI_img_list);
        for (int count = 0; count < arrImages.size(); count++) {

            ///////////////////////
            // Initialize layout
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.msi__tumbler_general_imgs, (ViewGroup) viewRoot, false);
            TextView txtView = (TextView) layout.findViewById(R.id.MSI_img_type);
            txtView.setText(arrImages.get(count).get(0).get_type().replace(_componentType.getComponentName() + " - ", ""));

            //////////////////
            // Display data
            final MSI_Image imageLeft = arrImages.get(count).get(0);
            final MSI_Image imageRight = arrImages.get(count).get(1);

            //////////////////////
            // Set listeners
            // Left image
            ImageView imgLeftView = (ImageView) layout.findViewById(R.id.MSI_img_left);
            if (_utilities.validateString(imageLeft.get_path()))
                imgLeftView.setImageDrawable(greenCamera);
            takePhoto_OnClickListener(imgLeftView, imageLeft);

            // Right image
            ImageView imgRightView = (ImageView) layout.findViewById(R.id.MSI_img_right);
            if (_utilities.validateString(imageRight.get_path()))
                imgRightView.setImageDrawable(greenCamera);
            takePhoto_OnClickListener(imgRightView, imageRight);

            ///////////////
            // Checkbox
            SetNotTakenCheck(layout, imageLeft, imageRight);

            // Add view
            viewRoot.addView(layout);

            // Update global variable
            _arrImages.add(imageLeft);
            _arrImages.add(imageRight);
        }

        if (arrImages.size() > 0) return true;

        return false;
    }

    private void SetNotTakenCheck(LinearLayout layout, final MSI_Image imageLeft, final MSI_Image imageRight)
    {
        final CheckBox leftCheckbox = (CheckBox) layout.findViewById(R.id.chk_MSI_left_checkbox);
        final CheckBox rightCheckbox = (CheckBox) layout.findViewById(R.id.chk_MSI_right_checkbox);

        // Display left checkbox
        if (imageLeft.get_not_taken() != 1)
        {
            leftCheckbox.setChecked(false);
        } else if (imageLeft.get_not_taken() == 1)
        {
            leftCheckbox.setChecked(true);
        }

        // Display right checkbox
        if (imageRight.get_not_taken() != 1)
        {
            rightCheckbox.setChecked(false);
        } else if (imageRight.get_not_taken() == 1)
        {
            rightCheckbox.setChecked(true);
        }

        // Listeners
        leftCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (_utilities.validateString(imageLeft.get_path()))
                    {
                        // Image exists
                        leftCheckbox.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Image exists! You can't check this on!", Toast.LENGTH_LONG).show();
                    } else {
                        imageLeft.set_not_taken(1);
                    }
                } else {
                    imageLeft.set_not_taken(0);
                }
            }
        });

        rightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (_utilities.validateString(imageRight.get_path()))
                    {
                        // Image exists
                        rightCheckbox.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Image exists! You can't check this on!", Toast.LENGTH_LONG).show();
                    } else {
                        imageRight.set_not_taken(1);
                    }
                } else {
                    imageRight.set_not_taken(0);
                }
            }
        });
    }

    private void takePhoto_OnClickListener(ImageView item, final MSI_Image image) {
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MSI_ImageModalActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                b.putParcelable("image", image);
                b.putInt("fixed_title", 1);
                intent.putExtras(b);
                startActivityForResult(intent, _utilities.OPEN_IMAGE_CAPTURE_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == _utilities.OPEN_IMAGE_CAPTURE_ACTIVITY) {

            Intent intent = new Intent(getApplicationContext(), MSI_MandatoryRecordsActivity.class);
            Bundle b = new Bundle();
            b.putLong("inspection_id", _inspectionId);
            b.putString("side", _side);
            b.putParcelable("componentType", _componentType);

            intent.putExtras(b);
            this.finish();
            startActivity(intent);
        }
    }

    private void RenderNavButtons()
    {
        // Back button
        Button backLink = (Button) findViewById(R.id.btn_MSI_TRGeneral_Back);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDB();
                MSI_MandatoryRecordsActivity.this.finish();
            }
        });

        // Next button
        Button nextLink = (Button) findViewById(R.id.btn_MSI_TRGeneral_Next);
        String nextItem = _componentType.getComponentName() + " Measurements";
        nextLink.setText(nextItem);
        nextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MSI_MeasurementPointsActivity.class);

                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                b.putParcelable("componentType", _componentType);
                b.putString("side", _side);
                intent.putExtras(b);

                SaveDB();
                startActivity(intent);
                MSI_MandatoryRecordsActivity.this.finish();
            }
        });
    }

    private void SaveDB()
    {
        // Images
        _db.updateMandatoryNotTaken(_inspectionId, _componentType.getComparTypeAuto(), _arrImages);
    }

}
