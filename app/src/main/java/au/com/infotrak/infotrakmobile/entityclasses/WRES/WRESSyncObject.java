package au.com.infotrak.infotrakmobile.entityclasses.WRES;

import android.content.Context;

import java.util.ArrayList;

import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;

public class WRESSyncObject {

    long serverInspectionId;
    long ModuleSubAuto;
    long SystemId;
    long JobsiteId;
    String InspectorId;
    String JobNumber;
    String OldTagNumber;
    String OverallComment;
    String OverallRecommendation;
    String CustomerReference;
    long CrackTests_TestPassed;
    String CrackTests_Comment;
    ArrayList<WRESSyncImage> CrackTestImages;
    ArrayList<WRESSyncImage> InitialImages;
    ArrayList<ComponentRecords> ComponentRecords;
    ArrayList<DipTestRecords> DipTestRecords;

    public WRESSyncObject(
            Context context,
            long id,
            long ModuleSubAuto,
            long systemId,
            long jobsiteId,
            String inspectorId,
            String jobNumber,
            String oldTagNumber,
            String overallComment,
            String overallRecommendation,
            String customerReference,
            long crackTests_TestPassed,
            String crackTests_Comment,
            int uom) {

        SystemId = systemId;
        JobsiteId = jobsiteId;
        InspectorId = inspectorId;
        JobNumber = jobNumber;
        OldTagNumber = oldTagNumber;
        OverallComment = overallComment;
        OverallRecommendation = overallRecommendation;
        CustomerReference = customerReference;
        CrackTests_TestPassed = crackTests_TestPassed;
        CrackTests_Comment = crackTests_Comment;

        // CrackTest Images
        WRESDataContext db = new WRESDataContext(context);
        CrackTestImages = db.selectSyncCrackTestImages(id);

        // Initial Images
        InitialImages = db.selectInitialImages(id);

        // Components
        ComponentRecords = db.selectComponentRecords(id, uom);

        // Dip Tests
        DipTestRecords = db.selectDipTestsRecords(id);
    }

    //////////////////////
    // Sync image object
    public static class WRESSyncImage {
        String Data;
        String ImageTypeDescription;
        String ImageTitle;
        String ImageComment;

        public WRESSyncImage(String data, String imageTypeDescription, String imageTitle, String imageComment) {
            Data = data;
            ImageTypeDescription = imageTypeDescription;
            ImageTitle = imageTitle;
            ImageComment = imageComment;
        }
    }

    ///////////////////
    // Initial images
    public ArrayList<WRESSyncImage> getInitialImages() {
        return InitialImages;
    }
    public void setInitialImages(ArrayList<WRESSyncImage> initialImages) {
        InitialImages = initialImages;
    }

    //////////////////
    // Components
    public static class ComponentRecords {
        long ComponentId;
        String Comment;
        double Measurement;
        String MeasurementToolId;
        String WornPercentage;
        ArrayList<Long> RecommendationId;
        ArrayList<WRESSyncImage> Images;
        public ComponentRecords(long componentId, String comment, double measurement, String measurementToolId, String wornPercentage, ArrayList<Long> recommendationId, ArrayList<WRESSyncImage> images) {
            ComponentId = componentId;
            Comment = comment;
            Measurement = measurement;
            MeasurementToolId = measurementToolId;
            WornPercentage = wornPercentage;
            RecommendationId = recommendationId;
            Images = images;
        }
    }

    ////////////////
    // DipTests
    public static class DipTestRecords {
        int Measurement;
        int ConditionId;
        String Comment;
        String Recommendation;
        int Number;
        ArrayList<WRESSyncImage> Images;

        public DipTestRecords(int measurement, int conditionId, String comment, String recommendation, int number, ArrayList<WRESSyncImage> images) {
            Measurement = measurement;
            ConditionId = conditionId;
            Comment = comment;
            Recommendation = recommendation;
            Number = number;
            Images = images;
        }
    }

