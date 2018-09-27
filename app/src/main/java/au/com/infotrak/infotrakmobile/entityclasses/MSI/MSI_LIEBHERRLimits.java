package au.com.infotrak.infotrakmobile.entityclasses.MSI;

/**
 * Created by NoumanB on 8/07/2016.
 */
public class MSI_LIEBHERRLimits {
    private long _compartIdAuto;
    private long _compartMeasurePointId;
    private String _lieTool;
    private double _impactSlope;
    private double _impactIntercept;
    private double _normalSlope;
    private double _normalIntercept;

    public MSI_LIEBHERRLimits(){}

    public MSI_LIEBHERRLimits(long compartIdAuto, long CompartMeasurePointId, String lieTool, double impactSlope, double impactIntercept, double normalSlope, double normalIntercept)
    {
        _compartIdAuto=compartIdAuto;
        _compartMeasurePointId = CompartMeasurePointId;
        _lieTool=lieTool;
        _impactSlope = impactSlope;
        _impactIntercept = impactIntercept;
        _normalSlope=normalSlope;
        _normalIntercept=normalIntercept;
    }

    public long GetComponentID() {
        return _compartIdAuto;
    }

    public long get_compartMeasurePointId() {
        return _compartMeasurePointId;
    }

    public void set_compartMeasurePointId(long _compartMeasurePointId) {
        this._compartMeasurePointId = _compartMeasurePointId;
    }

    public String GetTool() { return _lieTool; }
    public double GetImpactSlope() {return _impactSlope; }
    public double GetImpactIntercept() { return _impactIntercept;}
    public double GetNormalSlope() {return _normalSlope; }
    public double GetNormalIntercept() {return _normalIntercept;}
}
