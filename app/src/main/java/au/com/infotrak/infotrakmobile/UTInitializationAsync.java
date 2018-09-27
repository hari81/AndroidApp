package au.com.infotrak.infotrakmobile;

import android.os.AsyncTask;

import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusReader;

/**
 * Created by MasonS on 7/12/2017.
 */

public class UTInitializationAsync extends AsyncTask<InfoTrakApplication,Integer, Integer> {
    @Override
    protected Integer doInBackground(InfoTrakApplication...  _reader) {
        if(_reader != null)
             _reader[0].getCygnusReader();
        return 1;
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
    }
    @Override
    protected void onPostExecute(Integer result) {
    }
}