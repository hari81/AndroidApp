package au.com.infotrak.infotrakmobile.MSI_Screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import au.com.infotrak.infotrakmobile.MainActivity;

public class MSI_BlankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent mainIntent = new Intent(this, MainActivity.class);
        this.startActivity(mainIntent);
        this.finish();

    }
}
