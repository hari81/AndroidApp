package au.com.infotrak.infotrakmobile.entityclasses.MSI;

import java.util.ArrayList;

public class MSI_SyncObject {

    //////////////////
    // Upload image //
    //////////////////
    public static class MSI_UploadImage {
        private long UploadInspectionId;
        private String Title;
        private String Comment;
        private String FileName;
        private String Data;

        public MSI_UploadImage(long uploadInspectionId, String title, String comment, String fileName, String data) {
            UploadInspectionId = uploadInspectionId;
            Title = title;
            Comment = comment;
            FileName = fileName;
            Data = data;
        }
    }

    /////////////////
    // Sync object //
    /////////////////
    public static class MSI_Sync {
        String currentDateandTime;
        long serverInspectionId;
        long inspectionId;
        String serialNo;
        String smu;
        long equipmentid_auto;
        String examiner;
        String CustomerContact;
        int TrammingHours;
        String notes;
        String Jobsite_Comms;
        String leftShoeNo;
        String rightShoeNo;
        int impact;
        int abrasive;
        int packing;
        int moisture;
        String status;
        ArrayList<MSI_SyncImage> EquipmentImages;
        ArrayList<MSI_SyncImage> JobsiteImages;
        ArrayList<MSI_SyncAdditional> AdditionalImages;
        ArrayList<MSI_SyncMandatoryImage> MandatoryImages;
        ArrayList<MSI_SyncInspectionDetail> InspectionDetails;

        public MSI_Sync() {
        }

        public String getLeftShoeNo() {
            return leftShoeNo;
        }

        public void setLeftShoeNo(String leftShoeNo) {
            this.leftShoeNo = leftShoeNo;
        }

        public String getRightShoeNo() {
            return rightShoeNo;
        }

