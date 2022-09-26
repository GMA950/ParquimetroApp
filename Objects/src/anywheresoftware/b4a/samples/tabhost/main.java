package anywheresoftware.b4a.samples.tabhost;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "anywheresoftware.b4a.samples.tabhost", "anywheresoftware.b4a.samples.tabhost.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "anywheresoftware.b4a.samples.tabhost", "anywheresoftware.b4a.samples.tabhost.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "anywheresoftware.b4a.samples.tabhost.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.sql.SQL _mi_db = null;
public static anywheresoftware.b4a.objects.Timer _tmr = null;
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtname = null;
public anywheresoftware.b4a.objects.EditTextWrapper _exittext = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner1 = null;
public anywheresoftware.b4a.sql.SQL.CursorWrapper _mi_cursor = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonborrar = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btningresar = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndone = null;
public static String _patente = "";
public static String _patente2 = "";
public static String _ingreso = "";
public static String _salida = "";
public static String _horai = "";
public static String _fechai = "";
public static String _fechas = "";
public static String _horas = "";
public static boolean _autorizado = false;
public static int _cobro = 0;
public static int _rep = 0;
public static long _dt = 0L;
public static long _dt2 = 0L;
public static String _dg = "";
public static String _tg = "";
public anywheresoftware.b4a.objects.LabelWrapper _label4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label5 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label7 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _stattext = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndone2 = null;
public b4a.example.dateutils _dateutils = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp1 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp2 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp3 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp4 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp5 = null;
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 39;BA.debugLine="Dim bmp1, bmp2, bmp3, bmp4, bmp5  As Bitmap";
_bmp1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmp2 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmp3 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmp4 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
_bmp5 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 40;BA.debugLine="bmp1 = LoadBitmap(File.DirAssets, \"ico1.png\")";
_bmp1 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ico1.png");
 //BA.debugLineNum = 41;BA.debugLine="bmp2 = LoadBitmap(File.DirAssets, \"ico2.png\")";
_bmp2 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ico2.png");
 //BA.debugLineNum = 42;BA.debugLine="bmp3 = LoadBitmap(File.DirAssets, \"ico3.png\")";
_bmp3 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ico3.png");
 //BA.debugLineNum = 43;BA.debugLine="bmp4 = LoadBitmap(File.DirAssets, \"parking.png\")";
_bmp4 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"parking.png");
 //BA.debugLineNum = 44;BA.debugLine="bmp5 = LoadBitmap(File.DirAssets, \"stat.png\")";
_bmp5 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"stat.png");
 //BA.debugLineNum = 46;BA.debugLine="TabHost1.AddTabWithIcon (\"\", bmp1,bmp1, \"page1\")";
mostCurrent._tabhost1.AddTabWithIcon(mostCurrent.activityBA,"",(android.graphics.Bitmap)(_bmp1.getObject()),(android.graphics.Bitmap)(_bmp1.getObject()),"page1");
 //BA.debugLineNum = 47;BA.debugLine="TabHost1.AddTabWithIcon (\"\",bmp2,bmp2, \"page2\")";
mostCurrent._tabhost1.AddTabWithIcon(mostCurrent.activityBA,"",(android.graphics.Bitmap)(_bmp2.getObject()),(android.graphics.Bitmap)(_bmp2.getObject()),"page2");
 //BA.debugLineNum = 48;BA.debugLine="TabHost1.AddTabWithIcon (\"\", bmp3 ,bmp3, \"page3\")";
mostCurrent._tabhost1.AddTabWithIcon(mostCurrent.activityBA,"",(android.graphics.Bitmap)(_bmp3.getObject()),(android.graphics.Bitmap)(_bmp3.getObject()),"page3");
 //BA.debugLineNum = 51;BA.debugLine="imageview1.SetBackgroundImage(bmp4)";
mostCurrent._imageview1.SetBackgroundImageNew((android.graphics.Bitmap)(_bmp4.getObject()));
 //BA.debugLineNum = 53;BA.debugLine="ListView1.SingleLineLayout.Label.TextSize = 18";
mostCurrent._listview1.getSingleLineLayout().Label.setTextSize((float) (18));
 //BA.debugLineNum = 54;BA.debugLine="ListView1.SingleLineLayout.ItemHeight = 300";
mostCurrent._listview1.getSingleLineLayout().setItemHeight((int) (300));
 //BA.debugLineNum = 56;BA.debugLine="btnIngresar.Color = Colors.RGB(51, 204, 51)";
mostCurrent._btningresar.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (51),(int) (204),(int) (51)));
 //BA.debugLineNum = 57;BA.debugLine="ButtonBorrar.Color = Colors.RGB(255, 51, 0)";
mostCurrent._buttonborrar.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (255),(int) (51),(int) (0)));
 //BA.debugLineNum = 58;BA.debugLine="btnDone.Color = Colors.RGB(51, 204, 51)";
mostCurrent._btndone.setColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (51),(int) (204),(int) (51)));
 //BA.debugLineNum = 60;BA.debugLine="tmr.Initialize(\"tmr\", 60000)";
_tmr.Initialize(processBA,"tmr",(long) (60000));
 //BA.debugLineNum = 61;BA.debugLine="tmr.Enabled = True";
