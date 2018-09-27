package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.business.MSI.MSI_Utilities;
import au.com.infotrak.infotrakmobile.datastorage.MSI.MSI_Model_DB_Manager;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_ComponentType;
import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image;

public class MSI_AdditionalRecordsActivity extends AppCompatActivity {

    // Database context
    MSI_Model_DB_Manager _db = new MSI_Model_DB_Manager(this);
    MSI_Utilities _utilities = new MSI_Utilities(this);
    String _side = _utilities.LEFT;
    MSI_ComponentType _componentType;

    // Equipment ID
    private long _inspectionId;
    private ArrayList<MSI_Image> _arrMeasurementYesNo = new ArrayList<>();
    private ArrayList<MSI_Image> _arrClearanceImages = new ArrayList<>();
    private ArrayList<MSI_Image> _arrObservationImages = new ArrayList<>();

    // Image
    Drawable _greenCamera = null;

    private void GetInputParameters()
    {
        Bundle b = getIntent().getExtras();
        _inspectionId = b.getLong("inspection_id");
        _side = b.getString("side");
        _componentType = b.getParcelable("componentType");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msi__additional_screen);

        // Prevent the keyboard from popping up automatically.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Parse all the input parameters sent to this screen.
        GetInputParameters();

        // Initialize variables
        _greenCamera = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.camera_green);
        _db = new MSI_Model_DB_Manager(this);

        // Measurement record, Y/N tool
        Boolean bHasYNData = RenderMeasurementYesNoRecords();

        // Measurement record, not Y/N tool
        Boolean bHasMeasurementData = RenderMeasurementData();

        // Observation record
        Boolean bHasObservData = RenderObservationData();

        // Validate Additional Screen
        if (!bHasYNData && !bHasMeasurementData && !bHasObservData) {

            // Jump to Mandatory Image screen
            Intent intent = new Intent(getApplicationContext(), MSI_MandatoryRecordsActivity.class);
            Bundle b = new Bundle();
            b.putLong("inspection_id", _inspectionId);
            b.putParcelable("componentType", _componentType);
            b.putString("side", _side);
            intent.putExtras(b);

            startActivity(intent);
            MSI_AdditionalRecordsActivity.this.finish();
        }

        // Screen header
        TextView headerText = (TextView) findViewById(R.id.text_MSIMeasurementEntry_Side);
        headerText.setText(_componentType.getComponentName() + " - Additional Records");

        // Back button
        Button backLink = (Button) findViewById(R.id.btn_MSI_TRGeneral_Back);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveDB();

                Intent msi = new Intent(getApplicationContext(), MSInspectionActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                msi.putExtras(b);
                startActivity(msi);

                MSI_AdditionalRecordsActivity.this.finish();
            }
        });

        // Next button
        Button nextLink = (Button) findViewById(R.id.btn_MSI_TRGeneral_Next);
        String nextItem = "Next";
        nextLink.setText(nextItem);
        nextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveDB();

                Intent intent = new Intent(getApplicationContext(), MSI_MandatoryRecordsActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspection_id", _inspectionId);
                b.putParcelable("componentType", _componentType);
                b.putString("side", _side);
                intent.putExtras(b);
                startActivity(intent);

                MSI_AdditionalRecordsActivity.this.finish();
            }
        });
    }

    public Boolean RenderMeasurementYesNoRecords()
    {
        // Get lubrication images
        ArrayList<MSI_Image> images = _db.selectAdditionalRecordImages(
                _inspectionId, _componentType.getComparTypeAuto(), _utilities.IMG_ADDITIONAL_MEASUREMENT_YES_NO_RECORD);
        ArrayList<ArrayList<MSI_Image>> arrImages = _utilities.ReArrangeLeftRightImage(images);     // size should be one

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout viewRoot = (LinearLayout) findViewById(R.id.MSI_measurement_yes_no);
        for (int count = 0; count < arrImages.size(); count++) {

            ///////////////////////
            // Initialize layout
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.msi__measurement_yes_no_record, (ViewGroup) viewRoot, false);

            ///////////////////////
            // Display left data
            MSI_Image imageLeft = arrImages.get(count).get(0);
            MSI_Image imageRight = arrImages.get(count).get(1);

            // Selection of Yes / No - Left
            RadioButton radioYesLeft = (RadioButton) layout.findViewById(R.id.MSI_selection_yes_left);
            RadioButton radioNoLeft = (RadioButton) layout.findViewById(R.id.MSI_selection_no_left);
//            radioYesLeft.setOnCheckedChangeListener(OnRadioButtonChecked(_utilities.LEFT));
//            radioNoLeft.setOnCheckedChangeListener(OnRadioButtonChecked(_utilities.LEFT));
            int yesnoLeft = imageLeft.get_is_yes_no();
            switch (yesnoLeft) {
                case 0:
                    radioYesLeft.setChecked(false);
                    radioNoLeft.setChecked(true);
                    break;
                case 1:
                    radioYesLeft.setChecked(true);
                    radioNoLeft.setChecked(false);
                    break;
            }
            imageLeft.set_select_yes(radioYesLeft);
            imageLeft.set_select_no(radioNoLeft);

            // Selection of Yes / No - Right
            RadioButton radioYesRight = (RadioButton) layout.findViewById(R.id.MSI_selection_yes_right);
            RadioButton radioNoRight = (RadioButton) layout.findViewById(R.id.MSI_selection_no_right);
//            radioYesRight.setOnCheckedChangeListener(OnRadioButtonChecked(_utilities.RIGHT));
//            radioNoRight.setOnCheckedChangeListener(OnRadioButtonChecked(_utilities.RIGHT));
            int yesnoRight = imageRight.get_is_yes_no();
            switch (yesnoRight) {
                case 0:
                    radioYesRight.setChecked(false);
                    radioNoRight.setChecked(true);
                    break;
                case 1:
                    radioYesRight.setChecked(true);
                    radioNoRight.setChecked(false);
                    break;
            }
            imageRight.set_select_yes(radioYesRight);
            imageRight.set_select_no(radioNoRight);

            ///////////////////////
            // Title
            TextView txtView = (TextView) layout.findViewById(R.id.MSI_img_frame_clearance_no);
            txtView.setText(imageLeft.get_record_title());

            // Listener for images
            ImageView imageLeftView = (ImageView) layout.findViewById(R.id.MSI_img_lubrication_left);
            if (_utilities.validateString(imageLeft.get_path()))
                imageLeftView.setImageDrawable(_greenCamera);
            takePhoto_OnClickListener(imageLeftView, imageLeft);

            ImageView imageRightView = (ImageView) layout.findViewById(R.id.MSI_img_lubrication_right);
            if (_utilities.validateString(imageRight.get_path()))
                imageRightView.setImageDrawable(_greenCamera);
            takePhoto_OnClickListener(imageRightView, imageRight);

            viewRoot.addView(layout);

            // Update global variables
            _arrMeasurementYesNo.add(imageLeft);
            _arrMeasurementYesNo.add(imageRight);
        }

        if (arrImages.size() > 0) return true;

        return false;
    }

    public Boolean RenderObservationData()
    {
        // Get observation record
        ArrayList<MSI_Image> images = _db.selectAdditionalImages(
                _inspectionId, _componentType.getComparTypeAuto(), _utilities.IMG_ADDITIONAL_OBSERVATION_RECORD);
        ArrayList<ArrayList<MSI_Image>> arrImages = _utilities.ReArrangeLeftRightImage(images);

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout viewRoot = (LinearLayout) findViewById(R.id.MSI_img_frame_clearance);
        for (int count = 0; count < arrImages.size(); count++) {

            ///////////////////////
            // Initialize layout
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.msi__observation_record, (ViewGroup) viewRoot, false);
            viewRoot.addView(layout);

            //////////////////
            // Display data
            MSI_Image imageLeft = arrImages.get(count).get(0);
            MSI_Image imageRight = arrImages.get(count).get(1);

            // Title left
            TextView titleLeft = (TextView) layout.findViewById(R.id.MSI_title_left);
            titleLeft.setText(imageLeft.get_record_title());

            // Title right
            TextView titleRight = (TextView) layout.findViewById(R.id.MSI_title_right);
            titleRight.setText(imageRight.get_record_title());

            // Input left
            EditText inputLeft = (EditText) layout.findViewById(R.id.MSI_input_left);
            if (_utilities.validateString(imageLeft.get_input_value()))
                inputLeft.setText(imageLeft.get_input_value());
            imageLeft.set_input_view(inputLeft);

            // Input right
            EditText inputRight = (EditText) layout.findViewById(R.id.MSI_input_right);
            if (_utilities.validateString(imageRight.get_input_value()))
                inputRight.setText(imageRight.get_input_value());
            imageRight.set_input_view(inputRight);

            //////////////////////
            // Set listeners
            // Left image
            ImageView imgLeftView = (ImageView) layout.findViewById(R.id.MSI_img_left);
            if (_utilities.validateString(imageLeft.get_path()))
                imgLeftView.setImageDrawable(_greenCamera);
            takePhoto_OnClickListener(imgLeftView, imageLeft);

            // Right image
            ImageView imgRightView = (ImageView) layout.findViewById(R.id.MSI_img_right);
            if (_utilities.validateString(imageRight.get_path()))
                imgRightView.setImageDrawable(_greenCamera);
            takePhoto_OnClickListener(imgRightView, imageRight);

            // Update global variable
            _arrObservationImages.add(imageLeft);
            _arrObservationImages.add(imageRight);
        }

        if (arrImages.size() > 0) return true;

        return false;
    }

    public Boolean RenderMeasurementData()    {

        // Get clearance images
        ArrayList<MSI_Image> images = _db.selectAdditionalImages(_inspectionId, _componentType.getComparTypeAuto(), _utilities.IMG_ADDITIONAL_MEASUREMENT_RECORD);
        ArrayList<ArrayList<MSI_Image>> arrImages = _utilities.ReArrangeLeftRightImage(images);

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout viewRoot = (LinearLayout) findViewById(R.id.MSI_img_frame_clearance);
        for (int count = 0; count < arrImages.size(); count++) {

            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.msi__measurement_record, (ViewGroup) viewRoot, false);

            //////////////////
            // Display data
            MSI_Image imageLeft = arrImages.get(count).get(0);
            MSI_Image imageRight = arrImages.get(count).get(1);

            // Input left
            EditText inputLeft = (EditText) layout.findViewById(R.id.MSI_img_frame_clearance_input_left);
            if (_utilities.validateString(imageLeft.get_input_value()))
                inputLeft.setText(imageLeft.get_input_value());
            imageLeft.set_input_view(inputLeft);

            // Input right
            EditText inputRight = (EditText) layout.findViewById(R.id.MSI_img_frame_clearance_input_right);
            if (_utilities.validateString(imageRight.get_input_value()))
                inputRight.setText(imageRight.get_input_value());
            imageRight.set_input_view(inputRight);

            ///////////////////////
            // Title
            TextView txtView = (TextView) layout.findViewById(R.id.MSI_img_frame_clearance_no);
            txtView.setText(imageLeft.get_record_title());

            //////////////////////
            // Set listeners
            // Left image
            ImageView imgLeftView = (ImageView) layout.findViewById(R.id.MSI_img_frame_clearance_left);
            if (_utilities.validateString(imageLeft.get_path()))
                imgLeftView.setImageDrawable(_greenCamera);
            takePhoto_OnClickListener(imgLeftView, imageLeft);

            // Right image
            ImageView imgRightView = (ImageView) layout.findViewById(R.id.MSI_img_frame_clearance_right);
            if (_utilities.validateString(imageRight.get_path()))
                imgRightView.setImageDrawable(_greenCamera);
            takePhoto_OnClickListener(imgRightView, imageRight);

            // Update global variable
            _arrClearanceImages.add(imageLeft);
            _arrClearanceImages.add(imageRight);

            viewRoot.addView(layout);
        }

        if (arrImages.size() > 0) return true;

        return false;
    }

    private void takePhoto_OnClickListener(ImageView item, final MSI_Image image) {
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveDB();

                Intent intent = new Intent(getApplicationContext(), MSI_ImageModalActivity.class);
                Bundle b = new Bundle();
                b.putLong("inspectionId", _inspectionId);
                b.putParcelable("image", image);
                intent.putExtras(b);
                startActivityForResult(intent, _utilities.OPEN_IMAGE_CAPTURE_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == _utilities.OPEN_IMAGE_CAPTURE_ACTIVITY) {

            Intent intent = new Intent(getApplicationContext(), MSI_AdditionalRecordsActivity.class);
            Bundle b = new Bundle();
            b.putLong("inspection_id", _inspectionId);
            b.putParcelable("componentType", _componentType);
            b.putString("side", _side);
            intent.putExtras(b);
            this.finish();
            startActivity(intent);
        }
    }

    public void SaveDB()
    {
        // Lubrication selection
        for (int i = 0; i < _arrMeasurementYesNo.size(); i++)
        {
            MSI_Image image = _arrMeasurementYesNo.get(i);
            RadioButton selectYes = (RadioButton) image.get_select_yes();
            RadioButton selectNo = (RadioButton) image.get_select_no();
            if (selectYes.isChecked())
            {
                image.set_is_yes_no(1);
            } else if (selectNo.isChecked())
            {
                image.set_is_yes_no(0);
            }
        }
        _db.updateAdditionalYesNoSelection(_inspectionId, _componentType.getComparTypeAuto(), _arrMeasurementYesNo);

        // Clearance input values
        for (int i = 0; i < _arrClearanceImages.size(); i++)
        {
            MSI_Image image = _arrClearanceImages.get(i);
            EditText inputView = (EditText) image.get_input_view();
            String input = String.valueOf(inputView.getText());
            if (_utilities.validateString(input))
            {
                image.set_input_value(input);
            }
        }
        _db.updateAdditionalInput(_inspectionId, _componentType.getComparTypeAuto(), _arrClearanceImages);

        // Observation notes
        for (int i = 0; i < _arrObservationImages.size(); i++)
        {
            MSI_Image image = _arrObservationImages.get(i);
            EditText inputView = (EditText) image.get_input_view();
            String input = String.valueOf(inputView.getText());
            if (_utilities.validateString(input))
            {
                image.set_input_value(input);
            }
        }
        _db.updateAdditionalObservationInput(_inspectionId, _componentType.getComparTypeAuto(), _arrObservationImages);
    }

