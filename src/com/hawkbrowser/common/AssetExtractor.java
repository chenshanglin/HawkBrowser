package com.hawkbrowser.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;

public class AssetExtractor {

	private static final String TIMESTAMP_PREFIX = "asset_timestamp-";
    private static final int BUFFER_SIZE = 16 * 1024;
	
	private final Context mContext;
	private File mOutputDir;
	private static AssetExtractor sInstance = null;

	public static AssetExtractor get(Context context) {
		if (null == sInstance) {
			sInstance = new AssetExtractor(context);
		}

		return sInstance;
	}
	
	public String getOutputDir() {
		return mOutputDir.getPath();
	}

	public void Extract(String[] fileNames, String outDirName) {
		
		mOutputDir = new File(Util.getDataDir(mContext), outDirName);

		String timeStamp = checkTimeStamp();
		// already up-to date
		if(null == timeStamp)
			return;
		
        if(!mOutputDir.exists())
        	mOutputDir.mkdir();
		
        StringBuilder sbp = new StringBuilder();
        for (String file : fileNames) {
            if (sbp.length() > 0) sbp.append('|');
            sbp.append("\\Q" + file + "\\E");
        }
		
        Pattern filesToExtract = Pattern.compile(sbp.toString());
		AssetManager am = mContext.getResources().getAssets();
		
		try {
			
			byte[] buffer = null;
			String[] files = am.list("");
			
			for(String file : files) {
				if(!filesToExtract.matcher(file).matches())
					continue;
				
				File targetFile = new File(mOutputDir, file);
				if(targetFile.exists())
					continue;
				
				InputStream is = null;
				OutputStream os = null;
				
				try {
					is = am.open(file);
					os = new FileOutputStream(targetFile);
					if(null == buffer)
						buffer = new byte[BUFFER_SIZE];
					
					int count = 0;
					while((count = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
						os.write(buffer, 0, count);
					}
					
					os.flush();
					
                    if (targetFile.length() == 0) {
                        throw new IOException(file + " extracted with 0 length!");
                    }
                    
				} finally {
					try {
						if(null != is)
							is.close();
					} finally {
						if(null != os)
							os.close();
					}
				}
			}
		} catch(IOException e) {
			Util.deleteFiles(mOutputDir);
			return;
		}
		
		File timeStampFile = new File(mOutputDir, timeStamp);
		
		try {
			timeStampFile.createNewFile();
		} catch (IOException e) {
            if (Config.LOG_ENABLED)
                Log.e(Config.LOG_TAG, e.getMessage());
		}
	}
	
	private String checkTimeStamp() {
		
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = null;

		try {
			pi = pm.getPackageInfo(mContext.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			return TIMESTAMP_PREFIX;
		}

		if (pi == null) {
			return TIMESTAMP_PREFIX;
		}

		String expectedTimestamp = TIMESTAMP_PREFIX + pi.versionCode + "-"
				+ pi.lastUpdateTime;

		String[] timestamps = mOutputDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(TIMESTAMP_PREFIX);
			}
		});

		if (null == timestamps || timestamps.length != 1)  {
			// If there's no timestamp, nuke to be safe as we can't tell the age
			// of the files.
			// If there's multiple timestamps, something's gone wrong so nuke.
			return expectedTimestamp;
		}

		if (!expectedTimestamp.equals(timestamps[0])) {
			
			new File(mOutputDir, timestamps[0]).delete();			
			return expectedTimestamp;
		}

		// timestamp file is already up-to date.
		return null;
	}

	private AssetExtractor(Context context) {

		mContext = context;
		mOutputDir = new File(Util.getDataDir(context), "adblock");
	}

}
