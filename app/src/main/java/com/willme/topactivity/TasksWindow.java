/*
 *   Copyright (C) 2022 Ratul Hasan
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.willme.topactivity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.graphics.Typeface;
import android.content.Intent;

/**
 * Created by Wen on 16/02/2017.
 * Refactored by Ratul on 04/05/2022.
 */
public class TasksWindow {
    private static WindowManager.LayoutParams sWindowParams;
    public static WindowManager sWindowManager;
    private static View sView;
    private static int xInitCord = 0;
    private static int yInitCord = 0;
    private static int xInitMargin = 0;
    private static int yInitMargin = 0;
    private static String text, text1;
    private static TextView packageName, className, title;
    private static ClipboardManager clipboard;
    private static boolean firstLaunch = true;

    public static void init(final Context context) {
        sWindowManager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
                
        sWindowParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, 
            WindowManager.LayoutParams.WRAP_CONTENT, 
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT);
        //sWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        sWindowParams.gravity = Gravity.CENTER;
        sWindowParams.width = (context.getDisplay().getWidth()/2)+300;
        sWindowParams.windowAnimations = android.R.style.Animation_Toast;
        
        sView = LayoutInflater.from(context).inflate(R.layout.window_tasks,
                null);
        
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        LinearLayout bg = (LinearLayout) sView.findViewById(R.id.bg);
        packageName = (TextView) sView.findViewById(R.id.text);
        className = (TextView) sView.findViewById(R.id.text1);
        ImageView closeBtn = (ImageView) sView.findViewById(R.id.closeBtn);
        title = (TextView) sView.findViewById(R.id.title);
        overrideFonts(context, sView, "fonts/google_sans_regular.ttf"); 
        title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/google_sans_bold.ttf"), 0);
        
        closeBtn.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    dismiss(context);
                    SPHelper.setIsShowWindow(context, false);
                    NotificationActionReceiver.cancelNotification(context);
                    context.sendBroadcast(new Intent(MainActivity.ACTION_STATE_CHANGED));
                }
            });

        packageName.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT < 29) {
                        ClipData clip = ClipData.newPlainText("", packageName.getText().toString());
                        clipboard.setPrimaryClip(clip);
                    } else {
                        context.startActivity(new Intent(context, BackgroundActivity.class).putExtra(BackgroundActivity.STRING_COPY, packageName.getText().toString()));
                    }
                    Toast.makeText(context, "Package name copied", 0).show();
                }
            });

        className.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT < 29) {
                        ClipData clip = ClipData.newPlainText("", className.getText().toString());
                        clipboard.setPrimaryClip(clip);
                    } else {
                        context.startActivity(new Intent(context, BackgroundActivity.class).putExtra(BackgroundActivity.STRING_COPY, className.getText().toString()));
                    }
                    Toast.makeText(context, "Class name copied", 0).show();
                }
            });

        sView.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View view, MotionEvent event) {
                    WindowManager.LayoutParams layoutParams = sWindowParams;

                    int xCord = (int) event.getRawX();
                    int yCord = (int) event.getRawY();
                    int xCordDestination;
                    int yCordDestination;

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN : 
                            xInitCord = xCord;
                            yInitCord = yCord;
                            xInitMargin = layoutParams.x;
                            yInitMargin = layoutParams.y;
                            break;
                        case MotionEvent.ACTION_MOVE : 
                            int xDiffMove = xCord - xInitCord;
                            int yDiffMove = yCord - yInitCord;
                            xCordDestination = xInitMargin + xDiffMove;
                            yCordDestination = yInitMargin + yDiffMove;

                            layoutParams.x = xCordDestination;
                            layoutParams.y = yCordDestination;
                            sWindowManager.updateViewLayout(view, layoutParams);
                            break;
                        default :
                            return true;
                    }
                    return true;
                }
            });
    }
    
    public static void overrideFonts(final Context context, final View v, final String fontName) {
        try {
            Typeface typeace = Typeface.createFromAsset(context.getAssets(), fontName);
            if ((v instanceof ViewGroup)) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0;
                     i < vg.getChildCount();
                i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child, fontName);
                }
            } else {
                if ((v instanceof TextView)) {
                    ((TextView) v).setTypeface(typeace);
                } else {
                    if ((v instanceof EditText)) {
                        ((EditText) v).setTypeface(typeace);
                    } else {
                        if ((v instanceof Button)) {
                            ((Button) v).setTypeface(typeace);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), 1).show();
        }

    }
	
    public static void show(Context context, String pkg, String clas) {
        if (sWindowManager == null) {
            init(context);
        }
        text = pkg;
        text1 = clas;
        
        packageName.setText(text);
        className.setText(text1);
        
        try {
            sWindowManager.updateViewLayout(sView, sWindowParams);
        } catch(Exception e) {
            if (SPHelper.isShowWindow(context))
                sWindowManager.addView(sView, sWindowParams);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            QuickSettingTileService.updateTile(context);
    }

    public static void dismiss(Context context) {
        try {
            sWindowManager.removeView(sView);
        } catch (Exception e) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            QuickSettingTileService.updateTile(context);
    }
}
