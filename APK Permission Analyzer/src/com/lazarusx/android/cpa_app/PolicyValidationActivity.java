package com.lazarusx.android.cpa_app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;

import com.breezesoft.cpa_app.CPADatabaseHelper;
import com.googlecode.android4me.test.AXMLPrinter;

public class PolicyValidationActivity extends TabActivity implements Runnable {
	private TabHost tabHost;
	private File file;
	private ProgressDialog pd;
	private String package_name, version_name;
	private ArrayList<InfoItem> infoItems = new ArrayList<InfoItem>();
	private ArrayList<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
	private ListView infoListView, permissionListView;
	private InfoAdapter infoAdapter;
	private PermissionAdapter permissionAdapter;
	CPADatabaseHelper dbh;

	static final String PACKAGE_INFO = "PACKAGE_INFO";
	static final String POLICY_VALIDATION = "POLICY_VALIDATION";
	String category;
	private String ps;
	private String pw;

	static final private int INSTALL = Menu.FIRST;
	SharedPreferences settings;
	boolean datt = false;
	Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh = new CPADatabaseHelper(this);

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		ps = settings.getString("PS", "80");
		pw = settings.getString("PW", "50");
		datt = settings.getBoolean("DAT", false);

		Bundle bundle = getIntent().getExtras();
		ps = bundle.getString("PS");
		pw = bundle.getString("PW");
		if (bundle != null) {
			String filePath = bundle.getString("SelectedFilePath");
			if (filePath != "") {
				file = new File(filePath);
			}
		}