    /////////////////
    // Crack Tests
    public ArrayList<WRESSyncObject.ComponentRecords> getComponentRecords() {
        return ComponentRecords;
    }

    public void setComponentRecords(ArrayList<WRESSyncObject.ComponentRecords> componentRecords) {
        ComponentRecords = componentRecords;
    }

    //////////////////
    // Upload image //
    //////////////////
    public static class UploadImage {
        private long UploadInspectionId;
        private String Title;
        private String Comment;
        private String FileName;
        private String Data;

        public UploadImage(long uploadInspectionId, String title, String comment, String fileName, String data) {
            UploadInspectionId = uploadInspectionId;
            Title = title;
            Comment = comment;
            FileName = fileName;
            Data = data;
        }
    }

    /////////////////////////////
    // Create new chain
    public static class CreateNewChain {
        private String UserId;
        private long systemId;
        private int CustomerId;
        private long JobsiteId;
        private String Serial;
        private int HoursAtInstall;
        private int MakeAuto;
        private int ModelAuto;
        private LinkComponent LinkComponent;
        private BushingComponent BushingComponent;
        private ShoeComponent ShoeComponent;

        public long getSystemId() {
            return systemId;
        }

        public void setSystemId(long systemId) {
            this.systemId = systemId;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        public int getCustomerId() {
            return CustomerId;
        }

        public void setCustomerId(int customerId) {
            CustomerId = customerId;
        }

        public long getJobsiteId() {
            return JobsiteId;
        }

        public void setJobsiteId(long jobsiteId) {
            JobsiteId = jobsiteId;
        }

        public String getSerial() {
            return Serial;
        }

        public void setSerial(String serial) {
            Serial = serial;
        }

        public int getHoursAtInstall() {
            return HoursAtInstall;
        }

        public void setHoursAtInstall(int hoursAtInstall) {
            HoursAtInstall = hoursAtInstall;
        }

        public int getMakeAuto() {
            return MakeAuto;
        }

        public void setMakeAuto(int makeAuto) {
            MakeAuto = makeAuto;
        }

        public int getModelAuto() {
            return ModelAuto;
        }

        public void setModelAuto(int modelAuto) {
            ModelAuto = modelAuto;
        }

        public WRESSyncObject.LinkComponent getLinkComponent() {
            return LinkComponent;
        }

        public void setLinkComponent(WRESSyncObject.LinkComponent linkComponent) {
            LinkComponent = linkComponent;
        }

        public WRESSyncObject.BushingComponent getBushingComponent() {
            return BushingComponent;
        }

        public void setBushingComponent(WRESSyncObject.BushingComponent bushingComponent) {
            BushingComponent = bushingComponent;
        }

        public WRESSyncObject.ShoeComponent getShoeComponent() {
            return ShoeComponent;
        }

        public void setShoeComponent(WRESSyncObject.ShoeComponent shoeComponent) {
            ShoeComponent = shoeComponent;
        }
    }

    public static class LinkComponent {
        private int compartid_auto;
        private int brand_auto;
        private String budget_life;
        private String hours_on_surface;
        private String cost;

        public int getCompartid_auto() {
            return compartid_auto;
        }

        public void setCompartid_auto(int compartid_auto) {
            this.compartid_auto = compartid_auto;
        }

        public int getBrand_auto() {
            return brand_auto;
        }

        public void setBrand_auto(int brand_auto) {
            this.brand_auto = brand_auto;
        }

        public String getBudget_life() {
            return budget_life;
        }

        public void setBudget_life(String budget_life) {
            this.budget_life = budget_life;
        }

        public String getHours_on_surface() {
            return hours_on_surface;
        }

        public void setHours_on_surface(String hours_on_surface) {
            this.hours_on_surface = hours_on_surface;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }

    public static class BushingComponent {
        private int compartid_auto;
        private int brand_auto;
        private String budget_life;
        private String hours_on_surface;
        private String cost;

        public int getCompartid_auto() {
            return compartid_auto;
        }

