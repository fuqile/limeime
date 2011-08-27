/*    
 **    Copyright 2010, The LimeIME Open Source Project
 ** 
 **    Project Url: http://code.google.com/p/limeime/
 **                 http://android.toload.net/
 **
 **    This program is free software: you can redistribute it and/or modify
 **    it under the terms of the GNU General Public License as published by
 **    the Free Software Foundation, either version 3 of the License, or
 **    (at your option) any later version.

 **    This program is distributed in the hope that it will be useful,
 **    but WITHOUT ANY WARRANTY; without even the implied warranty of
 **    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 **    GNU General Public License for more details.

 **    You should have received a copy of the GNU General Public License
 **    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.toload.main.hd.limedb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.toload.main.hd.global.ImObj;
import net.toload.main.hd.global.KeyboardObj;
import net.toload.main.hd.global.LIME;
import net.toload.main.hd.global.LIMEPreferenceManager;
import net.toload.main.hd.global.Mapping;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

/**
 * @author Art Hung
 */
public class LimeDB extends SQLiteOpenHelper {

	private static boolean DEBUG = false;
	private static String TAG = "LIMEDB";
	
	//Jeremy '11,8,5
	private final static String INITIAL_RESULT_LIMIT = "10";
	private final static int INITIAL_RELATED_LIMIT = 5;

	private final static int DATABASE_VERSION = 66; 
	//private final static int DATABASE_RELATED_SIZE = 50;

	public final static String FIELD_id = "_id";
	public final static String FIELD_CODE = "code";
	public final static String FIELD_WORD = "word";
	public final static String FIELD_RELATED = "related";
	public final static String FIELD_SCORE = "score";
	public final static String FIELD_CODE3R = "code3r";

	public final static String FIELD_DIC_id = "_id";
	public final static String FIELD_DIC_pcode = "pcode";
	public final static String FIELD_DIC_pword = "pword";
	public final static String FIELD_DIC_ccode = "ccode";
	public final static String FIELD_DIC_cword = "cword";
	public final static String FIELD_DIC_score = "score";
	public final static String FIELD_DIC_is = "isDictionary";

	// for keyToChar
	private final static String DAYI_KEY = "1234567890qwertyuiopasdfghjkl;zxcvbnm,./";
	private final static String DAYI_CHAR =
		"言|牛|目|四|王|門|田|米|足|金|石|山|一|工|糸|火|艸|木|口|耳|人|革|日|土|手|鳥|月|立|女|虫|心|水|鹿|禾|馬|魚|雨|力|舟|竹";
	private final static String ARRAY_KEY = "qazwsxedcrfvtgbyhnujmik,ol.p;/";
	private final static String ARRAY_CHAR =
		"1^|1-|1v|2^|2-|2v|3^|3-|3v|4^|4-|4v|5^|5-|5v|6^|6-|6v|7^|7-|7v|8^|8-|8v|9^|9-|9v|0^|0-|0v|";
	private final static String BPMF_KEY = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik,9ol.0p;/-";
	private final static String BPMF_CHAR = 
		"ㄅ|ㄆ|ㄇ|ㄈ|ㄉ|ㄊ|ㄋ|ㄌ|ˇ|ㄍ|ㄎ|ㄏ|ˋ|ㄐ|ㄑ|ㄒ|ㄓ|ㄔ|ㄕ|ㄖ|ˊ|ㄗ|ㄘ|ㄙ|˙|ㄧ|ㄨ|ㄩ|ㄚ|ㄛ|ㄜ|ㄝ|ㄞ|ㄟ|ㄠ|ㄡ|ㄢ|ㄣ|ㄤ|ㄥ|ㄦ";
	
	
	private final static String SHIFTED_NUMBERIC_KEY = 		 	"!@#$%^&*()";
	private final static String SHIFTED_NUMBERIC_KEY_REMAP = 	"1234567890";
	private final static String SHIFTED_SYMBOL_KEY = 		 	"<>?=:";
	private final static String SHIFTED_SYMBOL_KEY_REMAP = 		",./-;";
	
	
	private final static String ETEN_KEY = 		 			"@`abcdefghijklmnopqrstuvwxyz12347890-=;',./?";
	private final static String ETEN_KEY_REMAP = 			"@`81v2uzrc9bdxasiqoknwme,j.l7634f0p;/-yh5tg?";
	//private final static String DESIREZ_ETEN_KEY_REMAP = 	"-`81v2uzrc9bdxasiqoknwme,j.l7634f0p;/-yh5tg/";
	//private final static String MILESTONE_ETEN_KEY_REMAP =  "-`81v2uzrc9bdxasiqoknwme,j.l7634f0p;/-yh5tg/";
	//private final static String MILESTONE3_ETEN_KEY_REMAP = "-h81v2uzrc9bdxasiqoknwme,j.l7634f0p;/-yh5tg/";
	private final static String DESIREZ_ETEN_DUALKEY 	= 		"o,ukm9iq5axes"; // remapped from "qweruiop,mlvn";
	private final static String DESIREZ_ETEN_DUALKEY_REMAP = 	"7634f0p;thg/-";
	private final static String MILESTONE_ETEN_DUALKEY 	= 		"o,ukm9iq5aec"; //remapped from "qweruiop,mvh";
	private final static String MILESTONE_ETEN_DUALKEY_REMAP = 	"7634f0p;th/-";
	private final static String MILESTONE2_ETEN_DUALKEY 	= 		"o,ukm9iq5aer"; //remapped from "qweruiop,mvg";
	private final static String MILESTONE2_ETEN_DUALKEY_REMAP = 	"7634f0p;th/-";
	private final static String MILESTONE3_ETEN_DUALKEY 	= 		"5aew"; // ",mvt"
	private final static String MILESTONE3_ETEN_DUALKEY_REMAP = 	"th/-";
	private final static String ETEN_CHAR= 
		"@|`|ㄚ|ㄅ|ㄒ|ㄉ|ㄧ|ㄈ|ㄐ|ㄏ|ㄞ|ㄖ|ㄎ|ㄌ|ㄇ|ㄋ|ㄛ|ㄆ|ㄟ|ㄜ|ㄙ|ㄊ|ㄩ|ㄍ|ㄝ|ㄨ|ㄡ|ㄠ" +
		"|˙|ˊ|ˇ|ˋ|ㄑ|ㄢ|ㄣ|ㄤ|ㄥ|ㄦ|ㄗ|ㄘ|ㄓ|ㄔ|ㄕ|?";
	private final static String DESIREZ_ETEN_CHAR= 
		"@|`|ㄚ|ㄅ|ㄒ|ㄉ|(ㄧ/ˇ)|ㄈ|ㄐ|ㄏ|(ㄞ/ㄢ)|ㄖ|ㄎ|(ㄌ/ㄕ)|(ㄇ/ㄘ)|(ㄋ/ㄦ)|(ㄛ/ㄣ)|(ㄆ/ㄤ)|(ㄟ/˙)" +
		"|(ㄜ/ˋ)|ㄙ|ㄊ|(ㄩ/ㄑ)|(ㄍ/ㄥ)|(ㄝ/ˊ)|ㄨ|ㄡ|ㄠ" +
		"|˙|ˊ|ˇ|ˋ|ㄑ|ㄢ|ㄣ|ㄤ|ㄥ|ㄦ|ㄗ|ㄘ|(ㄓ/ㄗ)|ㄔ|ㄕ|?";
	private final static String MILESTONE_ETEN_CHAR= 
		"ㄦ|`|ㄚ|ㄅ|ㄒ|ㄉ|(ㄧ/ˇ)|ㄈ|ㄐ|(ㄏ/ㄦ)|(ㄞ/ㄢ)|ㄖ|ㄎ|ㄌ|(ㄇ/ㄘ)|ㄋ|(ㄛ/ㄣ)|(ㄆ/ㄤ)|(ㄟ/˙)" +
		"|(ㄜ/ˋ)|ㄙ|ㄊ|(ㄩ/ㄑ)|(ㄍ/ㄥ)|(ㄝ/ˊ)|ㄨ|ㄡ|ㄠ" +
		"|˙|ˊ|ˇ|ˋ|ㄑ|ㄢ|ㄣ|ㄤ|ㄥ|ㄦ|ㄗ|ㄘ|(ㄓ/ㄗ)|ㄔ|ㄕ|ㄥ";
	private final static String MILESTONE2_ETEN_CHAR= 
		"ㄦ|`|ㄚ|ㄅ|ㄒ|ㄉ|(ㄧ/ˇ)|ㄈ|(ㄐ/ㄦ)|ㄏ|(ㄞ/ㄢ)|ㄖ|ㄎ|ㄌ|(ㄇ/ㄘ)|ㄋ|(ㄛ/ㄣ)|(ㄆ/ㄤ)|(ㄟ/˙)" +
		"|(ㄜ/ˋ)|ㄙ|ㄊ|(ㄩ/ㄑ)|(ㄍ/ㄥ)|(ㄝ/ˊ)|ㄨ|ㄡ|ㄠ" +
		"|˙|ˊ|ˇ|ˋ|ㄑ|ㄢ|ㄣ|ㄤ|ㄥ|ㄦ|ㄗ|ㄘ|(ㄓ/ㄗ)|ㄔ|ㄕ|ㄥ";
	private final static String MILESTONE3_ETEN_CHAR= 
		"ㄦ|ㄘ|ㄚ|ㄅ|ㄒ|ㄉ|ㄧ|ㄈ|ㄐ|ㄏ|ㄞ|ㄖ|ㄎ|ㄌ|(ㄇ/ㄘ)|ㄋ|ㄛ|ㄆ|ㄟ|ㄜ|ㄙ|(ㄊ/ㄦ)|ㄩ|ㄍ|ㄝ|ㄨ|ㄡ|ㄠ" +
		"|˙|ˊ|ˇ|ˋ|ㄑ|ㄢ|ㄣ|ㄤ|(ㄍ/ㄥ)|ㄦ|ㄗ|ㄘ|(ㄓ/ㄗ)|ㄔ|ㄕ|ㄥ";
	
	private final static String ETEN26_KEY =            	"qazwsxedcrfvtgbyhnujmikolp,.";
	private final static String ETEN26_KEY_REMAP_INITIAL = 	"y8lhnju2vkzewr1tcsmba9dixq<>";
	private final static String ETEN26_KEY_REMAP_FINAL =   	"y8lhnju7vk6ewr1tcsm3a94ixq<>";
	private final static String ETEN26_DUALKEY_REMAP = 		"o,gf;5p-s0/.p";
	private final static String ETEN26_DUALKEY = 			"yhvewrscpaxqs";
	private final static String ETEN26_CHAR_INITIAL = 	
		"(ㄗ/ㄟ)|ㄚ|ㄠ|(ㄘ/ㄝ)|ㄙ|ㄨ|ㄧ|ㄉ|(ㄕ/ㄒ)|ㄜ|ㄈ|(ㄍ/ㄑ)|(ㄊ/ㄤ)|(ㄐ/ㄓ)|ㄅ|ㄔ|(ㄏ/ㄦ)|(ㄋ/ㄣ)|ㄩ|ㄖ|(ㄇ/ㄢ)|ㄞ|ㄎ|ㄛ|(ㄌ/ㄥ)|(ㄆ/ㄡ)|，|。";
	private final static String ETEN26_CHAR_FINAL = 	
		"(ㄗ/ㄟ)|ㄚ|ㄠ|(ㄘ/ㄝ)|ㄙ|ㄨ|ㄧ|˙|(ㄕ/ㄒ)|ㄜ|ˊ|(ㄍ/ㄑ)|(ㄊ/ㄤ)|(ㄐ/ㄓ)|ㄅ|ㄔ|(ㄏ/ㄦ)|(ㄋ/ㄣ)|ㄩ|ˇ|(ㄇ/ㄢ)|ㄞ|ˋ|ㄛ|(ㄌ/ㄥ)|(ㄆ/ㄡ)|，|。";
	
	
	private final static String HSU_KEY =            		"azwsxedcrfvtgbyhnujmikolpq,.";
	private final static String HSU_KEY_REMAP_INITIAL = 	"hylnju2vbzfwe18csmra9d.xq`<>"; 
	private final static String HSU_KEY_REMAP_FINAL =   	"hylnju6vb3fwe18csm4a9d./q`<>";  
	private final static String HSU_DUALKEY_REMAP =		 	"gt5--,okip0;7";
	private final static String HSU_DUALKEY = 				"vfrx/uhecsadn";
	private final static String HSU_CHAR_INITIAL = 	
		"(ㄘ/ㄟ)|ㄗ|ㄠ|ㄙ|ㄨ|(ㄧ/ㄝ)|ㄉ|(ㄕ/ㄒ)|ㄖ|ㄈ|(ㄔ/ㄑ)|ㄊ|(ㄍ/ㄜ)|ㄅ|ㄚ|(ㄏ/ㄛ)|(ㄋ/ㄣ)|ㄩ|(ㄐ/ㄓ)|(ㄇ/ㄢ)|ㄞ|(ㄎ/ㄤ)|ㄡ|(ㄌ/ㄦ)|ㄆ|q|，|。";
	private final static String HSU_CHAR_FINAL = 	
		"(ㄘ/ㄟ)|ㄗ|ㄠ|˙|ㄨ|(ㄧ/ㄝ)|ˊ|(ㄕ/ㄒ)|ㄖ|ˇ|(ㄔ/ㄑ)|ㄊ|(ㄍ/ㄜ)|ㄅ|ㄚ|(ㄏ/ㄛ)|(ㄋ/ㄣ)|ㄩ|ˋ|(ㄇ/ㄢ)|ㄞ|(ㄎ/ㄤ)|ㄡ|(ㄥ/ㄦ)|ㄆ|q|，|。";
	
