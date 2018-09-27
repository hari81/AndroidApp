package au.com.infotrak.infotrakmobile.entityclasses.WRES;

/**
 * Created by PaulN on 22/03/2018.
 */

public class WRESTestpointImage {

    public long KEY_MEASURE_COMPARTTYPE_AUTO;
    public String KEY_MEASURE_TOOL;
    public byte[] KEY_MEASURE_IMAGE;

    public WRESTestpointImage(){}

    public WRESTestpointImage(long KEY_MEASURE_COMPARTTYPE_AUTO,
                              String KEY_MEASURE_TOOL,
                              byte[] KEY_MEASURE_IMAGE){}

    public long getKEY_MEASURE_COMPARTTYPE_AUTO() {
        return KEY_MEASURE_COMPARTTYPE_AUTO;
    }

    public void setKEY_MEASURE_COMPARTTYPE_AUTO(long KEY_MEASURE_COMPARTTYPE_AUTO) {
        this.KEY_MEASURE_COMPARTTYPE_AUTO = KEY_MEASURE_COMPARTTYPE_AUTO;
    }

    public String getKEY_MEASURE_TOOL() {
        return KEY_MEASURE_TOOL;
    }

    public void setKEY_MEASURE_TOOL(String KEY_MEASURE_TOOL) {
        this.KEY_MEASURE_TOOL = KEY_MEASURE_TOOL;
    }

    public byte[] getKEY_MEASURE_IMAGE() {
        return KEY_MEASURE_IMAGE;
    }

    public void setKEY_MEASURE_IMAGE(byte[] KEY_MEASURE_IMAGE) {
        this.KEY_MEASURE_IMAGE = KEY_MEASURE_IMAGE;
    }
}
