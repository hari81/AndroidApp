package au.com.infotrak.infotrakmobile;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;


/**
 * Created by NoumanB on 7/12/2015.
 */
//public class Preferences extends PreferenceFragment {
public class Preferences extends PreferenceActivity {

    SwitchPreference _usb;
    SwitchPreference _bt;
    //----
    SwitchPreference _eng;
    SwitchPreference _ind;
    SwitchPreference _por;
    SwitchPreference _chi;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        _usb = (SwitchPreference)findPreference("usb");
        _bt = (SwitchPreference)findPreference("bt");

        _eng = (SwitchPreference)findPreference("en");
        _ind = (SwitchPreference)findPreference("id");
        _por = (SwitchPreference)findPreference("pt");
        _chi = (SwitchPreference)findPreference("zh");

        //_ind.setChecked(false);

        _usb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                _usb.setChecked(true);
                _bt.setChecked(false);

                return true;
            }
        });

        _bt.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                _usb.setChecked(false);
                _bt.setChecked(true);

                return true;
            }
        });

        //--------------------------------------------
        _eng.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                _eng.setChecked(true);
                _ind.setChecked(false);
                _por.setChecked(false);
                _chi.setChecked(false);

                ((InfoTrakApplication)getApplication()).setPrfeChanged(true);
                return true;
            }
        });

        _ind.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                _eng.setChecked(false);
                _ind.setChecked(true);
                _por.setChecked(false);
                _chi.setChecked(false);

                ((InfoTrakApplication)getApplication()).setPrfeChanged(true);
                return true;
            }
        });

        _por.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                _eng.setChecked(false);
                _ind.setChecked(false);
                _por.setChecked(true);
                _chi.setChecked(false);

                ((InfoTrakApplication)getApplication()).setPrfeChanged(true);
                return true;
            }
        });

        _chi.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                _eng.setChecked(false);
                _ind.setChecked(false);
                _por.setChecked(false);
                _chi.setChecked(true);

                ((InfoTrakApplication)getApplication()).setPrfeChanged(true);
                return true;
            }
        });
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        getView().setBackgroundColor(Color.WHITE);
//        getView().setClickable(true);
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
//            savedInstanceState) {
//        View view = super.onCreateView(inflater, container, savedInstanceState);
//        view.setBackgroundColor(getResources().getColor(android.R.color.white));
//
//        return view;
//    }
}
