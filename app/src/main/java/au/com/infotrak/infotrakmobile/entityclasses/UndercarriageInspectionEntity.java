package au.com.infotrak.infotrakmobile.entityclasses;

import java.util.ArrayList;

/**
 * Created by SamuelC on 8/04/2015.
 */
public class UndercarriageInspectionEntity {

    public long EquipmentIdAuto;

    public String Examiner;

    public String InspectionDate;

    public String SMU;

    public int Impact;

    public int Abrasive;

    public int Moisture;

    public int Packing;

    public double TrackSagLeft;

    public double TrackSagRight;

    public double DryJointsLeft;

    public double DryJointsRight;

    public double ExtCannonLeft;

    public double ExtCannonRight;

    public String JobsiteComments;

    public String InspectorComments;
    
    public String leftTrackSagComment;
    public String rightTrackSagComment;
    public String leftCannonExtComment;
    public String rightCannonExtComment;
    public String leftDryJointsComment;
    public String rightDryJointsComment;
    public String leftScallopComment;
    public String rightScallopComment;

    public String leftTrackSagImage;
    public String rightTrackSagImage;
    public String leftCannonExtImage;
    public String rightCannonExtImage;
    public String leftDryJointsImage;
    public String rightDryJointsImage;
    public String leftScallopImage;
    public String rightScallopImage;

    public boolean travelledByKms;
    public int travelForward;   // hr
    public int travelReverse;   // hr
    public int travelForwardKm; // km
    public int travelReverseKm; // km

    public double leftScallop;
    public double rightScallop;

    public ArrayList<InspectionDetails> Details;

    //PRN9565
    public String SerialNo;

    // TT-49
    public String EquipmentImage;
}
