package au.com.infotrak.infotrakmobile.entityclasses.MSI;

/**
 * Created by SamuelC on 13/03/2015.
 */
public class MSI_CATLimits {
    private long _compartIdAuto;
    private long _compartMeasurePointId;
    private String _catTool;
    private int _slope;
    private double _adjToBase;
    private double _hiInflectionPoint;
    private double _hiSlope1;
    private double _hiIntercept1;
    private double _hiSlope2;
    private double _hiIntercept2;
    private double _miInflectionPoint;
    private double _miSlope1;
    private double _miIntercept1;
    private double _miSlope2;
    private double _miIntercept2;

    public MSI_CATLimits(long compartIdAuto, long CompartMeasurePointId, String catTool, int slope, double adjToBase, double hiInflectionPoint, double hiSlope1, double hiIntercept1, double hiSlope2, double hiIntercept2, double miInflectionPoint,
                         double miSlope1, double miIntercept1, double miSlope2, double miIntercept2) {
        _compartIdAuto = compartIdAuto;
        _compartMeasurePointId = CompartMeasurePointId;
        _catTool = catTool;
        _slope = slope;
        _adjToBase = adjToBase;
        _hiInflectionPoint = hiInflectionPoint;
        _hiSlope1 = hiSlope1;
        _hiIntercept1 = hiIntercept1;
        _hiSlope2 = hiSlope2;
        _hiIntercept2 = hiIntercept2;
        _miInflectionPoint = miInflectionPoint;
        _miSlope1 = miSlope1;
        _miIntercept1 = miIntercept1;
        _miSlope2 = miSlope2;
        _miIntercept2 = miIntercept2;
    }

    public MSI_CATLimits() {

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

    public String GetTool() { return _catTool; }

    public int GetSlope() { return _slope; }

    public double GetAdjToBase() { return _adjToBase; }

    public double GetHiInflectionPoint() {
        return _hiInflectionPoint;
    }

    public double GetHiSlope1() {
        return _hiSlope1;
    }

    public double GetHiIntercept1() {
        return _hiIntercept1;
    }

    public double GetHiSlope2() {
        return _hiSlope2;
    }

    public double GetHiIntercept2() {
        return _hiIntercept2;
    }

    public double GetMiInflectionPoint() {
        return _miInflectionPoint;
    }

    public double GetMiSlope1() {
        return _miSlope1;
    }

    public double GetMiIntercept1() {
        return _miIntercept1;
    }

    public double GetMiSlope2() {
        return _miSlope2;
    }

    public double GetMiIntercept2() {
        return _miIntercept2;
    }


}