        public void setRightShoeNo(String rightShoeNo) {
            this.rightShoeNo = rightShoeNo;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public long getInspectionId() {
            return inspectionId;
        }

        public void setInspectionId(long inspectionId) {
            this.inspectionId = inspectionId;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public long getServerInspectionId() {
            return serverInspectionId;
        }

        public void setServerInspectionId(long serverInspectionId) {
            this.serverInspectionId = serverInspectionId;
        }

        public String getCurrentDateandTime() {
            return currentDateandTime;
        }

        public void setCurrentDateandTime(String currentDateandTime) {
            this.currentDateandTime = currentDateandTime;
        }

        public Long getEquipmentid_auto() {
            return equipmentid_auto;
        }

        public void setEquipmentid_auto(Long equipmentid_auto) {
            this.equipmentid_auto = equipmentid_auto;
        }

        public String getExaminer() {
            return examiner;
        }

        public void setExaminer(String examiner) {
            this.examiner = examiner;
        }

        public String getCustomerContact() {
            return CustomerContact;
        }

        public void setCustomerContact(String customerContact) {
            this.CustomerContact = customerContact;
        }

        public String getSmu() {
            return smu;
        }

        public void setSmu(String smu) {
            this.smu = smu;
        }

        public int getTrammingHours() {
            return TrammingHours;
        }

        public void setTrammingHours(int trammingHours) {
            this.TrammingHours = trammingHours;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getJobsite_Comms() {
            return Jobsite_Comms;
        }

        public void setJobsite_Comms(String jobsite_Comms) {
            Jobsite_Comms = jobsite_Comms;
        }

        public int getImpact() {
            return impact;
        }

        public void setImpact(int impact) {
            this.impact = impact;
        }

        public int getAbrasive() {
            return abrasive;
        }

        public void setAbrasive(int abrasive) {
            this.abrasive = abrasive;
        }

        public int getPacking() {
            return packing;
        }

        public void setPacking(int packing) {
            this.packing = packing;
        }

        public int getMoisture() {
            return moisture;
        }

        public void setMoisture(int moisture) {
            this.moisture = moisture;
        }

        public ArrayList<MSI_SyncImage> getEquipmentImages() {
            return EquipmentImages;
        }

        public void setEquipmentImages(ArrayList<MSI_SyncImage> equipmentImages) {
            this.EquipmentImages = equipmentImages;
        }

        public ArrayList<MSI_SyncImage> getJobsiteImages() {
            return JobsiteImages;
        }

        public void setJobsiteImages(ArrayList<MSI_SyncImage> jobsiteImages) {
            JobsiteImages = jobsiteImages;
        }

        public ArrayList<MSI_SyncAdditional> getAdditionalImages() {
            return AdditionalImages;
        }

        public void setAdditionalImages(ArrayList<MSI_SyncAdditional> additionalImages) {
            AdditionalImages = additionalImages;
        }

        public ArrayList<MSI_SyncMandatoryImage> getMandatoryImages() {
            return MandatoryImages;
        }

        public void setMandatoryImages(ArrayList<MSI_SyncMandatoryImage> mandatoryImages) {
            MandatoryImages = mandatoryImages;
        }

        public ArrayList<MSI_SyncInspectionDetail> getInspectionDetails() {
            return InspectionDetails;
        }

        public void setInspectionDetails(ArrayList<MSI_SyncInspectionDetail> inspectionDetails) {
            InspectionDetails = inspectionDetails;
        }
    }

    public static class MSI_SyncImage {

        private int ServerId;
        private String ImageTitle;
        private String ImageComment;
        private String ImageFileName;

        public MSI_SyncImage(int ServerId, String imageTitle, String imageComment, String imageFileName) {
            this.ServerId = ServerId;
            this.ImageTitle = imageTitle;
            this.ImageComment = imageComment;
            this.ImageFileName = imageFileName;
        }

        public MSI_SyncImage(String imageTitle, String imageComment, String imageFileName) {
            this.ImageTitle = imageTitle;
            this.ImageComment = imageComment;
            this.ImageFileName = imageFileName;
        }

        public int getServerId() {
            return ServerId;
        }

        public void setServerId(int serverId) {
            ServerId = serverId;
        }

        public String getImageTitle() {
            return ImageTitle;
        }

        public void setImageTitle(String imageTitle) {
            ImageTitle = imageTitle;
        }

        public String getImageComment() {
            return ImageComment;
        }

        public void setImageComment(String imageComment) {
            ImageComment = imageComment;
        }

        public String getImageFileName() {
            return ImageFileName;
        }

        public void setImageFileName(String imageFileName) {
            ImageFileName = imageFileName;
        }

    }

    public static class MSI_SyncAdditional extends MSI_SyncImage{
        private String Reading;
        private String ToolCode;
        private int Side;
        private long compartTypeAuto;
        public MSI_SyncAdditional(
                long compartTypeAuto,
                String Reading,
                String ToolCode,
                int Side,
                int ServerId,
                String imageTitle,
                String imageComment,
                String imageFileName) {
            super(ServerId, imageTitle, imageComment, imageFileName);
            this.compartTypeAuto = compartTypeAuto;
            this.Reading = Reading;
            this.ToolCode = ToolCode;
            this.Side = Side;
        }

        public long getCompartTypeAuto() {
            return compartTypeAuto;
        }

        public void setCompartTypeAuto(long compartTypeAuto) {
            this.compartTypeAuto = compartTypeAuto;
        }

        public String getReading() {
            return Reading;
        }

        public void setReading(String reading) {
            Reading = reading;
        }

        public String getToolCode() {
            return ToolCode;
        }

        public void setToolCode(String toolCode) {
            ToolCode = toolCode;
        }

        public int getSide() {
            return Side;
        }

        public void setSide(int side) {
            Side = side;
        }
    }

    public static class MSI_SyncMandatoryImage extends MSI_SyncImage{
        private int Side;
        private long compartTypeAuto;
        public MSI_SyncMandatoryImage(
                long compartTypeAuto,
                int Side,
                int ServerId,
                String imageTitle,
                String imageComment,
                String imageFileName) {
            super(ServerId, imageTitle, imageComment, imageFileName);
            this.Side = Side;
            this.compartTypeAuto = compartTypeAuto;
        }



        public int getSide() {
            return Side;
        }

        public void setSide(int side) {
            Side = side;
        }
    }

    ///////////////////////////////////////////////////
    // Measurement points for each Inspection Detail //
    ///////////////////////////////////////////////////
    public static class MSI_SyncInspectionDetail {

        private long EqunitAuto;

        private ArrayList<MSI_SyncMeasurementPoint> MeasurementPoints;

        public long getEqunitAuto() {
            return EqunitAuto;
        }

        public void setEqunitAuto(long equnitAuto) {
            EqunitAuto = equnitAuto;
        }

        public ArrayList<MSI_SyncMeasurementPoint> getMeasurementPoints() {
            return MeasurementPoints;
        }

        public void setMeasurementPoints(ArrayList<MSI_SyncMeasurementPoint> measurementPoints) {
            MeasurementPoints = measurementPoints;
        }
    }

    public static class MSI_SyncMeasurementPoint {

        private long MeasurementPointId;
        private String ToolCode;
        private String Notes;
        private ArrayList<MSI_SyncMeasure> Measures;
        private ArrayList<MSI_SyncImage> Images;

        public String getNotes() {
            return Notes;
        }

        public void setNotes(String notes) {
            Notes = notes;
        }

        public long getMeasurementPointId() {
            return MeasurementPointId;
        }

        public void setMeasurementPointId(long measurementPointId) {
            MeasurementPointId = measurementPointId;
        }

        public String getToolCode() {
            return ToolCode;
        }

        public void setToolCode(String toolCode) {
            ToolCode = toolCode;
        }

        public ArrayList<MSI_SyncMeasure> getMeasures() {
            return Measures;
        }

        public void setMeasures(ArrayList<MSI_SyncMeasure> measures) {
            this.Measures = measures;
        }

        public ArrayList<MSI_SyncImage> getImages() {
            return Images;
        }

        public void setImages(ArrayList<MSI_SyncImage> images) {
            this.Images = images;
        }
    }

    public static class MSI_SyncMeasure {
        String reading;
        int measureNo;

        public String getReading() {
            return reading;
        }

        public void setReading(String reading) {
            this.reading = reading;
        }

        public int getMeasureNo() {
            return measureNo;
        }

        public void setMeasureNo(int measureNo) {
            this.measureNo = measureNo;
        }
    }

}
