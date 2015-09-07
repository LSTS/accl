package pt.lsts.accl.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


/**
 *
 *
 * Created by jloureiro on 31-08-2015.
 */
public class FileOperations {


    public static final String TAG = FileOperations.class.getSimpleName();

    public static final String mainDirStringActual = "/storage/emulated/0/data/pt.lsts.accl/";//Android 4.2+
    public static final String mainDirStringLegacy = "/storage/sdcard0/";//Android 4.0 4.1
	//public static final String mainDirStringPrior = "/mnt/sdcard/";//Before 4.0 - UNSUPPORTED
    public static String mainDirString=null;
    //public static final String mainDirString = "/storage/emulated/0/Android/data/pt.lsts.ASA/";

	public static void initDir(File dir) {
		dir.mkdirs();
	}

	/**
	 *  Checks if external storage is available for read and write
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 *  Checks if external storage is available to at least read
	 */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * Copy a file from an inputstream to an output stream previously created.
	 *
	 * @param in The inputstream of the file to be copied.
	 * @param out The outputstream of the destination of the file.
	 * @throws IOException Possible exception may occur while coping.
	 */
	public static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		writeLines(readLines(in), out);
	}

	/**
	 * Copy a file from a File obj to another File obj.
	 *
	 * @param fileIn The ojbect of the file to be copied.
	 * @param fileOut The object representing the destination File.
	 * @throws IOException Possible exception may occur while coping.
	 */
	public static void copyFile(File fileIn, File fileOut) throws IOException {
		writeLines(readLines(fileIn), fileOut);
	}

	/**
	 * Read lines from a File and place them on a String Vector.
	 *
	 * @param file The file to read the lines from.
	 * @return The Vector of String, one per line.
	 */
	public static Vector<String> readLines(File file) {
		try {
			return readLines(new FileInputStream(file));
		} catch (Exception e) {
			Log.e(TAG,"readLines: "+e.getMessage(),e);
		}
		return null;
	}

	/**
	 * Read lines from an Inputstream of a file previously created.
	 *
	 * @param in The inputstream to read the lines from.
	 * @return The Vector of String, one per line.
	 */
	public static Vector<String> readLines(InputStream in) {
		Vector<String> lines = new Vector<String>();
		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (Exception e) {
			Log.e(TAG, "readLines: "+e.getMessage(),e);
		}
		return lines;
	}

	/**
	 * Write a line in a outputstream previously created.
	 *
	 * @param line The String with the line to be written.
	 * @param out The outputstream of a File to write to.
	 */
	public static void writeLine(String line, OutputStream out) {
		byte[] buffer = line.getBytes();
		try {
			out.write(buffer);
			out.write("\n".getBytes());
		} catch (Exception e) {
			Log.e(TAG,"writeLine: "+e.getMessage(),e);
		}
	}

	/**
	 * Write may lines in a outputstream previously created.
	 *
	 * @param lines The Vector of String with the lines to be written.
	 * @param out The outputstream of a File to write to.
	 */
	public static void writeLines(Vector<String> lines, OutputStream out) {
		for (String line : lines)
			writeLine(line, out);
	}

	/**
	 * Write a line in a File.
	 *
	 * @param line The String with the line to be written.
	 * @param file The File to write the line into.
	 */
	public static void writeLine(String line, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			writeLine(line, out);
		} catch (Exception e) {
			Log.e(TAG,"writeLine: "+e.getMessage(),e);
		}
	}

	/**
	 * Write lines on a File.
	 *
	 * @param lines The Vector with the String of the lines to be written.
	 * @param file The File to write the lines into.
	 */
	public static void writeLines(Vector<String> lines, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			writeLines(lines, out);
		} catch (Exception e) {
			Log.e(TAG,"writeLine: "+e.getMessage(),e);
		}
	}

	/**
	 * Filter a list of Filenames by their extension.
	 *
	 * @param filesArray The List of files to be filtered.
	 * @param extension The wanted extension to filter by.
	 * @return The List of files filtered by the {@param extension}.
	 */
	public static String[] filterFilesByExtension(String[] filesArray,
			String extension) {
		Vector<String> filesVector = new Vector<String>();
		for (String file : filesArray) {
			if (file.endsWith("." + extension))
				filesVector.add(file);
		}
		String[] resultArray = new String[filesVector.size()];
		for (int i = 0; i < resultArray.length && i < filesVector.size(); i++)
			resultArray[i] = filesVector.get(i);
		return resultArray;
	}

	/**
	 * Remove the {@param extension} from the filename of an array of filenames.
	 * Filenames with different extensions remain.
	 *
	 * @param filesArray The Array with the filenames to have their extension removed
	 * @param extension The extension to be removed.
	 * @return The Array with the filenames without the extension.
	 */
	public static String[] removeExtension(String[] filesArray, String extension){
		String[] result = new String[filesArray.length];
		for (int i=0;i<filesArray.length;i++) {
			if (filesArray[i].endsWith("."+extension)) {
				result[i] = filesArray[i].substring(0, filesArray[i].length() - (extension.length()+1));
			}
		}
		return result;
	}

	/**
	 * Copy ALL assets in the asset folder.
	 *
	 * @param context The context of the application to get the assets folder from.
	 * @param listPath The path for the asset folder.
	 * @return true if every asset is copied successful, false otherwise.
	 */
    public static boolean copyAssetsFolder(Context context, String listPath) {
        File dir;
		boolean result=true;
        if (listPath.equalsIgnoreCase(""))
            dir = getMainDir();
        else
            dir = new File(getMainDir(),listPath+"/");
        dir.mkdirs();//initialize folder
        Log.i(TAG, dir.getAbsolutePath() + ".mkdirs()");
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(listPath);
            for (String filename : files) {
                if (assetManager.list(filename).length==0){
					boolean bool;
					if (listPath.equalsIgnoreCase("")) {
                        Log.i(TAG, "copySpecificAsset(context, " + filename + ");");
                        bool = copySpecificAsset(context, filename);
                    }else{
                        Log.i(TAG,"copySpecificAsset(context, "+listPath+"/"+filename+");");
                        bool = copySpecificAsset(context, listPath+"/"+filename);
                    }
					if (bool==false)
						result=false;
                }else{
                    Log.i(TAG,filename+".isFolder");
					result=false;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
        }
		return result;
    }

	/**
	 * Copy a specific Asset only.
	 *
	 * @param context The Application context to find asset folder.
	 * @param name The name/path to the Asset file to be copied.
	 * @return true if sucessfull, false otherwise.
	 */
    public static boolean copySpecificAsset(Context context, String name) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
            try {
                in = assetManager.open(name);
                FileOperations.initDir(getMainDir());
                File outFile = new File(getMainDir(), name);
                out = new FileOutputStream(outFile);
                FileOperations.copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                return true;

            } catch (IOException e) {
                Log.e(TAG, "Failed to copy asset file: " + name, e);
            }
        return false;
    }

	/**
	 * Get the main directory, depends on Android version.
	 *
	 * @return The File representing the main Directory.
	 */
    public static File getMainDir(){
        int SDK_INT = Build.VERSION.SDK_INT;
		if (SDK_INT==14 || SDK_INT==15 || SDK_INT==16) {// ICS 4.0 & JB 4.1 Legacy
			mainDirString=mainDirStringLegacy;
		}else { // After JB 4.2. Actual
			mainDirString = mainDirStringActual;
		}
		return new File(mainDirString);
    }

}