	private final static String DESIREZ_KEY =            			"@qazwsxedcrfvtgbyhnujmik?olp,.";
	private final static String DESIREZ_BPMF_KEY_REMAP = 			"1qaz2wsedc5tg6yh4uj8ik9ol0;-,.";
	private final static String DESIREZ_BPMF_DUALKEY_REMAP = 	"xrfvb3n7m,.p/";
	private final static String DESIREZ_BPMF_DUALKEY = 			"sedcg6h4jkl0;";
	private final static String DESIREZ_DUALKEY_REMAP = 		"1234567890;-/='";
	private final static String DESIREZ_DUALKEY = 				"qwertyuiop,vlnm";
	private final static String DESIREZ_BPMF_CHAR = 
		"ㄅ|ㄆ|ㄇ|ㄈ|ㄉ|ㄊ|(ㄋ/ㄌ)|(ㄍ/ㄐ)|(ㄎ/ㄑ)|(ㄏ/ㄒ)|ㄓ|ㄔ|(ㄕ/ㄖ)|(ˊ/ˇ)|ㄗ|(ㄘ/ㄙ)|(ˋ/˙)" +
		"|ㄧ|(ㄨ/ㄩ)|ㄚ|ㄛ|(ㄜ/ㄝ)|ㄞ|ㄟ|(ㄠ/ㄡ)|(ㄢ/ㄣ)|(ㄤ/ㄥ)|ㄦ|,|.";
	
	
	private final static String DESIREZ_DAYI_CHAR =
		"@|(言/石)|人|心|(牛/山)|革|水|(目/一)|日|鹿|(四/工)|土|禾|(王/糸)|手|馬|(門/火)|鳥|魚|(田/艸)|月|雨|"
		+"(米/木)|立|?|(足/口)|(女/竹)|(金/耳)|(力/虫)|舟";
	
	private final static String MILESTONE_DUALKEY_REMAP = 	"1234567890;'=-";
	private final static String MILESTONE_DUALKEY = 		"qwertyuiop,mhv"; 
	private final static String MILESTONE_KEY = "qazwsxedcrfvtgbyhnujmik,ol.p/?";
	private final static String MILESTONE_BPMF_CHAR = 
		"(ㄅ/ㄆ)|ㄇ|ㄈ|(ㄉ/ㄊ)|ㄋ|ㄌ|(ㄍ/ˇ)|ㄎ|ㄏ|(ㄐ/ˋ)|ㄑ|ㄒ|(ㄓ/ㄔ)|ㄕ|ㄖ|(ㄗ/ˊ)|ㄘ|ㄙ|(ㄧ/˙)" +
		"|ㄨ|ㄩ|(ㄚ/ㄛ)|ㄜ|(ㄝ/ㄤ)|(ㄞ/ㄟ)|ㄠ|ㄡ|(ㄢ/ㄣ)|ㄥ|ㄦ";
	private final static String MILESTONE_DAYI_CHAR = 
		"(言/石)|人|心|(牛/山)|革|水|(目/一)|日|鹿|(四/工)|土|禾|(王/糸)|手|馬|(門/火)|鳥|魚|(田/艸)|月|雨|"
		+"(米/木)|立|(力/虫)|(足/口)|女|舟|(金/耳)|竹|?";
	
	private final static String MILESTONE2_DUALKEY_REMAP = 	"1234567890;'=-";
	private final static String MILESTONE2_DUALKEY = 		"qwertyuiop,mgv";
	
	
	private final static String MILESTONE3_KEY = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik,9ol.0p/";
	private final static String MILESTONE3_DUALKEY_REMAP = 	";";
	private final static String MILESTONE3_DUALKEY = 		","; 
	private final static String MILESTONE3_BPMF_DUALKEY_REMAP = ";/";
	private final static String MILESTONE3_BPMF_DUALKEY = 		"l.";
	private final static String MILESTONE3_BPMF_CHAR = 
		"ㄅ|ㄆ|ㄇ|ㄈ|ㄉ|ㄊ|ㄋ|ㄌ|ˇ|ㄍ|ㄎ|ㄏ|ˋ|ㄐ|ㄑ|ㄒ|ㄓ|ㄔ|ㄕ|ㄖ|ˊ|ㄗ|ㄘ|ㄙ|˙|" +
		"ㄧ|ㄨ|ㄩ|ㄚ|ㄛ|ㄜ|ㄝ|ㄞ|ㄟ|(ㄠ/ㄤ)|(ㄡ/ㄥ)|ㄢ|ㄣ|ㄥ";
	private final static String MILESTONE3_DAYI_CHAR = 
		"言|石|人|心|牛|山|革|水|目|一|日|鹿|四|工|土|禾|王|糸|手|馬|門|火|鳥|魚|田|" +
		"艸|月|雨|米|木|立|(力/虫)|足|口|女|舟|金|耳|竹";
	

	private final static String CJ_KEY = "qwertyuiopasdfghjklzxcvbnm";
	private final static String CJ_CHAR = "手|田|水|口|廿|卜|山|戈|人|心|日|尸|木|火|土|竹|十|大|中|重|難|金|女|月|弓|一";
	
	private HashMap<String, HashMap<String,String>> keysDefMap = new HashMap<String, HashMap<String,String>>();
	private HashMap<String, HashMap<String,String>> keysReMap = new HashMap<String, HashMap<String,String>>();
	private HashMap<String, HashMap<String,String>> keysDualMap = new HashMap<String, HashMap<String,String>>();
	
	private String lastCode = "";
	private String lastValidDualCodeList = "";
	

	public String DELIMITER = "";

	private File filename = null;
	private String tablename = "custom";

	private int count = 0;
	private int percentageDone = 0;  // Jeremy '11,7,27
	//private int ncount = 0;
	private boolean finish = false;
	//private boolean relatedfinish = false;
	//Jeremy '11,6,16 keep the soft/physical keyboard flag from getmapping()
	private boolean isPhysicalKeyboardPressed = false;

	private LIMEPreferenceManager mLIMEPref;
	//private Map<String, String> codeDualMap = new HashMap<String, String>();

	private Context ctx;

	// Db loading thread.
	private Thread thread = null;
	private boolean threadAborted = false;
	


	public boolean isLoadingMappingFinished() {
		if(DEBUG) Log.i(TAG, "isLoadingMapingThreadAborted()"+finish+"");
		return this.finish;
	}

	public void setLoadingMappingThreadAborted(boolean value) {
		this.threadAborted = value;
	}

	public boolean isLoadingMappingThreadAborted() {
		if(DEBUG) Log.i(TAG, "isLoadingMapingThreadAborted()"+ threadAborted+"");
		return this.threadAborted;
	}
	
	public boolean isLoadingMappingThreadAlive() {
		boolean result = false;
		if(thread==null) result = false;
		else result = thread.isAlive();
		if(DEBUG) Log.i(TAG, "isLoadingMappingThreadAlive()"+ result+"");
		return result;
	}

	public void setFinish(boolean value) {
		
		this.finish = value;
	}
	
	/*
	 * For DBService to set the filename to be load to database
	 */
	public void setFilename(File filename) {
		this.filename = filename;
	}

	/*
	 * For LIMEService to setup tablename for further word mapping query
	 */
	public void setTablename(String tablename) {
		this.tablename = tablename;
		if (DEBUG) {
			Log.i("setTablename", "tablename:" + tablename + " this.tablename:"
					+ this.tablename);
		}
	}
	
	public String getTablename(){
		return this.tablename;
	}

	/*
	 * Initialize LIME database, Context and LIMEPreferenceManager
	 */
	public LimeDB(Context context) {
		
		super(context, LIME.DATABASE_NAME, null, DATABASE_VERSION);
		this.ctx = context;
		
		mLIMEPref = new LIMEPreferenceManager(ctx.getApplicationContext());
	/*	String dbtarget = mLIMEPref.getParameterString("dbtarget");
		if(dbtarget.equals("device")){
			super(context, LIME.DATABASE_NAME, null, DATABASE_VERSION);
		}else{
			super(context, LIME.DATABASE_NAME, null, DATABASE_VERSION);
		}*/


	}

