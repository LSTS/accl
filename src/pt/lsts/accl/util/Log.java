package pt.lsts.accl.util;


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
    
    public String logBaseDir = "/storage/emulated/0/data/pt.lsts.accl/log/messages/";//versions 4.2 and above
    public String logBaseDirLegacy = "/storage/sdcard0/data/pt.lsts.accl/log/messages/";//versions 4.0 and 4.1

    /**
     * Private constructor to ensure Singleton Pattern.
     */
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

    /**
     * Get the Path to the Log directory.
     * @return Full Path to the Log directory.
     */
    public String getLogDir() {
        return logPath;
    }

    /**
     * Get the Path to the Log directory of the Singleton Obj.
     * @return Full Path to the Log directory of the Singleton Obj.
     */
    public static String getLogDirSingleton() {
        return getInstance().logPath;
    }

    /**
     * Call ChangeLog: create new directory for a new log file.
     * @return true if succeed, false otherwise.
     */
    public static boolean changeLogSingleton() {
        return getInstance().changeLog();
    }

    /**
     * Reset log directory. Create a new log directory
     * @return true if succeed, false otherwise.
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

    /**
     * Get the Log Instance of Singleton Obj. Create it if it is null.
     * Private to ensure only certain methods may be called.
     * @return the Singleton Obj for Log.
     */
    private static Log getInstance() {
        if (instance == null)
            instance = new Log();

        return instance;
    }

    /**
     * Private method to Log an IMCMessage.
     * @param msg The IMCMessage to be logged.
     * @return true if succeed, false otherwise.
     */
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

    /**
     * Public method used to log an IMCMessage.
     * Main Method used outside of this class.
     * @param msg
     * @return
     */
    public static boolean log(IMCMessage msg) {
        return getInstance().logMessage(msg);
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
		while(true) {
			Thread.sleep(100);
			Log.log(new EstimatedState());
		}
	}

    /**
     * Change the current Path to be used to store log file.
     * @param newPath The new Full path to store Log.
     */
    public static void changeLogBaseDir(String newPath){
        getInstance().logBaseDir = newPath;
    }


}