package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.AppLog;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;

public class MSI_JobsiteConditionsActivity extends AppCompatActivity {
    // Database context
    MSI_Model_DB_Manager _db;
    MSI_Utilities _utilities = new MSI_Utilities(this);

    // Equipment ID and serial No
    private long _inspectionId;
    String equipmentNo;

    // Jobsite ID
    private long jobsiteId;

    // Constants
    private String IMAGE_PREFIX = "JC";
    private String ADDITIONAL_IMAGE_PREFIX = "JC_ADD";

    private String IMAGE_CODE_IMPACT = "1";
    private String IMAGE_CODE_ABRASIVE = "2";
    private String IMAGE_CODE_PACKING = "3";
    private String IMAGE_CODE_MOISTURE = "4";

    private int CONDITION_LOW = 0;
    private int CONDITION_MODERATE = 1;
    private int CONDITION_HIGH = 2;

    // Jobsite condition information
    private int impact = CONDITION_LOW;
    private int abrasive = CONDITION_LOW;
    private int packing = CONDITION_LOW;
    private int moisture = CONDITION_LOW;
    private String overallObservationComments = "";

    // Code triggered for return from MSI_ImageCapture activity (set to ticket number)
    private static final int OPEN_IMAGE_CAPTURE_ACTIVITY = 379;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msi__jobsite_conditions);

        // Prevent the keyboard from popping up automatically.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get the _inspectionId parameter that was passed to this screen.
        Bundle bundle = getIntent().getExtras();
        _inspectionId = bundle.getLong("inspectionId");

        // Get a new _db for this activity.
        _db = new MSI_Model_DB_Manager(getApplicationContext());

        // Get the equipment Serial No.
        Equipment equipment = _db.SelectEquipmentByInspectionId(_inspectionId);
        equipmentNo = equipment.GetSerialNo();

        // Overall Observation Comments
        final EditText etComments = (EditText) findViewById(R.id.et_MSI_JC_overallObservationComments);

        // Get the jobsite.
        jobsiteId = _db.SelectJobsiteIdByInspectionId(_inspectionId);
        final Jobsite jobsite = _db.SelectJobsiteByInspectionId(_inspectionId, jobsiteId);

        // Measurement Unit
        SetMeasureUnitRadioButton(jobsite.GetUOM());

        // Load the previously saved state.
        if(jobsite != null) {
            impact = jobsite.GetImpact();
            abrasive = jobsite.GetAbrasive();
            packing = jobsite.GetPacking();
            moisture = jobsite.GetMoisture();
            overallObservationComments = jobsite.GetJobsiteComments();

            loadJobsiteConditions();
        }

        // Navigate back to the menu.
        Button backLink = (Button) findViewById(R.id.btnMSIJobsiteConditions_Back);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overallObservationComments = etComments.getText().toString();

                // Save jobsite condition.
                saveDB();
                MSI_JobsiteConditionsActivity.this.finish();
            }
        });

        /////////////////////
        // Render images
        RenderStandardImages();
    }

    private void RenderStandardImages()
    {
        // Initialization
        ImageView imgImpact = (ImageView) findViewById(R.id.img_MSI_JC_Impact);
        ImageView imgAbrasive = (ImageView) findViewById(R.id.img_MSI_JC_Abrasive);
        ImageView imgPacking = (ImageView) findViewById(R.id.img_MSI_JC_Packing);
        ImageView imgMoisture = (ImageView) findViewById(R.id.img_MSI_JC_Moisture);

        // Get Standard images from DB
        ArrayList<MSI_Image> arrayImages = _db.selectJobsiteImages(_inspectionId);
        Drawable greenCamera = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.camera_green);

        MSI_Image impactImage = getStandardImage(arrayImages, _utilities.IMG_IMPACT);
        if (impactImage == null)
            impactImage = SetImageObject(_inspectionId, _utilities.IMPACT, _utilities.IMG_IMPACT);
        else
            imgImpact.setImageDrawable(greenCamera);

        MSI_Image abrasiveImage = getStandardImage(arrayImages, _utilities.IMG_ABRASIVE);
        if (abrasiveImage == null)
            abrasiveImage = SetImageObject(_inspectionId, _utilities.ABRASIVE, _utilities.IMG_ABRASIVE);
        else
            imgAbrasive.setImageDrawable(greenCamera);

        MSI_Image packingImage = getStandardImage(arrayImages, _utilities.IMG_PACKING);
        if (packingImage == null)
            packingImage = SetImageObject(_inspectionId, _utilities.PACKING, _utilities.IMG_PACKING);
        else
            imgPacking.setImageDrawable(greenCamera);

        MSI_Image moistureImage = getStandardImage(arrayImages, _utilities.IMG_MOISTURE);
        if (moistureImage == null)
            moistureImage = SetImageObject(_inspectionId, _utilities.MOISTURE, _utilities.IMG_MOISTURE);
        else
            imgMoisture.setImageDrawable(greenCamera);

        // Set listeners
        takePhoto_OnClickListener(imgImpact, impactImage, 1);
        takePhoto_OnClickListener(imgAbrasive, abrasiveImage, 1);
        takePhoto_OnClickListener(imgPacking, packingImage, 1);
        takePhoto_OnClickListener(imgMoisture, moistureImage, 1);

        ////////////////////////
        // Additional images
        RenderAdditionalImages(arrayImages);
    }

    private void RenderAdditionalImages(ArrayList<MSI_Image> arrayImages)
    {
        // Get additional images
        ArrayList<MSI_Image> arrAdditionalImages = getAdditionalImages(arrayImages);
        for (int i = 0; i < _utilities.IMG_ADDITION_NO; i++)
        {
            // Get view
            String viewId = "MSI_img_additional_" + (i + 1);
            int resID = getResources().getIdentifier(viewId, "id", getPackageName());
            ImageView imageView = (ImageView) this.findViewById(resID);

            MSI_Image image = SetImageObject(_inspectionId, "", _utilities.IMG_JOBSITE_ADDITION);
            if (i < arrAdditionalImages.size()) {

                // Display image
                image = arrAdditionalImages.get(i);
                if (_utilities.validateString(image.get_path())) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(image.get_path());
                    imageView.setImageBitmap(myBitmap);
                }

                // Display title
                if (_utilities.validateString(image.get_img_title())) {
                    String titleId = "MSI_text_additional_" + (i + 1);
                    int resTitleId = getResources().getIdentifier(titleId, "id", getPackageName());
                    TextView titleView = (TextView) this.findViewById(resTitleId);
                    titleView.setText(image.get_img_title());
                }
            }

            // Set listener
            takePhoto_OnClickListener(imageView, image, 0);
        }

    }

    private ArrayList<MSI_Image> getAdditionalImages(ArrayList<MSI_Image> arrayImages)
    {
        ArrayList<MSI_Image> arrImages = new ArrayList<>();
        for (int count = 0; count < arrayImages.size(); count++)
        {
            MSI_Image image = arrayImages.get(count);
            if (image.get_type().equals(_utilities.IMG_JOBSITE_ADDITION))
                arrImages.add(image);
        }

        return arrImages;
    }

    private MSI_Image getStandardImage(ArrayList<MSI_Image> arrayImages, String imgType)
    {
        for (int count = 0; count < arrayImages.size(); count++)
        {
            MSI_Image image = arrayImages.get(count);
            if (image.get_type().equals(imgType))
                return image;
        }

        return null;
    }

    private MSI_Image SetImageObject(long inspectionId, String title, String type)
    {
        MSI_Image image = new MSI_Image();
        image.set_img_title(title);
        image.set_type(type);
        image.set_inspection_id(inspectionId);

        return image;
    }

    private void takePhoto_OnClickListener(ImageView item, final MSI_Image image, final int fixedTitle) {
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Save DB first
                saveDB();

                // Call image activity
                Intent intent = new Intent(getApplicationContext(), MSI_ImageModalActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                b.putParcelable("image", image);
                if (fixedTitle == 1)
                    b.putInt("fixed_title", 1);
                intent.putExtras(b);
                startActivityForResult(intent, _utilities.OPEN_IMAGE_CAPTURE_ACTIVITY);
            }
        });
    }

    private void SetMeasureUnitRadioButton(int value ){

        switch(value){
            case 0:
                ((RadioButton) findViewById(R.id.rb_measurement_unit_inches)).setChecked(true);
                break;

            case 1:
                ((RadioButton)findViewById(R.id.rb_measurement_unit_mm)).setChecked(true);
                break ;
        }

    }

    // Check that the jobsite condition values are within acceptable range.
    private boolean validRange(int condition) {
        if((condition >= CONDITION_LOW) && (condition <= CONDITION_HIGH)) {
            return true;
        }

        return false;
    }

    // Load jobsite conditions on opening screen.
    public void loadJobsiteConditions() {
        RadioGroup rgImpact = (RadioGroup) findViewById(R.id.rg_MSI_JC_Impact);
        RadioGroup rgAbrasive = (RadioGroup) findViewById(R.id.rg_MSI_JC_Abrasive);
        RadioGroup rgPacking = (RadioGroup) findViewById(R.id.rg_MSI_JC_Packing);
        RadioGroup rgMoisture = (RadioGroup) findViewById(R.id.rg_MSI_JC_Moisture);

        // Check that all conditions are valid first.
        if(!validRange(impact) || !validRange(abrasive) || !validRange(packing) || !validRange(moisture)) {
            AppLog.log("Error: Jobsite condition values are not valid!");
            return;
        }

        // Add a (+1) offset since the first child element is a TextView.
        ((RadioButton) rgImpact.getChildAt(impact + 1)).setChecked(true);
        ((RadioButton) rgAbrasive.getChildAt(abrasive + 1)).setChecked(true);
        ((RadioButton) rgPacking.getChildAt(packing + 1)).setChecked(true);
        ((RadioButton) rgMoisture.getChildAt(moisture + 1)).setChecked(true);

        EditText etComments = (EditText) findViewById(R.id.et_MSI_JC_overallObservationComments);
        etComments.setText(overallObservationComments);
    }

    // Handle selection of jobsite conditions.
    public void jobsiteConditionHandler(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        // Update condition based on which radiobutton was selected.
        switch(v.getId()) {
            // Impact
            case R.id.rb_MSI_JC_Impact_Low:
                if(checked) {
                    impact = CONDITION_LOW;
                }
                break;
            case R.id.rb_MSI_JC_Impact_Moderate:
                if(checked) {
                    impact = CONDITION_MODERATE;
                }
                break;
            case R.id.rb_MSI_JC_Impact_High:
                if(checked) {
                    impact = CONDITION_HIGH;
                }
                break;

            // Abrasive
            case R.id.rb_MSI_JC_Abrasive_Low:
                if(checked) {
                    abrasive = CONDITION_LOW;
                }
                break;
            case R.id.rb_MSI_JC_Abrasive_Moderate:
                if(checked) {
                    abrasive = CONDITION_MODERATE;
                }
                break;
            case R.id.rb_MSI_JC_Abrasive_High:
                if(checked) {
                    abrasive = CONDITION_HIGH;
                }
                break;

            // Packing
            case R.id.rb_MSI_JC_Packing_Low:
                if(checked) {
                    packing = CONDITION_LOW;
                }
                break;
            case R.id.rb_MSI_JC_Packing_Moderate:
                if(checked) {
                    packing = CONDITION_MODERATE;
                }
                break;
            case R.id.rb_MSI_JC_Packing_High:
                if(checked) {
                    packing = CONDITION_HIGH;
                }
                break;

            // Moisture
            case R.id.rb_MSI_JC_Moisture_Low:
                if(checked) {
                    moisture = CONDITION_LOW;
                }
                break;
            case R.id.rb_MSI_JC_Moisture_Moderate:
                if(checked) {
                    moisture = CONDITION_MODERATE;
                }
                break;
            case R.id.rb_MSI_JC_Moisture_High:
                if(checked) {
                    moisture = CONDITION_HIGH;
                }
                break;

            // Default
            default:
                break;
        }
    }

    private void saveDB()
    {
        Jobsite jobsite = _db.SelectJobsiteByInspectionId(_inspectionId, jobsiteId);
        if(jobsite != null) {

            // Impact, Abrasive, ...
            jobsite.SetImpact(impact);
            jobsite.SetAbrasive(abrasive);
            jobsite.SetPacking(packing);
            jobsite.SetMoisture(moisture);

            // Jobsite Comment
            jobsite.SetJobsiteComments(overallObservationComments);

            // Measurement Unit
            if(((RadioButton)findViewById(R.id.rb_measurement_unit_inches)).isChecked())
                jobsite.SetUOM(0);
            if(((RadioButton)findViewById(R.id.rb_measurement_unit_mm)).isChecked())
                jobsite.SetUOM(1);

            _db.updateJobsite(_inspectionId, jobsite);
        }
    }

//    // Show thumbnails for the additional images.
//    private void showThumbnail(ImageView photo1, String imageName) {
//        MSIImageHandler mImageHandler = new MSIImageHandler();
//        Uri fileUri = mImageHandler.getPhotoByName(imageName, equipmentNo);
//
//        if(fileUri != null) {
//            Drawable d = Drawable.createFromPath(fileUri.getPath());
//            photo1.setImageDrawable(d);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == OPEN_IMAGE_CAPTURE_ACTIVITY) {
            MSI_JobsiteConditionsActivity.this.recreate();
        }
    }

    public void measurementUnitSelection(View v) {

        boolean checked = ((RadioButton) v).isChecked();
        switch(v.getId()) {
            case R.id.rb_measurement_unit_mm:
                if(checked) {
                }
                break;
            case R.id.rb_measurement_unit_inches:
                if(checked) {
                }
                break;
            default:
                break;
        }
    }
}
