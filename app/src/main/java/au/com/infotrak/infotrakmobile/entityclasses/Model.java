package au.com.infotrak.infotrakmobile.entityclasses;

/**
 * Created by SamuelC on 18/03/2015.
 */
public class Model {
    private int _modelid;
    private String _modelname;

    public Model(int modelid, String modelname) {
        _modelid = modelid;
        _modelname = modelname;
    }

    public int GetModelId() {
        return _modelid;
    }

    public String GetModelName() {
        return _modelname;
    }
}