		category = bundle.getString("Category");
		tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.info_tab, tabHost.getTabContentView(), true);
		tabHost.addTab(tabHost.newTabSpec(PACKAGE_INFO).setIndicator(getString(R.string.package_info), getResources().getDrawable(R.drawable.ic_tab_package_info)).setContent(R.id.widget_package_info));
		tabHost.addTab(tabHost.newTabSpec(POLICY_VALIDATION).setIndicator(getString(R.string.policy_validation), getResources().getDrawable(R.drawable.ic_tab_policy_validation))
				.setContent(R.id.widget_policy_validation));

		infoListView = (ListView) this.findViewById(R.id.infoListView);
		infoAdapter = new InfoAdapter(this, R.layout.info_item, infoItems);
		infoListView.setAdapter(infoAdapter);
		infoItems.add(new InfoItem("File Name", file.getName()));

		permissionListView = (ListView) this.findViewById(R.id.permissionListView);
		permissionAdapter = new PermissionAdapter(this, R.layout.permission_item, permissionItems, Integer.parseInt(ps), Integer.parseInt(pw));
		permissionListView.setAdapter(permissionAdapter);

		//pd = ProgressDialog.show(this, "Working...", "Validating Package");
		//thread = new Thread(this);
		//thread.start();
		 getPackageInfo();
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("PS", ps);
		editor.putString("PW", pw);
		editor.putBoolean("DAT", datt);
		editor.commit();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem preferencesItem = menu.add(0, INSTALL, Menu.NONE, R.string.install_menu);

		preferencesItem.setIcon(android.R.drawable.ic_menu_add);

		preferencesItem.setShortcut('0', 'i');
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (INSTALL): {
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			startActivity(installIntent);
			return true;
		}
		}
		return false;
	}

	public void run() {
		getPackageInfo();
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			pd.dismiss();
			infoAdapter.notifyDataSetChanged();
			permissionAdapter.notifyDataSetChanged();
		}
	};

	private void getPackageInfo() {
		if (!datt) {
			copyFile("/sdcard/apks.db", "/data/data/com.lazarusx.android.cpa_app/databases/apks.db");
			datt = true;
		}
		try {
			ZipInputStream in;
			in = new ZipInputStream(new FileInputStream(file));
			ZipEntry z;
			String xmlName = "AndroidManifest.xml";
			while ((z = in.getNextEntry()) != null) {
				if (z.getName().equalsIgnoreCase(xmlName)) {
					FileOutputStream out = openFileOutput("raw.xml", Context.MODE_PRIVATE);
					int b;
					while ((b = in.read()) != -1) {
						out.write(b);
					}
					out.close();
					break;
				}
			}
			in.close();

			String arg[] = { getFilesDir().getAbsolutePath() + "/raw.xml" };
			FileOutputStream xmlStream = openFileOutput("AndroidManifest.xml", Context.MODE_WORLD_WRITEABLE);
			PrintStream ps = new PrintStream(xmlStream, false, "UTF-8");
			PrintStream systemOut = System.out;
			System.setOut(ps);
			AXMLPrinter.main(arg);
			System.setOut(systemOut);
			xmlStream.close();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			String xmlPath = getFilesDir().getAbsolutePath() + "/AndroidManifest.xml";
			Document doc = db.parse(new File(xmlPath));

			NodeList manifestList = doc.getElementsByTagName("manifest");
			package_name = manifestList.item(0).getAttributes().getNamedItem("package").getNodeValue();
			infoItems.add(new InfoItem("Package Name", package_name));
			version_name = manifestList.item(0).getAttributes().getNamedItem("android:versionName").getNodeValue();
			infoItems.add(new InfoItem("Version Name", version_name));
			NodeList permissionList = doc.getElementsByTagName("uses-permission");
			for (int i = 0; i < permissionList.getLength(); i++) {
				Node node = permissionList.item(i);
				String permission = node.getAttributes().item(0).getNodeValue();
				permissionItems.add(new PermissionItem(permission, "Percentage in " + category, dbh.getFreq(permissionNameConvert(permission.substring(19)), category)));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		dbh.close();

		//thread.stop();
	}

	public static String permissionNameConvert(String permission_name) {
		String result;
		result = "pm001";
		if (permission_name.equals("ACCESS_FINE_LOCATION"))
			result = "pm001";
		if (permission_name.equals("ACCESS_COARSE_LOCATION"))
			result = "pm002";
		if (permission_name.equals("INTERNET"))
			result = "pm003";
		if (permission_name.equals("WRITE_EXTERNAL_STORAGE"))
			result = "pm004";
		if (permission_name.equals("WAKE_LOCK"))
			result = "pm005";
		if (permission_name.equals("ACCESS_WIFI_STATE"))
			result = "pm006";
		if (permission_name.equals("ACCESS_NETWORK_STATE"))
			result = "pm007";
		if (permission_name.equals("SET_WALLPAPER"))
			result = "pm008";
		if (permission_name.equals("CAMERA"))
			result = "pm009";
		if (permission_name.equals("READ_PHONE_STATE"))
			result = "pm010";
		if (permission_name.equals("GET_ACCOUNTS"))
			result = "pm011";
		if (permission_name.equals("VIBRATE"))
			result = "pm012";
		if (permission_name.equals("CALL_PHONE"))
			result = "pm013";
		if (permission_name.equals("SEND_SMS"))
			result = "pm014";
		if (permission_name.equals("RECEIVE_SMS"))
			result = "pm015";
		if (permission_name.equals("RECEIVE_MMS"))
			result = "pm016";
		if (permission_name.equals("READ_SMS"))
			result = "pm017";
		if (permission_name.equals("WRITE_SMS"))
			result = "pm018";
		if (permission_name.equals("READ_CONTACTS"))
			result = "pm019";
		if (permission_name.equals("WRITE_CONTACTS"))
			result = "pm020";
		if (permission_name.equals("CHANGE_NETWORK_STATE"))
			result = "pm021";
		if (permission_name.equals("WRITE_SETTINGS"))
			result = "pm022";
		if (permission_name.equals("DISABLE_KEYGUARD"))
			result = "pm023";
		if (permission_name.equals("GET_TASKS"))
			result = "pm024";
		if (permission_name.equals("RECEIVE_BOOT_COMPLETED"))
			result = "pm025";
		if (permission_name.equals("KILL_BACKGROUND_PROCESSES"))
			result = "pm026";
		if (permission_name.equals("RESTART_PACKAGES"))
			result = "pm027";
		if (permission_name.equals("BROADCAST_STICKY"))
			result = "pm028";
		if (permission_name.equals("INSTALL_DRM"))
			result = "pm029";
		if (permission_name.equals("READ_CALENDAR"))
			result = "pm030";
		if (permission_name.equals("FLASHLIGHT"))
			result = "pm031";
		if (permission_name.equals("USE_CREDENTIALS"))
			result = "pm032";
		if (permission_name.equals("MANAGE_ACCOUNTS"))
			result = "pm033";
		if (permission_name.equals("RECORD_AUDIO"))
			result = "pm034";
		if (permission_name.equals("MODIFY_AUDIO_SETTINGS"))
			result = "pm035";
		if (permission_name.equals("READ_SYNC_SETTINGS"))
			result = "pm036";
		if (permission_name.equals("WRITE_SYNC_SETTINGS"))
			result = "pm037";
		if (permission_name.equals("CHANGE_WIFI_STATE"))
			result = "pm038";
		if (permission_name.equals("SUBSCRIBED_FEEDS_WRITE"))
			result = "pm039";
		if (permission_name.equals("SUBSCRIBED_FEEDS_READ"))
			result = "pm040";
		if (permission_name.equals("READ_SYNC_STATS"))
			result = "pm041";
		if (permission_name.equals("AUTHENTICATE_ACCOUNTS"))
			result = "pm042";
		if (permission_name.equals("ACCOUNT_MANAGER"))
			result = "pm043";
		if (permission_name.equals("BLUETOOTH"))
			result = "pm044";
		if (permission_name.equals("BLUETOOTH_ADMIN"))
			result = "pm045";
		if (permission_name.equals("SYSTEM_ALERT_WINDOW"))
			result = "pm046";
		if (permission_name.equals("ACCESS_CHECKIN_PROPERTIES"))
			result = "pm047";
		if (permission_name.equals("ACCESS_LOCATION_EXTRA_COMMANDS"))
			result = "pm048";
		if (permission_name.equals("ACCESS_MOCK_LOCATION"))
			result = "pm049";
		if (permission_name.equals("ACCESS_SURFACE_FLINGER"))
			result = "pm050";
		if (permission_name.equals("BATTERY_STATS"))
			result = "pm051";
		if (permission_name.equals("BIND_APPWIDGET"))
			result = "pm052";
		if (permission_name.equals("BIND_DEVICE_ADMIN"))
			result = "pm053";
		if (permission_name.equals("BIND_INPUT_METHOD"))
			result = "pm054";
		if (permission_name.equals("BIND_WALLPAPER"))
			result = "pm055";
		if (permission_name.equals("BRICK"))
			result = "pm056";
		if (permission_name.equals("BROADCAST_PACKAGE_REMOVED"))
			result = "pm057";
		if (permission_name.equals("BROADCAST_SMS"))
			result = "pm058";
		if (permission_name.equals("BROADCAST_WAP_PUSH"))
			result = "pm059";
		if (permission_name.equals("CALL_PRIVILEGED"))
			result = "pm060";
		if (permission_name.equals("CHANGE_COMPONENT_ENABLED_STATE"))
			result = "pm061";
		if (permission_name.equals("CHANGE_CONFIGURATION"))
			result = "pm062";
		if (permission_name.equals("CHANGE_WIFI_MULTICAST_STATE"))
			result = "pm063";
		if (permission_name.equals("CLEAR_APP_CACHE"))
			result = "pm064";
		if (permission_name.equals("CLEAR_APP_USER_DATA"))
			result = "pm065";
		if (permission_name.equals("CONTROL_LOCATION_UPDATES"))
			result = "pm066";
		if (permission_name.equals("DELETE_CACHE_FILES"))
			result = "pm067";
		if (permission_name.equals("DELETE_PACKAGES"))
			result = "pm068";
		if (permission_name.equals("DEVICE_POWER"))
			result = "pm069";
		if (permission_name.equals("DIAGNOSTIC"))
			result = "pm070";
		if (permission_name.equals("DUMP"))
			result = "pm071";
		if (permission_name.equals("EXPAND_STATUS_BAR"))
			result = "pm072";
		if (permission_name.equals("FACTORY_TEST"))
			result = "pm073";
		if (permission_name.equals("FORCE_BACK"))
			result = "pm074";
		if (permission_name.equals("GET_PACKAGE_SIZE"))
			result = "pm075";
		if (permission_name.equals("GLOBAL_SEARCH"))
			result = "pm076";
		if (permission_name.equals("HARDWARE_TEST"))
			result = "pm077";
		if (permission_name.equals("INJECT_EVENTS"))
			result = "pm078";
		if (permission_name.equals("INSTALL_LOCATION_PROVIDER"))
			result = "pm079";
		if (permission_name.equals("INSTALL_PACKAGES"))
			result = "pm080";
		if (permission_name.equals("INTERNAL_SYSTEM_WINDOW"))
			result = "pm081";
		if (permission_name.equals("MANAGE_APP_TOKENS"))
			result = "pm082";
		if (permission_name.equals("MASTER_CLEAR"))
			result = "pm083";
		if (permission_name.equals("MODIFY_PHONE_STATE"))
			result = "pm084";
		if (permission_name.equals("MOUNT_FORMAT_FILESYSTEMS"))
			result = "pm085";
		if (permission_name.equals("MOUNT_UNMOUNT_FILESYSTEMS"))
			result = "pm086";
		if (permission_name.equals("NFC"))
			result = "pm087";
		if (permission_name.equals("PERSISTENT_ACTIVITY"))
			result = "pm088";
		if (permission_name.equals("PROCESS_OUTGOING_CALLS"))
			result = "pm089";
		if (permission_name.equals("READ_FRAME_BUFFER"))
			result = "pm090";
		if (permission_name.equals("READ_INPUT_STATE"))
			result = "pm091";
		if (permission_name.equals("READ_LOGS"))
			result = "pm092";
		if (permission_name.equals("REBOOT"))
			result = "pm093";
		if (permission_name.equals("RECEIVE_WAP_PUSH"))
			result = "pm094";
		if (permission_name.equals("REORDER_TASKS"))
			result = "pm095";
		if (permission_name.equals("SET_ACTIVITY_WATCHER"))
			result = "pm096";
		if (permission_name.equals("SET_ALARM"))
			result = "pm097";
		if (permission_name.equals("SET_ALWAYS_FINISH"))
			result = "pm098";
		if (permission_name.equals("SET_ANIMATION_SCALE"))
			result = "pm099";
		if (permission_name.equals("SET_DEBUG_APP"))
			result = "pm100";
		if (permission_name.equals("SET_ORIENTATION"))
			result = "pm101";
		if (permission_name.equals("SET_PREFERRED_APPLICATIONS"))
			result = "pm102";
		if (permission_name.equals("SET_PROCESS_LIMIT"))
			result = "pm103";
		if (permission_name.equals("SET_TIME"))
			result = "pm104";
		if (permission_name.equals("SET_TIME_ZONE"))
			result = "pm105";
		if (permission_name.equals("SET_WALLPAPER_HINTS"))
			result = "pm106";
		if (permission_name.equals("SIGNAL_PERSISTENT_PROCESSES"))
			result = "pm107";
		if (permission_name.equals("STATUS_BAR"))
			result = "pm108";
		if (permission_name.equals("UPDATE_DEVICE_STATS"))
			result = "pm109";
		if (permission_name.equals("USE_SIP"))
			result = "pm110";
		if (permission_name.equals("WRITE_APN_SETTINGS"))
			result = "pm111";
		if (permission_name.equals("WRITE_CALENDAR"))
			result = "pm112";
		if (permission_name.equals("WRITE_GSERVICES"))
			result = "pm113";
		if (permission_name.equals("WRITE_SECURE_SETTINGS"))
			result = "pm114";
		if (permission_name.equals("WRITE_SETTINGS"))
			result = "pm115";
		if (permission_name.equals("SEND_DOWNLOAD_COMPLETED_INTENTS"))
			result = "pm116";
		if (permission_name.equals("SHUTDOWN"))
			result = "pm117";
		if (permission_name.equals("READ_HISTORY_BOOKMARKS"))
			result = "pm118";
		if (permission_name.equals("WRITE_HISTORY_BOOKMARKS"))
			result = "pm119";
		if (permission_name.equals("WRITE_USER_DICTIONARY"))
			result = "pm120";
		if (permission_name.equals("FORCE_STOP_PACKAGES"))
			result = "pm121";
		if (permission_name.equals("PACKAGE_USAGE_STATS"))
			result = "pm122";
		if (permission_name.equals("MOVE_PACKAGE"))
			result = "pm123";
		if (permission_name.equals("ACCESS_DOWNLOAD_MANAGER"))
			result = "pm124";
		if (permission_name.equals("READ_USER_DICTIONARY"))
			result = "pm125";
		if (permission_name.equals(""))
			result = "pm126";
		if (permission_name.equals(""))
			result = "pm127";
		if (permission_name.equals(""))
			result = "pm128";
		if (permission_name.equals(""))
			result = "pm129";
		if (permission_name.equals(""))
			result = "pm130";
		if (permission_name.equals(""))
			result = "pm131";
		if (permission_name.equals(""))
			result = "pm132";
		if (permission_name.equals(""))
			result = "pm133";
		if (permission_name.equals(""))
			result = "pm134";
		if (permission_name.equals(""))
			result = "pm135";
		if (permission_name.equals(""))
			result = "pm136";
		if (permission_name.equals(""))
			result = "pm137";
		if (permission_name.equals(""))
			result = "pm138";
		if (permission_name.equals(""))
			result = "pm139";
		if (permission_name.equals(""))
			result = "pm140";
		return result;
	}

	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();

		}
	}

}
