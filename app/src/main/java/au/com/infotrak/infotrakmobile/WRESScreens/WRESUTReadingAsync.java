package au.com.infotrak.infotrakmobile.WRESScreens;

import android.os.AsyncTask;

import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusReader;

/**
 * Created by MasonS on 7/12/2017.
 */

public class WRESUTReadingAsync extends AsyncTask<CygnusReader,Integer, CygnusData> {
    @Override
    protected CygnusData doInBackground(CygnusReader...  _reader) {
        if(_reader != null)
            return _reader[0].readData();
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected void onPostExecute(CygnusData result) {
        delegate.processFinish(result);
    }

    public WRESUTAsyncResponse delegate = null;

    public WRESUTReadingAsync(WRESUTAsyncResponse delegate){
        this.delegate = delegate;
    }
}