_tmr.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 63;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 65;BA.debugLine="Mi_DB.Initialize(File.DirInternal,\"Autos.db\",Tru";
_mi_db.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"Autos.db",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 67;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 68;BA.debugLine="Try";
try { //BA.debugLineNum = 69;BA.debugLine="Mi_DB.ExecNonQuery(\"CREATE TABLE IF NOT EXISTS";
_mi_db.ExecNonQuery("CREATE TABLE IF NOT EXISTS Mi_Tabla1 (id INTEGER PRIMARY KEY AUTOINCREMENT, patente TEXT, ingreso DATETIME, salida DATETIME, cobro INTEGER DEFAULT '0',  repetido INTEGER DEFAULT '0')");
 //BA.debugLineNum = 70;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 71;BA.debugLine="ToastMessageShow(\"Crea la base\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Crea la base"),anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e27) {
			processBA.setLastException(e27); //BA.debugLineNum = 73;BA.debugLine="Log(\"ERROR de Creacion base de datos: \"&LastExc";
anywheresoftware.b4a.keywords.Common.Log("ERROR de Creacion base de datos: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 75;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 };
 //BA.debugLineNum = 77;BA.debugLine="Spinner1.Add(\"Seleccione Filtro\")";
mostCurrent._spinner1.Add("Seleccione Filtro");
 //BA.debugLineNum = 78;BA.debugLine="Spinner1.Add(\"Todos\")";
mostCurrent._spinner1.Add("Todos");
 //BA.debugLineNum = 79;BA.debugLine="Spinner1.Add(\"Estacionados\")";
mostCurrent._spinner1.Add("Estacionados");
 //BA.debugLineNum = 80;BA.debugLine="Spinner1.Add(\"Retirados\")";
mostCurrent._spinner1.Add("Retirados");
 //BA.debugLineNum = 81;BA.debugLine="dt2 = DateTime.now";
_dt2 = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 82;BA.debugLine="dg = DateTime.Date(dt2).SubString2(0:10)";
mostCurrent._dg = anywheresoftware.b4a.keywords.Common.DateTime.Date(_dt2).substring((int) (0),(int) (10));
 //BA.debugLineNum = 83;BA.debugLine="tg = DateTime.Time(dt2).SubString2(0:5)";
mostCurrent._tg = anywheresoftware.b4a.keywords.Common.DateTime.Time(_dt2).substring((int) (0),(int) (5));
 //BA.debugLineNum = 84;BA.debugLine="Label4.Text = \"Fecha: \" & dg";
mostCurrent._label4.setText(BA.ObjectToCharSequence("Fecha: "+mostCurrent._dg));
 //BA.debugLineNum = 85;BA.debugLine="Label5.Text =\"Hora: \" & tg";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Hora: "+mostCurrent._tg));
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _finishing) throws Exception{
 //BA.debugLineNum = 87;BA.debugLine="Sub Activity_Pause (Finishing As Boolean)";
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _btndone_click() throws Exception{
String _sal1 = "";
String _sal2 = "";
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp5 = null;
int _i = 0;
String _start = "";
b4a.example.dateutils._period _p = null;
anywheresoftware.b4a.keywords.StringBuilderWrapper _sb = null;
 //BA.debugLineNum = 107;BA.debugLine="Sub btnDone_Click";
 //BA.debugLineNum = 108;BA.debugLine="Dim sal1 As String";
_sal1 = "";
 //BA.debugLineNum = 109;BA.debugLine="Dim sal2 As String";
_sal2 = "";
 //BA.debugLineNum = 110;BA.debugLine="Dim bmp5 As Bitmap";
_bmp5 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 111;BA.debugLine="bmp5 = LoadBitmap(File.DirAssets, \"parking2.png\")";
_bmp5 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"parking2.png");
 //BA.debugLineNum = 112;BA.debugLine="If ExitText.Text<>\"\" Then";
if ((mostCurrent._exittext.getText()).equals("") == false) { 
 //BA.debugLineNum = 113;BA.debugLine="patente2 = ExitText.Text.ToUpperCase";
mostCurrent._patente2 = mostCurrent._exittext.getText().toUpperCase();
 //BA.debugLineNum = 114;BA.debugLine="If Regex.IsMatch(\"^[A-Z]{4}[0-9]{2}|[A-Z]{2}[0-9";
if (anywheresoftware.b4a.keywords.Common.Regex.IsMatch("^[A-Z]{4}[0-9]{2}|[A-Z]{2}[0-9]{4}$",mostCurrent._patente2)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 115;BA.debugLine="ToastMessageShow(\"Patente no válida\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Patente no válida"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 116;BA.debugLine="Return";
if (true) return "";
 }else {
 //BA.debugLineNum = 118;BA.debugLine="Mi_Cursor=Mi_DB.ExecQuery(\"SELECT * FROM Mi_tab";
mostCurrent._mi_cursor.setObject((android.database.Cursor)(_mi_db.ExecQuery("SELECT * FROM Mi_tabla1 WHERE patente = '"+mostCurrent._patente2+"' and salida IS '' ORDER BY id ASC")));
 //BA.debugLineNum = 119;BA.debugLine="If Mi_Cursor.RowCount>0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 120;BA.debugLine="For i=0 To Mi_Cursor.RowCount-1";
{
final int step13 = 1;
final int limit13 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;(step13 > 0 && _i <= limit13) || (step13 < 0 && _i >= limit13) ;_i = ((int)(0 + _i + step13))  ) {
 //BA.debugLineNum = 121;BA.debugLine="Mi_Cursor.Position=i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 123;BA.debugLine="dt = DateTime.now";
_dt = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 124;BA.debugLine="sal1 = DateTime.Date(dt).SubString2(0:10)";
_sal1 = anywheresoftware.b4a.keywords.Common.DateTime.Date(_dt).substring((int) (0),(int) (10));
 //BA.debugLineNum = 125;BA.debugLine="sal2 = DateTime.Time(dt).SubString2(0:5)";
_sal2 = anywheresoftware.b4a.keywords.Common.DateTime.Time(_dt).substring((int) (0),(int) (5));
 //BA.debugLineNum = 126;BA.debugLine="Dim start As String = Mi_Cursor.GetString(\"in";
_start = mostCurrent._mi_cursor.GetString("ingreso");
 //BA.debugLineNum = 127;BA.debugLine="Dim p As Period = DateUtils.PeriodBetween(Dat";
_p = mostCurrent._dateutils._periodbetween(mostCurrent.activityBA,anywheresoftware.b4a.keywords.Common.DateTime.DateParse(_start),_dt);
 //BA.debugLineNum = 130;BA.debugLine="If p.Hours < 1 And p.Minutes < 15 Then";
if (_p.Hours<1 && _p.Minutes<15) { 
 //BA.debugLineNum = 131;BA.debugLine="cobro = 500";
_cobro = (int) (500);
 }else {
 //BA.debugLineNum = 133;BA.debugLine="cobro = 500 + p.Minutes*30 + p.Hours*60*30 +";
_cobro = (int) (500+_p.Minutes*30+_p.Hours*60*30+_p.Days*24*60*30);
 };
 //BA.debugLineNum = 135;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 136;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 137;BA.debugLine="sb.Append(\"Ticket Parquimetro\").Append(CRLF)";
_sb.Append("Ticket Parquimetro").Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 138;BA.debugLine="sb.Append(CRLF)";
_sb.Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 139;BA.debugLine="sb.Append(\"Patente \").Append(Mi_Cursor.GetStr";
_sb.Append("Patente ").Append(mostCurrent._mi_cursor.GetString("patente")).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 140;BA.debugLine="sb.Append(CRLF)";
_sb.Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 141;BA.debugLine="sb.Append(\"Ingreso: \").Append(start.SubString";
_sb.Append("Ingreso: ").Append(_start.substring((int) (0),(int) (10))).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 142;BA.debugLine="sb.Append(\"A las: \").Append(start.SubString2(";
_sb.Append("A las: ").Append(_start.substring((int) (11),(int) (16))).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 143;BA.debugLine="sb.Append(CRLF)";
_sb.Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 144;BA.debugLine="sb.Append(\"Salida: \").Append(sal1).Append(CRL";
_sb.Append("Salida: ").Append(_sal1).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 145;BA.debugLine="sb.Append(\"A las: \").Append(sal2).Append(CRLF";
_sb.Append("A las: ").Append(_sal2).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 146;BA.debugLine="sb.Append(CRLF)";
_sb.Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 147;BA.debugLine="sb.Append(\"Total a Pagar: $\").Append(cobro).A";
_sb.Append("Total a Pagar: $").Append(BA.NumberToString(_cobro)).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 148;BA.debugLine="Msgbox(sb.ToString, \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_sb.ToString()),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 150;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 151;BA.debugLine="Try";
try { //BA.debugLineNum = 152;BA.debugLine="Mi_DB.ExecNonQuery2(\"UPDATE Mi_tabla1 SET sa";
_mi_db.ExecNonQuery2("UPDATE Mi_tabla1 SET salida = ?, cobro = ? WHERE patente = ? AND cobro IS '0'",anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{anywheresoftware.b4a.keywords.Common.DateTime.Date(_dt),BA.NumberToString(_cobro),mostCurrent._patente2}));
 //BA.debugLineNum = 153;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 154;BA.debugLine="imageview1.SetBackgroundImage(bmp5)";
mostCurrent._imageview1.SetBackgroundImageNew((android.graphics.Bitmap)(_bmp5.getObject()));
 } 
       catch (Exception e45) {
			processBA.setLastException(e45); //BA.debugLineNum = 156;BA.debugLine="Log(\"catch: \" & LastException.Message)";
anywheresoftware.b4a.keywords.Common.Log("catch: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 158;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 }
};
 }else {
 //BA.debugLineNum = 161;BA.debugLine="ToastMessageShow(\"Patente no encontrada\", True";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Patente no encontrada"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 162;BA.debugLine="Return";
if (true) return "";
 };
 };
 }else {
 //BA.debugLineNum = 166;BA.debugLine="ToastMessageShow(\"Ingrese Patente\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Ingrese Patente"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 167;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 169;BA.debugLine="End Sub";
return "";
}
public static String  _btndone2_click() throws Exception{
int _sal1 = 0;
int _sal2 = 0;
String _sal3 = "";
int _i = 0;
anywheresoftware.b4a.keywords.StringBuilderWrapper _sb = null;
 //BA.debugLineNum = 322;BA.debugLine="Sub btnDone2_Click";
 //BA.debugLineNum = 323;BA.debugLine="Dim sal1 As Int";
_sal1 = 0;
 //BA.debugLineNum = 324;BA.debugLine="Dim sal2 As Int";
_sal2 = 0;
 //BA.debugLineNum = 325;BA.debugLine="Dim sal3 As String";
_sal3 = "";
 //BA.debugLineNum = 326;BA.debugLine="If ExitText.Text<>\"\" Then";
if ((mostCurrent._exittext.getText()).equals("") == false) { 
 //BA.debugLineNum = 327;BA.debugLine="patente2 = ExitText.Text.ToUpperCase";
mostCurrent._patente2 = mostCurrent._exittext.getText().toUpperCase();
 //BA.debugLineNum = 328;BA.debugLine="If Regex.IsMatch(\"^[A-Z]{4}[0-9]{2}|[A-Z]{2}[0-9";
if (anywheresoftware.b4a.keywords.Common.Regex.IsMatch("^[A-Z]{4}[0-9]{2}|[A-Z]{2}[0-9]{4}$",mostCurrent._patente2)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 329;BA.debugLine="ToastMessageShow(\"Patente no válida\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Patente no válida"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 330;BA.debugLine="Return";
if (true) return "";
 }else {
 //BA.debugLineNum = 332;BA.debugLine="Mi_Cursor=Mi_DB.ExecQuery(\"SELECT SUM(cobro) as";
mostCurrent._mi_cursor.setObject((android.database.Cursor)(_mi_db.ExecQuery("SELECT SUM(cobro) as cobrototal FROM Mi_tabla1 WHERE patente = '"+mostCurrent._patente2+"' ORDER BY id ASC")));
 //BA.debugLineNum = 333;BA.debugLine="If Mi_Cursor.RowCount>0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 334;BA.debugLine="For i=0 To Mi_Cursor.RowCount-1";
{
final int step12 = 1;
final int limit12 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;(step12 > 0 && _i <= limit12) || (step12 < 0 && _i >= limit12) ;_i = ((int)(0 + _i + step12))  ) {
 //BA.debugLineNum = 335;BA.debugLine="Mi_Cursor.Position=i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 337;BA.debugLine="sal2 = Mi_Cursor.GetString(\"cobrototal\")";
_sal2 = (int)(Double.parseDouble(mostCurrent._mi_cursor.GetString("cobrototal")));
 }
};
 //BA.debugLineNum = 341;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 342;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 343;BA.debugLine="sb.Append(\"Estadistico Parquimetro\").Append(CR";
_sb.Append("Estadistico Parquimetro").Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 344;BA.debugLine="sb.Append(CRLF)";
_sb.Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 345;BA.debugLine="sb.Append(\"Patente \").Append(patente2).Append(";
_sb.Append("Patente ").Append(mostCurrent._patente2).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 346;BA.debugLine="sb.Append(CRLF)";
_sb.Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 347;BA.debugLine="sb.Append(\"Recurrencias: \").Append(Mi_Cursor.R";
_sb.Append("Recurrencias: ").Append(BA.NumberToString(mostCurrent._mi_cursor.getRowCount()+1)).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 348;BA.debugLine="sb.Append(CRLF)";
_sb.Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 349;BA.debugLine="sb.Append(\"Cobro Total: $\").Append(sal2).Appen";
_sb.Append("Cobro Total: $").Append(BA.NumberToString(_sal2)).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 //BA.debugLineNum = 350;BA.debugLine="Msgbox(sb.ToString, \"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_sb.ToString()),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 352;BA.debugLine="ToastMessageShow(\"Patente no encontrada\", True";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Patente no encontrada"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 353;BA.debugLine="Return";
if (true) return "";
 };
 };
 }else {
 //BA.debugLineNum = 357;BA.debugLine="ToastMessageShow(\"Ingrese Patente\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Ingrese Patente"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 358;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 360;BA.debugLine="End Sub";
return "";
}
public static String  _btningresar_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 278;BA.debugLine="Sub btnIngresar_Click";
 //BA.debugLineNum = 279;BA.debugLine="If txtName.Text <> \"\" Then";
if ((mostCurrent._txtname.getText()).equals("") == false) { 
 //BA.debugLineNum = 282;BA.debugLine="patente = txtName.Text.ToUpperCase";
mostCurrent._patente = mostCurrent._txtname.getText().toUpperCase();
 //BA.debugLineNum = 283;BA.debugLine="If Regex.IsMatch(\"^[A-Z]{4}[0-9]{2}|[A-Z]{2}[0-9";
if (anywheresoftware.b4a.keywords.Common.Regex.IsMatch("^[A-Z]{4}[0-9]{2}|[A-Z]{2}[0-9]{4}$",mostCurrent._patente)==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 284;BA.debugLine="ToastMessageShow(\"Patente no válida\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Patente no válida"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 285;BA.debugLine="Return";
if (true) return "";
 }else {
 //BA.debugLineNum = 287;BA.debugLine="autorizado = True";
_autorizado = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 288;BA.debugLine="Mi_Cursor=Mi_DB.ExecQuery(\"SELECT * FROM Mi_tab";
mostCurrent._mi_cursor.setObject((android.database.Cursor)(_mi_db.ExecQuery("SELECT * FROM Mi_tabla1 WHERE patente = '"+mostCurrent._patente+"' ORDER BY id ASC")));
 //BA.debugLineNum = 289;BA.debugLine="If Mi_Cursor.RowCount>0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 290;BA.debugLine="For i=0 To Mi_Cursor.RowCount-1";
{
final int step10 = 1;
final int limit10 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;(step10 > 0 && _i <= limit10) || (step10 < 0 && _i >= limit10) ;_i = ((int)(0 + _i + step10))  ) {
 //BA.debugLineNum = 291;BA.debugLine="Mi_Cursor.Position=i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 292;BA.debugLine="If Not (Mi_Cursor.GetString(\"salida\").Length";
if (anywheresoftware.b4a.keywords.Common.Not(mostCurrent._mi_cursor.GetString("salida").length()>0)) { 
 //BA.debugLineNum = 293;BA.debugLine="autorizado = False";
_autorizado = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 294;BA.debugLine="ToastMessageShow(\"Vehículo ya ingresado\", Tr";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Vehículo ya ingresado"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 295;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 };
 //BA.debugLineNum = 299;BA.debugLine="rep = Mi_Cursor.RowCount+1";
_rep = (int) (mostCurrent._mi_cursor.getRowCount()+1);
 //BA.debugLineNum = 300;BA.debugLine="If autorizado Then";
if (_autorizado) { 
 //BA.debugLineNum = 301;BA.debugLine="ingreso = DateTime.Date(dt2)";
mostCurrent._ingreso = anywheresoftware.b4a.keywords.Common.DateTime.Date(_dt2);
 //BA.debugLineNum = 302;BA.debugLine="salida = \"\"";
mostCurrent._salida = "";
 //BA.debugLineNum = 303;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 304;BA.debugLine="Try";
try { //BA.debugLineNum = 305;BA.debugLine="Mi_DB.ExecNonQuery2(\"INSERT INTO Mi_tabla1 (p";
_mi_db.ExecNonQuery2("INSERT INTO Mi_tabla1 (patente, ingreso, salida, repetido) VALUES (?,?,?,?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{mostCurrent._patente,mostCurrent._ingreso,mostCurrent._salida,BA.NumberToString(_rep)}));
 //BA.debugLineNum = 306;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 307;BA.debugLine="ToastMessageShow(\"Patente Ingresada Exitosame";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Patente Ingresada Exitosamente"),anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e29) {
			processBA.setLastException(e29); //BA.debugLineNum = 309;BA.debugLine="Log(\"catch: \" & LastException.Message)";
anywheresoftware.b4a.keywords.Common.Log("catch: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 311;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 //BA.debugLineNum = 312;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 313;BA.debugLine="txtName.Text=\"\"";
mostCurrent._txtname.setText(BA.ObjectToCharSequence(""));
 };
 };
 }else {
 //BA.debugLineNum = 317;BA.debugLine="ToastMessageShow(\"Complete toda la información\",";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Complete toda la información"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 318;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 320;BA.debugLine="End Sub";
return "";
}
public static String  _buttonborrar_click() throws Exception{
 //BA.debugLineNum = 254;BA.debugLine="Sub ButtonBorrar_Click";
 //BA.debugLineNum = 256;BA.debugLine="Mi_DB.Initialize(File.DirInternal,\"Autos.db\",True";
_mi_db.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"Autos.db",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 258;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 259;BA.debugLine="Try";
try { //BA.debugLineNum = 260;BA.debugLine="Mi_DB.ExecNonQuery(\"DROP TABLE Mi_Tabla1\")";
_mi_db.ExecNonQuery("DROP TABLE Mi_Tabla1");
 //BA.debugLineNum = 261;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 262;BA.debugLine="Msgbox (\"Tabla Eliminada\",\"Mensaje\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Tabla Eliminada"),BA.ObjectToCharSequence("Mensaje"),mostCurrent.activityBA);
 } 
       catch (Exception e8) {
			processBA.setLastException(e8); //BA.debugLineNum = 264;BA.debugLine="Log(\"ERROR de Creacion base de datos: \"&LastExce";
anywheresoftware.b4a.keywords.Common.Log("ERROR de Creacion base de datos: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 266;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 //BA.debugLineNum = 267;BA.debugLine="Mi_DB.BeginTransaction";
_mi_db.BeginTransaction();
 //BA.debugLineNum = 268;BA.debugLine="Try";
try { //BA.debugLineNum = 269;BA.debugLine="Mi_DB.ExecNonQuery(\"CREATE TABLE IF NOT EXISTS M";
_mi_db.ExecNonQuery("CREATE TABLE IF NOT EXISTS Mi_Tabla1 (id INTEGER PRIMARY KEY AUTOINCREMENT, patente TEXT, ingreso DATETIME, salida DATETIME, cobro INTEGER DEFAULT '0', repetido INTEGER DEFAULT '0')");
 //BA.debugLineNum = 270;BA.debugLine="Mi_DB.TransactionSuccessful";
_mi_db.TransactionSuccessful();
 //BA.debugLineNum = 271;BA.debugLine="ToastMessageShow(\"Crea la base\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Crea la base"),anywheresoftware.b4a.keywords.Common.True);
 } 
       catch (Exception e17) {
			processBA.setLastException(e17); //BA.debugLineNum = 273;BA.debugLine="Log(\"ERROR de Creacion base de datos: \"&LastExce";
anywheresoftware.b4a.keywords.Common.Log("ERROR de Creacion base de datos: "+anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 275;BA.debugLine="Mi_DB.EndTransaction";
_mi_db.EndTransaction();
 //BA.debugLineNum = 276;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 17;BA.debugLine="Dim TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim txtName, ExitText As EditText";
mostCurrent._txtname = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._exittext = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim imageview1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim ListView1 As ListView";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim Spinner1 As Spinner";
mostCurrent._spinner1 = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim Mi_Cursor As Cursor";
mostCurrent._mi_cursor = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim ButtonBorrar, btnIngresar, btnDone As Button";
mostCurrent._buttonborrar = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btningresar = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._btndone = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim patente, patente2, ingreso, salida, horaI, fe";
mostCurrent._patente = "";
mostCurrent._patente2 = "";
mostCurrent._ingreso = "";
mostCurrent._salida = "";
mostCurrent._horai = "";
mostCurrent._fechai = "";
mostCurrent._fechas = "";
mostCurrent._horas = "";
 //BA.debugLineNum = 25;BA.debugLine="Dim autorizado As Boolean";
_autorizado = false;
 //BA.debugLineNum = 26;BA.debugLine="Dim cobro As Int";
_cobro = 0;
 //BA.debugLineNum = 27;BA.debugLine="Dim rep As Int";
_rep = 0;
 //BA.debugLineNum = 28;BA.debugLine="DateTime.DateFormat = \"dd-MM-yyyy HH:mm:ss\"";
anywheresoftware.b4a.keywords.Common.DateTime.setDateFormat("dd-MM-yyyy HH:mm:ss");
 //BA.debugLineNum = 29;BA.debugLine="Dim dt, dt2 As Long";
_dt = 0L;
_dt2 = 0L;
 //BA.debugLineNum = 30;BA.debugLine="Dim dg, tg As String";
mostCurrent._dg = "";
mostCurrent._tg = "";
 //BA.debugLineNum = 31;BA.debugLine="Dim Label4, Label5 As Label";
mostCurrent._label4 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._label5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private Label7 As Label";
mostCurrent._label7 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private statText As EditText";
mostCurrent._stattext = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private btnDone2 As Button";
mostCurrent._btndone2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _listview1_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 200;BA.debugLine="Sub ListView1_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 202;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 12;BA.debugLine="Dim Mi_DB As SQL";
_mi_db = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 13;BA.debugLine="Dim tmr As Timer";
_tmr = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _spinner1_itemclick(int _position,Object _value) throws Exception{
int _i = 0;
 //BA.debugLineNum = 204;BA.debugLine="Sub Spinner1_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 205;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 206;BA.debugLine="Mi_Cursor=Mi_DB.ExecQuery(\"SELECT * FROM Mi_tabla";
mostCurrent._mi_cursor.setObject((android.database.Cursor)(_mi_db.ExecQuery("SELECT * FROM Mi_tabla1 ORDER BY id ASC")));
 //BA.debugLineNum = 207;BA.debugLine="If Value = \"Estacionados\" Then";
if ((_value).equals((Object)("Estacionados"))) { 
 //BA.debugLineNum = 208;BA.debugLine="If Mi_Cursor.RowCount>0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 210;BA.debugLine="For i=0 To Mi_Cursor.RowCount-1";
{
final int step5 = 1;
final int limit5 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;(step5 > 0 && _i <= limit5) || (step5 < 0 && _i >= limit5) ;_i = ((int)(0 + _i + step5))  ) {
 //BA.debugLineNum = 211;BA.debugLine="Mi_Cursor.Position=i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 212;BA.debugLine="fechaI = Mi_Cursor.GetString(\"ingreso\").SubStr";
mostCurrent._fechai = mostCurrent._mi_cursor.GetString("ingreso").substring((int) (0),(int) (10));
 //BA.debugLineNum = 213;BA.debugLine="horaI = Mi_Cursor.GetString(\"ingreso\").SubStri";
mostCurrent._horai = mostCurrent._mi_cursor.GetString("ingreso").substring((int) (11),(int) (16));
 //BA.debugLineNum = 214;BA.debugLine="If Mi_Cursor.GetString(\"salida\").Length = 0 Th";
if (mostCurrent._mi_cursor.GetString("salida").length()==0) { 
 //BA.debugLineNum = 215;BA.debugLine="ListView1.AddSingleLine(\"Patente: \" & Mi_Curs";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence("Patente: "+mostCurrent._mi_cursor.GetString("patente")+anywheresoftware.b4a.keywords.Common.CRLF+"Ingreso: "+mostCurrent._fechai+" a las: "+mostCurrent._horai+anywheresoftware.b4a.keywords.Common.CRLF+"Recurrencias: "+mostCurrent._mi_cursor.GetString("repetido")));
 };
 }
};
 };
 };
 //BA.debugLineNum = 220;BA.debugLine="If Value = \"Retirados\" Then";
if ((_value).equals((Object)("Retirados"))) { 
 //BA.debugLineNum = 221;BA.debugLine="If Mi_Cursor.RowCount>0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 223;BA.debugLine="For i=0 To Mi_Cursor.RowCount-1";
{
final int step17 = 1;
final int limit17 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;(step17 > 0 && _i <= limit17) || (step17 < 0 && _i >= limit17) ;_i = ((int)(0 + _i + step17))  ) {
 //BA.debugLineNum = 224;BA.debugLine="Mi_Cursor.Position=i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 225;BA.debugLine="fechaI = Mi_Cursor.GetString(\"ingreso\").SubStr";
mostCurrent._fechai = mostCurrent._mi_cursor.GetString("ingreso").substring((int) (0),(int) (10));
 //BA.debugLineNum = 226;BA.debugLine="horaI = Mi_Cursor.GetString(\"ingreso\").SubStri";
mostCurrent._horai = mostCurrent._mi_cursor.GetString("ingreso").substring((int) (11),(int) (16));
 //BA.debugLineNum = 227;BA.debugLine="If Mi_Cursor.GetString(\"salida\").Length > 0 Th";
if (mostCurrent._mi_cursor.GetString("salida").length()>0) { 
 //BA.debugLineNum = 228;BA.debugLine="fechaS = Mi_Cursor.GetString(\"salida\").SubStr";
mostCurrent._fechas = mostCurrent._mi_cursor.GetString("salida").substring((int) (0),(int) (10));
 //BA.debugLineNum = 229;BA.debugLine="horaS = Mi_Cursor.GetString(\"salida\").SubStri";
mostCurrent._horas = mostCurrent._mi_cursor.GetString("salida").substring((int) (11),(int) (16));
 //BA.debugLineNum = 230;BA.debugLine="ListView1.AddSingleLine(\"Patente: \" & Mi_Curs";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence("Patente: "+mostCurrent._mi_cursor.GetString("patente")+anywheresoftware.b4a.keywords.Common.CRLF+"Ingreso: "+mostCurrent._fechai+" a las: "+mostCurrent._horai+anywheresoftware.b4a.keywords.Common.CRLF+"Salida: "+mostCurrent._fechas+" a las: "+mostCurrent._horas+anywheresoftware.b4a.keywords.Common.CRLF+"Cobro: $"+mostCurrent._mi_cursor.GetString("cobro")+anywheresoftware.b4a.keywords.Common.CRLF+"Recurrencias: "+mostCurrent._mi_cursor.GetString("repetido")));
 };
 }
};
 };
 };
 //BA.debugLineNum = 235;BA.debugLine="If Value = \"Todos\" Then";
if ((_value).equals((Object)("Todos"))) { 
 //BA.debugLineNum = 236;BA.debugLine="If Mi_Cursor.RowCount>0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 238;BA.debugLine="For i=0 To Mi_Cursor.RowCount-1";
{
final int step31 = 1;
final int limit31 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;(step31 > 0 && _i <= limit31) || (step31 < 0 && _i >= limit31) ;_i = ((int)(0 + _i + step31))  ) {
 //BA.debugLineNum = 239;BA.debugLine="Mi_Cursor.Position=i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 240;BA.debugLine="fechaI = Mi_Cursor.GetString(\"ingreso\").SubStr";
mostCurrent._fechai = mostCurrent._mi_cursor.GetString("ingreso").substring((int) (0),(int) (10));
 //BA.debugLineNum = 241;BA.debugLine="horaI = Mi_Cursor.GetString(\"ingreso\").SubStri";
mostCurrent._horai = mostCurrent._mi_cursor.GetString("ingreso").substring((int) (11),(int) (16));
 //BA.debugLineNum = 242;BA.debugLine="fechaS = \"\"";
mostCurrent._fechas = "";
 //BA.debugLineNum = 243;BA.debugLine="horaS = \"\"";
mostCurrent._horas = "";
 //BA.debugLineNum = 244;BA.debugLine="If Mi_Cursor.GetString(\"salida\").Length > 0 Th";
if (mostCurrent._mi_cursor.GetString("salida").length()>0) { 
 //BA.debugLineNum = 245;BA.debugLine="fechaS = Mi_Cursor.GetString(\"salida\").SubStr";
mostCurrent._fechas = mostCurrent._mi_cursor.GetString("salida").substring((int) (0),(int) (10));
 //BA.debugLineNum = 246;BA.debugLine="horaS = Mi_Cursor.GetString(\"salida\").SubStri";
mostCurrent._horas = mostCurrent._mi_cursor.GetString("salida").substring((int) (11),(int) (16));
 };
 //BA.debugLineNum = 248;BA.debugLine="ListView1.AddSingleLine(\"Patente: \" & Mi_Curso";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence("Patente: "+mostCurrent._mi_cursor.GetString("patente")+anywheresoftware.b4a.keywords.Common.CRLF+"Ingreso: "+mostCurrent._fechai+" a las: "+mostCurrent._horai+anywheresoftware.b4a.keywords.Common.CRLF+"Salida: "+mostCurrent._fechas+" a las: "+mostCurrent._horas+anywheresoftware.b4a.keywords.Common.CRLF+"Cobro: $"+mostCurrent._mi_cursor.GetString("cobro")+anywheresoftware.b4a.keywords.Common.CRLF+"Recurrencias: "+mostCurrent._mi_cursor.GetString("repetido")));
 }
};
 };
 };
 //BA.debugLineNum = 252;BA.debugLine="End Sub";
return "";
}
public static String  _tabhost1_tabchanged() throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp4 = null;
int _i = 0;
 //BA.debugLineNum = 171;BA.debugLine="Sub TabHost1_TabChanged";
 //BA.debugLineNum = 172;BA.debugLine="Activity.Title = \"Current Tab = \" & TabHost1.Curr";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("Current Tab = "+BA.NumberToString(mostCurrent._tabhost1.getCurrentTab())));
 //BA.debugLineNum = 173;BA.debugLine="Dim bmp4 As Bitmap";
_bmp4 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 174;BA.debugLine="bmp4 = LoadBitmap(File.DirAssets, \"parking.png\")";
_bmp4 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"parking.png");
 //BA.debugLineNum = 175;BA.debugLine="imageview1.SetBackgroundImage(bmp4)";
mostCurrent._imageview1.SetBackgroundImageNew((android.graphics.Bitmap)(_bmp4.getObject()));
 //BA.debugLineNum = 176;BA.debugLine="If TabHost1.CurrentTab = 1 Then";
if (mostCurrent._tabhost1.getCurrentTab()==1) { 
 //BA.debugLineNum = 177;BA.debugLine="Spinner1.SelectedIndex = Spinner1.IndexOf(\"Selec";
mostCurrent._spinner1.setSelectedIndex(mostCurrent._spinner1.IndexOf("Seleccione Filtro"));
 //BA.debugLineNum = 178;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 179;BA.debugLine="Mi_Cursor=Mi_DB.ExecQuery(\"SELECT * FROM Mi_tabl";
mostCurrent._mi_cursor.setObject((android.database.Cursor)(_mi_db.ExecQuery("SELECT * FROM Mi_tabla1 ORDER BY id ASC")));
 //BA.debugLineNum = 180;BA.debugLine="If Mi_Cursor.RowCount>0 Then";
if (mostCurrent._mi_cursor.getRowCount()>0) { 
 //BA.debugLineNum = 182;BA.debugLine="For i=0 To Mi_Cursor.RowCount-1";
{
final int step10 = 1;
final int limit10 = (int) (mostCurrent._mi_cursor.getRowCount()-1);
_i = (int) (0) ;
for (;(step10 > 0 && _i <= limit10) || (step10 < 0 && _i >= limit10) ;_i = ((int)(0 + _i + step10))  ) {
 //BA.debugLineNum = 183;BA.debugLine="Mi_Cursor.Position=i";
mostCurrent._mi_cursor.setPosition(_i);
 //BA.debugLineNum = 184;BA.debugLine="fechaI = Mi_Cursor.GetString(\"ingreso\").SubStr";
mostCurrent._fechai = mostCurrent._mi_cursor.GetString("ingreso").substring((int) (0),(int) (10));
 //BA.debugLineNum = 185;BA.debugLine="horaI = Mi_Cursor.GetString(\"ingreso\").SubStri";
mostCurrent._horai = mostCurrent._mi_cursor.GetString("ingreso").substring((int) (11),(int) (16));
 //BA.debugLineNum = 186;BA.debugLine="fechaS = \"\"";
mostCurrent._fechas = "";
 //BA.debugLineNum = 187;BA.debugLine="horaS = \"\"";
mostCurrent._horas = "";
 //BA.debugLineNum = 188;BA.debugLine="If Mi_Cursor.GetString(\"salida\").Length > 0 Th";
if (mostCurrent._mi_cursor.GetString("salida").length()>0) { 
 //BA.debugLineNum = 189;BA.debugLine="fechaS = Mi_Cursor.GetString(\"salida\").SubStr";
mostCurrent._fechas = mostCurrent._mi_cursor.GetString("salida").substring((int) (0),(int) (10));
 //BA.debugLineNum = 190;BA.debugLine="horaS = Mi_Cursor.GetString(\"salida\").SubStri";
mostCurrent._horas = mostCurrent._mi_cursor.GetString("salida").substring((int) (11),(int) (16));
 };
 //BA.debugLineNum = 192;BA.debugLine="ListView1.AddSingleLine(\"Patente: \" & Mi_Curso";
mostCurrent._listview1.AddSingleLine(BA.ObjectToCharSequence("Patente: "+mostCurrent._mi_cursor.GetString("patente")+anywheresoftware.b4a.keywords.Common.CRLF+"Ingreso: "+mostCurrent._fechai+" a las: "+mostCurrent._horai+anywheresoftware.b4a.keywords.Common.CRLF+"Salida: "+mostCurrent._fechas+" a las: "+mostCurrent._horas+anywheresoftware.b4a.keywords.Common.CRLF+"Cobro: $"+mostCurrent._mi_cursor.GetString("cobro")+anywheresoftware.b4a.keywords.Common.CRLF+"Recurrencias: "+mostCurrent._mi_cursor.GetString("repetido")));
 }
};
 };
 };
 //BA.debugLineNum = 196;BA.debugLine="txtName.Text = \"\"";
mostCurrent._txtname.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 197;BA.debugLine="ExitText.Text = \"\"";
mostCurrent._exittext.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 198;BA.debugLine="End Sub";
return "";
}
public static String  _tmr_tick() throws Exception{
 //BA.debugLineNum = 94;BA.debugLine="Sub tmr_Tick";
 //BA.debugLineNum = 95;BA.debugLine="dt2 = DateTime.now";
_dt2 = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 97;BA.debugLine="dg = DateTime.Date(dt2).SubString2(0:10)";
mostCurrent._dg = anywheresoftware.b4a.keywords.Common.DateTime.Date(_dt2).substring((int) (0),(int) (10));
 //BA.debugLineNum = 98;BA.debugLine="tg = DateTime.Time(dt2).SubString2(0:5)";
mostCurrent._tg = anywheresoftware.b4a.keywords.Common.DateTime.Time(_dt2).substring((int) (0),(int) (5));
 //BA.debugLineNum = 99;BA.debugLine="Label4.Text = \"Fecha: \" & dg";
mostCurrent._label4.setText(BA.ObjectToCharSequence("Fecha: "+mostCurrent._dg));
 //BA.debugLineNum = 100;BA.debugLine="Label5.Text = \"Hora: \" & tg";
mostCurrent._label5.setText(BA.ObjectToCharSequence("Hora: "+mostCurrent._tg));
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}
}
