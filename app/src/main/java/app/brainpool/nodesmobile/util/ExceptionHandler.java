package app.brainpool.nodesmobile.util;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import app.brainpool.nodesmobile.MainActivity;
import app.brainpool.nodesmobile.R;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Activity myContext;

    public ExceptionHandler(Activity context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append(myContext.getString(R.string.cause_of_error));
        errorReport.append(stackTrace.toString());
        Log.e(TAG, errorReport.toString());
        Intent intent = new Intent(myContext, MainActivity.class);
        intent.putExtra(myContext.getString(R.string.error), errorReport.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myContext.startActivity(intent);
        myContext.overridePendingTransition(0, 0);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}