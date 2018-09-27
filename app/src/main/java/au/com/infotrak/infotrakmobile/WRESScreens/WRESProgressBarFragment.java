package au.com.infotrak.infotrakmobile.WRESScreens;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;

public class WRESProgressBarFragment extends DialogFragment {

    private View _rootView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
        }
    }

    public static WRESProgressBarFragment newInstance(WRESEquipment dataObj) {

        WRESProgressBarFragment fragment = new WRESProgressBarFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("data", dataObj);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize
        _rootView = inflater.inflate(R.layout.wres_progress_bar_fragment, container, false);

        LinearLayout layout = (LinearLayout) _rootView.findViewById(R.id.wres_progressbar_layout);
        layout.setBackgroundColor(Color.DKGRAY);

        ProgressBar progress = (ProgressBar) _rootView.findViewById(R.id.wres_progressbar);
        progress.getIndeterminateDrawable().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SCREEN);

        TextView text = (TextView) _rootView.findViewById(R.id.wres_progressbar_text);
        text.setTextColor(Color.WHITE);
        text.setBackgroundColor(Color.DKGRAY);

        return _rootView;
    }

}
