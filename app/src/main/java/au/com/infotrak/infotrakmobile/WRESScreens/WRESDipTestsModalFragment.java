package au.com.infotrak.infotrakmobile.WRESScreens;

/**
 * Created by PaulN on 22/03/2018.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.adapters.WRES.WRESSpinnerAdapter;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESImage;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPin;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESPinCondition;

public class WRESDipTestsModalFragment extends DialogFragment implements Parcelable {

    private WRESDataContext _db = null;
    private WRESUtilities _utilities = new WRESUtilities(null);
    private Context _context = null;
    private WRESPin _pin = new WRESPin();
    private View _rootView = null;

    // Common data which doesn't relate to particular pin
    private byte[] _linkImage;
    private ArrayList<WRESPinCondition> _arrConditions = null;
    private long _totalPin;

    // Camera
    WRESImageCaptureDialog _dialogPhoto = null;
    Uri _fileUri = null;
    public int _intOneImgWidth = 60; // dp
    public int _intImgFirstLine = 2;
    public int _intImgPerLine = 11;
    public float _maxWidth = 0;

    // Communicate with UI
    private final Handler _handlerModal = new Handler();

    private ProgressDialog _progressDialog;

    @SuppressLint("ValidFragment")
    protected WRESDipTestsModalFragment(Parcel in) {
        _pin = in.readParcelable(WRESPin.class.getClassLoader());
        _linkImage = in.createByteArray();
        _arrConditions = in.createTypedArrayList(WRESPinCondition.CREATOR);
        _totalPin = in.readLong();
        _fileUri = in.readParcelable(Uri.class.getClassLoader());
        _intOneImgWidth = in.readInt();
        _intImgFirstLine = in.readInt();
        _intImgPerLine = in.readInt();
        _maxWidth = in.readFloat();
    }

    public static final Creator<WRESDipTestsModalFragment> CREATOR = new Creator<WRESDipTestsModalFragment>() {
        @Override
        public WRESDipTestsModalFragment createFromParcel(Parcel in) {
            return new WRESDipTestsModalFragment(in);
        }

        @Override
        public WRESDipTestsModalFragment[] newArray(int size) {
            return new WRESDipTestsModalFragment[size];
        }
    };

    private void SetProgressBar() {
        _progressDialog = new ProgressDialog(_context);
        _progressDialog.setMessage(getString(R.string.text_data_loading));
        _progressDialog.show();
    }
    private void HideProgressBar()
    {
        _progressDialog.dismiss();
    }

    public WRESDipTestsModalFragment() {}

    /////////////////////////////////////////
    // Activity and fragment communication
    public static WRESDipTestsModalFragment newInstance(WRESPin link) {
        WRESDipTestsModalFragment fragment = new WRESDipTestsModalFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("data", link);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(_pin, i);
        parcel.writeByteArray(_linkImage);
        parcel.writeTypedList(_arrConditions);
        parcel.writeLong(_totalPin);
        parcel.writeParcelable(_fileUri, i);
        parcel.writeInt(_intOneImgWidth);
        parcel.writeInt(_intImgFirstLine);
        parcel.writeInt(_intImgPerLine);
        parcel.writeFloat(_maxWidth);
    }

    // Container Activity must implement this interface
    public interface OnFragmentListener {
        public void onCloseFragment();
        public void onUpdateFragment(Uri fileUri, WRESImageCaptureDialog dialogPhoto);
    }

    OnFragmentListener mCallback;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentListener");
        }
    }


    //////////////////////////////////////
    // Fragment content
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            _pin = bundle.getParcelable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Fragment views
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        _rootView = inflater.inflate(R.layout.wres_dip_tests_modal_fragment, container, false);
        _context = _rootView.getContext();
        getDialog().setCanceledOnTouchOutside(false);   // Not allow to close when tabbing the screen

        // Fragment width
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        _maxWidth = size.x - _utilities.convertDpToPixel(20);
        _rootView.setMinimumWidth((int) _maxWidth);

        // Fragment objects
        SetCommonObject();

        // Fragment's "Cancel" button
        Button btnClose = (Button) _rootView.findViewById(R.id.wres_modal_close);
        btnClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        // Update DB
                        updateDBPin();

                        // Previous screen
                        mCallback.onCloseFragment();       // Communicate with Activity
                    }
                });

        // Fragment's previous/ next button
        SetPreviousNextBtn();

        // Fragment's Header
        SetHeader();

        // Fragment's Condition area
        SetCondition();

        // Fragment's recommendation area
        SetRecommendation();

        // Fragment's comment area
        SetComment();

        return _rootView;
    }

    private void InitialDialogPhoto(WRESImage imageObj)
    {
        _dialogPhoto = new WRESImageCaptureDialog(getActivity(), imageObj, _pin.get_inspection_id());
        _dialogPhoto.setDialogResult(new WRESImageCaptureDialog.OnMyDialogResult(){

            @Override
            public void finish(String result){
                _fileUri = Uri.parse(result);
            }

            @Override
            public void saveImage(WRESImage image) {
                // INSERT DB
                String strImgPath = image.get_image_path();
                File file = new File(strImgPath);
                if(file.exists())
                    _db.insertPinImg(_pin, image);

                // Put image in
                ReArrangeImgLayout();

                // Close dialog
                _dialogPhoto.dismiss();
                EnableDeviceRotation();
            }

            @Override
            public void updateImage(WRESImage image) {
                // Update DB
                File file = new File(image.get_image_path());
                if(file.exists())
                    _db.updatePinImage(image);

                // Close dialog
                _dialogPhoto.dismiss();
                EnableDeviceRotation();
            }

            @Override
            public void removeImage(String strImgPath) {
                _db.deletePinImg(_pin.get_id(), strImgPath);
                ReArrangeImgLayout();

                // Close dialog
                _dialogPhoto.dismiss();
                EnableDeviceRotation();
            }

            @Override
            public void closeImage() {
                _dialogPhoto.dismiss();
                EnableDeviceRotation();
            }
        });
    }

    private void SetCommonObject()
    {
        _db = new WRESDataContext(_context);
        _utilities = new WRESUtilities(_context);
        _arrConditions = _utilities.GetLinkConditionArray(_pin.get_arrConditionDescr(), _pin.get_arrConditionId());
        _linkImage = _pin.get_linkImage();
        _totalPin = _pin.get_totalPin();
    }

    private void SetCondition()
    {

        /////////////////////
        // Dip level
        EditText txtLevel = (EditText) _rootView.findViewById(R.id.wres_dip_level);
        if (_pin.get_dip_test_level() != -1)
        {
            txtLevel.setText(Integer.toString(_pin.get_dip_test_level()));
        } else
        {
            txtLevel.setText("");
        }

        ////////////////////
        // Condition button
        Spinner spinnerCondition = (Spinner) _rootView.findViewById(R.id.wres_link_condition);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                _context,
                R.layout.wres_spinner_layout,
                _arrConditions);
        spinnerCondition.setAdapter(
                new WRESSpinnerAdapter(
                        spinnerArrayAdapter,
                        R.layout.wres_spinner_row_nothing_selected,
                        R.layout.wres_spinner_row_something_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        _context));

        // Set spinner value
        long conditionId = _pin.get_condition();
        if (conditionId > 0)
        {
            int spinnerPosition = -1;
            for (int i = 0; i < _arrConditions.size(); i++)
            {
                if (_arrConditions.get(i).getCondition_id() == conditionId) {
                    spinnerPosition = i + 1;
                    break;
                }
            }

            if (spinnerPosition != -1)
                spinnerCondition.setSelection(spinnerPosition);
        }

        // Set spinner listener
        spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                WRESPinCondition selectedItem = (WRESPinCondition) parentView.getItemAtPosition(position);
                if (selectedItem != null)
                    _pin.set_condition(selectedItem.getCondition_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private void SetRecommendation() {
        EditText editTxt = (EditText) _rootView.findViewById(R.id.wres_recommendation);
        if (_pin.get_recommendation() != null)
        {
            editTxt.setText(_pin.get_recommendation());
        } else
        {
            editTxt.setText("");
        }
    }

    private void SetComment() {
        EditText editTxt = (EditText) _rootView.findViewById(R.id.wres_comment);
        if (_pin.get_comment() != null)
        {
            editTxt.setText(_pin.get_comment());
        } else
        {
            editTxt.setText("");
        }
    }

    private void SetHeader()
    {
        // Get Image from Link component
        ImageView imgView = (ImageView) _rootView.findViewById(R.id.wres_modal_left_img);
        if (_linkImage == null)
        {
            imgView.setImageResource(R.mipmap.wres_no_image);
        } else {
            Bitmap bMap = BitmapFactory.decodeByteArray(_linkImage, 0, _linkImage.length);
            imgView.setImageBitmap(bMap);
        }

        // Detail
        TextView typeValue = (TextView) _rootView.findViewById(R.id.wres_link_position);
        typeValue.setText("POSITION " + _pin.get_link_auto());

        // Camera button
        ImageView btnCamera = (ImageView) _rootView.findViewById(R.id.wres_modal_take_photo);
        btnCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        onTakephoto();
                    }
                });

        // Image area
        LinearLayout line1 = (LinearLayout) _rootView.findViewById(R.id.wres_modal_img_line1);
        LinearLayout lineMore = (LinearLayout) _rootView.findViewById(R.id.wres_modal_img_line_more);
        ArrayList<WRESImage> arrImgs = _db.selectPinImg(_pin);
        SetImglayout(arrImgs, line1, lineMore);
    }

    private void onTakephoto() {

        // Lock orientation
        DisableDeviceRotation();

        // Show dialog
        InitialDialogPhoto(new WRESImage("", "", "", ""));
        _dialogPhoto.show();
    }

    private void ReCalculateMoreImg()
    {
        // Size of 1 image
        float oneImgSize = _utilities.convertDpToPixel(_intOneImgWidth);
        float maxWidthLineOne = _maxWidth - _utilities.convertDpToPixel(260);
        _intImgFirstLine = (int) (maxWidthLineOne / oneImgSize);

        // more line
        _intImgPerLine = (int) (_maxWidth / oneImgSize);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == _utilities.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                File localFile = _utilities.getLocalFilePath(_pin.get_inspection_id());
                try {

                    // Rotate
                    _utilities.onPictureTaken(_fileUri);

                    // Resize
                    _utilities.resize(_fileUri);

                    // Copy into local folder
                    _utilities.copyFile(new File(_fileUri.getPath()), localFile);

                    // Delete file
                    new File(_fileUri.getPath()).delete();

                    // Show dialog image
                    String strImgPath = localFile.getPath();
                    _dialogPhoto.showImage(strImgPath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void SetImglayout(ArrayList<WRESImage> arrImages, View viewLine1, View viewImgMore)
    {
        ReCalculateMoreImg();
        LayoutInflater inflater = LayoutInflater.from(_context);

        /////////////////////////////////
        // Add more photo row or not
        // 1st line
        int intImgAll = arrImages.size();
        int countImg = 0;
        for (countImg = 0; countImg < intImgAll; countImg++)
        {
            if (countImg == _intImgFirstLine) break;

            // Insert into line 1
            final WRESImage image = arrImages.get(countImg);
            final ImageView imageView = (ImageView) inflater.inflate(R.layout.wres_img_more, (ViewGroup) viewLine1, false);
            final File imgFile = new  File(image.get_image_path());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    InitialDialogPhoto(image);
                    _dialogPhoto.show();
                }
            });

            ((ViewGroup) viewLine1).addView(imageView);
        }

        // More line
        int intMoreImgs = intImgAll - _intImgFirstLine;
        while (intMoreImgs > 0)
        {
            // Insert
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.wres_initialdetail_imgs_more, (ViewGroup) viewImgMore, false);
            for (int count = 0; count < _intImgPerLine; count++)
            {
                if (intMoreImgs == 0) break;

                ImageView imgView = (ImageView) inflater.inflate(R.layout.wres_img_more, layout, false);

                final WRESImage image = arrImages.get(countImg);
                final File imgFile = new  File(image.get_image_path());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgView.setImageBitmap(myBitmap);
                imgView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        InitialDialogPhoto(image);
                        _dialogPhoto.show();
                    }
                });

                layout.addView(imgView);

                // Reset
                countImg++;
                intMoreImgs = intMoreImgs - 1;
            }

            ((ViewGroup) viewImgMore).addView(layout);
        }
    }

    private void ReArrangeImgLayout()
    {
        // Put image in
        LinearLayout line1 = (LinearLayout) _rootView.findViewById(R.id.wres_modal_img_line1);
        LinearLayout lineMore = (LinearLayout) _rootView.findViewById(R.id.wres_modal_img_line_more);
        line1.removeAllViews();
        lineMore.removeAllViews();
        ArrayList<WRESImage> photos = _db.selectPinImg(_pin);

        // Image layout
        SetImglayout(photos, line1, lineMore);
    }

    private void SetPreviousNextBtn()
    {
        //////////////////////
        // Previous button
        Button btnPrevious = (Button) _rootView.findViewById(R.id.wres_previous_pin);
        btnPrevious.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        if (_pin.get_link_auto() - 1 > 0) {
                            updatePin();
                            WRESPin previousPin = _db.updateAndSelectPreviousPin(_pin);
                            previousPin.set_totalPin(_totalPin);
                            if (previousPin != null) {

                                _pin = previousPin;

                                // Reset layout
                                RefreshLayout();
                            }
                        }

                    }
                });
        btnPrevious.setText("PREVIOUS PIN (" + (_pin.get_link_auto() - 1) + "/" + _totalPin + ")");
        if (_pin.get_link_auto() == 1)
            btnPrevious.setVisibility(View.INVISIBLE);
        else
            btnPrevious.setVisibility(View.VISIBLE);

        //////////////////////
        // Next button
        Button btnNext = (Button) _rootView.findViewById(R.id.wres_next_pin);
        btnNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        if (_pin.get_link_auto() + 1 <= _totalPin) {
                            updatePin();
                            WRESPin nextPin = _db.updateAndSelectNextPin(_pin);
                            nextPin.set_totalPin(_totalPin);
                            if (nextPin != null) {

                                _pin = nextPin;

                                // Reset layout
                                RefreshLayout();
                            }
                        }

                    }
                });
        btnNext.setText("NEXT PIN (" + (_pin.get_link_auto() + 1) + "/" + _totalPin + ")");
        if (_pin.get_link_auto() ==  _totalPin)
            btnNext.setVisibility(View.INVISIBLE);
        else
            btnNext.setVisibility(View.VISIBLE);
    }

    private void RefreshLayout()
    {
        // Fragment's previous/ next button
        Button btnPrevious = (Button) _rootView.findViewById(R.id.wres_previous_pin);
        btnPrevious.setText("PREVIOUS PIN (" + (_pin.get_link_auto() - 1) + "/" + _totalPin + ")");
        if (_pin.get_link_auto() == 1)
            btnPrevious.setVisibility(View.INVISIBLE);
        else
            btnPrevious.setVisibility(View.VISIBLE);

        Button btnNext = (Button) _rootView.findViewById(R.id.wres_next_pin);
        btnNext.setText("NEXT PIN (" + (_pin.get_link_auto() + 1) + "/" + _totalPin + ")");
        if (_pin.get_link_auto() ==  _totalPin)
            btnNext.setVisibility(View.INVISIBLE);
        else
            btnNext.setVisibility(View.VISIBLE);

        // Fragment's Header
        TextView typeValue = (TextView) _rootView.findViewById(R.id.wres_link_position);
        typeValue.setText("POSITION " + _pin.get_link_auto());
        ReArrangeImgLayout();

        // Fragment's Condition area
        SetCondition();

        // Fragment's recommendation area
        SetRecommendation();

        // Fragment's comment area
        SetComment();
    }

    private void updatePin()
    {
        // Dip level
        EditText level = (EditText) _rootView.findViewById(R.id.wres_dip_level);
        Integer intLevel = _utilities.IsInteger(level.getText().toString());
        if (intLevel != null)
        {
            _pin.set_dip_test_level(intLevel);
        }

        // Recommendations
        EditText recommendation = (EditText) _rootView.findViewById(R.id.wres_recommendation);
        _pin.set_recommendation(recommendation.getText().toString());

        // Comment
        EditText comment = (EditText) _rootView.findViewById(R.id.wres_comment);
        _pin.set_comment(comment.getText().toString());
    }

    private void updateDBPin()
    {
        // UPDATE PIN
        updatePin();
        _db.updatePinInfo(_pin);
    }


    //////////////
    // Utilities
    private void open_img_modal(final String imgPath) {

        // custom dialog
        final Dialog dialog = new Dialog(_context);
        dialog.setContentView(R.layout.wres_img_modal);

        // set the custom dialog components - text, image and button
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);
        image.setImageBitmap(myBitmap);

        // Close button
        Button closeButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Delete button
        Button deleteButton = (Button) dialog.findViewById(R.id.dialogButtonRemove);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _db.deletePinImg(_pin.get_id(), imgPath);
                ReArrangeImgLayout();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /////////////////////////////
    // Prevent device rotation //
    /////////////////////////////
    private void DisableDeviceRotation() {
        if (getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    private void EnableDeviceRotation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //Restore the fragment's state here
            _fileUri = Uri.parse(savedInstanceState.getString("fileUri"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fileUri", String.valueOf(_fileUri));

        // update fragment in Activity
        mCallback.onUpdateFragment(_fileUri, _dialogPhoto);
    }
}