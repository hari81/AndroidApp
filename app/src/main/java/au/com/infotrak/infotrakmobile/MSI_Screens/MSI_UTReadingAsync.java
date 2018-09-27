package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.os.AsyncTask;

import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusData;
import au.com.infotrak.infotrakmobile.CygnusBlueTooth.CygnusReader;

public class MSI_UTReadingAsync extends AsyncTask<CygnusReader,Integer, CygnusData> {

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

    public MSI_MeasurementEntryActivity.MSI_UTAsyncResponse delegate = null;

    public MSI_UTReadingAsync(MSI_MeasurementEntryActivity.MSI_UTAsyncResponse delegate){
        this.delegate = delegate;
    }
}
