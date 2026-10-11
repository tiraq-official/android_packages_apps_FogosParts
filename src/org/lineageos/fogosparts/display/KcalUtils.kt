/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.fogosparts.display

import android.content.Context
import androidx.preference.PreferenceManager
import org.lineageos.fogosparts.FileUtils
import org.lineageos.fogosparts.KCAL_DIR
import org.lineageos.fogosparts.PREF_KCAL_CONT
import org.lineageos.fogosparts.PREF_KCAL_ENABLED
import org.lineageos.fogosparts.PREF_KCAL_HUE
import org.lineageos.fogosparts.PREF_KCAL_SAT
import org.lineageos.fogosparts.PREF_KCAL_VAL

object KcalUtils {

    // UI slider ranges (0 = neutral / the panel's normal output)
    const val HUE_UI_MIN = -180
    const val HUE_UI_MAX = 180
    const val SAT_UI_MIN = -100
    const val SAT_UI_MAX = 100
    // Intensity and contrast can only be reduced: their HSIC hardware fields are
    // 8-bit, so the neutral value (255) is already the maximum. Anything above
    // 255 is masked off and wraps.
    const val RED_UI_MIN = -100
    const val RED_UI_MAX = 0

    // Kernel ranges / defaults (from the display driver)
    private const val HUE_KMAX = 1536        // 0..1536 == 0..360 degrees
    private const val SAT_KMIN = 128
    private const val SAT_KMAX = 383
    private const val ADJ_KDEFAULT = 255
    private const val VAL_KMAX = 255         // PA_VAL_MASK / PA_CONT_MASK are 8-bit

    private fun node(name: String) = "$KCAL_DIR/$name"

    fun isSupported(): Boolean = FileUtils.fileExists(node("kcal_enable"))

    fun isEnabled(): Boolean =
        FileUtils.readOneLine(node("kcal_enable"))?.trim() == "1"

    fun setEnabled(enabled: Boolean): Boolean =
        FileUtils.writeLine(node("kcal_enable"), if (enabled) "1" else "0")

    // Hue: UI degrees (-180..180) -> kernel 0..1536 (0..360 deg; negatives wrap)
    fun setHue(uiDegrees: Int): Boolean {
        val deg = ((uiDegrees % 360) + 360) % 360
        return FileUtils.writeLine(node("kcal_hue"), (deg * HUE_KMAX / 360).toString())
    }

    // Saturation: UI -100..100 -> kernel 128..383 (neutral 255)
    fun setSat(ui: Int): Boolean {
        val k = (ADJ_KDEFAULT + ui * (SAT_KMAX - ADJ_KDEFAULT) / SAT_UI_MAX)
            .coerceIn(SAT_KMIN, SAT_KMAX)
        return FileUtils.writeLine(node("kcal_sat"), k.toString())
    }

    // Intensity / contrast: UI -100..0 -> kernel 128..255 (neutral 255 = max)
    private fun reduceToKernel(ui: Int): Int =
        (ADJ_KDEFAULT + ui * (VAL_KMAX - ADJ_KDEFAULT) / -RED_UI_MIN)
            .coerceIn(SAT_KMIN, VAL_KMAX)

    fun setVal(ui: Int): Boolean =
        FileUtils.writeLine(node("kcal_val"), reduceToKernel(ui).toString())

    fun setCont(ui: Int): Boolean =
        FileUtils.writeLine(node("kcal_cont"), reduceToKernel(ui).toString())

    // Back to the driver defaults
    fun resetToDefaults(): Boolean {
        var ok = setHue(0)
        ok = setSat(0) && ok
        ok = setVal(0) && ok
        ok = setCont(0) && ok
        return ok
    }

    // Re-apply the saved state after boot (the driver resets to defaults on reboot)
    fun restoreOnBoot(context: Context) {
        if (!isSupported()) return
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        setEnabled(prefs.getBoolean(PREF_KCAL_ENABLED, true))
        setHue(prefs.getInt(PREF_KCAL_HUE, 0))
        setSat(prefs.getInt(PREF_KCAL_SAT, 0))
        setVal(prefs.getInt(PREF_KCAL_VAL, 0))
        setCont(prefs.getInt(PREF_KCAL_CONT, 0))
    }
}