	/**
	 * Create SQLite Database and create related tables
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Start from 3.0v no need to create internal database
	}

	/*
	 * Update Database Schema
	 * 
	 * @see
	 * android.database.sqlite.SQLit eOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Start from 3.0v no need to create internal database
		
	}
	
	private void checkCode3RIndexAndRecsordsInPhonetic(){
		//mLIMEPref.setParameter("checkLDPhonetic", "");
		if (DEBUG)
			Log.i(TAG, "checkCode3RIndexAndRecsordsInPhonetic(): checked:" 
					+ mLIMEPref.getParameterString("checkLDPhonetic") 
					+ " has valid code3r index and records:" + mLIMEPref.getParameterBoolean("doLDPhonetic", false));
		String doLDPhonetic = mLIMEPref.getParameterString("checkLDPhonetic", "");
		if(!doLDPhonetic.equals("done")){
			SQLiteDatabase db = null;
			String dbtarget = mLIMEPref.getParameterString("dbtarget");
			String dblocation = "";
			if(dbtarget.equals("sdcard")){
				dblocation = LIME.DATABASE_DECOMPRESS_FOLDER_SDCARD + File.separator + LIME.DATABASE_NAME;
			}else{
				dblocation = LIME.DATABASE_DECOMPRESS_FOLDER + File.separator + LIME.DATABASE_NAME;
			}
			db = SQLiteDatabase.openDatabase(dblocation, null, SQLiteDatabase.OPEN_READWRITE);
			Cursor cursor = db.query("sqlite_master", null, "type='index' and name = 'phonetic_idx_code3r'", 
					null, null, null, null);
			boolean hasIndex = false, hasValidRecords = false;
			if(cursor.moveToFirst()){
				Log.d(TAG, "checkLDPhonetic(), code3r index is exist!!");
				hasIndex =true;
			}
			cursor = db.query("phonetic", null, "code3r='ru'", null, null, null, null);
			if(cursor.moveToFirst()){
				Log.d(TAG, "checkLDPhonetic(), code3r has vaid records.!!");
				hasValidRecords = true;
			}
			
			mLIMEPref.setParameter("checkLDPhonetic", "done");
			mLIMEPref.setParameter("doLDPhonetic", hasIndex && hasValidRecords);
			
		}
	}
	
	public void updateDBVersion(){
			
			checkCode3RIndexAndRecsordsInPhonetic();
			
			String kbversion = mLIMEPref.getParameterString("kbversion");

			// Upgrade DB version below 330
			if(kbversion == null || kbversion.equals("") || Integer.parseInt(kbversion) < 330){

				SQLiteDatabase db = null;
				String dbtarget = mLIMEPref.getParameterString("dbtarget");
				String dblocation = "";
				if(dbtarget.equals("sdcard")){
					dblocation = LIME.DATABASE_DECOMPRESS_FOLDER_SDCARD + File.separator + LIME.DATABASE_NAME;
				}else{
					dblocation = LIME.DATABASE_DECOMPRESS_FOLDER + File.separator + LIME.DATABASE_NAME;
				}
				db = SQLiteDatabase.openDatabase(dblocation, null, SQLiteDatabase.OPEN_READWRITE);
				
				int count = db.query("keyboard", null, FIELD_CODE +" = 'limenumsym'", null, null, null, null, null).getCount();
				
				if(count == 0){
					try{
					
						ContentValues 	cv = new ContentValues();
						cv.put("code", "limenumsym");
						cv.put("name", "LIMENUMSYM");
						cv.put("desc", "LIME 預設鍵盤");
						cv.put("type", "phone");
						cv.put("image", "lime_number_symbol_keyboard_priview");
						cv.put("imkb", "lime_number_symbol");
						cv.put("imshiftkb", "lime_number_symbol_shift");
						cv.put("engkb", "lime_english_number");
						cv.put("engshiftkb", "lime_english_shift");
						cv.put("symbolkb", "symbols");
						cv.put("symbolshiftkb", "symbols_shift");
						cv.put("disable", "false");
						db.insert("keyboard" ,null , cv);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				db.close();
				mLIMEPref.setParameter("kbversion","330");
			}
	
			// Upgrade DB version below 332
			if(kbversion == null || kbversion.equals("") || Integer.parseInt(kbversion) < 332){

				SQLiteDatabase db = null;
				String dbtarget = mLIMEPref.getParameterString("dbtarget");
				String dblocation = "";
				if(dbtarget.equals("sdcard")){
					dblocation = LIME.DATABASE_DECOMPRESS_FOLDER_SDCARD + File.separator + LIME.DATABASE_NAME;
				}else{
					dblocation = LIME.DATABASE_DECOMPRESS_FOLDER + File.separator + LIME.DATABASE_NAME;
				}
				db = SQLiteDatabase.openDatabase(dblocation, null, SQLiteDatabase.OPEN_READWRITE);
				
				boolean hasNewTable = false;
				try{
					//int count = db.query("cj5", null, null, null, null, null, null, null).getCount();
					 hasNewTable = true;
				}catch(Exception e){}
				
				if(!hasNewTable){
					try{

						db.execSQL("CREATE TABLE cj5 (" + FIELD_id + " INTEGER primary key autoincrement, " + " "
								+ FIELD_CODE + " text, " + FIELD_CODE3R + " text, " + FIELD_WORD + " text, " + FIELD_RELATED + " text, " + FIELD_SCORE + " integer)");
	
						db.execSQL("CREATE TABLE ecj (" + FIELD_id + " INTEGER primary key autoincrement, " + " "
								+ FIELD_CODE + " text, " + FIELD_CODE3R + " text, " + FIELD_WORD + " text, " + FIELD_RELATED + " text, " + FIELD_SCORE + " integer)");
	
						db.execSQL("CREATE TABLE wb (" + FIELD_id + " INTEGER primary key autoincrement, " + " "
								+ FIELD_CODE + " text, " + FIELD_CODE3R + " text, " + FIELD_WORD + " text, " + FIELD_RELATED + " text, " + FIELD_SCORE + " integer)");

						// To modify the user keyboard selection
						String keybaord_state_string = mLIMEPref.getSelectedKeyboardState();
						
						if(!keybaord_state_string.equals("0;1;2;3;4;5;6;7;8;9;10;11")){
							String ks[] = keybaord_state_string.split(";");
							String update_value = "";
							for(String u: ks){
								try{
									int i = Integer.parseInt(u);
									if(i < 3){
										update_value += i+";";
									}else{
										update_value += (i+2)+";";
									}
								}catch(Exception e){}
							}
							mLIMEPref.setParameter("keyboard_state", update_value);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				db.close();
				mLIMEPref.setParameter("kbversion","332");
			}
			/*//Jeremy '11,8,5 to set code3r = code column on phonetic for old table without code3r built in loadfile.
			// Upgrade DB version below 333
			//mLIMEPref.setParameter("kbversion","332");
			if(kbversion == null || kbversion.equals("") || Integer.parseInt(kbversion) < 333){
				SQLiteDatabase db = null;
				String dbtarget = mLIMEPref.getParameterString("dbtarget");
				String dblocation = "";
				if(dbtarget.equals("sdcard")){
					dblocation = LIME.DATABASE_DECOMPRESS_FOLDER_SDCARD + File.separator + LIME.DATABASE_NAME;
				}else{
					dblocation = LIME.DATABASE_DECOMPRESS_FOLDER + File.separator + LIME.DATABASE_NAME;
				}
				db = SQLiteDatabase.openDatabase(dblocation, null, SQLiteDatabase.OPEN_READWRITE);
				Cursor cursor = db.query("phonetic", null, FIELD_CODE3R + " = 'ru'", null, null, null, null);
				if(!cursor.moveToFirst()) {
					db.execSQL("update phonetic set code3r = code");
				}
				db.close();
				mLIMEPref.setParameter("kbversion","333");
			}*/
			// Upgrade DB version below 333
			/*if(kbversion == null || kbversion.equals("") || Integer.parseInt(kbversion) < 333){
				
				KeyboardObj tobj = getKeyboardObj("limenumsym");
				if(tobj == null){
					try{
						
						ContentValues 	cv = new ContentValues();
						cv.put("code", "wb");
						cv.put("name", "鈭�");
						cv.put("desc", "鈭�頛詨瘜��);
						cv.put("type", "phone");
						cv.put("image", "wb_keyboard_preview");
						cv.put("imkb", "lime_wb");
						cv.put("imshiftkb", "lime_wb_shift");
						cv.put("engkb", "lime_english_number");
						cv.put("engshiftkb", "lime_english_shift");
						cv.put("symbolkb", "symbols");
						cv.put("symbolshiftkb", "symbols_shift");
						cv.put("disable", "false");
						db.insert("keyboard" ,null , cv);
						
						db.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				mLIMEPref.setParameter("kbversion","333");
			}*/
		
	}
	
	
	public SQLiteDatabase getSqliteDb(boolean readonly){
		
		// Execute database schema update process
		updateDBVersion();
		
		try{
			SQLiteDatabase db = null;
			String dbtarget = mLIMEPref.getParameterString("dbtarget");
			//Log.i("ART", "Load Database Target : " + dbtarget);
			if(dbtarget.equals("sdcard")){
				String sdcarddb = LIME.DATABASE_DECOMPRESS_FOLDER_SDCARD + File.separator + LIME.DATABASE_NAME;

				if(readonly){
					db = SQLiteDatabase.openDatabase(sdcarddb, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
				}else{
					db = SQLiteDatabase.openDatabase(sdcarddb, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
				}
			}else{
				String devicedb = LIME.DATABASE_DECOMPRESS_FOLDER + File.separator + LIME.DATABASE_NAME;
				
				if(readonly){
					db = SQLiteDatabase.openDatabase(devicedb, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
					//db = this.getReadableDatabase();
				}else{
					db = SQLiteDatabase.openDatabase(devicedb, null, SQLiteDatabase.OPEN_READWRITE |SQLiteDatabase.NO_LOCALIZED_COLLATORS);
					//db = this.getWritableDatabase();
				}
			}
			/*if(DEBUG) 
				Log.i("LIMEDB:getSqliteDb()", "database version:" + db.getVersion());
			// Insert the new lime_number_symbol keyboard record
			if(!readonly && db.getVersion() < 68){
				ContentValues 	cv = new ContentValues();
				cv.put("code", "limenumsym");
				cv.put("name", "LIMENUMSYM");
				cv.put("desc", "LIME+�詨�蝚西��萇");
				cv.put("type", "phone");
				cv.put("image", "lime_number_symbol_keyboard_priview");
				cv.put("imkb", "lime_number_symbol");
				cv.put("imshiftkb", "lime_number_symbol_shift");
				cv.put("engkb", "lime_english_number");
				cv.put("engshiftkb", "lime_english_shift");
				cv.put("symbolkb", "symbols");
				cv.put("symbolshiftkb", "symbols_shift");
				cv.put("disable", "false");

				db.insert("keybaord", null, cv);
				
				db.setVersion(68);
			}*/
			
			return db;
		}catch(Exception e){e.printStackTrace();}
			 
		return null;

	}

	/**
	 * Base on given table name to remove records
	 */
	public synchronized void deleteAll(String table) {
		if(thread != null){
			threadAborted = true;
			while(thread.isAlive()){
				Log.d(TAG, "deleteAll():waiting for thread stopped...");
				SystemClock.sleep(1000);
			};
		}

		SQLiteDatabase db = this.getSqliteDb(false);
		//db.execSQL("DELETE FROM " + table);
		if(countMapping(table)>0)
			db.delete(table, null, null);
		db.close();
		
		finish = false;
		resetImInfo(table);
		//mLIMEPref.setParameter("im_loading", false);
		//mLIMEPref.setParameter("im_loading_table", "");
		
		
	}

	/**
	 * Empty Related table records
	 */
	public synchronized void  deleteUserDictAll() {
		mLIMEPref.setTotalUserdictRecords("0");
		// -------------------------------------------------------------------------
		SQLiteDatabase db = this.getSqliteDb(false);
		db.delete("related", FIELD_DIC_score + " > 0", null);
		db.close();
	}

	/**
	 * Count total amount of specific table
	 * 
	 * @return
	 */
	public int countMapping(String table) {

		try {
			SQLiteDatabase db = this.getSqliteDb(true);
			int total = db.rawQuery("SELECT * FROM " + table, null).getCount();
			db.close();
			//Log.i("countMapping", "Table," + table + ": " + total);
			return total;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getCount(){
		return count;
	}
	
	public int getPercentageDone(){
		return percentageDone;
	}
	
	/**
	 * Count total amount loaded records amount
	 * 
	 * @return
	 */
	public int countUserdic() {

		int total = 0;
		try {
			SQLiteDatabase db = this.getSqliteDb(true);
			total += db.rawQuery(
					"SELECT * FROM related where " + FIELD_DIC_score + " > 0",
					null).getCount();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	/**
	 * Insert mapping item into database
	 * 
	 * @param source
	 */
	public synchronized void insertList(ArrayList<String> source) {

		this.identifyDelimiter(source);

		SQLiteDatabase db = this.getSqliteDb(false);
		for (String unit : source) {

			try {
				String code = unit.substring(0, unit.indexOf(this.DELIMITER));
				String word = unit.substring(unit.indexOf(this.DELIMITER) + 1);

				if (code == null || code.trim().equals("")) {
					continue;
				} else {
					code = code.toLowerCase();
				}
				if (word == null || word.trim().equals("")) {
					continue;
				}
				if (code.equalsIgnoreCase("@VERSION@")) {
					mLIMEPref.setTableVersion("lime", word.trim());
					continue;
				}

				ContentValues cv = new ContentValues();
				cv.put(FIELD_CODE, code);
				cv.put(FIELD_WORD, word);
				cv.put(FIELD_SCORE, 0);

				db.insert("mapping", null, cv);
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
	}

	public synchronized void addOrUpdateUserdictRecord(String pword, String cword){
		// Jeremy '11,6,12
		// Return if not learing related words and cword is not null (recording word frequency in IM relatedlist field)
		if(!mLIMEPref.getLearnRelatedWord() && cword!=null) return;

		int dictotal = Integer.parseInt(mLIMEPref.getTotalUserdictRecords());
		
		if(DEBUG) 
			Log.i(TAG, "addOrUpdateUserdictRecord(): pword:"+pword+" cword:"+cword + "dictotoal:"+dictotal);
		
		Mapping munit = this.isUserDictExist(pword , cword);
		
		SQLiteDatabase db = this.getSqliteDb(false);
		
		ContentValues cv = new ContentValues();
		try {
			if (munit == null) {
				cv.put(FIELD_DIC_pword, pword);
				cv.put(FIELD_DIC_cword, cword);
				cv.put(FIELD_DIC_score, 1);
				db.insert("related", null, cv);
				dictotal++;
				mLIMEPref.setTotalUserdictRecords(String.valueOf(dictotal));
				if(DEBUG) 
					Log.i(TAG, "addOrUpdateUserdictRecord(): new record, dictotal:" + dictotal);
			}else{//the item exist in preload related database.
				int score = munit.getScore()+1;
			  	cv.put(FIELD_SCORE, score);
			  	db.update("related", cv, FIELD_id + " = " + munit.getId(), null);
			  	if(DEBUG) 
			  		Log.i(TAG, "addOrUpdateUserdictRecord():update score on existing record; score:"+score);
			}
		} catch (Exception e) {
			db.close();
			e.printStackTrace();
		}
		db.close();

	}
	/**
	 * learn user dict based on the input maaping list.
	 * 
	 * @param srclist
	 *//*
	 //Jeremy '11,8,1 moved to postfinishinput in searchservice.
	//Jeremy '11,6,12 rename from addDictionary for consistency 
	public void addUserDict(List<Mapping> srclist) {
		//Jeremy '11,6,12 move the db updating to addOrUpdateUserdictRecord
		if(DEBUG){
			Log.i(TAG, "addUserDict():Entering addDictionary, srclist.size=" + srclist.size());
		}
		
		
		if (srclist != null && srclist.size() > 0) {

			for (int i = 0; i < srclist.size(); i++) {

				Mapping unit = srclist.get(i);
				if(unit == null){continue;}
				    
				if(i+1 <srclist.size()){
					Mapping unit2 = srclist.get((i + 1));
					if(unit2 == null){continue;}				
					if (unit != null 
			    		&& unit.getWord() != null && !unit.getWord().equals("")
			    		&& unit2 != null
			    		&& unit2.getWord() != null && !unit2.getWord().equals("")) {
						addOrUpdateUserdictRecord(unit.getWord(),unit2.getWord());
					}
				}
			}
		}
	}*/
	/**
	 * Add new mapping into current table
	 * @param code, word
	 */
	//Jeremy '11, 7, 31 add new phrase mapping into current table (for LD phrase learning). 
	public synchronized void addOrUpdateMappingRecord(String code, String word) {
		if(DEBUG)
				Log.i(TAG, "addOrUpdateMappingRecord(), code = " + code + ". word=" + word  );
		SQLiteDatabase db = this.getSqliteDb(false);
		
		Mapping munit = isMappingExist(code, word);
		ContentValues cv = new ContentValues();
		try {
			if(munit==null){
				if (code.length()>0 && word.length()>0) {
					cv.put(FIELD_CODE, code);
					if(tablename.equals("phonetic")) cv.put(FIELD_CODE3R, code.replaceAll("[3467]", ""));
					cv.put(FIELD_WORD, word);
					cv.put(FIELD_SCORE, 1);
					db.insert(tablename, null, cv);
					if(DEBUG)
						Log.i(TAG, "addOrUpdateMappingRecord(): mapping is not existed, new cored inserted");
				}
				// build related list
				//updateRelatedList(code);
				
				
			}else{//the item exist in preload related database.
				int score = munit.getScore()+1;
			  	cv.put(FIELD_SCORE, score);
			  	db.update(tablename, cv, FIELD_id + " = " + munit.getId(), null);
			  	if(DEBUG) 
			  		Log.i(TAG, "addOrUpdateMappingRecord(): mapping is existed, update score on existing record; score:"+score);
			}
		} catch (Exception e) {
			db.close();
			e.printStackTrace();
		}
		db.close();

	
	}
	
	/**
	 * Add score to the mapping item
	 * 
	 * @param srcunit
	 */
	public synchronized void addScore(Mapping srcunit) {
		
			//Jeremy '11,7,31  even selected from realted list, udpate the corresponding score in im table.
			// Jeremy '11,6,12 Id=null denotes selection from related list in im table
//			if(srcunit !=null && srcunit.getId()== null && 
//				srcunit.getWord() != null  && !srcunit.getWord().trim().equals("")){
//				String code = srcunit.getCode().trim().toLowerCase();
//				if(DEBUG) Log.i("LIMEDb.addScore()","related selectd, code:" + code);
//				// sotre the phrase frequency of relatedlist in reated table with cword =null
//				addOrUpdateUserdictRecord(srcunit.getWord(),null); 
//				//updateRelatedList(code); move to search service Jeremy '11,7,29
//			
//			}else 
		SQLiteDatabase db = this.getSqliteDb(false);
		try {
			if (srcunit != null && //srcunit.getId() != null &&
					srcunit.getWord() != null  &&
					!srcunit.getWord().trim().equals("") ) {
					if(DEBUG) Log.i(TAG, "addScore(): addScore on code:"+srcunit.getCode());

				if(srcunit.isDictionary()){
					ContentValues cv = new ContentValues();
					cv.put(FIELD_SCORE, srcunit.getScore() + 1);
					
					
					db.update("related", cv, FIELD_id + " = " + srcunit.getId(), null);
					db.close();
				}else{
					ContentValues cv = new ContentValues();
					cv.put(FIELD_SCORE, srcunit.getScore() + 1);
	
					// Jeremy 11',7,29  update according to word instead of ID, may have multiple records mathing word but withd diff code/id 
					db.update(tablename, cv, FIELD_WORD + " = '" + srcunit.getWord() + "'", null);
					db.close();
				}
			}
		} catch (Exception e) {
			db.close();
			e.printStackTrace();
		}
	}
	
	
	// Add by jeremy '10, 4, 1. For reverse lookup
	/**
	 * Reverse lookup on keyword.
	 * 
	 * @param keyword
	 * @return
	 */
	public String getRMapping(String keyword) {

		//Log.i("ART", "run get rmapping:"+ keyword);
		String table = mLIMEPref.getRerverseLookupTable(tablename);

		if (table.equals("none")) {
			return null;
		}

		String result = new String("");
		try {

			if (keyword != null && !keyword.trim().equals("")) {
				Cursor cursor = null;
				SQLiteDatabase db = this.getSqliteDb(true);
				cursor = db.query(table, null, FIELD_WORD + " = '" + keyword +"'", null, null,
						null, null, null);
				if (DEBUG) {
					Log.i(TAG,"getRmapping():tablename:" + table + "  keyworad:"
							+ keyword + "  cursor.getCount:"
							+ cursor.getCount());
				}

				if (cursor.moveToFirst()) {
					int codeColumn = cursor.getColumnIndex(FIELD_CODE);
					int wordColumn = cursor.getColumnIndex(FIELD_WORD);
					result = cursor.getString(wordColumn) + "="
							+ keyToKeyname(cursor.getString(codeColumn), table, false);
					if (DEBUG) {
						Log.i(TAG, "getRmapping():Code:"
								+ cursor.getString(codeColumn));
					}

					while (cursor.moveToNext()) {
						result = result
								+ "; "
								+ keyToKeyname(cursor.getString(codeColumn),
										table, false);
						if (DEBUG) {
							Log.i(TAG,"getRmapping():Code:"
									+ cursor.getString(codeColumn));
						}
					}
				}

				if (cursor != null) {
					cursor.deactivate();
					cursor.close();
				}
				db.close();
			}
		} catch (Exception e) {
		}

		if (DEBUG) {
			Log.i("getRmapping", "Result:" + result);
		}

		return result;
	}
	/**
	 * Jeremy '11,7,26  Updated related list 
	 * 
	 * @param code
	 */
	public synchronized List<Mapping> updateRelatedList(String code){
		// Jeremy '11,7,31  rebuild relatedlist by query from table directly
		// Update relatedlist in IM table now.
		SQLiteDatabase db = this.getSqliteDb(false);
		char[] charray = code.toCharArray();
		charray[code.length()-1]++;
		String nextcode = new String(charray);
		
		String selectString = "SELECT * FROM '" + tablename +"" +
				"' WHERE " + FIELD_CODE + " > '" + code + "' AND " + FIELD_CODE + " < '" + nextcode + "'"+
				" ORDER BY " + FIELD_SCORE + " DESC LIMIT 50";
		Cursor cursor = db.rawQuery(selectString ,null);
		
		if(DEBUG) 
			Log.i(TAG, "updateRelatedList(): raw query string: "+ selectString);
		
		LinkedList <Mapping> scorelist = null;
		
		if(cursor.moveToFirst()){
			HashSet <String> duplicateCheck = new HashSet<String>();
			scorelist = new LinkedList<Mapping>();
			int idColumn = cursor.getColumnIndex(FIELD_id);
			int codeColumn = cursor.getColumnIndex(FIELD_CODE);
			int wordColumn = cursor.getColumnIndex(FIELD_WORD);
			int scoreColumn = cursor.getColumnIndex(FIELD_SCORE);
			do {		
				
				Mapping munit = new Mapping();
				munit.setCode(cursor.getString(codeColumn));
				munit.setWord(cursor.getString(wordColumn));
				munit.setId(cursor.getString(idColumn));
				munit.setScore(cursor.getInt(scoreColumn));
				munit.setDictionary(false);
				
				if(munit.getWord() == null || munit.getWord().trim().equals(""))
					continue;
				
				if(duplicateCheck.add(munit.getWord())){
					scorelist.add(munit);
				}
			} while (cursor.moveToNext());
		
		// Rebuild the related list string and update the record.
			String newRelatedlist = "";
			
			for(Mapping munit : scorelist){
				if(newRelatedlist.equals("")) newRelatedlist = munit.getWord();
				else newRelatedlist = newRelatedlist + "|" + munit.getWord();
					
			}
			ContentValues cv = new ContentValues();
			cv.put(FIELD_RELATED, newRelatedlist);
			if(isMappingExist(code,null)!=null){
				db.update(tablename, cv, FIELD_CODE + " = '" + code + "'", null);
			}else{
				cv.put(FIELD_CODE, code);
				db.insert(tablename, null, cv);
			}
			
			if(DEBUG) 
				Log.i(TAG, "updateRelatedList(): the new relatedlist:" + newRelatedlist);	
		}
		
		db.close();
		return scorelist;
	}
//Rewrite by Jeremy 11,6,4.  Supporting array and dayi now.
	public String keyToKeyname(String code, String table, Boolean composingText) {
		//QP and LD may >4.
		//if(composingText && tablename.equals("phonetic") && code.length()>4 ) // phonetic never has code length >4 
		//	return code;
		
		String keyboardtype = mLIMEPref.getPhysicalKeyboardType();
		String phonetickeyboardtype = mLIMEPref.getPhoneticKeyboardType();
		String keytable = table;
		
		if(DEBUG) 
			Log.i(TAG, "keyToKeyname():code:" + code + 
				" table:"+table + " tablename:" + tablename +
				" isPhysicalKeybaordPressed:" + isPhysicalKeyboardPressed +
				" keyboardtype: " + keyboardtype +
				" composingText:" + composingText		);
		
		
		
		if(isPhysicalKeyboardPressed){
			if(composingText && table.equals("phonetic")) {// doing composing popup
				keytable = table + keyboardtype + phonetickeyboardtype;
			} else if(composingText)
				keytable = table + keyboardtype;
		}else if(composingText && tablename.equals("phonetic") ){
				keytable = table + phonetickeyboardtype;
		}
		if(DEBUG)
			Log.i(TAG, "keyToKeyname():keytable:" + keytable); 
		
		if(composingText){// building composing text and has dual mapped codes		
			String dualCodeList = lastValidDualCodeList;
			if(!code.equals(lastCode)){
				// unsynchronized cache. do the preprocessing again.
				preProcessingForExtraQueryConditions(preProcessingRemappingCode(code));
			}
			
			if(dualCodeList!=null ){
				if(DEBUG) 
					Log.i(TAG,"keyToKeyname():dualCodelist:" + dualCodeList + 
						" table:"+table + " tablename:" + tablename);
				code = dualCodeList;
				if(tablename.equals("phonetic")){
					keytable = "phonetic";
					keyboardtype = "normal_keyboard";
					phonetickeyboardtype = "standard";
				}if(tablename.equals("dayi")){
					keytable = "dayi";
					keyboardtype = "normal_keyboard";
				}
				
			}
		}
		
		if(DEBUG)
			Log.i(TAG, "keyToKeyname():code:" + code + 
				" table:"+table + " tablename:" + tablename + " keytable:"+keytable);
		
		if(keysDefMap.get(keytable)==null
				|| keysDefMap.get(keytable).size()==0){
			
			String keyString="", keynameString="", finalKeynameString = null;
			//Jeremy 11,6,4 Load keys and keynames from im table.
			keyString = getImInfo(table,"imkeys");
			keynameString = getImInfo(table,"imkeynames");
			
			//Log.i("ART",keyString);
			//Log.i("ART",keynameString);
			
			if(table.equals("phonetic")|| table.equals("dayi") ||
					keyString.equals("")||keynameString.equals("")){
				if(table.equals("cj")||table.equals("scj")||table.equals("cj5")||table.equals("ecj")){
					keyString = CJ_KEY;
					keynameString = CJ_CHAR;
				}else if(table.equals("phonetic") ) { 
					if(composingText){  // building composing text popup
						if(phonetickeyboardtype.equals("eten")){
							keyString = ETEN_KEY;
							if(keyboardtype.equals("milestone") && isPhysicalKeyboardPressed)
								keynameString = MILESTONE_ETEN_CHAR;
							else if(keyboardtype.equals("milestone2") && isPhysicalKeyboardPressed)
								keynameString = MILESTONE2_ETEN_CHAR;
							else if(keyboardtype.equals("milestone3") && isPhysicalKeyboardPressed)
								keynameString = MILESTONE3_ETEN_CHAR;
							else if(keyboardtype.equals("desireZ") && isPhysicalKeyboardPressed)
								keynameString = DESIREZ_ETEN_CHAR;
							else
								keynameString = ETEN_CHAR;
						}else if(phonetickeyboardtype.equals("eten26")){
							keyString = ETEN26_KEY;
							keynameString = ETEN26_CHAR_INITIAL;
							finalKeynameString = ETEN26_CHAR_FINAL;
						}else if(phonetickeyboardtype.equals("hsu")){
							keyString = HSU_KEY;
							keynameString = HSU_CHAR_INITIAL;
							finalKeynameString = HSU_CHAR_FINAL;
						}else if((keyboardtype.equals("milestone")||keyboardtype.equals("milestone2")) 
								&& isPhysicalKeyboardPressed){
							keyString = MILESTONE_KEY;
							keynameString = MILESTONE_BPMF_CHAR;
						}else if(keyboardtype.equals("milestone3") && isPhysicalKeyboardPressed){
								keyString = MILESTONE3_KEY;
								keynameString = MILESTONE3_BPMF_CHAR;
						}else if(keyboardtype.equals("desireZ") && isPhysicalKeyboardPressed){
							keyString = DESIREZ_KEY;
							keynameString = DESIREZ_BPMF_CHAR;
						}else{
							keyString = BPMF_KEY;
							keynameString = BPMF_CHAR;
						}
							
					}else{ 
						keyString = BPMF_KEY;
						keynameString = BPMF_CHAR;
					}
				}else if(table.equals("array")) {
					keyString = ARRAY_KEY;
					keynameString = ARRAY_CHAR;
				}else if(table.equals("dayi")) {
					if(isPhysicalKeyboardPressed&&composingText){ // only do this on composing mapping popup
						if(keyboardtype.equals("milestone")||keyboardtype.equals("milestone2")){
							keyString = MILESTONE_KEY;
							keynameString = MILESTONE_DAYI_CHAR;
						}else if(keyboardtype.equals("milestone3")){
								keyString = MILESTONE3_KEY;
								keynameString = MILESTONE3_DAYI_CHAR;
						}else if(keyboardtype.equals("desireZ")){
							keyString = DESIREZ_KEY;
							keynameString = DESIREZ_DAYI_CHAR;
						}else{
							keyString = DAYI_KEY;
							keynameString = DAYI_CHAR;
						}
					}else{
						keyString = DAYI_KEY;
						keynameString = DAYI_CHAR;
					}
				}
			}
			if(DEBUG) 
				Log.i("limedb:keyToKeyname()", "keyboardtype:" +keyboardtype + " phonetickeyboardtype:" + phonetickeyboardtype + 
					" composing?:" + composingText +
					" keyString:"+keyString + " keynameString:" +keynameString + " finalkeynameString:" + finalKeynameString);
			
			HashMap<String,String> keyMap = new HashMap<String,String>();
			HashMap<String,String> finalKeyMap = null;
			if(finalKeynameString != null)
				finalKeyMap = new HashMap<String,String>();
			
			String charlist[] = keynameString.split("\\|");
			String finalCharlist[] = null;
			
			if(finalKeyMap != null)
				finalCharlist = finalKeynameString.split("\\|");
				
			// Ignore the exception of key name mapping.
			try{
				for (int i = 0; i < keyString.length(); i++) {
					keyMap.put(keyString.substring(i, i + 1), charlist[i]);
					if(finalKeyMap != null && finalCharlist!=null)
						finalKeyMap.put(keyString.substring(i, i + 1), finalCharlist[i]);
				}
			}catch(Exception e){}
			
			keyMap.put("|", "|"); //put the seperator for multi-code display
			keysDefMap.put(keytable, keyMap);
			if(finalKeyMap != null)
				keysDefMap.put("final_"+keytable, finalKeyMap);
			
		}
		
		
		
		// Starting doing key to keyname conversion ------------------------------------
		if(keysDefMap.get(keytable)==null 
				|| keysDefMap.get(keytable).size()==0){
			if(DEBUG) Log.i("limedb:keyToKeyname()","nokeysDefMap found!!");
			return code;
		
		}else{
			String result = "";
			HashMap <String,String> keyMap = keysDefMap.get(keytable);
			HashMap <String,String> finalKeyMap = keysDefMap.get("final_"+keytable);
			// do the real conversion
			
			if(finalKeyMap == null){
				for (int i = 0; i < code.length(); i++) {
					String c = keyMap.get(code.substring(i, i + 1));
					if(c!=null) result = result + c;
				}
			}else{
				
				if(code.length()==1){
						
					String c = "";
					if(phonetickeyboardtype.equals("eten26") &&
							(code.equals("q") || code.equals("w") 
							|| code.equals("d")|| code.equals("f") 
							|| code.equals("j") || code.equals("k"))){ 
						// Dual mapped INITIALS have words mapped for ��and �� for ETEN26
						c = keyMap.get(code);
					}else if (phonetickeyboardtype.equals("hsu") &&
						(code.equals("a") || code.equals("e") ||
						code.equals("d") || code.equals("f") ||code.equals("j"))){
						// Dual mapped INITIALS have words mapped for a and e   
						// and no mapped word on finals d,f,j  for HSU
						c = keyMap.get(code);
					}else{
						c = finalKeyMap.get(code);
					}
					if(c!=null) result = c.trim();
				}else{
					for (int i = 0; i < code.length(); i++) {
						String c = "";
						if(i>0){
								c = finalKeyMap.get(code.substring(i, i + 1));
						}else{
							c = keyMap.get(code.substring(i, i + 1));
						}
						if(c!=null) result = result + c.trim();
					}
				
				}
			}
			if(DEBUG) 
				Log.i("limedb:keyToKeyname()","returning:" + result);
			
			if(result.equals("")){
				return code;
			}else{
				return result;
			}
		}
		
		
	}

	/*
	 * Retrieve matched records
	 
	public Pair<List<Mapping>,List<Mapping>> getMappingSimiliar(String code) {

		Pair<List<Mapping>,List<Mapping>> result = null;
		
		if(mLIMEPref.getSimilarCodeCandidates() > 0){
			//HashSet<String> wordlist = new HashSet<String>();
	
			if (code != null && !code.trim().equals("")) {
				int ssize = mLIMEPref.getSimilarCodeCandidates();
				boolean sort = mLIMEPref.getSortSuggestions();
				
				code = code.toLowerCase();
				if(code != null){
					// Process the escape characters of query
					code = code.replaceAll("'", "''");
				}
				SQLiteDatabase db = this.getSqliteDb(true);
				try {
					Cursor cursor = null;
					// When Code3r mode is disable
					if(sort){
						cursor = db.query(tablename, null, FIELD_CODE + " LIKE '"
								+ code + "%' ", null, null, null, FIELD_SCORE +" DESC LIMIT " + ssize, null);
					}else{
						cursor = db.query(tablename, null, FIELD_CODE + " LIKE '"
								+ code + "%' LIMIT " + ssize, null, null, null, null, null);
					}
	
					result = buildQueryResult(code, cursor);
	
					if (cursor != null) {
						cursor.deactivate();
						cursor.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				db.close();
			}
		}
		return result;
	}*/
	
	/*
	 * Retrieve matched records
	 */
	public Pair<List<Mapping>,List<Mapping>> getMapping(String code, boolean softKeyboard, boolean getAllRecords) {
		boolean sort = true;
		if(softKeyboard) sort = mLIMEPref.getSortSuggestions();
		else sort = mLIMEPref.getPhysicalKeyboardSortSuggestions();
		isPhysicalKeyboardPressed = !softKeyboard;
		if(DEBUG) 
			Log.i(TAG, "getmapping(): code:"+ code + "| doLDPhonetic=" +mLIMEPref.getParameterBoolean("doLDPhonetic"));

		// Add by Jeremy '10, 3, 27. Extension on multi table query.
		lastCode = code;
		lastValidDualCodeList = null; // reset the lastValidDualCodeList
		Pair<List<Mapping>,List<Mapping>> result = null;

		//Two-steps qeury code pre-processing. Jeremy '11,6,15
		// Step.1 Code re-mapping.  
		code = preProcessingRemappingCode(code);
		// Step.2 Build extra query conditions. (e.g. 3row remap)
		String extraConditions = preProcessingForExtraQueryConditions(code);
		//Jeremy '11,6,11 seperated suggestions sorting option for physical keyboard
	
		try{
			if (!code.equals("")) {
				
				SQLiteDatabase db = this.getSqliteDb(true);
				try {
					Cursor cursor = null;
					// Jeremy '11,8,2 Query code3r instead of code for code contains no tone symbols
					String selectClause;
					if(tablename.equals("phonetic")
							&& mLIMEPref.getParameterBoolean("doLDPhonetic", false) 
							&& !code.matches(".+[3467 ].*")){
							//&& !(code.contains("3")||code.contains("4")
							//||code.contains("6")||code.contains("7")|| code.endsWith(" "))){
						selectClause = FIELD_CODE3R + " = '" + code + "' " + extraConditions;
					}else{
						selectClause = FIELD_CODE + " = '" + code.trim() + "' " + extraConditions;
					}
					
					if(DEBUG) 
						Log.i(TAG, "getMapping(): selectClause=" + selectClause  );
					// Jeremy '11,8,5 limit initial query to limited records
					String limitClause = null;
					if(!getAllRecords)
						limitClause = INITIAL_RESULT_LIMIT;
					
					// Jeremy '11,6,15 Using query with preprocessed code and extra query conditions.
					if(sort){
						cursor = db.query(tablename, null, selectClause
								, null, null, null, FIELD_SCORE +" DESC", limitClause);
					}else{
						cursor = db.query(tablename, null, selectClause
								, null, null, null, null, limitClause);
					}
					
	
					result = buildQueryResult(code, cursor, getAllRecords);
	
					if (cursor != null) {
						cursor.deactivate();
						cursor.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				db.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	private String preProcessingRemappingCode(String code){
		if(DEBUG) 
			Log.i(TAG, "preProcessingRemappingCode(): code="+code);
		if(code != null){
			String keyboardtype = mLIMEPref.getPhysicalKeyboardType();
			String phonetickeyboardtype = mLIMEPref.getPhoneticKeyboardType();
			String keyString = "", keyRemapString ="", finalKeyRemapString = null;
			String newcode = code;
			String remaptable = tablename;
			
			// Build cached hashmap remapping table name 
			if(isPhysicalKeyboardPressed ){
				if(tablename.equals("phonetic"))
					remaptable = tablename + keyboardtype + phonetickeyboardtype;
				else
					remaptable = tablename + keyboardtype;
			}else if(tablename.equals("phonetic")) 
					remaptable = tablename + phonetickeyboardtype;
		
			
			// Build cached hashmap remapping table if it's not exist
			if(keysReMap.get(remaptable)==null
					|| keysReMap.get(remaptable).size()==0){
			
				if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("eten26")){
					keyString = ETEN26_KEY;
					keyRemapString = ETEN26_KEY_REMAP_INITIAL;
					finalKeyRemapString = ETEN26_KEY_REMAP_FINAL;
				}else if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("hsu")){
					keyString = HSU_KEY;
					keyRemapString = HSU_KEY_REMAP_INITIAL;
					finalKeyRemapString = HSU_KEY_REMAP_FINAL;
				}else if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("eten")){
					keyString = ETEN_KEY;
					/*
					if((keyboardtype.equals("milestone")||keyboardtype.equals("milestone2")) 
							&& isPhysicalKeyboardPressed)
						keyRemapString = MILESTONE_ETEN_KEY_REMAP;
					else if(keyboardtype.equals("milestone3") && isPhysicalKeyboardPressed)
						keyRemapString = MILESTONE3_ETEN_KEY_REMAP;
					else if(keyboardtype.equals("desireZ") && isPhysicalKeyboardPressed)
						keyRemapString = DESIREZ_ETEN_KEY_REMAP;
					else
					*/
					keyRemapString = ETEN_KEY_REMAP;
				}else if(isPhysicalKeyboardPressed 
					&& tablename.equals("phonetic") && keyboardtype.equals("desireZ")){
					//Desire Z phonetic keybaord
					keyString = DESIREZ_KEY;
					keyRemapString = DESIREZ_BPMF_KEY_REMAP;
				}else if(!isPhysicalKeyboardPressed){
					if(tablename.equals("dayi") || tablename.equals("ez")
						  ||tablename.equals("phonetic")&&phonetickeyboardtype.equals("standard") ){
						keyString = SHIFTED_NUMBERIC_KEY + SHIFTED_SYMBOL_KEY;
						keyRemapString = SHIFTED_NUMBERIC_KEY_REMAP + SHIFTED_SYMBOL_KEY_REMAP;
					}else if(tablename.equals("array")){
						keyString =  SHIFTED_SYMBOL_KEY;
						keyRemapString =  SHIFTED_SYMBOL_KEY_REMAP;
					}
					
				}
				
				
				if(!keyString.equals("")){
					HashMap<String,String> reMap = new HashMap<String,String>();
					HashMap<String,String> finalReMap = null;
					if( finalKeyRemapString!=null)
					finalReMap = new HashMap<String,String>();
				
					for (int i = 0; i < keyString.length(); i++) {
						reMap.put(keyString.substring(i, i + 1), keyRemapString.substring(i, i + 1));
						if(finalReMap!=null)
							finalReMap.put(keyString.substring(i, i + 1), finalKeyRemapString.substring(i, i + 1));
					}
					keysReMap.put(remaptable, reMap);
					if(finalReMap!=null)
						keysReMap.put("final_"+remaptable, finalReMap);
				}
			}
			
			// Do the remapping here using the cached remapping table
					
			if(keysReMap.get(remaptable)==null 
						|| keysReMap.get(remaptable).size()==0){
				return code;
			}
			else{
				HashMap<String,String> reMap = keysReMap.get(remaptable);
				HashMap<String,String> finalReMap =  keysReMap.get("final_"+remaptable);
				
				newcode = "";
				String c = null;
				
				if(finalReMap == null){
					for (int i = 0; i < code.length(); i++) {
						String s = code.substring(i, i + 1);
						c = reMap.get(s);
						if(c!=null) newcode = newcode + c;
						else newcode = newcode + s;
					}

				}else {

					if(code.length() == 1){
						if(phonetickeyboardtype.equals("eten26") &&
								(code.equals("q") || code.equals("w") 
										|| code.equals("d")|| code.equals("f") 
										|| code.equals("j") || code.equals("k"))){ 
							// Dual mapped INITIALS have words mapped for ��and �� for ETEN26
							c = reMap.get(code);
						}else if (phonetickeyboardtype.equals("hsu") &&
								(code.equals("a") || code.equals("e") ||
										code.equals("d") || code.equals("f") ||code.equals("j"))){
							// Dual mapped INITIALS have words mapped for a and e   
							// and no mapped word on finals d,f,j  for HSU
							c = reMap.get(code);
						}else{
							/*if(tablename.equals("array")){
								if(code.equals("<")){
									c = finalReMap.get(',');
								}else if(code.equals(">")){
									c = finalReMap.get('.');
								}else if(code.equals("?")){
									c = finalReMap.get('/');
								}else if(code.equals(":")){
									c = finalReMap.get(';');
								}
							}else{

							}*/
							c = finalReMap.get(code);
						}
						if(c!=null) newcode = c;
						else newcode = code;

					}else {			
						for (int i = 0; i < code.length(); i++) {
							String s = code.substring(i, i + 1);
							if(i>0)
								c = finalReMap.get(s);
							else
								c = reMap.get(code.substring(i, i + 1));

							if(c!=null) newcode = newcode + c;
							else newcode = newcode + s;
						}
					}
				}
			}
					
			//Process the escape characters of query
			newcode = newcode.replaceAll("'", "''");
			if(DEBUG) 
				Log.i(TAG, "preProcessingRemappingCode():newcode="+newcode);
			return newcode;
		}else
			return "";
	}
	
	private String preProcessingForExtraQueryConditions(String code){
		if(DEBUG) 
			Log.i(TAG, "preProcessingForExtraQueryConditions(): code="+code);
		
		if(code != null ){
			String keyboardtype = mLIMEPref.getPhysicalKeyboardType();
			String phonetickeyboardtype = mLIMEPref.getPhoneticKeyboardType();
			String dualcode ="";
			String dualKey = "";
			String dualKeyRemap = "";
			String remaptable = tablename;
			if(isPhysicalKeyboardPressed ){
				if(tablename.equals("phonetic"))
					remaptable = tablename + keyboardtype + phonetickeyboardtype;
				else
					remaptable = tablename + keyboardtype;
			}else if(tablename.equals("phonetic")){
					remaptable = tablename + phonetickeyboardtype;
			}
			
			
			if(keysDualMap.get(remaptable)==null
					|| keysDualMap.get(remaptable).size()==0){
				if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("eten26")){
					dualKey = ETEN26_DUALKEY;
					dualKeyRemap = ETEN26_DUALKEY_REMAP;	
				}else if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("hsu")){
						dualKey = HSU_DUALKEY;
						dualKeyRemap = HSU_DUALKEY_REMAP;
				}else if(keyboardtype.equals("milestone") && isPhysicalKeyboardPressed ){
					if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("eten")){
						dualKey = MILESTONE_ETEN_DUALKEY;
						dualKeyRemap = MILESTONE_ETEN_DUALKEY_REMAP;
					}else{
						dualKey = MILESTONE_DUALKEY;
						dualKeyRemap = MILESTONE_DUALKEY_REMAP;
					}
				}else if(keyboardtype.equals("milestone2") && isPhysicalKeyboardPressed ){
					if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("eten")){
						dualKey = MILESTONE2_ETEN_DUALKEY;
						dualKeyRemap = MILESTONE2_ETEN_DUALKEY_REMAP;
					}else{
						dualKey = MILESTONE2_DUALKEY;
						dualKeyRemap = MILESTONE2_DUALKEY_REMAP;
					}
				}else if(keyboardtype.equals("milestone3") && isPhysicalKeyboardPressed ){
					if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("eten")){
						dualKey = MILESTONE3_ETEN_DUALKEY;
						dualKeyRemap = MILESTONE3_ETEN_DUALKEY_REMAP;
					}else if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("standard")){
						dualKey = MILESTONE3_BPMF_DUALKEY;
						dualKeyRemap = MILESTONE3_BPMF_DUALKEY_REMAP;
					}else{
						dualKey = MILESTONE3_DUALKEY;
						dualKeyRemap = MILESTONE3_DUALKEY_REMAP;
					}
				}else if(keyboardtype.equals("desireZ") && isPhysicalKeyboardPressed ) {
					if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("eten")){
						dualKey = DESIREZ_ETEN_DUALKEY;
						dualKeyRemap = DESIREZ_ETEN_DUALKEY_REMAP;
					}else if(tablename.equals("phonetic")&&phonetickeyboardtype.equals("standard")){
						dualKey = DESIREZ_BPMF_DUALKEY;
						dualKeyRemap = DESIREZ_BPMF_DUALKEY_REMAP;
					}else{
						dualKey = DESIREZ_DUALKEY;
						dualKeyRemap = DESIREZ_DUALKEY_REMAP;
					}
				}
				HashMap<String,String> reMap = new HashMap<String,String>();
				if(DEBUG)
					Log.i(TAG, "preProcessingForExtraQueryConditions(): dualKey="+dualKey+" dualKeyRemap="+dualKeyRemap);
				for(int i=0; i< dualKey.length(); i++){
					String key = dualKey.substring(i,i+1);
					String value = dualKeyRemap.substring(i,i+1);
					//Process the escape characters of query
					if(key.equals("'")) key = "''";
					if(value.equals("'")) value = "''";
					reMap.put(key, value);
					reMap.put(value, value);
				}
				keysDualMap.put(remaptable, reMap);
			}
			// do real prcoessing now
			if(keysDualMap.get(remaptable)==null
					|| keysDualMap.get(remaptable).size()==0){
				dualcode = code;
			}else{

				HashMap<String,String> reMap = keysDualMap.get(remaptable);
				dualcode = "";
				// testing if code contains dual mapped characters. 
				for (int i = 0; i < code.length(); i++) {
					String c = reMap.get(code.substring(i, i + 1));
					if(c!=null) dualcode = dualcode + c;
				}
				if(DEBUG)
					Log.i(TAG, "preProcessingForExtraQueryConditions(): dualcode="+dualcode);
				
				
			}
			//Jeremy '11,8,12 if phonetic has tone symbol in the middle do the expanddualcode
			if(!dualcode.equalsIgnoreCase(code)
					|| !code.equalsIgnoreCase(lastCode) // '11,8,18 Jeremy
					||(tablename.equals("phonetic") && code.matches(".+[ 3467].+") ) 
				){
				return expandDualCode(code, remaptable);
			}
		}
		return "";
	}
	
	private HashSet<String> buildDualCodeList(String code, String keytablename){
		
		if(DEBUG) Log.i(TAG, "buildDualCodeList(): code:"+ code);
		
		HashMap<String,String> codeDualMap = keysDualMap.get(keytablename);
	
		HashSet<String> dualCodeList = new HashSet<String>();
		dualCodeList.add(code);

		if(codeDualMap != null && codeDualMap.size()>0) {

			do{
				int currentListSize = dualCodeList.size();
				boolean codeInserted = false;
				HashSet<String> tempSet = new HashSet<String>(dualCodeList);
				for(String currentCode : tempSet) {
					//String currentCode = dualCodeList.get(i);
					if(DEBUG) Log.i(TAG, "buildDualCodeList():currentSize:"+ currentListSize + " curretnCode:" + currentCode);
					for(int j=0; j< currentCode.length(); j++){
						String c = currentCode.substring(j, j+1);

						if(codeDualMap.get(c)!=null){
							//Log.i("LIMEDB:buildDualCodeList()","dualCode found:"+ c + " -> " + codeDualMap.get(c));
							String newCode = "";
							String n = codeDualMap.get(c);
							if(currentCode.length() == 1) newCode = n;
							else{
								if(j==0) 
									newCode = n + currentCode.substring(1,currentCode.length());
								else if(j==currentCode.length()) 
									newCode = currentCode.substring(0, currentCode.length()-1) + n;
								else
									newCode = currentCode.substring(0,j) + n 
									+ currentCode.substring(j+1, currentCode.length());
							}

							if(dualCodeList.add(newCode)){
								if(DEBUG) 
									Log.i(TAG, "buildDualCodeList(): code added:"+ newCode);
								codeInserted = true;
							}

						}
					}
				}
				if(!codeInserted) break;

			}while(true);
		}
		//Jeremy '11,8,12 added for continuous typing.  
		if(tablename.equals("phonetic")){			
			HashSet<String> tempList = new HashSet<String>(dualCodeList);
			for(String iterator_code: tempList){
				if(iterator_code.matches(".+[ 3467].+")){ // regular expression mathes tone in the middle
					String newCode = iterator_code.replaceAll("[ 3467]","");
					dualCodeList.add(newCode);
					if(DEBUG) Log.i(TAG, "buildDualCodeList(): code added:"+ newCode);
				}
			}
		}

		//if(dualCodeList.size()==1)
		//	return null;
		//else
			return dualCodeList;
			
		
		
		
	}
	
	private String expandDualCode(String code, String keytablename){
		
		HashSet <String> dualCodeList = buildDualCodeList(code, keytablename);
		String result="";
		String validDualCodeList = "";
		//SQLiteDatabase db = this.getSqliteDb(false);
		
		if(dualCodeList != null) {
			for(String dualcode : dualCodeList){
				if(DEBUG) 
					Log.i(TAG, "expandDualCode(): processing dualcode: " + dualcode + "|");
				String codeCol = FIELD_CODE;
				if(tablename.equals("phonetic")&& mLIMEPref.getParameterBoolean("doLDPhonetic", false) 
						&& !code.matches(".+[3467 ].*"))
					codeCol = FIELD_CODE3R;
				
				dualcode = dualcode.trim();
			//Jeremy '11,8, 26 move valid code list building to buildqueryresult to avoid repeat query.
//				String[] col = {"DISTINCT " + codeCol};
//				
//				try{
//					Cursor cursor = db.query(tablename, col, 
//							codeCol + " = '" + dualcode + "'", 
//								null, null, null, null, null);
//					if(cursor.moveToFirst()){ //fist entry exist, the code is valid.
				if(validDualCodeList.equals("")) validDualCodeList = dualcode;
				else validDualCodeList = validDualCodeList + "|" + dualcode;

				if(!dualcode.equals(code))
					result = result + " OR "+  codeCol + "= '"+ dualcode +"'";
		
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				 
			}
//			db.close();
			if(validDualCodeList.equals(""))
				lastValidDualCodeList = null;
			else
				lastValidDualCodeList = validDualCodeList;
		}
		if(DEBUG)
			Log.i(TAG, "expandDualCode(): result:" + result + " validDualCodeList:" + validDualCodeList);
		return result;
		
		
	}

	/*
	 * Process search results
	 */
	private Pair<List<Mapping>,List<Mapping>> buildQueryResult(String query_code, Cursor cursor, Boolean getAllRecords) {
		
		
		List<Mapping> result = new ArrayList<Mapping>();
		List<Mapping> relatedresult = new ArrayList<Mapping>();
		Pair<List<Mapping>,List<Mapping>> resultPair = new Pair<List<Mapping>,List<Mapping>>(result, relatedresult);
		HashSet<String> duplicateCheck = new HashSet<String>();
		HashSet<String> validCodeMap = new HashSet<String>();  //Jeremy '11,8,26
		int rsize = 0;
		
		boolean useCode3r =tablename.equals("phonetic")
				&& mLIMEPref.getParameterBoolean("doLDPhonetic", false) 
				&& !query_code.matches(".+[3467 ].*");
		if(DEBUG) Log.i(TAG,"buildQueryResutl(): cursor.getCount()=" + cursor.getCount());
		if (cursor.moveToFirst()) {

			int idColumn = cursor.getColumnIndex(FIELD_id);
			int codeColumn = cursor.getColumnIndex(FIELD_CODE);
			int code3rColumn = cursor.getColumnIndex(FIELD_CODE3R);
			int wordColumn = cursor.getColumnIndex(FIELD_WORD);
			int scoreColumn = cursor.getColumnIndex(FIELD_SCORE);
			int relatedColumn = cursor.getColumnIndex(FIELD_RELATED);		
			HashMap<String, String> relatedMap = new HashMap<String, String>();
			
			do {		
				String code = cursor.getString(codeColumn);
				String relatedlist = cursor.getString(relatedColumn);
				Mapping munit = new Mapping();
				munit.setWord(cursor.getString(wordColumn));
				munit.setId(cursor.getString(idColumn));
				munit.setCode(code);
				
				//Jeremy '11,8,26 build valid code map
				if(useCode3r)
					validCodeMap.add(cursor.getString(code3rColumn));
				else
					validCodeMap.add(code);
				

				// 06/Aug/2011 by Art: ignore the result when word == keyToKeyname(code)
				// Only apply to Array IM
				try{
					if(code != null && code.length() == 1 && tablename.equals("array")){
						if(keyToKeyname(code, tablename, false).equals(munit.getWord())){
							continue;
						}
					}
				}catch(Exception e){}
				
				if(relatedMap.get(code) == null){
					relatedMap.put(code, relatedlist);
				}
				munit.setScore(cursor.getInt(scoreColumn));
				munit.setDictionary(false);
				
				if(munit.getWord() == null || munit.getWord().trim().equals(""))
					continue;
				
				if(duplicateCheck.add(munit.getWord())){
					result.add(munit);
				}
				rsize++;
			} while (cursor.moveToNext());
			
			//Jeremy '11,8,26 build valid code map
			if(validCodeMap.size()>0){
				lastValidDualCodeList="";
				for(String validCode : validCodeMap){
					if(lastValidDualCodeList.equals("")) lastValidDualCodeList = validCode;
					else lastValidDualCodeList = lastValidDualCodeList + "|" + validCode;
				}
			}

			int ssize = mLIMEPref.getSimilarCodeCandidates();
			//Jeremy '11,6,1 The related field may have only one word and thus no "|" inside
			//Jeremy '11,6,11 allow multiple relatedlist from different codes.
			int scount = 0;
			for(Entry<String, String> entry: relatedMap.entrySet()){
				String relatedlist = entry.getValue();
				if (ssize > 0 && relatedlist != null && scount <= ssize){ 
					String templist[] = relatedlist.split("\\|");
				
					for (String unit : templist) {
						if(ssize != 0 && scount > ssize){break;}
						if(duplicateCheck.add(unit)){
							Mapping munit = new Mapping();
							munit.setWord(unit);
							//munit.setPword(relatedlist);
							munit.setScore(0);
							munit.setCode(entry.getKey());
							//Jeremy '11,6,18 skip if word is empty
							if(munit.getWord() == null || munit.getWord().trim().equals(""))
								continue;
							relatedresult.add(munit);
							scount++;
							// Jeremy '11, 8, 5 break if limit number exceeds
							if(!getAllRecords && scount == INITIAL_RELATED_LIMIT)  break;
						}
					}
				}
			}
		}
		if(query_code.length() == 1){
			// processing full shaped , and .
			if( (query_code.equals(",")||query_code.equals("<")) && duplicateCheck.add("，") ){
				Mapping temp = new Mapping();
				temp.setCode(query_code);
				temp.setWord("，");
				if(result.size()>3)
					result.add(3,temp);
				else
					result.add(temp);
			}
			if( (query_code.equals(".")||query_code.equals(">")) && duplicateCheck.add("。") ){
				Mapping temp = new Mapping();
				temp.setCode(query_code);
				temp.setWord("。");
				if(result.size()>3)
					result.add(3,temp);
				else
					result.add(temp);
			}
		}
		
		
		Mapping temp = new Mapping();
		temp.setCode("has_more_records");
		temp.setWord("...");
		
		if(!getAllRecords && rsize == Integer.parseInt(INITIAL_RESULT_LIMIT))
			result.add(temp);
		if(!getAllRecords && relatedresult.size() == INITIAL_RELATED_LIMIT)
			relatedresult.add(temp);
		
		if(DEBUG)
			Log.i(TAG, "buildQueryResult():query_code:" + query_code + " query_code.length:" + query_code.length()
				+ " result.size=" + result.size() + " query size:" + rsize 
				+ " relatedlist.size=" + relatedresult.size());
		return resultPair;
	}

	/**
	 * 
	 * @return Cursor for
	 */
	public Cursor getDictionaryAll() {
		Cursor cursor = null;
		SQLiteDatabase db = this.getSqliteDb(true);

		cursor = db.query("dictionary", null, null, null, null, null, null, null);
		return cursor;
	}

	/**
	 * Get dictionary database contents
	 * 
	 * @param keyword
	 * @return
	 */
	public List<Mapping> queryUserDict(String pword) {
		
		List<Mapping> result = new LinkedList<Mapping>();


		if(mLIMEPref.getSimiliarEnable()){
			
			if (pword != null && !pword.trim().equals("")) {
	
				Cursor cursor = null;
	
				SQLiteDatabase db = this.getSqliteDb(true);
				// Jeremy '11,8.23 remove group by condition to avoid sorting ordr
				// Jeremy '11,8,1 add group by cword to remove duplicate items.
				//Jeremy '11,6,12, Add constraint on cword is not null (cword =null is for recoding im related list selected count).
				cursor = db.query("related", null, FIELD_DIC_pword + " = '"
						+ pword + "' AND " + FIELD_DIC_cword + " IS NOT NULL"
						, null, null , null, FIELD_SCORE + " DESC", null);
	
				if (cursor.moveToFirst()) {
	
					int pwordColumn = cursor.getColumnIndex(FIELD_DIC_pword);
					int cwordColumn = cursor.getColumnIndex(FIELD_DIC_cword);
					int scoreColumn = cursor.getColumnIndex(FIELD_DIC_score);
					int idColumn = cursor.getColumnIndex(FIELD_id);
					do {
						Mapping munit = new Mapping();
						munit.setId(cursor.getString(idColumn));
						munit.setPword(cursor.getString(pwordColumn));
						munit.setCode("");
						munit.setWord(cursor.getString(cwordColumn));
						munit.setScore(cursor.getInt(scoreColumn));
						munit.setDictionary(true);
						result.add(munit);
					} while (cursor.moveToNext());
				}
	
				if (cursor != null) {
					cursor.deactivate();
					cursor.close();
				}
				db.close();
			}
		}
		return result;
	}

	/**
	 * Load source file and add records into database
	 */
	public synchronized void loadFile(final String table) {
		finish = false;
		percentageDone = 0;
		count = 0;
		if (thread != null ) {
			//threadAborted = true;
			while(thread.isAlive()){
				Log.d(TAG, "loadFile():waiting for last loading thread stopped...");
				SystemClock.sleep(1000);
			}
			thread = null;
		}

		thread = new Thread() {

			public void run() {
				// Reset Database Table		
				SQLiteDatabase db = getSqliteDb(false);
				try {
					if(countMapping(table)>0) 	db.delete(table, null, null);
					if(DEBUG) Log.i(TAG, "loadfile(), table = " + table +" kbversion" + 
							Integer.parseInt(mLIMEPref.getParameterString("kbversion")));
					if(table.equals("phonetic") &&
							!mLIMEPref.getParameterBoolean("doLDPhonetic", false) ) {
						if(DEBUG) Log.i(TAG, "loadfile(), build code3r index here.");
						mLIMEPref.setParameter("doLDPhonetic", true);
						db.execSQL("CREATE INDEX phonetic_idx_code3r ON phonetic(code3r)");
						
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				db.close();
				resetImInfo(table);
				

				//boolean hasMappingVersion = false;
				boolean isCinFormat = false;
				//ArrayList<ContentValues> resultlist = new ArrayList<ContentValues>();

				String imname = "";
				String line = "";
				String endkey ="";
				String selkey ="";
				String spacestyle = "";
				String imkeys = "";
				String imkeynames = "";


				// Check if source file is .cin format
				if (filename.getName().toLowerCase().endsWith(".cin")) {
					isCinFormat = true;
				}

				// Base on first 100 line to identify the Delimiter
				try {
					// Prepare Source File
					FileReader fr = new FileReader(filename);
					BufferedReader buf = new BufferedReader(fr);
					//boolean firstline = true;
					int i = 0;
					List<String> templist = new ArrayList<String>();
					while ((line = buf.readLine()) != null
							&& isCinFormat == false) {
						templist.add(line);
						if (i >= 100) {
							break;
						} else {
							i++;
						}
					}
					identifyDelimiter(templist);
					templist.clear();
					buf.close();
					fr.close();
				} catch (Exception e) {
				}

				// Create Related Words
				Map<String, String> hm = new HashMap<String, String>();

				db = getSqliteDb(false);
				db.beginTransaction();

				try {
					// Prepare Source File
					long fileLength = filename.length();
					long processedLength = 0;
					FileReader fr = new FileReader(filename);
					BufferedReader buf = new BufferedReader(fr);
					boolean firstline = true;
					boolean inChardefBlock = false;
					boolean inKeynameBlock = false;
					//String precode = "";

					while ((line = buf.readLine()) != null && !threadAborted) {
						processedLength += line.getBytes().length + 2; // +2 for the eol mark.
						percentageDone = (int) ((float)processedLength/(float)fileLength *50);
						if(DEBUG)
							Log.i(TAG, "loadFile():loadFile()"+ percentageDone +"% processed" 
									+ ". processedLength:" + processedLength + ". fileLength:" + fileLength);
						if(percentageDone>49) percentageDone = 49;
						/*
						 * If source is cin format start from the tag %chardef
						 * begin until %chardef end
						 */
						if (isCinFormat) {
							if (!(inChardefBlock || inKeynameBlock)) {
								// Modified by Jeremy '10, 3, 28. Some .cin have
								// double space between $chardef and begin or
								// end
								if (line != null
										&& line.trim().toLowerCase().startsWith("%chardef")
										&& line.trim().toLowerCase().endsWith("begin")
								) {
									inChardefBlock = true;
								}
								if (line != null
										&& line.trim().toLowerCase().startsWith("%keyname")
										&& line.trim().toLowerCase().endsWith("begin")
								) {
									inKeynameBlock = true;
								}
								// Add by Jeremy '10, 3 , 27
								// use %cname as mapping_version of .cin
								// Jeremy '11,6,5 add selkey, endkey and spacestyle support
								if (!(  line.trim().toLowerCase().startsWith("%cname")
										||line.trim().toLowerCase().startsWith("%selkey")
										||line.trim().toLowerCase().startsWith("%endkey")
										||line.trim().toLowerCase().startsWith("%spacestyle")
								)) {
									continue;
								}
							}
							if (line != null
									&& line.trim().toLowerCase().startsWith("%keyname")
									&& line.trim().toLowerCase().endsWith("end")
							) {
								inKeynameBlock = false;
								continue;
							}
							if (line != null
									&& line.trim().toLowerCase().startsWith("%chardef")
									&& line.trim().toLowerCase().endsWith("end")
							) {
								break;
							}
						}

						// Check if file contain BOM MARK at file header
						if (firstline) {
							byte srcstring[] = line.getBytes();
							if (srcstring.length > 3) {
								if (srcstring[0] == -17 && srcstring[1] == -69
										&& srcstring[2] == -65) {
									byte tempstring[] = new byte[srcstring.length - 3];
									//int a = 0;
									for (int j = 3; j < srcstring.length; j++) {
										tempstring[j - 3] = srcstring[j];
									}
									line = new String(tempstring);
								}
							}
							firstline = false;
						} else 	if (line == null || line.trim().equals("") || line.length() < 3) {
							continue;
						}

						try {

							String code = null, word = null;
							if (isCinFormat) {
								if (line.indexOf("\t") != -1) {
									code = line
									.substring(0, line.indexOf("\t"));
									word = line
									.substring(line.indexOf("\t") + 1);
								} else if (line.indexOf(" ") != -1) {
									code = line.substring(0, line.indexOf(" "));
									word = line
									.substring(line.indexOf(" ") + 1);
								}
							} else {
								code = line.substring(0, line
										.indexOf(DELIMITER));
								word = line
								.substring(line.indexOf(DELIMITER) + 1);
							}
							if (code == null || code.trim().equals("")) {
								continue;
							} else {
								code = code.trim();
							}
							if (word == null || word.trim().equals("")) {
								continue;
							} else {
								word = word.trim();
							}
							if (code.toLowerCase().contains("@version@")) {
								imname = word.trim();
								continue;
							} else if (code.toLowerCase().contains("%cname")) {
								imname = word.trim();
								continue;
							} else if (code.toLowerCase().contains("%selkey")) {
								selkey = word.trim();
								if(DEBUG) Log.i(TAG, "loadfile(): selkey:"+selkey);
								continue;
							} else if (code.toLowerCase().contains("%endkey")) {
								endkey = word.trim();
								if(DEBUG) Log.i(TAG, "loadfile(): endkey:"+endkey);
								continue;	
							} else if (code.toLowerCase().contains("%spacestyle")) {
								spacestyle = word.trim();
								continue;	
							} else {
								code = code.toLowerCase();
							}
							if(inKeynameBlock) {  //Jeremy '11,6,5 preserve keyname blocks here.
								imkeys = imkeys + code.toLowerCase().trim();
								String c = word.trim();
								if(!c.equals("")){
									if(imkeynames.equals(""))
										imkeynames = c;
									else
										imkeynames = imkeynames + "|"+c; 
								}

							}
							else {
								if (code.length() > 1) {
									//Jeremy '11,6,1  put the exact match word in the first word of related field
									if (hm.get(code) != null && hm.get(code).startsWith("|"))
										hm.put(code, word+hm.get(code));
									else
										hm.put(code,word);	

									for (int k = 1; k < code.length(); k++) {
										String rootkey = code.substring(0, code.length() - k);
										if (hm.get(rootkey) != null) {
											String tempvalue = hm.get(rootkey);
											if (hm.get(rootkey) != null
													&& hm.get(rootkey).indexOf(word) == -1) {
												if(hm.get(rootkey).split("\\|").length < 50){
													hm.put(rootkey, tempvalue + "|" + word);
												}
											}
										} else {
											hm.put(rootkey, "|"+word);
										}
									}
								}

								count++;
								ContentValues cv = new ContentValues();
								cv.put(FIELD_CODE, code);
								if(table.equals("phonetic")) {
									String code3r = code.replaceAll("[3467]", "");
									cv.put(FIELD_CODE3R, code3r);;
									//Log.i(TAG, "loadfile(), code=" + code+ "code3r="+code3r);
								}
								cv.put(FIELD_WORD, word);
								cv.put(FIELD_SCORE, 0);
								db.insert(table, null, cv);
							}

						} catch (StringIndexOutOfBoundsException e) {}
					}

					buf.close();
					fr.close();

					db.setTransactionSuccessful();
				} catch (Exception e) {
					db.close();
					setImInfo(table, "amount", "0");
					setImInfo(table, "source", "Failed!!!");
					e.printStackTrace();
				} finally {
					if(DEBUG) Log.i(TAG, "loadfile(): main import loop final section");
					db.endTransaction();
					db.close();
				}
				


				if(!threadAborted){
					db = getSqliteDb(false);
					db.beginTransaction();
					try{
						long entrySize = hm.size();
						long i = 0;
						for(Entry<String, String> entry: hm.entrySet())	{
							if(threadAborted) 	break;
							percentageDone = (int) ((float)(i++)/(float)entrySize *50 +50);
							if(percentageDone>99) percentageDone = 99;

							if(!entry.getValue().contains("|"))  // does not have related words; only has exact mappings
								continue;
							try{
								ContentValues cv = new ContentValues();
								String code = entry.getKey().replaceAll("'", "''");
								String tempValue = entry.getValue();
								String newValue = "";							
								//The related field starts with "|" mean no exact code coreesponding and has to insert new one.
								if (entry.getValue().startsWith("|")){
									cv.put(FIELD_CODE, code);
									newValue = tempValue.substring(1, tempValue.length());
									cv.put(FIELD_RELATED, newValue);
									db.insert(table, null, cv);
								}
								else{
									//The first word is the exact code corresponding word and has to be trimmed from related field
									newValue = tempValue.substring(tempValue.indexOf("|")+1
											, tempValue.length());
									cv.put(FIELD_RELATED, newValue);
									db.update(table, cv, FIELD_CODE +"='"+code+"'", null);
								}
								if(DEBUG)
									Log.i(TAG, "loadfile():create related field. code ="+entry.getKey()+" related = " + entry.getValue()+" trimmedRelated:" + newValue);


							}catch(Exception e2){
								// Just ignore all problem statement
								Log.i(TAG, "loadfile():create related field error on code ="+entry.getKey()+" related = " + entry.getValue());
							}

						}
						db.setTransactionSuccessful();
					}catch (Exception e){
						setImInfo(table, "amount", "0");
						setImInfo(table, "source", "Failed!!!");
						e.printStackTrace();
					}finally {
						if(DEBUG) Log.i(TAG, "loadfile(): related list buiding loop final section");
						db.endTransaction();
						db.close();
					}
					
					
				}


				if(!threadAborted) {
					if(!threadAborted) percentageDone = 100;
					finish = true;
				
					mLIMEPref.setParameter("_table", "");

					setImInfo(table, "source", filename.getName());
					setImInfo(table, "name", imname);
					setImInfo(table, "amount", String.valueOf(count));
					setImInfo(table, "import", new Date().toLocaleString());

					if(DEBUG) 
						Log.i("limedb:loadfile()","Fianlly section: source:" 
							+ getImInfo(table,"source") + " amount:"+getImInfo(table,"amount"));

					// If user download from LIME Default IM SET then fill in related information
					if(filename.getName().equals("phonetic.lime") || filename.getName().equals("phonetic_adv.lime")){
						setImInfo("phonetic", "selkey", "123456789");
						setImInfo("phonetic", "endkey", "3467'[]\\=<>?:\"{}|~!@#$%^&*()_+");
						setImInfo("phonetic", "imkeys", ",-./0123456789;abcdefghijklmnopqrstuvwxyz'[]\\=<>?:\"{}|~!@#$%^&*()_+");
						setImInfo("phonetic", "imkeynames", "ㄝ|ㄦ|ㄡ|ㄥ|ㄢ|ㄅ|ㄉ|ˇ|ˋ|ㄓ|ˊ|˙|ㄚ|ㄞ|ㄤ|ㄇ|ㄖ|ㄏ|ㄎ|ㄍ|ㄑ|ㄕ|ㄘ|ㄛ|ㄨ|ㄜ|ㄠ|ㄩ|ㄙ|ㄟ|ㄣ|ㄆ|ㄐ|ㄋ|ㄔ|ㄧ|ㄒ|ㄊ|ㄌ|ㄗ|ㄈ|、|「|」|＼|＝|，|。|？|：|；|『|』|│|～|！|＠|＃|＄|％|︿|＆|＊|（|）|－|＋");
					}if(filename.getName().equals("array.lime")){
						setImInfo("array", "selkey", "1234567890");
						setImInfo("array", "imkeys", "abcdefghijklmnopqrstuvwxyz./;,?*#1#2#3#4#5#6#7#8#9#0");
						setImInfo("array", "imkeynames", "1-|5⇣|3⇣|3-|3⇡|4-|5-|6-|8⇡|7-|8-|9-|7⇣|6⇣|9⇡|0⇡|1⇡|4⇡|2-|5⇡|7⇡|4⇣|2⇡|2⇣|6⇡|1⇣|9⇣|0⇣|0-|8⇣|？|＊|1|2|3|4|5|6|7|8|9|0");
					}else{
						if (!selkey.equals("")) setImInfo(table, "selkey", selkey);
						if (!endkey.equals("")) setImInfo(table, "endkey", endkey);
						if (!spacestyle.equals("")) setImInfo(table, "spacestyle", spacestyle);
						if (!imkeys.equals("")) setImInfo(table, "imkeys", imkeys);
						if (!imkeynames.equals("")) setImInfo(table, "imkeynames", imkeynames);
					}
					if(DEBUG) 
						Log.i(TAG, "loadfile():update IM info: imkeys:" +imkeys + " imkeynames:"+imkeynames);

					// If there is no keyboard assigned for current input method then use default keyboard layout
					//String keyboard = getImInfo(table, "keyboard");
					//if(keyboard == null || keyboard.equals("")){
					//setImInfo(table, "keyboard", "lime");
					// '11,5,23 by Jeremy: Preset keyboard info. by tablename
					KeyboardObj kobj = getKeyboardObj(table);
					if( table.equals("phonetic")){
						String selectedPhoneticKeyboardType = 
							mLIMEPref.getParameterString("phonetic_keyboard_type", "standard");
						if(selectedPhoneticKeyboardType.equals("standard")){
							kobj = 	getKeyboardObj("phonetic");
						}else if(selectedPhoneticKeyboardType.equals("eten")){
							kobj = 	getKeyboardObj("limenumsym");
						}else if(selectedPhoneticKeyboardType.equals("eten26")||selectedPhoneticKeyboardType.equals("hsu")){
							if(mLIMEPref.getParameterBoolean("number_row_in_english", false)){
								kobj = 	getKeyboardObj("limenum");
							}else{
								kobj = 	getKeyboardObj("lime");
							}
						}
					}else if( table.equals("cj5")){					
						kobj = getKeyboardObj("cj");
					}else if( table.equals("ecj")){				
						kobj = getKeyboardObj("cj");
					}else if( table.equals("array")){					
						kobj = getKeyboardObj("arraynum");
					}else if( table.equals("array10")){					
						kobj = getKeyboardObj("phonenum");
					}else if( table.equals("wb")){					
						kobj = getKeyboardObj("cj");
					}else if( kobj == null){					
						kobj = getKeyboardObj("lime");
					}
					setIMKeyboard(table, kobj.getDescription(), kobj.getCode());
				}



			}

		};
		threadAborted = false;
		thread.start();
	}
	
	public ContentValues getInsertItem(String code, String word) {
		try {
				ContentValues cv = new ContentValues();
				cv.put(FIELD_CODE, code);
				cv.put(FIELD_WORD, word);
				cv.put(FIELD_SCORE, 0);
				return cv;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Identify the delimiter of the source file
	 * 
	 * @param src
	 */
	public void identifyDelimiter(List<String> src) {

		int commaCount = 0;
		int tabCount = 0;
		int pipeCount = 0;

		if (this.DELIMITER.equals("")) {
			for (String line : src) {
				if (line.indexOf("\t") != -1) {
					tabCount++;
				}
				if (line.indexOf(",") != -1) {
					commaCount++;
				}
				if (line.indexOf("|") != -1) {
					pipeCount++;
				}
			}
			if (commaCount > 0 || tabCount > 0 || pipeCount > 0) {
				if (commaCount >= tabCount && commaCount >= pipeCount) {
					this.DELIMITER = ",";
				} else if (tabCount >= commaCount && tabCount >= pipeCount) {
					this.DELIMITER = "\t";
				} else if (pipeCount >= tabCount && pipeCount >= commaCount) {
					this.DELIMITER = "|";
				}
			}
		}
	}
	
	/**
	 * Check if the specific mapping exists in current table
	 * 
	 * @param code
	 * @param word
	 * @return
	 */
	public Mapping isMappingExist(String code, String word) {
		Mapping munit =null;
		if (code != null && code.trim().length()>0){
			SQLiteDatabase db = this.getSqliteDb(true);
			try {
				Cursor cursor = null;
				
				if(word==null || word.trim().length()==0){
					cursor = db.query(tablename, null, FIELD_CODE + " = '"
							+ code + "'" , null, null, null, null, null);
				}else{
					cursor = db.query(tablename, null, FIELD_CODE + " = '"
						+ code + "'" + " AND " + FIELD_WORD + " = '"
						+ word + "'", null, null, null, null, null);
				}
				
				munit = new Mapping();
				if (cursor.moveToFirst()) {
					int idColumn = cursor.getColumnIndex(FIELD_id);
					int codeColumn = cursor.getColumnIndex(FIELD_CODE);
					int wordColumn = cursor.getColumnIndex(FIELD_WORD);
					int scoreColumn = cursor.getColumnIndex(FIELD_SCORE);
					int relatedColumn = cursor.getColumnIndex(FIELD_RELATED);		
					
					munit.setId(cursor.getString(idColumn));
					munit.setCode(cursor.getString(codeColumn));
					munit.setWord(cursor.getString(wordColumn));
					munit.setScore(cursor.getInt(scoreColumn));
					munit.setRelated(cursor.getString(relatedColumn));
					munit.setDictionary(false);
					
				} 
				
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
				db.close();
			}
		}
		return null;
		
	}

	/**
	 * Check if usesr dictionary record exists
	 * 
	 * @param pword
	 * @param cword
	 * @return
	 */
	public Mapping isUserDictExist(String pword, String cword) {

		Mapping munit =null;
		if (pword != null && !pword.trim().equals("")){
			SQLiteDatabase db = this.getSqliteDb(true);
			try {
				Cursor cursor = null;
				
				if(cword==null || cword.trim().equals("")){
					cursor = db.query("related", null, FIELD_DIC_pword + " = '"
							+ pword + "'" + " AND " + FIELD_DIC_cword + " IS NULL"
							, null, null, null, null, null);
				}else{
					cursor = db.query("related", null, FIELD_DIC_pword + " = '"
						+ pword + "'" + " AND " + FIELD_DIC_cword + " = '"
						+ cword + "'", null, null, null, null, null);
				}
				
				if (cursor.moveToFirst()) {
					int pwordColumn = cursor.getColumnIndex(FIELD_DIC_pword);
					int cwordColumn = cursor.getColumnIndex(FIELD_DIC_cword);
					int scoreColumn = cursor.getColumnIndex(FIELD_DIC_score);
					int idColumn = cursor.getColumnIndex(FIELD_id);
					
					munit = new Mapping();
						munit.setId(cursor.getString(idColumn));
						munit.setPword(cursor.getString(pwordColumn));
						munit.setWord(cursor.getString(cwordColumn));
						munit.setScore(cursor.getInt(scoreColumn));
						munit.setDictionary(true);
					
				} 
				db.close();
			} catch (Exception e) {
				db.close();
				e.printStackTrace();
			}
		}
		return munit;
	}
	

	/**
	 * @param srcunit
	 */
	public synchronized void resetImInfo(String im) {
		String removeString = "DELETE FROM im WHERE code='"+im+"'";
		SQLiteDatabase db = this.getSqliteDb(false);
			           db.execSQL(removeString);
					   db.close();
	}
	
	/**
	 * @param srcunit
	 */
	public String getImInfo(String im, String field) {
		try{
			//String value = "";
			String selectString = "SELECT * FROM im WHERE code='"+im+"' AND title='"+field+"'";
			SQLiteDatabase db = this.getSqliteDb(true);
	
			Cursor cursor = db.rawQuery(selectString ,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int descCol = cursor.getColumnIndex("desc");
				String iminfo = cursor.getString(descCol);
				db.close();
				return iminfo;
			}
			
			db.close();
		}catch(Exception e){}
		return "";
	}
	
	/**
	 * @param srcunit
	 */
	public synchronized void removeImInfo(String im, String field) {
		String removeString = "DELETE FROM im WHERE code='"+im+"' AND title='"+field+"'";
		SQLiteDatabase db = this.getSqliteDb(false);
			           db.execSQL(removeString);
					   db.close();
	}
	

	/**
	 * @param srcunit
	 */
	public synchronized void setImInfo(String im, String field, String value) {

		ContentValues cv = new ContentValues();
					  cv.put("code", im);
					  cv.put("title", field);
					  cv.put("desc", value);
		
					  removeImInfo(im, field);
					  
		SQLiteDatabase db = this.getSqliteDb(false);
			           db.insert("im",null, cv);
					   db.close();
	}

	public List<ImObj> getImList() {
		List<ImObj> result = new LinkedList<ImObj>();
		try {
			SQLiteDatabase db = this.getSqliteDb(true);
			Cursor cursor = db.query("im", null, null, null, null, null, "code ASC", null);
			if (cursor.moveToFirst()) {
				do{
					String title = cursor.getString(cursor.getColumnIndex("title"));
					if(title.equals("keyboard")){
						ImObj kobj = new ImObj();
							  kobj.setCode(cursor.getString(cursor.getColumnIndex("code")));
							  kobj.setKeyboard(cursor.getString(cursor.getColumnIndex("keyboard")));
							  result.add(kobj);
					}
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.i(TAG,"getImList(): Cannot get IM List : " + e );
		}
		return result;
	}
	
	public KeyboardObj getKeyboardObj(String keyboard){
		if(keyboard == null || keyboard.equals(""))
			return null;
		KeyboardObj kobj=null;
		try {
			SQLiteDatabase db = this.getSqliteDb(true);
			Cursor cursor = db.query("keyboard", null, FIELD_CODE +" = '"+keyboard+"'", null, null, null, null, null);
			if (cursor.moveToFirst()) {
				kobj = new KeyboardObj();
				kobj.setCode(cursor.getString(cursor.getColumnIndex("code")));
				kobj.setName(cursor.getString(cursor.getColumnIndex("name")));
				kobj.setDescription(cursor.getString(cursor.getColumnIndex("desc")));
				kobj.setType(cursor.getString(cursor.getColumnIndex("type")));
				kobj.setImage(cursor.getString(cursor.getColumnIndex("image")));
				kobj.setImkb(cursor.getString(cursor.getColumnIndex("imkb")));
				kobj.setImshiftkb(cursor.getString(cursor.getColumnIndex("imshiftkb")));
				kobj.setEngkb(cursor.getString(cursor.getColumnIndex("engkb")));
				kobj.setEngshiftkb(cursor.getString(cursor.getColumnIndex("engshiftkb")));
				kobj.setSymbolkb(cursor.getString(cursor.getColumnIndex("symbolkb")));
				kobj.setSymbolshiftkb(cursor.getString(cursor.getColumnIndex("symbolshiftkb")));
				kobj.setDefaultkb(cursor.getString(cursor.getColumnIndex("defaultkb")));
				kobj.setDefaultshiftkb(cursor.getString(cursor.getColumnIndex("defaultshiftkb")));
				kobj.setExtendedkb(cursor.getString(cursor.getColumnIndex("extendedkb")));
				kobj.setExtendedshiftkb(cursor.getString(cursor.getColumnIndex("extendedshiftkb")));
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kobj;
	}
	
	public String getKeyboardInfo(String keyboardCode, String field) {
		String info=null;
		try {
			SQLiteDatabase db = this.getSqliteDb(true);
			Cursor cursor = db.query("keyboard", null, FIELD_CODE +" = '"+keyboardCode+"'"
					, null, null, null, null, null);
			if (cursor.moveToFirst()) {
				info = cursor.getString(cursor.getColumnIndex(field));
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	
	}

	public List<KeyboardObj> getKeyboardList() {
		
		
		List<KeyboardObj> result = new LinkedList<KeyboardObj>();
		try {
			SQLiteDatabase db = this.getSqliteDb(true);
			Cursor cursor = db.query("keyboard", null, null, null, null, null, "name ASC", null);
			if (cursor.moveToFirst()) {
				do{
					KeyboardObj kobj = new KeyboardObj();
								kobj.setCode(cursor.getString(cursor.getColumnIndex("code")));
								kobj.setName(cursor.getString(cursor.getColumnIndex("name")));
								kobj.setDescription(cursor.getString(cursor.getColumnIndex("desc")));
								kobj.setType(cursor.getString(cursor.getColumnIndex("type")));
								kobj.setImage(cursor.getString(cursor.getColumnIndex("image")));
								kobj.setImkb(cursor.getString(cursor.getColumnIndex("imkb")));
								kobj.setImshiftkb(cursor.getString(cursor.getColumnIndex("imshiftkb")));
								kobj.setEngkb(cursor.getString(cursor.getColumnIndex("engkb")));
								kobj.setEngshiftkb(cursor.getString(cursor.getColumnIndex("engshiftkb")));
								kobj.setSymbolkb(cursor.getString(cursor.getColumnIndex("symbolkb")));
								kobj.setSymbolshiftkb(cursor.getString(cursor.getColumnIndex("symbolshiftkb")));
								kobj.setDefaultkb(cursor.getString(cursor.getColumnIndex("defaultkb")));
								kobj.setDefaultshiftkb(cursor.getString(cursor.getColumnIndex("defaultshiftkb")));
								kobj.setExtendedkb(cursor.getString(cursor.getColumnIndex("extendedkb")));
								kobj.setExtendedshiftkb(cursor.getString(cursor.getColumnIndex("extendedshiftkb")));
					result.add(kobj);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public synchronized void setIMKeyboard(String im, String value,
			String keyboard) {

		ContentValues cv = new ContentValues();
					  cv.put("code", im);
					  cv.put("title", "keyboard");
					  cv.put("desc", value);
					  cv.put("keyboard", keyboard);
		
					  removeImInfo(im, "keyboard");
					  
		SQLiteDatabase db = this.getSqliteDb(false);
			           db.insert("im",null, cv);
			           db.close();
			           
		
	}

	public String getKeyboardCode(String im) {
		try{
			//String value = "";
			String selectString = "SELECT * FROM im WHERE code='"+im+"' AND title='keyboard'";
			SQLiteDatabase db = this.getSqliteDb(true);
	
			Cursor cursor = db.rawQuery(selectString ,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int descCol = cursor.getColumnIndex("keyboard");
				String keyboardCode = cursor.getString(descCol);
				db.close();
				return keyboardCode;
			}
			db.close();
		}catch(Exception e){}
		return "";
	}

	public List<String> queryDictionary(String word) {
		List<String> result = new ArrayList<String>();
		try{
			//String value = "";
			int ssize = mLIMEPref.getSimilarCodeCandidates();
			String selectString = "SELECT word FROM dictionary WHERE word MATCH '"+word+"*' ORDER BY word ASC LIMIT "+ssize+";";
			SQLiteDatabase db = this.getSqliteDb(true);
	
			Cursor cursor = db.rawQuery(selectString ,null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do{
					String w = cursor.getString(cursor.getColumnIndex("word"));
					if(w != null && !w.equals("")){
						result.add(w);
					}
				} while (cursor.moveToNext());
			}
			
			db.close();
		}catch(Exception e){}
		
		return result;
	}

}