package au.com.infotrak.infotrakmobile.entityclasses;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by NoumanB on 14/12/2015.
 */
public class FileOperations {

    private static final String COMMA_DELIMITER=",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    //Constructor
    public FileOperations(){}

    public int WriteDataToFile(EquipmentInspectionList equipList)
    {
        int iReturnValue = 1;

        // For already saved equipments
        String fileName = "/TrackInspection.csv";
        String filePath = "";

        FileWriter fw = null;
        try
        {

            File FileStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");


            if (FileStorageDir.isDirectory())
            {
                String[] children = FileStorageDir.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(FileStorageDir, children[i]).delete();
                }
            }

            // Create the storage directory if it does not exist
            if (! FileStorageDir.exists()){
                if (! FileStorageDir.mkdirs()){
                    Log.d("Infotrak", "failed to create directory");
                    return -3;
                }
            }

            boolean fileExists = false;
            filePath = FileStorageDir+ File.separator + fileName;
            try{
                File file = new File(filePath);
                if(file.exists()) {
                    //file.delete();
                    fileExists = true;
                }
            }
            catch(Exception e)
            {}

            fw = new FileWriter(filePath,true);
            //UndercarriageInspectionEntity class
            String strColumnHeader = "SerialNo,EquipmentId,Examiner,InspectionDate,SMU,Impact,Abrasive,Moisture,Packing,TrackSagLeft,TrackSagRight,DryJointsLeft,DryJointsRight,ExtCannonLeft,ExtCannonRight,JobsiteComments,InspectionComments";

            if(!fileExists) {
                fw.append(strColumnHeader);
                fw.append(NEW_LINE_SEPARATOR);
            }



            for(int i=0;i<equipList.GetEquipmentsInspectionsList().size(); i++)
            {
                UndercarriageInspectionEntity obj = equipList.GetEquipmentsInspectionsList().get(i);

                fw.append(obj.SerialNo);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(String.valueOf(obj.EquipmentIdAuto)));
                fw.append(COMMA_DELIMITER);
                fw.append(obj.Examiner);
                fw.append(COMMA_DELIMITER);
                fw.append(obj.InspectionDate);
                fw.append(COMMA_DELIMITER);
                fw.append(obj.SMU);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.Impact));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.Abrasive));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.Moisture));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.Packing));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.TrackSagLeft));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.TrackSagRight));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.DryJointsLeft));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.DryJointsRight));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.ExtCannonLeft));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.ExtCannonRight));
                fw.append(COMMA_DELIMITER);
                fw.append(obj.JobsiteComments);
                fw.append(COMMA_DELIMITER);
                fw.append(obj.InspectorComments);

                fw.append(NEW_LINE_SEPARATOR);

                //Write Track Inspection Details
                iReturnValue = WriteInspectionDetails(obj.Details,obj.EquipmentIdAuto,obj.SerialNo);
            }
        }
        catch(Exception e)
        {}
        finally {
            try {
                fw.flush();
                fw.close();
            }catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }


        //Save Data for New Equipments
        iReturnValue = WriteEquipmentData(equipList.GetNewEquipmentsInspectionsList());

        return iReturnValue;
    }

    public int WriteInspectionDetails(ArrayList<InspectionDetails> inspectDetails,long equipmentId,String SerialNo)
    {
        int iReturnValue = 0;
        String fileName = "/TrackInspectionDetails.csv";
        String filePath = "";
        FileWriter fw = null;
        try
        {
            File FileStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            boolean fileExists = false;
            filePath = FileStorageDir+ File.separator + fileName;
            try{
                File file = new File(filePath);
                if(file.exists()) {
                    //file.delete();
                    fileExists = true;
                }
            }
            catch(Exception e)
            {}

            fw = new FileWriter(filePath,true);
            //InspectionDetails class
            String strColumnHeader = "SerialNo,EquipmentId_auto,CompartIdAuto,TrackUnitAuto,Reading,PercentageWorn,ToolUsed,Comments,Image,AttachmentType,FlangeType";

            if(!fileExists) {
                fw.append(strColumnHeader);
                fw.append(NEW_LINE_SEPARATOR);
            }

            for(int i=0;i<inspectDetails.size(); i++)
            {
                fw.append(SerialNo);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipmentId));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(inspectDetails.get(i).CompartIdAuto));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(inspectDetails.get(i).TrackUnitAuto));
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).Reading);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(inspectDetails.get(i).PercentageWorn));
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).ToolUsed);
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).Comments);
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).Image);
                fw.append(String.valueOf(inspectDetails.get(i).AttachmentType));
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).FlangeType);

                fw.append(NEW_LINE_SEPARATOR);
            }
            iReturnValue = 1;
        }
        catch(Exception e)
        {
            iReturnValue = -1;
        }
        finally {
            try {
                fw.flush();
                fw.close();
            }catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
        return iReturnValue;
    }



    //For New Equipment
    public int WriteEquipmentData(ArrayList<Equipment> equipList)
    {
        int iReturnValue = 0;
        String fileName = "/NewEquipmentData.csv";
        String filePath = "";

        FileWriter fw = null;
        try
        {
            File FileStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            boolean fileExists = false;
            filePath = FileStorageDir+ File.separator + fileName;
            try{
                File file = new File(filePath);
                if(file.exists()) {
                   // file.delete();
                    fileExists = true;
                }
            }
            catch(Exception e)
            {}

            fw = new FileWriter(filePath,true);

            //Write header
            //Equipment
            String strColumnHeader = "UnitNo,SerialNo,Customer,Jobsite,Family,Model,SMU,Location,Image,ImageRes,EquipmentID,Jobsite_auto,Status,IsNew,Customer_auto,Model_auto,Examiner,CreationDate,CustomerName,JobsiteName";


            if(!fileExists) {
                fw.append(strColumnHeader);
                fw.append(NEW_LINE_SEPARATOR);
            }

            for(int i=0; i< equipList.size(); i++)
            {
                fw.append(equipList.get(i).GetUnitNo());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetSerialNo());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetCustomer());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetJobsite());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetFamily());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetModel());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetSMU());
                fw.append(COMMA_DELIMITER);
                if(equipList.get(i).GetLocation() != null)
                    fw.append(equipList.get(i).GetLocation().toString());
                else
                    fw.append("");
                fw.append(COMMA_DELIMITER);
                if(equipList.get(i).GetImage() != null)
                    fw.append(equipList.get(i).GetImage().toString());
                else
                    fw.append("");
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipList.get(i).GetImageRes()));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipList.get(i).GetEquipmentID()));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipList.get(i).GetJobsiteAuto()));
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetStatus());
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipList.get(i).GetIsNew()));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipList.get(i).GetCustomerId()));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipList.get(i).GetModelId()));
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetExaminer());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetCreationDate());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetCustomerName());
                fw.append(COMMA_DELIMITER);
                fw.append(equipList.get(i).GetJobsiteName());

                ///
                fw.append(NEW_LINE_SEPARATOR);

                iReturnValue = WriteEquipmentDetails(equipList.get(i).GetEquipmentDetails(),equipList.get(i).GetSerialNo());
                WriteInspectionToFileForNewEquipment(equipList.get(i).GetEquipmentInspection(),equipList.get(i).GetEquipmentID(),equipList.get(i).GetSerialNo());

            }

        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            iReturnValue = -1;
        }
        finally
        {
            try {
                fw.flush();
                fw.close();
            }catch(IOException e)
            {
                System.out.println(e.getMessage());
                iReturnValue = -1;
            }
        }

        return iReturnValue;
    }

    public int WriteEquipmentDetails(ArrayList<EquipmentDetails> details,String SerialNo)
    {
        int iReturnValue = 1;
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String fileName = "/EquipmentDetailsData.csv";
        String filePath = "";
        FileWriter fw = null;
        try {
            File FileStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            filePath = FileStorageDir + File.separator + fileName;
            boolean fileExists = false;

            try {
                File file = new File(filePath);
                if (file.exists()) {
                    //file.delete();
                    fileExists = true;
                }
            } catch (Exception e) { iReturnValue = -1;
            }

            String strColumnHeader = "SerialNo,Equnit_auto,CompartType,CompartId_auto,CompartId,Compart,Pos,Side,Image,EquipmentId_auto,FlangeType,Reading,Tool,Method,Comments,InspectionImage";

            fw = new FileWriter(filePath, true);
            if(!fileExists) {

                fw.append(strColumnHeader);
                fw.append(NEW_LINE_SEPARATOR);
            }

            for(int i=0; i< details.size(); i++)
            {
                fw.append(SerialNo);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(details.get(i).GetEqunitAuto()));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(details.get(i).GetComponentType()));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(details.get(i).GetCompartIdAuto()));
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetCompartId());
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetCompart());
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(details.get(i).GetPosition()));
                fw.append(COMMA_DELIMITER);
                if(details.get(i).GetImage() != null)
                    fw.append(details.get(i).GetImage().toString());
                else
                    fw.append("");
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetSide());
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(details.get(i).GetEquipmentIdAuto()));
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetFlangeType());
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetReading());
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetTool());
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetMethod());
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetComments());
                fw.append(COMMA_DELIMITER);
                fw.append(details.get(i).GetInspectionImage());



                fw.append(NEW_LINE_SEPARATOR);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            iReturnValue = -1;
        }
        finally
        {
            try {
                fw.flush();
                fw.close();
            }catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
         return iReturnValue;
    }



    public int WriteInspectionToFileForNewEquipment(UndercarriageInspectionEntity obj,long equipmentid,String SerialNo)
    {
        int iReturnValue = 1;

        // For already saved equipments
        String fileName = "/TrackInspectionForNewEquipment.csv";
        String filePath = "";

        FileWriter fw = null;
        try
        {
            File FileStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            // Create the storage directory if it does not exist
            if (! FileStorageDir.exists()){
                if (! FileStorageDir.mkdirs()){
                    Log.d("Infotrak", "failed to create directory");
                    return -3;
                }
            }

            boolean fileExists = false;
            filePath = FileStorageDir+ File.separator + fileName;
            try{
                File file = new File(filePath);
                if(file.exists()) {
                    //file.delete();
                    fileExists = true;
                }
            }
            catch(Exception e)
            {}

            fw = new FileWriter(filePath,true);
            //UndercarriageInspectionEntity class
            String strColumnHeader = "SerialNo,EquipmentId,Examiner,InspectionDate,SMU,Impact,Abrasive,Moisture,Packing,TrackSagLeft,TrackSagRight,DryJointsLeft,DryJointsRight,ExtCannonLeft,ExtCannonRight,JobsiteComments,InspectionComments";

            if(!fileExists) {
                fw.append(strColumnHeader);
                fw.append(NEW_LINE_SEPARATOR);
            }
                fw.append(SerialNo);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipmentid));
                fw.append(COMMA_DELIMITER);
                fw.append(obj.Examiner);
                fw.append(COMMA_DELIMITER);
                fw.append(obj.InspectionDate);
                fw.append(COMMA_DELIMITER);
                fw.append(obj.SMU);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.Impact));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.Abrasive));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.Moisture));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.Packing));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.TrackSagLeft));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.TrackSagRight));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.DryJointsLeft));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.DryJointsRight));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.ExtCannonLeft));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(obj.ExtCannonRight));
                fw.append(COMMA_DELIMITER);
                fw.append(obj.JobsiteComments);
                fw.append(COMMA_DELIMITER);
                fw.append(obj.InspectorComments);

                fw.append(NEW_LINE_SEPARATOR);

                //Write Track Inspection Details
                iReturnValue = WriteInspectionDetailsForNewEquipment(obj.Details,equipmentid,SerialNo);
        }
        catch(Exception e)
        {}
        finally {
            try {
                fw.flush();
                fw.close();
            }catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }

        return iReturnValue;
    }


    public int WriteInspectionDetailsForNewEquipment(ArrayList<InspectionDetails> inspectDetails,long equipmentid,String SerialNo)
    {
        int iReturnValue = 0;
        String fileName = "/TrackInspectionDetailsForNewEquipment.csv";
        String filePath = "";
        FileWriter fw = null;
        try
        {
            File FileStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "InfoTrakData");

            boolean fileExists = false;
            filePath = FileStorageDir+ File.separator + fileName;
            try{
                File file = new File(filePath);
                if(file.exists()) {
                    //file.delete();
                    fileExists = true;
                }
            }
            catch(Exception e)
            {}

            fw = new FileWriter(filePath,true);
            //InspectionDetails class
            String strColumnHeader = "SerialNo,Equipmentid_auto,CompartIdAuto,TrackUnitAuto,Reading,PercentageWorn,ToolUsed,Comments,Image,AttachmentType,FlangeType";

            if(!fileExists) {
                fw.append(strColumnHeader);
                fw.append(NEW_LINE_SEPARATOR);
            }

            for(int i=0;i<inspectDetails.size(); i++)
            {
                fw.append(SerialNo);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(equipmentid));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(inspectDetails.get(i).CompartIdAuto));
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(inspectDetails.get(i).TrackUnitAuto));
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).Reading);
                fw.append(COMMA_DELIMITER);
                fw.append(String.valueOf(inspectDetails.get(i).PercentageWorn));
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).ToolUsed);
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).Comments);
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).Image);
                fw.append(String.valueOf(inspectDetails.get(i).AttachmentType));
                fw.append(COMMA_DELIMITER);
                fw.append(inspectDetails.get(i).FlangeType);

                fw.append(NEW_LINE_SEPARATOR);
            }
            iReturnValue = 1;
        }
        catch(Exception e)
        {
            iReturnValue = -1;
        }
        finally {
            try {
                fw.flush();
                fw.close();
            }catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
        return iReturnValue;
    }


    public boolean isFileExist(String filePath)
    {
        boolean bFileExist = false;

        try {
            File file = new File(filePath);
            if (file.exists()) {
                bFileExist = true;
            }
        } catch (Exception e) {
        }

        return bFileExist;
    }

    public boolean Read()
    {
        return true;
    }


}
