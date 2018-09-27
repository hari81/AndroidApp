package au.com.infotrak.infotrakmobile.entityclasses.WRES;

/**
 * Created by NoumanB on 8/07/2016.
 */
public class WRESKOMATSULimits {
    private long _compartIdAuto;
    private String _komTool;
    private double _impactSecondOrder;
    private double _impactSlope;
    private double _impactIntercept;
    private double _normalSecondOrder;
    private double _normalSlope;
    private double _normalIntercept;

    public WRESKOMATSULimits(){}

    public WRESKOMATSULimits(long compartIdAuto, String komTool, double impactSecondOrder, double impactSlope, double impactIntercept, double normalSecondOrder, double normalSlope, double normalIntercept)
    {
        _compartIdAuto=compartIdAuto;
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
    public String GetTool() { return _komTool; }
    public double GetImpactSecondOrder() {return  _impactSecondOrder;}
    public double GetImpactSlope() {return _impactSlope; }
    public double GetImpactIntercept() { return _impactIntercept;}
    public double GetNormalSecondOrder() {return _normalSecondOrder; }
    public double GetNormalSlope() {return _normalSlope; }
    public double GetNormalIntercept() {return _normalIntercept;}
}