        public void setCompartid_auto(int compartid_auto) {
            this.compartid_auto = compartid_auto;
        }

        public int getBrand_auto() {
            return brand_auto;
        }

        public void setBrand_auto(int brand_auto) {
            this.brand_auto = brand_auto;
        }

        public String getBudget_life() {
            return budget_life;
        }

        public void setBudget_life(String budget_life) {
            this.budget_life = budget_life;
        }

        public String getHours_on_surface() {
            return hours_on_surface;
        }

        public void setHours_on_surface(String hours_on_surface) {
            this.hours_on_surface = hours_on_surface;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }

    public static class ShoeComponent {
        private int compartid_auto;
        private int brand_auto;
        private String budget_life;
        private String hours_on_surface;
        private String cost;
        private int shoe_size_id;
        private String grouser;

        public int getCompartid_auto() {
            return compartid_auto;
        }

        public void setCompartid_auto(int compartid_auto) {
            this.compartid_auto = compartid_auto;
        }

        public int getBrand_auto() {
            return brand_auto;
        }

        public void setBrand_auto(int brand_auto) {
            this.brand_auto = brand_auto;
        }

        public String getBudget_life() {
            return budget_life;
        }

        public void setBudget_life(String budget_life) {
            this.budget_life = budget_life;
        }

        public String getHours_on_surface() {
            return hours_on_surface;
        }

        public void setHours_on_surface(String hours_on_surface) {
            this.hours_on_surface = hours_on_surface;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public int getShoe_size_id() {
            return shoe_size_id;
        }

        public void setShoe_size_id(int shoe_size_id) {
            this.shoe_size_id = shoe_size_id;
        }

        public String getGrouser() {
            return grouser;
        }

        public void setGrouser(String grouser) {
            this.grouser = grouser;
        }
    }


    public long getServerInspectionId() {
        return serverInspectionId;
    }

    public void setServerInspectionId(long serverInspectionId) {
        this.serverInspectionId = serverInspectionId;
    }

    public long getModuleSubAuto() {
        return ModuleSubAuto;
    }

    public void setModuleSubAuto(long ModuleSubAuto) {
        this.ModuleSubAuto = ModuleSubAuto;
    }

    public long getSystemId() {
        return SystemId;
    }

    public void setSystemId(long systemId) {
        SystemId = systemId;
    }

    public long getJobsiteId() {
        return JobsiteId;
    }

    public void setJobsiteId(long jobsiteId) {
        JobsiteId = jobsiteId;
    }

    public String getInspectorId() {
        return InspectorId;
    }

    public void setInspectorId(String inspectorId) {
        InspectorId = inspectorId;
    }

    public String getJobNumber() {
        return JobNumber;
    }

    public void setJobNumber(String jobNumber) {
        JobNumber = jobNumber;
    }

    public String getOldTagNumber() {
        return OldTagNumber;
    }

    public void setOldTagNumber(String oldTagNumber) {
        OldTagNumber = oldTagNumber;
    }

    public String getOverallComment() {
        return OverallComment;
    }

    public void setOverallComment(String overallComment) {
        OverallComment = overallComment;
    }

    public String getOverallRecommendation() {
        return OverallRecommendation;
    }

    public void setOverallRecommendation(String overallRecommendation) {
        OverallRecommendation = overallRecommendation;
    }

    public String getCustomerReference() {
        return CustomerReference;
    }

    public void setCustomerReference(String customerReference) {
        CustomerReference = customerReference;
    }

    public long getCrackTests_TestPassed() {
        return CrackTests_TestPassed;
    }

    public void setCrackTests_TestPassed(long crackTests_TestPassed) {
        CrackTests_TestPassed = crackTests_TestPassed;
    }

    public String getCrackTests_Comment() {
        return CrackTests_Comment;
    }

    public void setCrackTests_Comment(String crackTests_Comment) {
        CrackTests_Comment = crackTests_Comment;
    }
}
