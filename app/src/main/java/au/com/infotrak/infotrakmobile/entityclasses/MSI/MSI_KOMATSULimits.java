package au.com.infotrak.infotrakmobile.entityclasses.MSI;

/**
 * Created by NoumanB on 8/07/2016.
 */
public class MSI_KOMATSULimits {
    private long _compartIdAuto;
    private long _compartMeasurePointId;
    private String _komTool;
    private double _impactSecondOrder;
    private double _impactSlope;
    private double _impactIntercept;
    private double _normalSecondOrder;
    private double _normalSlope;
    private double _normalIntercept;

    public MSI_KOMATSULimits(){}

    public MSI_KOMATSULimits(long compartIdAuto, long CompartMeasurePointId, String komTool, double impactSecondOrder, double impactSlope, double impactIntercept, double normalSecondOrder, double normalSlope, double normalIntercept)
    {
        _compartIdAuto=compartIdAuto;
        _compartMeasurePointId = CompartMeasurePointId;
        _komTool=komTool;
        _impactSecondOrder = impactSecondOrder;
        _impactSlope = impactSlope;
        _impactIntercept = impactIntercept;
        _normalSecondOrder = normalSecondOrder;
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

    public String GetTool() { return _komTool; }
    public double GetImpactSecondOrder() {return  _impactSecondOrder;}
    public double GetImpactSlope() {return _impactSlope; }
    public double GetImpactIntercept() { return _impactIntercept;}
    public double GetNormalSecondOrder() {return _normalSecondOrder; }
    public double GetNormalSlope() {return _normalSlope; }
    public double GetNormalIntercept() {return _normalIntercept;}
}
