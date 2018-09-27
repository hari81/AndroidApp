package au.com.infotrak.infotrakmobile.entityclasses.WRES;

/**
 * Created by PaulN on 28/03/2018.
 */

public class WRESJobsite {

    long _crsf_auto;
    long _module_sub_auto;
    String _jobsite;
    int _uom;
    private int _impact = 2;    // High impact

    public WRESJobsite(long _crsf_auto, long _module_sub_auto, String _jobsite, int _uom, int _impact) {
        this._crsf_auto = _crsf_auto;
        this._module_sub_auto = _module_sub_auto;
        this._jobsite = _jobsite;
        this._uom = _uom;
        this._impact = _impact;
    }

    public WRESJobsite(long _crsf_auto, long _module_sub_auto, String _jobsite, int _uom) {
        this._crsf_auto = _crsf_auto;
        this._module_sub_auto = _module_sub_auto;
        this._jobsite = _jobsite;
        this._uom = _uom;
    }

    public WRESJobsite(int _uom, int _impact) {
        this._uom = _uom;
        this._impact = _impact;
    }

    public int get_impact() {
        return _impact;
    }

    public void set_impact(int _impact) {
        this._impact = _impact;
    }

    public int get_uom() {
        return _uom;
    }

    public void set_uom(int _uom) {
        this._uom = _uom;
    }

    public long get_crsf_auto() {
        return _crsf_auto;
    }

    public void set_crsf_auto(long _crsf_auto) {
        this._crsf_auto = _crsf_auto;
    }

    public String get_jobsite() {
        return _jobsite;
    }

    public void set_jobsite(String _jobsite) {
        this._jobsite = _jobsite;
    }

    public long get_module_sub_auto() {
        return _module_sub_auto;
    }

    public void set_module_sub_auto(long _module_sub_auto) {
        this._module_sub_auto = _module_sub_auto;
    }
}
