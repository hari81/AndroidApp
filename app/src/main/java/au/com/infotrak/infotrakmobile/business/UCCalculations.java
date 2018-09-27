package au.com.infotrak.infotrakmobile.business;

import au.com.infotrak.infotrakmobile.datastorage.InfotrakDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.CATLimits;
import au.com.infotrak.infotrakmobile.entityclasses.ComponentInspection;
import au.com.infotrak.infotrakmobile.entityclasses.HITACHILimits;
import au.com.infotrak.infotrakmobile.entityclasses.ITMLimits;
import au.com.infotrak.infotrakmobile.entityclasses.Jobsite;
import au.com.infotrak.infotrakmobile.entityclasses.KOMATSULimits;
import au.com.infotrak.infotrakmobile.entityclasses.LIEBHERRLimits;

/**
 * Created by SamuelC on 27/03/2015.
 */
public class UCCalculations {

    public static long GetPercentage(ComponentInspection componentInspection){

        String reading = componentInspection.GetReading();
        InfotrakDataContext dbContext = componentInspection.GetDBContext();
        if(reading == null || reading.equals(""))
            return -1;

        long jobsiteId = dbContext.GetJobsiteByEquipmentId(componentInspection.GetEquipmentID());
        switch (componentInspection.GetMethod().toUpperCase()) {
            case "CAT":
                return UseCATMethod(componentInspection, jobsiteId, dbContext);
            case "ITM":
                return UseITMMethod(componentInspection, dbContext);
            case "KOMATSU":
                return UseKomatsuMethod(componentInspection,jobsiteId, dbContext);
            case "HITACHI":
                return UseHitachiMethod(componentInspection,jobsiteId, dbContext);
            case "LIEBHERR":
                return UseLiebherrMethod(componentInspection,jobsiteId, dbContext);
        }
        return 0;
    }

