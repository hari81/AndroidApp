package au.com.infotrak.infotrakmobile.business.WRES;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESComponent;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESTestpointImage;

/**
 * Created by PaulN on 21/03/2018.
 */

public class CallServiceAPIXML extends AsyncTask<String, String, String> {

    private Context _context;
    private OnCallAPIListener<String> _CallBack;
    public Exception _Exception;
    private String _partno_emiliter = ": ";
    private long _inspectionId = 0;

    // Constructor
    public CallServiceAPIXML(Context context, OnCallAPIListener listener, long inspectionId)
    {
        _context = context;
        _CallBack = listener;
        _inspectionId = inspectionId;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        String type = params[1]; // Type of call
        String resultToDisplay = "";
        InputStream in;
        WRESDataContext db = new WRESDataContext(_context);

        // HTTP Get
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            switch (type) {
                case "Component":
                    ArrayList<WRESComponent> arrayComponent = GetComponentList(parser);
                    try {
                        for (int count = 0; count < arrayComponent.size(); count++) {
                            db.insertComponent(arrayComponent.get(count));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case "Testpoint":
                    ArrayList<WRESTestpointImage> arrayTestPoint = GetTestpointImgList(parser);
                    try {
                        for (int count = 0; count < arrayTestPoint.size(); count++) {
                            db.insertTestPoint(arrayTestPoint.get(count));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            _Exception = e;
            return e.getMessage();
        }

        return resultToDisplay;
    }

    private ArrayList<WRESComponent> GetComponentList(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<WRESComponent> arrComponentList = new ArrayList<>();
        WRESComponent currentComponent = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String currentName = parser.getName();

                switch (currentName) {
                    case "a:ComponentEntity":
                        if (currentComponent != null)
                            arrComponentList.add(currentComponent);
                        currentComponent = new WRESComponent();
                        currentComponent.set_inspection_id(_inspectionId);
                        break;
                    case "a:ComponentId":
                        if (currentComponent != null)
                            currentComponent.set_eq_unitauto(Long.parseLong(parser.nextText()));
                        break;
                    case "a:ComponentImage":
                        if (currentComponent != null)
                            currentComponent.set_image(Base64.decode(parser.nextText(), Base64.DEFAULT));
                        break;
                    case "a:ModuleSubAuto":
                        if (currentComponent != null)
                            currentComponent.set_module_sub_auto(Long.parseLong(parser.nextText()));
                        break;
                    case "a:ComponentName":
                        if (currentComponent != null)
                            currentComponent.set_compart(parser.nextText());
                        break;
                    case "a:PartNo":
                        if (currentComponent != null) {
                            String[] partNo = parser.nextText().split(_partno_emiliter);
                            currentComponent.set_comparttype(partNo[0]);
                            currentComponent.set_compartid(partNo[1]);
                            break;
                        }
                        break;
                    case "a:ComponentPosition":
                        if (currentComponent != null)
                            //currentComponent.SetPos(Integer.parseInt(parser.nextText()));
                        break;
                    case "a:ComponentSide":
                        if (currentComponent != null)
                            //currentComponent.SetSide(parser.nextText());
                        break;
                    case "a:DefaultTool":
                        if (currentComponent != null)
                            currentComponent.set_default_tool(parser.nextText());
                        break;
                    case "a:ComponentType":
                        if (currentComponent != null)
                            currentComponent.set_comparttype_auto(Long.parseLong(parser.nextText()));
                        break;
                    case "a:ComponentMethod":
                        if (currentComponent != null)
                            currentComponent.set_method(parser.nextText());
                        break;
                    case "a:ComponentIdAuto":
                        if (currentComponent != null)
                            currentComponent.set_compartid_auto(Long.parseLong(parser.nextText()));
                }
            }
            eventType = parser.next();
        }
        arrComponentList.add(currentComponent);

        return arrComponentList;
    }

    private ArrayList<WRESTestpointImage> GetTestpointImgList(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<WRESTestpointImage> arrayList = new ArrayList<>();
        WRESTestpointImage currentTestpoint = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String currentName = parser.getName();

                switch (currentName) {
                    case "a:TestPointImageEntity":
                        if (currentTestpoint != null)
                            arrayList.add(currentTestpoint);
                        currentTestpoint = new WRESTestpointImage();
                        break;
                    case "a:CompartType":
                        if (currentTestpoint != null)
                            currentTestpoint.setKEY_MEASURE_COMPARTTYPE_AUTO(Long.parseLong(parser.nextText()));
                        break;
                    case "a:TestPointImage":
                        if (currentTestpoint != null)
                            currentTestpoint.setKEY_MEASURE_IMAGE(Base64.decode(parser.nextText(), Base64.DEFAULT));
                        break;
                    case "a:Tool":
                        if (currentTestpoint != null)
                            currentTestpoint.setKEY_MEASURE_TOOL(parser.nextText());
                        break;
                }
            }
            eventType = parser.next();
        }
        arrayList.add(currentTestpoint);

        return arrayList;
    }

    protected void onPostExecute(String result) {
        if (_CallBack != null) {
            if (_Exception == null) {
                _CallBack.onSuccess(result);
            } else {
                _CallBack.onFailure(_Exception);
            }
        }
    }
}
