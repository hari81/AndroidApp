package au.com.infotrak.infotrakmobile.entityclasses;

import au.com.infotrak.infotrakmobile.business.UCCalculations;
import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;

/**
 * Created by SamuelC on 25/03/2015.
 */
public class ComponentInspection extends Component {
    private String _reading;
    private String _tool;
    private String _method;
    private String _comments;
    private String _inspectionImage;
    private Integer _isfreezed;
    private InfotrakDataContext _dbContext;


    public ComponentInspection(long equnitAuto, long equipmentIdAuto, long compartIdAuto, String compartid, String compart, Integer pos, String side, byte[] image, long compType, String reading, String tool, String method, InfotrakDataContext dbContext, String comments, String inspectionImage, String flangeType,int isNew) {
        super(equnitAuto, equipmentIdAuto, compartIdAuto, compartid, compart, pos, side, image, compType,flangeType,isNew);
        _reading = reading;
        _tool = tool;
        _method = method;
        _comments = comments;
        _dbContext = dbContext;
        _inspectionImage = inspectionImage;
        _isfreezed = 0;
    }

    public ComponentInspection(long equnitAuto, long equipmentIdAuto, long compartIdAuto, String compartid, String compart, Integer pos, String side, byte[] image, long compType, String reading, String tool, String method, InfotrakDataContext dbContext, String comments, String inspectionImage, int isFreezed,String flangeType,int isNew) {
        super(equnitAuto, equipmentIdAuto, compartIdAuto, compartid, compart, pos, side, image, compType,flangeType,isNew);
        _reading = reading;
        _tool = tool;
        _method = method;
        _comments = comments;
        _dbContext = dbContext;
        _inspectionImage = inspectionImage;
        _isfreezed = isFreezed;
    }
    public ComponentInspection(long equnitAuto, long equipmentIdAuto, long compartIdAuto, String compartid, String compart, Integer pos, String side, byte[] image, long compType, String reading, String tool, String method, InfotrakDataContext dbContext, String comments, String inspectionImage, int isFreezed,String flangeType,int isNew, double lastReading, int lastWornPercentage, int toolId, String toolSymbol) {
        super(equnitAuto, equipmentIdAuto, compartIdAuto, compartid, compart, pos, side, image, compType,flangeType,isNew, lastReading, lastWornPercentage, toolId, toolSymbol);
        _reading = reading;
        _tool = tool;
        _method = method;
        _comments = comments;
        _dbContext = dbContext;
        _inspectionImage = inspectionImage;
        _isfreezed = isFreezed;
    }
    public ComponentInspection() {

    }

    public long GetPercentage(InfotrakDataContext dbContext) {
        _dbContext = dbContext;
        return UCCalculations.GetPercentage(this);
    }

    public String GetTool() { return _tool; }

    public void SetTool(String tool) {
        _tool = tool;
    }

    public String GetReading() {
        return _reading;
    }

    public void SetReading(String reading) { _reading = reading; }


    public int GetFreezedState()
    {
        return _isfreezed;
    }

    public void SetFreezeState(int i)
    {
        _isfreezed = i;
    }

    public String GetMethod() { return _method; }

    public void SetMethod(String method) {
        _method = method;
    }

    public InfotrakDataContext GetDBContext() {
        return _dbContext;
    }

    public void SetComments(String comments) { _comments = comments; }

    public String GetComments() {
        return _comments;
    }

    public String GetInspectionImage() {
        return _inspectionImage;
    }

    public void SetContext() {_dbContext = null;}
}
