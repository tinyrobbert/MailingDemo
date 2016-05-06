package horizon.pb.com.mailing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private TextView tx_version;
    private Button mButtonUpdate;
    private ProgressDialog mProgressDialog;
    private static final int requestSuccess = 0x00000001;
    private Object statusBarManagerService;
    private Method methodDisable;
    private final static int SCREENPINNING_MODE_FLAG = 57081856;
    private final static int NORMAL_MODE_FLAG = 0;
    private final static int SCREENPINNING_MODE_WITH_BACK_FLAG = 52887552;
    private final static int DATE_SETTINGS_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonUpdate = (Button) findViewById(R.id.btn_update);
        mButtonUpdate.setOnClickListener(onClickListener);
        try
        {
            Class clsStatusBarManager = Class.forName("android.app.StatusBarManager");
            Context appContext = getApplicationContext();
            if (appContext != null) {
                statusBarManagerService = appContext.getSystemService("statusbar");
                methodDisable = clsStatusBarManager.getMethod("disable", int.class);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
//            reflectionError(e);
        } catch (NoSuchMethodException e) {
//            reflectionError(e);
            e.printStackTrace();
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    public static String getVersionCode(Context context) {
        return getPackageInfo(context).versionName;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_pin) {
            // Enable BACK button to return from Settings
            disableStatusBar(SCREENPINNING_MODE_FLAG);
            return true;
        }
        if (id == R.id.action_unpin) {
            // Enable BACK button to return from Settings
            disableStatusBar(NORMAL_MODE_FLAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case requestSuccess:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    MessageDialog();
            }
        }
    };


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_update:
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(MainActivity.this);
                        mProgressDialog.setMessage(getText(R.string.progressDialog_message));
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                        Message msg = new Message();
                        msg.what = requestSuccess;
                        mHandler.sendMessageDelayed(msg, 3000);
                    }
            }
        }
    };


    private void MessageDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(getString(R.string.dialog_title))
                .setMessage("Current App Version is:v" + getVersionCode(getApplicationContext()) + ",The Lastest App Version is v2.0,need to Download new app")
                .setNegativeButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }



    private void disableStatusBar(int flag) {
        try {
            methodDisable.invoke(statusBarManagerService, flag);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
