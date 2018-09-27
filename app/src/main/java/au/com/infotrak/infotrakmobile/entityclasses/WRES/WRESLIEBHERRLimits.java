package au.com.infotrak.infotrakmobile.entityclasses.WRES;

/**
 * Created by NoumanB on 8/07/2016.
 */
public class WRESLIEBHERRLimits {
    private long _compartIdAuto;
    private String _lieTool;
    private double _impactSlope;
    private double _impactIntercept;
    private double _normalSlope;
    private double _normalIntercept;

    public WRESLIEBHERRLimits(){}

    public WRESLIEBHERRLimits(long compartIdAuto, String lieTool, double impactSlope, double impactIntercept, double normalSlope, double normalIntercept)
    {
        _compartIdAuto=compartIdAuto;
        _lieTool=lieTool;
        _impactSlope = impactSlope;
        _impactIntercept = impactIntercept;
        _normalSlope=normalSlope;
        _normalIntercept=normalIntercept;
    }

    public long GetComponentID() {
        return _compartIdAuto;
    }
    public String GetTool() { return _lieTool; }
    public double GetImpactSlope() {return _impactSlope; }
    public double GetImpactIntercept() { return _impactIntercept;}
    public double GetNormalSlope() {return _normalSlope; }
    public double GetNormalIntercept() {return _normalIntercept;}
}
