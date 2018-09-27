package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.Equipment;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;

public class MSI_EquipmentPhotosActivity extends AppCompatActivity{

    // Database context
    MSI_Model_DB_Manager _db;
    MSI_Utilities _utilities = new MSI_Utilities(this);

    // Equipment ID and serial No
    private long _inspectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msi__equipment_photos);

        // Prevent the keyboard from popping up automatically.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get the _inspectionId parameter that was passed to this screen.
        Bundle bundle = getIntent().getExtras();
        _inspectionId = bundle.getLong("inspectionId");

        // Get a new _db for this activity.
        _db = new MSI_Model_DB_Manager(getApplicationContext());

        // Get the equipment Serial No.
        final Equipment equipment = _db.SelectEquipmentByInspectionId(_inspectionId);

        /////////////////////
        // Render images
        RenderStandardImages();

        ///////////////////
        // Render Notes
        RenderNotes(equipment);

        ///////////////////////
        // Navigation buttons
        RenderNavButtons();
    }

    private void RenderStandardImages()
    {
        // Get images
        ArrayList<MSI_Image> arrImages = _db.selectEquipmentImages(_inspectionId);

        // Mandatory images
        Drawable greenCamera = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.camera_green);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout viewRoot = (LinearLayout) findViewById(R.id.MSI_img_list);
        for (int count = 0; count < arrImages.size(); count++) {

            final MSI_Image image = arrImages.get(count);
            if (image.get_type().equals(_utilities.IMG_EQUIPMENT_ADDITION))
            {
                continue;
            }

            ///////////////////////
            // Initialize layout
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.msi__equipment_mandatory_images, (ViewGroup) viewRoot, false);
            TextView txtView = (TextView) layout.findViewById(R.id.MSI_img_type);
            txtView.setText(arrImages.get(count).get_type().replace(_utilities.EQUIPMENT + " - ", ""));

            //////////////////
            // Display data
            ImageView imgView = (ImageView) layout.findViewById(R.id.msi_img);
            if (_utilities.validateString(image.get_path()))
                imgView.setImageDrawable(greenCamera);

            //////////////////////
            // Set listeners
            takePhoto_OnClickListener(imgView, image, 1);

            // Add view
            viewRoot.addView(layout);
        }

        ////////////////////////
        // Additional images
        RenderAdditionalImages(arrImages);
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

            MSI_Image image = SetImageObject(_inspectionId, "", _utilities.IMG_EQUIPMENT_ADDITION);
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
            if (image.get_type().equals(_utilities.IMG_EQUIPMENT_ADDITION))
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == _utilities.OPEN_IMAGE_CAPTURE_ACTIVITY) {
            MSI_EquipmentPhotosActivity.this.recreate();
        }
    }

    // Load available data from the equipment record.
    private void RenderNotes(Equipment equipment) {
        if (equipment != null) {
            EditText etGeneralNotes = (EditText) findViewById(R.id.et_MSI_EGN_EquipmentGeneralNotes);
            etGeneralNotes.setText(equipment.GetEquipmentGeneralNotes());
        }
    }

    // Save entered data to the equipment record.
    private void saveDB() {
        EditText etEquipmentGeneralNotes = (EditText) findViewById(R.id.et_MSI_EGN_EquipmentGeneralNotes);
        String equipmentGeneralNotes = etEquipmentGeneralNotes.getText().toString();
        _db.updateEquipmentNote(_inspectionId, equipmentGeneralNotes);
    }

    public void RenderNavButtons()
    {
        // Navigate back to the menu.
        Button backLink = (Button) findViewById(R.id.btnMSIEquipmenGeneralNotes_Back);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDB();
                MSI_EquipmentPhotosActivity.this.finish();
            }
        });

        // Navigate to the Jobsite Conditions screen.
        Button nextLink = (Button) findViewById(R.id.btnMSIEquipmenGeneralNotes_Next);
        nextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveDB();

                Intent jcIntent = new Intent(getApplicationContext(), MSI_JobsiteConditionsActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                jcIntent.putExtras(b);
                startActivity(jcIntent);
                MSI_EquipmentPhotosActivity.this.finish();
            }
        });
    }
}
