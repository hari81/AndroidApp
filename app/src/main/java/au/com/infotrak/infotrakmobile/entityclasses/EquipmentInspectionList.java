package au.com.infotrak.infotrakmobile.entityclasses;

import java.util.ArrayList;

/**
 * Created by NoumanB on 2/11/2015.
 */
public class EquipmentInspectionList {
    private ArrayList<UndercarriageInspectionEntity> _equipmentsInspection;
    private ArrayList<Equipment> _newEquipments;
    private int _totalEquipments;

    public EquipmentInspectionList(ArrayList<UndercarriageInspectionEntity> e,ArrayList<Equipment> equip)
    {
        _equipmentsInspection = e;
        _newEquipments = equip;
        _totalEquipments =0;
    }

    public EquipmentInspectionList()
    {
        _equipmentsInspection = new ArrayList<UndercarriageInspectionEntity>();
        _newEquipments = new ArrayList<Equipment>();
        _totalEquipments = 0;
    }

    public ArrayList<UndercarriageInspectionEntity> GetEquipmentsInspectionsList(){return _equipmentsInspection;}
    public void SetEquipmentsInspectionsList(ArrayList<UndercarriageInspectionEntity> e) {_equipmentsInspection = e;}

    public ArrayList<Equipment> GetNewEquipmentsInspectionsList() {return _newEquipments;}
    public void SetNewEquipmentsInspectionsList(ArrayList<Equipment> e) {_newEquipments=e;}

    public int GetTotalEquipments(){return _totalEquipments;}
    public void SetTotalEquipments(int value){_totalEquipments = value; }
}
