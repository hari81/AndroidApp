package au.com.infotrak.infotrakmobile;

import android.os.AsyncTask;

import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusReader;

/**
 * Created by MasonS on 7/12/2017.
 */

public class UTReadingAsync extends AsyncTask<CygnusReader,Integer, CygnusData> {
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

    public UTAsyncResponse delegate = null;

    public UTReadingAsync(UTAsyncResponse delegate){
        this.delegate = delegate;
    }
}
