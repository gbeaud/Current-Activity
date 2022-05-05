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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by Wen on 16/02/2017.
 * Refactored by Ratul on 04/05/2022.
 */
@TargetApi(Build.VERSION_CODES.N)
public class AppShortcutsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SPHelper.hasAccess(this) && WatchingAccessibilityService.getInstance() == null)
            startService(new Intent().setClass(this, WatchingAccessibilityService.class));
        if (!MainActivity.usageStats(this) || !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_FROM_QS_TILE, true);
            startActivity(intent);
        }

        boolean isShow = !SPHelper.isShowWindow(this);
        SPHelper.setIsShowWindow(this, isShow);
        if (!isShow) {
            TasksWindow.dismiss(this);
            NotificationActionReceiver.showNotification(this, true);
        } else {
            String act1 = getClass().getName();
            
            TasksWindow.show(this, getPackageName(), act1);
            NotificationActionReceiver.showNotification(this, false);
            startService(new Intent(this, WatchingService.class));
        }
        sendBroadcast(new Intent(MainActivity.ACTION_STATE_CHANGED));
        finish();
    }
}
