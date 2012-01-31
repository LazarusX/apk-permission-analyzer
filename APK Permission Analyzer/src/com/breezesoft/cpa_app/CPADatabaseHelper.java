package com.breezesoft.cpa_app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.lazarusx.android.cpa_app.R;

public class CPADatabaseHelper extends SQLiteOpenHelper {
	private final static int DB_VERSION = 1;
	public final static String DB_NAME = "apks.db";
	public static final String PACKAGE_NAME = "com.lazarusx.android.cpa_app";
	public static final String DB_PATH = "/data/data/com.lazarusx.android.cap_app/databases/apks.db";

	private SQLiteDatabase db;

	public CPADatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		db = getWritableDatabase();
	}

	public void closeDatabase() {
		this.db.close();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public int getFreq(String pmname, String catename) {
		float count = countRows(catename, pmname);
		float sum = countRows(catename);
		return (int) (count / sum * 100);
	}

	private int countRows(String catename, String pmname) {
		String cr = "SELECT COUNT(*) FROM apks WHERE " + pmname + " = 1 and category = '" + categoryNameConvert(catename) + "';";
		Cursor c = db.rawQuery(cr, null);
		// Cursor c = db.rawQuery("SELECT COUNT(*) FROM apks;", null);
		int count = 0;
		if (c.moveToFirst()) {
			do {
				count = c.getInt(0);
			} while (c.moveToNext());
		}
		return count;
	}

	private int countRows(String catename) {
		String cr = "SELECT COUNT(*) FROM apks WHERE  category = '" + categoryNameConvert(catename) + "';";
		Cursor c = db.rawQuery(cr, null);
		// Cursor c = db.rawQuery("SELECT COUNT(*) FROM apks;", null);
		int count = 0;
		if (c.moveToFirst()) {
			do {
				count = c.getInt(0);
			} while (c.moveToNext());
		}
		return count;
	}

	private static String categoryNameConvert(String category_name) {
		String result;
		result = "0";
		if (category_name.equals("GCasual"))
			result = "0";
		if (category_name.equals("GSports Games"))
			result = "1";
		if (category_name.equals("GLive Wallpaper"))
			result = "2";
		if (category_name.equals("GRacing"))
			result = "3";
		if (category_name.equals("GArcade & Action"))
			result = "4";
		if (category_name.equals("GBrain & Puzzle"))
			result = "5";
		if (category_name.equals("GWidgets"))
			result = "6";
		if (category_name.equals("GCards & Casino"))
			result = "7";
		if (category_name.equals("Libraries & Demo"))
			result = "8";
		if (category_name.equals("Personalization"))
			result = "9";
		if (category_name.equals("Transportation"))
			result = "10";
		if (category_name.equals("Sports"))
			result = "11";
		if (category_name.equals("Health & Fitness"))
			result = "12";
		if (category_name.equals("Business"))
			result = "13";
		if (category_name.equals("Wallpaper"))
			result = "14";
		if (category_name.equals("Comics"))
			result = "15";
		if (category_name.equals("Medical"))
			result = "16";
		if (category_name.equals("Books & Reference"))
			result = "17";
		if (category_name.equals("Weather"))
			result = "18";
		if (category_name.equals("Entertainment"))
			result = "19";
		if (category_name.equals("Media & Video"))
			result = "20";
		if (category_name.equals("Tools"))
			result = "21";
		if (category_name.equals("Photography"))
			result = "22";
		if (category_name.equals("Productivity"))
			result = "23";
		if (category_name.equals("Education"))
			result = "24";
		if (category_name.equals("News & Magazines"))
			result = "25";
		if (category_name.equals("Travel & Local"))
			result = "26";
		if (category_name.equals("Lifestyle"))
			result = "27";
		if (category_name.equals("Social"))
			result = "28";
		if (category_name.equals("Widgets"))
			result = "29";
		if (category_name.equals("Finance"))
			result = "30";
		if (category_name.equals("Shopping"))
			result = "31";
		if (category_name.equals("Communication"))
			result = "32";
		if (category_name.equals("Music & Audio"))
			result = "33";
		return result;
	}

}
