package com.astoev.cave.survey.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;
import com.astoev.cave.survey.R;
import com.astoev.cave.survey.service.Workspace;
import com.astoev.cave.survey.util.ConfigUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: astoev
 * Date: 1/24/12
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class UIUtilities {

    public static void showNotification(int aResourceId) {
        showNotification(ConfigUtil.getContext(), aResourceId, null);
    }

    public static void showNotification(String aMessage) {
        showNotification(ConfigUtil.getContext(), aMessage, null);
    }

    public static void showNotification(final Activity aContext, final int aResourceId, final Object aParams) {
        aContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(aContext, aContext.getString(aResourceId, aParams), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public static void showNotification(final Activity aContext, final String aMessage, final Object aParams) {
        aContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(aContext, aMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public static void showRawMessage(Context aContext, String rawMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
        builder.setTitle("Debug");
        builder.setMessage(rawMessage);
        builder.show();
    }

    public static void showAlertDialog(Context aContext, int aTitleId, int aMemoId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
        builder.setTitle(aTitleId);
        builder.setMessage(aMemoId);
        builder.show();
    }

    public static void reportException(Context aContext, Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
        builder.setTitle(R.string.error);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        builder.setMessage(sw.toString());
        builder.show();
    }

    public static void showBusy(Context aContext) {
        ProgressDialog dialog = ProgressDialog.show(aContext, "",
                aContext.getString(R.string.busy), true);
        dialog.show();
    }

}
