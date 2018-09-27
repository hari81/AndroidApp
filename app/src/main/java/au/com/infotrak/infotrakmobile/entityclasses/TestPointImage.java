package au.com.infotrak.infotrakmobile.entityclasses;

/**
 * Created by SamuelC on 13/03/2015.
 */
public class TestPointImage {
    private long _id;
    private long _compartTypeAuto;
    private String _tool;
    private byte[] _image;

    public TestPointImage(long comparTypeAuto, String tool, byte[] image) {
        _compartTypeAuto = comparTypeAuto;
        _tool = tool;
       _image = image;
    }

    public TestPointImage() {

    }

    public long GetID() {
        return _id;
    }

    public void SetID(long id) { _id = id; }

    public long GetCompType() {
        return _compartTypeAuto;
    }

    public void SetCompType(long compType) { _compartTypeAuto = compType; }

    public String GetTool() {
        return _tool;
    }

    public void SetTool(String tool) { _tool = tool; }

    public byte[] GetImage() {
        return _image;
    }

    public void SetImage(byte[] image) {
        _image = image;
    }
}
