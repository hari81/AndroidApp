package au.com.infotrak.infotrakmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.ImageEntity;


public class SplashActivity extends Activity {

    public String apiUrl;
    public String apiGetApplicationData;
    public String apiGetDefaultSkin;

    private Integer SkinId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        apiGetApplicationData = apiUrl + "GetApplicationLogo";
        apiGetDefaultSkin = apiUrl + "GetDefaultSkin";

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "InfoTrak_Pictures");
        File SkinFile = new File(mediaStorageDir.getAbsolutePath() + File.separator + "skin.txt");

        //PRN10234 //PRN10577 - check for the file if it exists
        if(!SkinFile.exists()) {
            try {
                String result = new CallServiceAPIJSONGetDefaultSkin().execute(apiGetDefaultSkin).get();
            } catch (Exception e) {
                AppLog.log("Async call to get skin from webservice: " + e.getMessage());
            }
        }
        else
        {
            String text="";
            try {
                BufferedReader br = new BufferedReader(new FileReader(SkinFile));
                String line;

                while ((line = br.readLine()) != null) {
                    text = line;
                }
                br.close();

                try {
                    String[] strColors = text.replace("\"","").split("-");
                    ((InfoTrakApplication)getApplication()).setSkin(getIntFromColor(Integer.parseInt(strColors[1]),Integer.parseInt(strColors[3]),Integer.parseInt(strColors[5])));
                    //int colors[] = { ((InfoTrakApplication)getApplication()).getSkin(), ((InfoTrakApplication)getApplication()).getSkin() };

                    // TT-238 Changed the application splash screen and login screen background.
                    int colors[] = {Color.rgb(51, 51, 51), Color.rgb(51, 51, 51)};

                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM, colors);
                    View view = findViewById(R.id.backgroundColor);
                    view.setBackground(gradientDrawable);
                }
                catch(Exception e)
                {
                   AppLog.log("Error while getting a color from skin file" + e.getMessage());
                }
            }
            catch (IOException e) {
                AppLog.log("Splash screen skin reading from file: " + e.getMessage());
            }
        }


        File logoFile = new File(mediaStorageDir.getAbsolutePath() + File.separator + "infotrak_logo.png");
        if (! logoFile.exists()) {
            new CallServiceAPIJSONGetApplicationData().execute(apiGetApplicationData);
        }
        else
        {
            Bitmap img = BitmapFactory.decodeFile(mediaStorageDir + File.separator + "infotrak_logo.png");
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(img);
        }

        // 05-Mar-2018: Removed to use the logo from server instead.
        //((ImageView) findViewById(R.id.imageView)).setImageDrawable(getDrawable(R.drawable.tt_logo));

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        /* Duration of wait */
        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this,Login.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            // Delete User details

            InfotrakDataContext dataContext = new InfotrakDataContext(this);
            dataContext.deleteUserDetails();

            Intent intent = new Intent(this, Login.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    private class CallServiceAPIJSONGetApplicationData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String resultToDisplay = "";
            InputStream in;

            // HTTP Get
            try {

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            try {
                resultToDisplay = convertInputStreamToString(in);
            } catch (IOException e) {
                e.printStackTrace();
            }



            return resultToDisplay;
        }

        protected void onPostExecute(String result) {

            JSONObject json = null;
            ImageEntity obj = new ImageEntity();
            if (!result.equals("")) {
                try {
                    json = new JSONObject(result);

                    obj._logoName = json.getString("_logoName");
                    obj._logo = json.getString("_logo");

                    Bitmap img = base64ToBitmap(obj._logo);
                    ((ImageView) findViewById(R.id.imageView)).setImageBitmap(img);

                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), "InfoTrak_Pictures");

                    if (! mediaStorageDir.exists()){
                        mediaStorageDir.mkdirs();
                    }

                    byte[] imgBytes = Base64.decode(obj._logo,Base64.DEFAULT);
                    FileOutputStream imageOutFile = new FileOutputStream(mediaStorageDir.getPath() + File.separator +  obj._logoName);
                    imageOutFile.write(imgBytes);
                    imageOutFile.close();

                } catch (JSONException e) {
                    AppLog.log(e.getMessage());
                }
                catch (Exception e)
                {
                    AppLog.log(e.getMessage());
                }


            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;


        }

        private Bitmap base64ToBitmap(String b64) {
            byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }
    }

    private class CallServiceAPIJSONGetDefaultSkin extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String resultToDisplay = "";
            InputStream in;
            // HTTP Get
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }

            try {
                resultToDisplay = convertInputStreamToString(in);
            } catch (IOException e) {
                e.printStackTrace();
            }



            return resultToDisplay;
        }

        protected void onPostExecute(String result) {
            if (!result.equals("") && !result.equals("0")) {

                try {
                    String[] strColors = result.replace("\"","").split("-");
                    ((InfoTrakApplication)getApplication()).setSkin(getIntFromColor(Integer.parseInt(strColors[1]),Integer.parseInt(strColors[3]),Integer.parseInt(strColors[5])));
                    //int colors[] = { ((InfoTrakApplication)getApplication()).getSkin(), ((InfoTrakApplication)getApplication()).getSkin() };

                    // TT-238 Changed the application splash screen and login screen background.
                    int colors[] = {Color.rgb(51, 51, 51), Color.rgb(51, 51, 51)};

                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM, colors);
                    View view = findViewById(R.id.backgroundColor);
                    view.setBackground(gradientDrawable);
                }
                catch(Exception e)
                {
                   AppLog.log("OnPostExecute call for skin: " + e.getMessage());
                }

                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "InfoTrak_Pictures");

                if (! mediaStorageDir.exists()){
                    mediaStorageDir.mkdirs();
                }

                try {
                    final File myFile = new File(mediaStorageDir, "skin.txt");
                    if (!myFile.exists())
                    {
                        myFile.createNewFile();
                    }
                    FileOutputStream  outputStream = new FileOutputStream(myFile);
                    outputStream.write(result.getBytes());
                    outputStream.close();

                } catch (Exception e) {
                    AppLog.log("While saving skin in file: "+ e.getMessage());
                }
            }
            else
            {
                ((InfoTrakApplication)getApplication()).setSkin(0);
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;
        }

        public int getIntFromColor(int Red, int Green, int Blue){
            Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
            Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
            Blue = Blue & 0x000000FF; //Mask out anything not blue.

            return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
        }
    }
}
