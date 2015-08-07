package pt.lsts.accl.util;


import pt.lsts.accl.util.FileOperations;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.ImcStringDefs;

import java.io.File;
import java.util.Vector;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;


public class Log{

    private static Log instance = null;
    private IMCOutputStream ios = null;
    protected SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd'/'HHmmss");
    {
    	fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    protected String logPath = null;
    
    public String logBaseDir = "/storage/emulated/0/Android/data/pt.lsts.accl/log/messages/";//versions 4.2 and above
    public String logBaseDirLegacy = "/storage/sdcard0/data/pt.lsts.accl/log/messages/";//versions 4.0 and 4.1

    private Log() {
        changeLog();

        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (Log.this) {
                            IMCOutputStream copy = ios;
                            ios = null;
                            copy.close(); 
                        }                                               
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLogDir() {
        return logPath;
    }

    public static String getLogDirSingleton() {
        return instance.logPath;
    }
    
    public static boolean changeLogSingleton() {
        return instance.changeLog();
    }

    /**
     * @return
     */
    public boolean changeLog() {
        logPath = logBaseDir + fmt.format(new Date());
        
        File outputDir = new File(logPath);
        outputDir.mkdirs();
        
        IMCOutputStream iosTmp = null;
        try {
        	OutputStream fos = new GZIPOutputStream(new FileOutputStream(new File(outputDir.getAbsolutePath() + "/IMC.xml.gz")));
        	fos.write(ImcStringDefs.getDefinitions().getBytes());
        	fos.close();
        	iosTmp = new IMCOutputStream(new GZIPOutputStream(new FileOutputStream(new File(outputDir, "Data.lsf.gz"))));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (iosTmp != null) {
            synchronized (this) {
                if (ios != null) {
                    try {
                        ios.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ios = iosTmp;    
            }            
            return true;
        }
        return false;
    }

    private static Log getInstance() {
        if (instance == null)
            instance = new Log();

        return instance;
    }

    private synchronized boolean logMessage(IMCMessage msg) {
        try {
            if (ios != null)
                ios.writeMessage(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean log(IMCMessage msg) {
        return getInstance().logMessage(msg);
    }
    
    public static void main(String[] args) throws Exception {
		while(true) {
			Thread.sleep(100);
			Log.log(new EstimatedState());
		}
	}

    public static void changeLogBaseDir(String newPath){
        getInstance().logBaseDir = newPath;
    }
	
    /*
	public static String getFullPath(File fileDir){
				DateFormat dateFormatDay = new SimpleDateFormat("yyyyMMdd");
		DateFormat dateFormatHours = new SimpleDateFormat("HHmmss");
		Date date = new Date();

		String path="/data/pt.lsts.accl";			
		path += "/";
		path += dateFormatDay.format(date);
		path += "/";
		path += "messages";
		path += "/";
		path += dateFormatDay.format(date);
		path += "/";
		path += dateFormatHours.format(date);
		path += "/";

		fileDir = new File (fileDir.getAbsolutePath()+path);
		//File file = new File(fileDir, ("Data" + "." + "lsf"));
		fileDir.mkdirs();

		return fileDir.getAbsolutePath();
	}
	*/

}