    private static long UseITMMethod(ComponentInspection componentInspection, InfotrakDataContext dbContext) {
        double reading = Double.parseDouble(componentInspection.GetReading());
        ITMLimits itmLimits = dbContext.GetITMLimitsForComponent(componentInspection);

        if(itmLimits != null) {
            if(itmLimits.GetStartDepthNew() > itmLimits.GetWearDepth100Percent()) {
                if (reading <= itmLimits.GetStartDepthNew() && reading > itmLimits.GetWearDepth10Percent())
                    return LinearFunction(itmLimits.GetWearDepth10Percent(), itmLimits.GetStartDepthNew(), 10, 0, reading);
                if (reading <= itmLimits.GetWearDepth10Percent() && reading > itmLimits.GetWearDepth20Percent())
                    return LinearFunction(itmLimits.GetWearDepth20Percent(), itmLimits.GetWearDepth10Percent(), 20, 10, reading);
                if (reading <= itmLimits.GetWearDepth20Percent() && reading > itmLimits.GetWearDepth30Percent())
                    return LinearFunction(itmLimits.GetWearDepth30Percent(), itmLimits.GetWearDepth20Percent(), 30, 20, reading);
                if (reading <= itmLimits.GetWearDepth30Percent() && reading > itmLimits.GetWearDepth40Percent())
                    return LinearFunction(itmLimits.GetWearDepth40Percent(), itmLimits.GetWearDepth30Percent(), 40, 30, reading);
                if (reading <= itmLimits.GetWearDepth40Percent() && reading > itmLimits.GetWearDepth50Percent())
                    return LinearFunction(itmLimits.GetWearDepth50Percent(), itmLimits.GetWearDepth40Percent(), 50, 40, reading);
                if (reading <= itmLimits.GetWearDepth50Percent() && reading > itmLimits.GetWearDepth60Percent())
                    return LinearFunction(itmLimits.GetWearDepth60Percent(), itmLimits.GetWearDepth50Percent(), 60, 50, reading);
                if (reading <= itmLimits.GetWearDepth60Percent() && reading > itmLimits.GetWearDepth70Percent())
                    return LinearFunction(itmLimits.GetWearDepth70Percent(), itmLimits.GetWearDepth60Percent(), 70, 60, reading);
                if (reading <= itmLimits.GetWearDepth70Percent() && reading > itmLimits.GetWearDepth80Percent())
                    return LinearFunction(itmLimits.GetWearDepth80Percent(), itmLimits.GetWearDepth70Percent(), 80, 70, reading);
                if (reading <= itmLimits.GetWearDepth80Percent() && reading > itmLimits.GetWearDepth90Percent())
                    return LinearFunction(itmLimits.GetWearDepth90Percent(), itmLimits.GetWearDepth80Percent(), 90, 80, reading);
                if (reading <= itmLimits.GetWearDepth90Percent() && reading > itmLimits.GetWearDepth100Percent())
                    return LinearFunction(itmLimits.GetWearDepth100Percent(), itmLimits.GetWearDepth90Percent(), 100, 90, reading);
                if (reading <= itmLimits.GetWearDepth100Percent() && reading > itmLimits.GetWearDepth110Percent())
                    return LinearFunction(itmLimits.GetWearDepth110Percent(), itmLimits.GetWearDepth100Percent(), 110, 100, reading);
                if (reading <= itmLimits.GetWearDepth110Percent() && reading > itmLimits.GetWearDepth120Percent())
                    return LinearFunction(itmLimits.GetWearDepth120Percent(), itmLimits.GetWearDepth110Percent(), 120, 110, reading);

                return 120;
            }else {
                if (reading >= itmLimits.GetStartDepthNew() && reading < itmLimits.GetWearDepth10Percent())
                    return LinearFunction(itmLimits.GetWearDepth10Percent(), itmLimits.GetStartDepthNew(), 10, 0, reading);
                if (reading >= itmLimits.GetWearDepth10Percent() && reading < itmLimits.GetWearDepth20Percent())
                    return LinearFunction(itmLimits.GetWearDepth20Percent(), itmLimits.GetWearDepth10Percent(), 20, 10, reading);
                if (reading >= itmLimits.GetWearDepth20Percent() && reading < itmLimits.GetWearDepth30Percent())
                    return LinearFunction(itmLimits.GetWearDepth30Percent(), itmLimits.GetWearDepth20Percent(), 30, 20, reading);
                if (reading >= itmLimits.GetWearDepth30Percent() && reading < itmLimits.GetWearDepth40Percent())
                    return LinearFunction(itmLimits.GetWearDepth40Percent(), itmLimits.GetWearDepth30Percent(), 40, 30, reading);
                if (reading >= itmLimits.GetWearDepth40Percent() && reading < itmLimits.GetWearDepth50Percent())
                    return LinearFunction(itmLimits.GetWearDepth50Percent(), itmLimits.GetWearDepth40Percent(), 50, 40, reading);
                if (reading >= itmLimits.GetWearDepth50Percent() && reading < itmLimits.GetWearDepth60Percent())
                    return LinearFunction(itmLimits.GetWearDepth60Percent(), itmLimits.GetWearDepth50Percent(), 60, 50, reading);
                if (reading >= itmLimits.GetWearDepth60Percent() && reading < itmLimits.GetWearDepth70Percent())
                    return LinearFunction(itmLimits.GetWearDepth70Percent(), itmLimits.GetWearDepth60Percent(), 70, 60, reading);
                if (reading >= itmLimits.GetWearDepth70Percent() && reading < itmLimits.GetWearDepth80Percent())
                    return LinearFunction(itmLimits.GetWearDepth80Percent(), itmLimits.GetWearDepth70Percent(), 80, 70, reading);
                if (reading >= itmLimits.GetWearDepth80Percent() && reading < itmLimits.GetWearDepth90Percent())
                    return LinearFunction(itmLimits.GetWearDepth90Percent(), itmLimits.GetWearDepth80Percent(), 90, 80, reading);
                if (reading >= itmLimits.GetWearDepth90Percent() && reading < itmLimits.GetWearDepth100Percent())
                    return LinearFunction(itmLimits.GetWearDepth100Percent(), itmLimits.GetWearDepth90Percent(), 100, 90, reading);
                if (reading >= itmLimits.GetWearDepth100Percent() && reading < itmLimits.GetWearDepth110Percent())
                    return LinearFunction(itmLimits.GetWearDepth110Percent(), itmLimits.GetWearDepth100Percent(), 110, 100, reading);
                if (reading >= itmLimits.GetWearDepth110Percent() && reading < itmLimits.GetWearDepth120Percent())
                    return LinearFunction(itmLimits.GetWearDepth120Percent(), itmLimits.GetWearDepth110Percent(), 120, 110, reading);

                return 120;
            }
        }else
            return 0;
    }

    private static long LinearFunction(double x2, double x1, int y2, int y1, double reading) {
        double m = (y2 - y1)/(x2 - x1);
        double c = y1 - (x1*m);

        return Math.round((m*reading) + c);
    }

