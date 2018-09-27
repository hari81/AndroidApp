package au.com.infotrak.infotrakmobile;
/**
 * Created by Tin on 14/12/2015.
 */

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;

public class AppLog {
    private static File error_dir;
    public static void log(Exception ex)
    {
        log(ex.getMessage());

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        String stackMessage = writer.toString();
        log(stackMessage);
    }
    public static void log(String Message)
    {
        if(BuildConfig.DEBUG){
            if(!prepare_directory()) return;
            String filename = error_dir.getPath() + File.separator +  get_file_name();

            try {
                Message = get_time() + " : " + Message;
                FileOutputStream fout = new FileOutputStream(filename, true);
                OutputStreamWriter osw = new OutputStreamWriter(fout);
                osw.write(Message + "\r\n");
                osw.flush();
                osw.close();
                fout.close();
            }catch (Exception ex)
            {
                //No logging
            }
        }
    }

    private static boolean prepare_directory()
    {
        boolean result = false;
        error_dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "InfoTrak_Errors");

        if(!error_dir.exists()) {
            result=error_dir.mkdir();
        }else
        {
            result = true;
        }
        return result;
    }

    private static String get_time(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }
    private static String get_file_name() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate  + ".txt";
    }
}