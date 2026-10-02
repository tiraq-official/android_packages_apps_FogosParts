/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.fogosparts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.lineageos.fogosparts.power.BypassChargingUtils

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_BOOT_COMPLETED -> {
                BypassChargingUtils.restoreOnBoot(context)
            }
        }
    }
}
