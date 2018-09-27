package au.com.infotrak.infotrakmobile.entityclasses.MSI;

/**
 * Created by NoumanB on 8/07/2016.
 */
public class MSI_HITACHILimits {
    private long _compartIdAuto;
    private long _compartMeasurePointId;
    private String _hitTool;
    private double _impactSlope;
    private double _impactIntercept;
    private double _normalSlope;
    private double _normalIntercept;

    public MSI_HITACHILimits(){}

    public MSI_HITACHILimits(long compartIdAuto, long CompartMeasurePointId, String hitTool, double impactSlope, double impactIntercept, double normalSlope, double normalIntercept)
    {
        _compartIdAuto=compartIdAuto;
        _compartMeasurePointId = CompartMeasurePointId;
        _hitTool=hitTool;
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

    public String GetTool() { return _hitTool; }
    public double GetImpactSlope() {return _impactSlope; }
    public double GetImpactIntercept() { return _impactIntercept;}
    public double GetNormalSlope() {return _normalSlope; }
    public double GetNormalIntercept() {return _normalIntercept;}
}
