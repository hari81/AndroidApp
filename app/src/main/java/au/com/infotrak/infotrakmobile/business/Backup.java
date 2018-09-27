package au.com.infotrak.infotrakmobile.business;
import android.app.Activity;
import android.os.Environment;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Backup {

    Activity _activity;

    public Backup(Activity _activity) {
        this._activity = _activity;
    }

    public Boolean exportBackupWRESData()
    {
        ////////////////////////
        // Destination folder
        String backupFolder = "workshop_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString();
        File destDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),
                "InfoTrakData" + File.separator
                        + backupFolder);

        ///////////////////////
        // Database
        File dbFile = _activity.getDatabasePath("infotrak_wres");
        try {
            FileUtils.copyFileToDirectory(dbFile, destDir);

        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return false;
        } finally {
        }

        //////////////////////
        // Images folder
        try {
            // Source
            File srcDir = new File(_activity.getApplicationContext().getFilesDir(), "wres");
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (Exception e)
        {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }

        return true;
    }

}