    private static long UseCATMethod(ComponentInspection componentInspection, long jobsiteId, InfotrakDataContext dbContext) {
        double slope;
        double intercept;
        double reading = Double.parseDouble(componentInspection.GetReading());

        Jobsite jobsite = dbContext.GetJobsiteById(jobsiteId, componentInspection.GetEquipmentID());
        int impact = jobsite.GetImpact();
        int measureUnit = jobsite.GetUOM();

        CATLimits limitsForComponent = dbContext.GetCATLimitsForComponent(componentInspection);

        if(limitsForComponent != null) {

            if( measureUnit == 1 ) {  // is mm need convert
                reading = reading / 25.4; //TODO Check measure units
            }
            reading = reading + limitsForComponent.GetAdjToBase();
            if(limitsForComponent.GetSlope() == 0){
                if(impact < 2){
                    if(reading >= limitsForComponent.GetMiInflectionPoint()){
                        slope = limitsForComponent.GetMiSlope1();
                        intercept = limitsForComponent.GetMiIntercept1();
                    }
                    else {
                        slope = limitsForComponent.GetMiSlope2();
                        intercept = limitsForComponent.GetMiIntercept2();
                    }
                }else {
                    if (reading >= limitsForComponent.GetHiInflectionPoint()) {
                        slope = limitsForComponent.GetHiSlope1();
                        intercept = limitsForComponent.GetHiIntercept1();
                    } else {
                        slope = limitsForComponent.GetHiSlope2();
                        intercept = limitsForComponent.GetHiIntercept2();
                    }
                }
            }else{
                if(impact < 2){
                    if(reading <= limitsForComponent.GetMiInflectionPoint()){
                        slope = limitsForComponent.GetMiSlope1();
                        intercept = limitsForComponent.GetMiIntercept1();
                    }
                    else {
                        slope = limitsForComponent.GetMiSlope2();
                        intercept = limitsForComponent.GetMiIntercept2();
                    }
                }else {
                    if (reading <= limitsForComponent.GetHiInflectionPoint()) {
                        slope = limitsForComponent.GetHiSlope1();
                        intercept = limitsForComponent.GetHiIntercept1();
                    } else {
                        slope = limitsForComponent.GetHiSlope2();
                        intercept = limitsForComponent.GetHiIntercept2();
                    }
                }
            }

        }
        else{
            slope = 0;
            intercept = 0;
        }


        return Math.round((reading*slope)+intercept);
    }

    private static long UseKomatsuMethod(ComponentInspection componentInspection, long jobsiteId, InfotrakDataContext dbContext)
    {
        double secondorder;
        double slope;
        double intercept;
        double reading = Double.parseDouble(componentInspection.GetReading());

        Jobsite jobsite = dbContext.GetJobsiteById(jobsiteId, componentInspection.GetEquipmentID());
        int impact = jobsite.GetImpact();
        int measureUnit = jobsite.GetUOM();

        KOMATSULimits limitsForComponent = dbContext.GetKomatsuLimitsForComponent(componentInspection);

        if(limitsForComponent != null) {
            if( measureUnit == 1 ) {  // is mm need convert
                reading = reading / 25.4; //TODO Check measure units
            }
            if(impact < 2){
                secondorder = limitsForComponent.GetNormalSecondOrder();
                slope = limitsForComponent.GetNormalSlope();
                intercept = limitsForComponent.GetNormalIntercept();
            }else {
                secondorder = limitsForComponent.GetImpactSecondOrder();
                slope = limitsForComponent.GetImpactSlope();
                intercept = limitsForComponent.GetImpactIntercept();
            }
        }else{
            secondorder = 0;
            slope = 0;
            intercept = 0;
        }

        return Math.round((Math.pow(reading,2.0)*secondorder)+(reading*slope)+intercept);

    }

    private static long UseHitachiMethod(ComponentInspection componentInspection, long jobsiteId, InfotrakDataContext dbContext)
    {
        double slope;
        double intercept;
        double reading = Double.parseDouble(componentInspection.GetReading());

        Jobsite jobsite = dbContext.GetJobsiteById(jobsiteId, componentInspection.GetEquipmentID());
        int impact = jobsite.GetImpact();
        int measureUnit = jobsite.GetUOM();

        HITACHILimits limitsForComponent = dbContext.GetHitachiLimitsForComponent(componentInspection);

        if(limitsForComponent != null) {
            if( measureUnit == 1 ) {  // is mm need convert
                reading = reading / 25.4; //TODO Check measure units
            }
            if(impact < 2){
                slope = limitsForComponent.GetNormalSlope();
                intercept = limitsForComponent.GetNormalIntercept();
            }else {
                slope = limitsForComponent.GetImpactSlope();
                intercept = limitsForComponent.GetImpactIntercept();
            }
        }else{
            slope = 0;
            intercept = 0;
        }

        return Math.round((reading*slope)+intercept);

    }

    private static long UseLiebherrMethod(ComponentInspection componentInspection, long jobsiteId, InfotrakDataContext dbContext)
    {
        double slope;
        double intercept;
        double reading = Double.parseDouble(componentInspection.GetReading());

        Jobsite jobsite = dbContext.GetJobsiteById(jobsiteId, componentInspection.GetEquipmentID());
        int impact = jobsite.GetImpact();
        int measureUnit = jobsite.GetUOM();

        LIEBHERRLimits limitsForComponent = dbContext.GetLiebherrLimitsForComponent(componentInspection);

        if(limitsForComponent != null) {
            if( measureUnit == 1 ) {  // is mm need convert
                reading = reading / 25.4; //TODO Check measure units
            }
            if(impact < 2){
                slope = limitsForComponent.GetNormalSlope();
                intercept = limitsForComponent.GetNormalIntercept();
            }else {
                slope = limitsForComponent.GetImpactSlope();
                intercept = limitsForComponent.GetImpactIntercept();
            }
        }else{
            slope = 0;
            intercept = 0;
        }

        return Math.round((reading*slope)+intercept);

    }
}
