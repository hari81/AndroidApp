package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.DataObject;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;

public class WRESHeaderFlowFragment extends android.support.v4.app.Fragment {

    Context _context = null;

    private DataObject data = new DataObject(
            1,
            "",
            "",
            ""
    );

    // COMMUNICATION INTERFACE
    OnFragmentListener mCallback;
    public interface OnFragmentListener {
        public WRESEquipment SaveDB();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentListener");
        }
    }

    // CREATE FRAGMENT
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            data = bundle.getParcelable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.wres_step_header_flow_fragment, container, false);
        _context = rootView.getContext();

        RelativeLayout layout1Off = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_1_off);
        RelativeLayout layout1On = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_1_on);
        RelativeLayout layout1Done = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_1_done);
        RelativeLayout layout2Off = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_2_off);
        RelativeLayout layout2On = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_2_on);
        RelativeLayout layout2Done = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_2_done);
        RelativeLayout layout3Off = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_3_off);
        RelativeLayout layout3On = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_3_on);
        RelativeLayout layout3Done = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_3_done);
        RelativeLayout layout4Off = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_4_off);
        RelativeLayout layout4On = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_4_on);
        RelativeLayout layout4Done = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_4_done);
        RelativeLayout layout5Off = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_5_off);
        RelativeLayout layout5On = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_5_on);
        RelativeLayout layout5Done = (RelativeLayout) rootView.findViewById(R.id.step_fragment_title_5_done);
        if (data.intStepNo == 1) {
            layout1Off.setVisibility(View.GONE);
            layout1On.setVisibility(View.VISIBLE);
            layout1Done.setVisibility(View.GONE);
        } else if (data.intStepNo == 2) {
            layout1Off.setVisibility(View.GONE);
            layout1On.setVisibility(View.GONE);
            layout1Done.setVisibility(View.VISIBLE);
            layout2Off.setVisibility(View.GONE);
            layout2On.setVisibility(View.VISIBLE);
            layout2Done.setVisibility(View.GONE);
        } else if (data.intStepNo == 3) {
            layout1Off.setVisibility(View.GONE);
            layout1On.setVisibility(View.GONE);
            layout1Done.setVisibility(View.VISIBLE);
            layout2Off.setVisibility(View.GONE);
            layout2On.setVisibility(View.GONE);
            layout2Done.setVisibility(View.VISIBLE);
            layout3Off.setVisibility(View.GONE);
            layout3On.setVisibility(View.VISIBLE);
            layout3Done.setVisibility(View.GONE);
        } else if (data.intStepNo == 4) {
            layout1Off.setVisibility(View.GONE);
            layout1On.setVisibility(View.GONE);
            layout1Done.setVisibility(View.VISIBLE);
            layout2Off.setVisibility(View.GONE);
            layout2On.setVisibility(View.GONE);
            layout2Done.setVisibility(View.VISIBLE);
            layout3Off.setVisibility(View.GONE);
            layout3On.setVisibility(View.GONE);
            layout3Done.setVisibility(View.VISIBLE);
            layout4Off.setVisibility(View.GONE);
            layout4On.setVisibility(View.VISIBLE);
            layout4Done.setVisibility(View.GONE);
        } else if (data.intStepNo == 5) {
            layout1Off.setVisibility(View.GONE);
            layout1On.setVisibility(View.GONE);
            layout1Done.setVisibility(View.VISIBLE);
            layout2Off.setVisibility(View.GONE);
            layout2On.setVisibility(View.GONE);
            layout2Done.setVisibility(View.VISIBLE);
            layout3Off.setVisibility(View.GONE);
            layout3On.setVisibility(View.GONE);
            layout3Done.setVisibility(View.VISIBLE);
            layout4Off.setVisibility(View.GONE);
            layout4On.setVisibility(View.GONE);
            layout4Done.setVisibility(View.VISIBLE);
            layout5Off.setVisibility(View.GONE);
            layout5On.setVisibility(View.VISIBLE);
            layout5Done.setVisibility(View.GONE);
        }


        // Set data
        TextView txtSerialno = (TextView) rootView.findViewById(R.id.wres_txt_serialno);
        txtSerialno.setText(data.strChain);
        TextView txtCustomer = (TextView) rootView.findViewById(R.id.wres_txt_customer);
        txtCustomer.setText(data.strCust);
        TextView txtJobsite = (TextView) rootView.findViewById(R.id.wres_txt_jobiste);
        txtJobsite.setText(data.strJobsite);

        // Set listener
        layout1Off.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep1Screen();
                    }
                });
        layout1On.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep1Screen();
                    }
                });
        layout1Done.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep1Screen();
                    }
                });
        layout2Off.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep2Screen();
                    }
                });
        layout2On.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep2Screen();
                    }
                });
        layout2Done.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep2Screen();
                    }
                });
        layout3Off.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep3Screen();
                    }
                });
        layout3On.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep3Screen();
                    }
                });
        layout3Done.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep3Screen();
                    }
                });
        layout4Off.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep4Screen();
                    }
                });
        layout4On.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep4Screen();
                    }
                });
        layout4Done.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep4Screen();
                    }
                });
        layout5Off.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep5Screen();
                    }
                });
        layout5On.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep5Screen();
                    }
                });
        layout5Done.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        launchStep5Screen();
                    }
                });

        return rootView;

    }

    public static WRESHeaderFlowFragment newInstance(DataObject dataObj) {

        WRESHeaderFlowFragment fragment = new WRESHeaderFlowFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("data", dataObj);
        fragment.setArguments(arguments);

        return fragment;
    }

    ///////////////////////
    // Screen navigation //
    ///////////////////////
    public void launchStep1Screen() {

        WRESEquipment equipment = mCallback.SaveDB();

        Intent intent = new Intent(_context, WRESInitialDetailsActivity.class);
        intent.putExtra("equipment", equipment);
        startActivity(intent);
        getActivity().finish();
    }

    public void launchStep2Screen() {

        WRESEquipment equipment = mCallback.SaveDB();

        Intent intent = new Intent(_context, WRESMeasureComponentsActivity.class);
        intent.putExtra("equipment", equipment);
        startActivity(intent);
        getActivity().finish();
    }

    public void launchStep3Screen() {

        WRESEquipment equipment = mCallback.SaveDB();

        Intent intent = new Intent(_context, WRESDipTestsActivity.class);
        intent.putExtra("equipment", equipment);
        startActivity(intent);
        getActivity().finish();
    }

    public void launchStep4Screen() {

        WRESEquipment equipment = mCallback.SaveDB();

        Intent intent = new Intent(_context, WRESCrackTestsActivity.class);
        intent.putExtra("wsre_id", equipment.get_id());
        startActivity(intent);
        getActivity().finish();
    }

    public void launchStep5Screen() {

        WRESEquipment equipment = mCallback.SaveDB();

        Intent intent = new Intent(_context, WRESReviewSubmitActivity.class);
        intent.putExtra("wsre_id", equipment.get_id());
        startActivity(intent);
        getActivity().finish();
    }
}
