/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.fogosparts.power

import android.content.Context
import androidx.preference.PreferenceManager
import org.lineageos.fogosparts.BYPASS_CHARGING_NODE
import org.lineageos.fogosparts.FileUtils
import org.lineageos.fogosparts.PREF_BYPASS_CHARGING_MODE

object BypassChargingUtils {

    const val MODE_NORMAL = 0
    const val MODE_BYPASS = 1

    fun isSupported(): Boolean = FileUtils.fileExists(BYPASS_CHARGING_NODE)

    fun getMode(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(PREF_BYPASS_CHARGING_MODE, MODE_NORMAL)
    }

    // Reads the live kernel state: "1" = normal, "0" = bypass.
    fun getHardwareMode(): Int {
        val value = FileUtils.readOneLine(BYPASS_CHARGING_NODE)?.trim() ?: return MODE_NORMAL
        return if (value == "1") MODE_NORMAL else MODE_BYPASS
    }

    // UI mode -> kernel value
    private fun kernelValueFor(mode: Int): String =
        if (mode == MODE_NORMAL) "1" else "0"

    fun setMode(context: Context, mode: Int): Boolean {
        val targetMode = if (mode == MODE_BYPASS) MODE_BYPASS else MODE_NORMAL

        val success = FileUtils.writeLine(BYPASS_CHARGING_NODE, kernelValueFor(targetMode))
        if (success) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_BYPASS_CHARGING_MODE, targetMode)
                .apply()
        }
        return success
    }

    // The module parameter resets to 1 (normal) on every boot, so re-apply the
    // user's choice after boot.
    fun restoreOnBoot(context: Context) {
        if (!isSupported()) return
        val mode = getMode(context)
        if (mode == MODE_BYPASS) {
            FileUtils.writeLine(BYPASS_CHARGING_NODE, kernelValueFor(mode))
        }
    }
}
