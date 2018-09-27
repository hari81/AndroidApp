package au.com.infotrak.infotrakmobile.entityclasses.MSI;

public class MSI_WornPercentage {

    long _equipmentid_auto;
    long _compartid_auto;
    long _measurementpoint_id;
    String _method;
    String _reading;
    String _tool;

    public String get_tool() {
        return _tool;
    }

    public void set_tool(String _tool) {
        this._tool = _tool;
    }

    public long get_equipmentid_auto() {
        return _equipmentid_auto;
    }

    public void set_equipmentid_auto(long _equipmentid_auto) {
        this._equipmentid_auto = _equipmentid_auto;
    }

    public long get_compartid_auto() {
        return _compartid_auto;
    }

    public void set_compartid_auto(long _compartid_auto) {
        this._compartid_auto = _compartid_auto;
    }

    public long get_measurementpoint_id() {
        return _measurementpoint_id;
    }

    public void set_measurementpoint_id(long _measurementpoint_id) {
        this._measurementpoint_id = _measurementpoint_id;
    }

    public String get_method() {
        return _method;
    }

    public void set_method(String _method) {
        this._method = _method;
    }

    public String get_reading() {
        return _reading;
    }

    public void set_reading(String _reading) {
        this._reading = _reading;
    }
}
