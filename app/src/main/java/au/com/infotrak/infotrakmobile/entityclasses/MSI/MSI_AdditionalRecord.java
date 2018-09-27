package au.com.infotrak.infotrakmobile.entityclasses.MSI;

public class MSI_AdditionalRecord {

    String _record_tool;
    String _record_type;
    String title;
    String _AdditionalId;

    public MSI_AdditionalRecord(String title, String _record_type, String _record_tool, String _AdditionalId) {
        this._record_tool = _record_tool;
        this._record_type = _record_type;
        this.title = title;
        this._AdditionalId = _AdditionalId;
    }

    public String get_AdditionalId() {
        return _AdditionalId;
    }

    public void set_AdditionalId(String _AdditionalId) {
        this._AdditionalId = _AdditionalId;
    }

    public String get_record_tool() {
        return _record_tool;
    }

    public void set_record_tool(String _record_tool) {
        this._record_tool = _record_tool;
    }

    public String get_record_type() {
        return _record_type;
    }

    public void set_record_type(String _record_type) {
        this._record_type = _record_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
