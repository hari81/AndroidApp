package au.com.infotrak.infotrakmobile.entityclasses;

/**
 * Created by NoumanB on 23/07/2015.
 *
 * Created this class against PRN8514. We have a requirement to save new equipment,components and inspections to the main db.
 * Can't use the current classes due to the dbContext member variable.
 */
public class EquipmentDetails {

    public long EqunitAuto;
    public long CompType;
    public long CompartIdAuto;
    public String Compartid;
    public String Compart;
    public Integer Pos;
    public byte[] Image;
    public String Side;
    public long EquipmentidAuto;
    public String FlangeType;
    public String Reading;
    public String Tool;
    public String Method;
    public String Comments;
    public String InspectionImage;



    public long GetEqunitAuto(){return EqunitAuto;}
    public long GetComponentType(){return CompType;}
    public long GetCompartIdAuto(){return CompartIdAuto;}
    public String GetCompartId(){return Compartid;}
    public String GetCompart(){return Compart;}
    public Integer GetPosition(){return Pos;}
    public byte[] GetImage(){return Image;}
    public String GetSide(){return Side;}
    public Long GetEquipmentIdAuto(){return EquipmentidAuto;}
    public String GetFlangeType(){return FlangeType;}
    public String GetReading(){return Reading;}
    public String GetTool(){return Tool;}
    public String GetMethod(){return Method;}
    public String GetComments(){return Comments;}
    public String GetInspectionImage(){return InspectionImage;}
}