//    /**
//     *  Clear keyboard focus when touching outside of the EditText.
//     * @param event
//     * @return
//     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (v instanceof EditText) {
//                Rect outRect = new Rect();
//                v.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
//                    v.clearFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        }
//        return super.dispatchTouchEvent( event );
//    }
//

//    /**
//     * Set up the image views and text views for the Mandatory and Additional images.
//     */
//    private void initialiseImages()
//    {
//        int MAX_ITEMS_FRAME_CLEARANCE = 3;
//        int MAX_ITEMS_ADDITIONAL_IMAGES = 4;
//
//        // Get the icon to be used for indicating that an measurement image is available.
//        greenCamera = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.camera_green);
//
//        // Get the list of photos for this equipment.
//        photos = dbContext.GetMSIPhotosByEquipmentId(equipmentId);
//
//        // Index for the ImageViews.
//        int imageIndex = 1;
//
//        // Sufficient Lubrication
//        setUpImageViews(IMAGE_PREFIX_SUFFICIENT_LUBRICATION, "", "L", imageIndex);
//        setUpImageViews(IMAGE_PREFIX_SUFFICIENT_LUBRICATION, "", "R", imageIndex + 1);
//        imageIndex += 2;
//
//        // Frame Clearance
//        for(int i=0; i<MAX_ITEMS_FRAME_CLEARANCE; i++)
//        {
//            setUpImageViews(IMAGE_PREFIX_FRAME_CLEARANCE, String.valueOf(i), "L", imageIndex);
//            setUpImageViews(IMAGE_PREFIX_FRAME_CLEARANCE, String.valueOf(i), "R", imageIndex + 1);
//            imageIndex += 2;
//        }
//
//        // Additional Images
//        for(int j=0; j<MAX_ITEMS_ADDITIONAL_IMAGES; j++)
//        {
//            // Indexed from 1+
//            setUpImageViewsAdditional(ADDITIONAL_IMAGE_PREFIX, String.valueOf(j+1));
//        }
//    }

//    /**
//     * Show thumbnails for the additional images.
//     * @param photo1        The ImageView containing the photo.
//     * @param imageName     The name of the Image.
//     */
//    private void showThumbnail(ImageView photo1, String imageName)
//    {
//        Uri fileUri = mImageHandler.getPhotoByName(imageName, equipmentNo);
//
//        if(fileUri != null) {
//            Drawable d = Drawable.createFromPath(fileUri.getPath());
//            photo1.setImageDrawable(d);
//        }
//    }
}
