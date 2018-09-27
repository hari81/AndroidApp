package au.com.infotrak.infotrakmobile.entityclasses;

/**
 * Created by SamuelC on 13/03/2015.
 */
public class ITMLimits {
    private long _compartIdAuto;
    private String _itmTool;
    private double _startDepthNew;
    private double _wearDepth10Percent;
    private double _wearDepth20Percent;
    private double _wearDepth30Percent;
    private double _wearDepth40Percent;
    private double _wearDepth50Percent;
    private double _wearDepth60Percent;
    private double _wearDepth70Percent;
    private double _wearDepth80Percent;
    private double _wearDepth90Percent;
    private double _wearDepth100Percent;
    private double _wearDepth110Percent;
    private double _wearDepth120Percent;

    public ITMLimits(long compartIdAuto, String itmTool, double startDepthNew, double wearDepth10Percent, double wearDepth20Percent, double wearDepth30Percent, double wearDepth40Percent, double wearDepth50Percent,
                     double wearDepth60Percent, double wearDepth70Percent, double wearDepth80Percent, double wearDepth90Percent, double wearDepth100Percent, double wearDepth110Percent, double wearDepth120Percent) {
        _compartIdAuto = compartIdAuto;
        _itmTool = itmTool;
        _startDepthNew = startDepthNew;
        _wearDepth10Percent = wearDepth10Percent;
        _wearDepth20Percent = wearDepth20Percent;
        _wearDepth30Percent = wearDepth30Percent;
        _wearDepth40Percent = wearDepth40Percent;
        _wearDepth50Percent = wearDepth50Percent;
        _wearDepth60Percent = wearDepth60Percent;
        _wearDepth70Percent = wearDepth70Percent;
        _wearDepth80Percent = wearDepth80Percent;
        _wearDepth90Percent = wearDepth90Percent;
        _wearDepth100Percent = wearDepth100Percent;
        _wearDepth110Percent = wearDepth110Percent;
        _wearDepth120Percent = wearDepth120Percent;
    }

    public ITMLimits() {

    }

    public long GetComponentID() {
        return _compartIdAuto;
    }

    public String GetTool() { return _itmTool; }

    public double GetStartDepthNew() {
        return _startDepthNew;
    }

    public double GetWearDepth10Percent() {
        return _wearDepth10Percent;
    }

    public double GetWearDepth20Percent() {
        return _wearDepth20Percent;
    }

    public double GetWearDepth30Percent() {
        return _wearDepth30Percent;
    }

    public double GetWearDepth40Percent() {
        return _wearDepth40Percent;
    }

    public double GetWearDepth50Percent() {
        return _wearDepth50Percent;
    }

    public double GetWearDepth60Percent() {
        return _wearDepth60Percent;
    }

    public double GetWearDepth70Percent() {
        return _wearDepth70Percent;
    }

    public double GetWearDepth80Percent() {
        return _wearDepth80Percent;
    }

    public double GetWearDepth90Percent() {
        return _wearDepth90Percent;
    }

    public double GetWearDepth100Percent() {
        return _wearDepth100Percent;
    }

    public double GetWearDepth110Percent() {
        return _wearDepth110Percent;
    }

    public double GetWearDepth120Percent() {
        return _wearDepth120Percent;
    }
}
