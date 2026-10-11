/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.fogosparts.display

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.android.settingslib.widget.SettingsBasePreferenceFragment
import com.android.settingslib.widget.SliderPreference
import org.lineageos.fogosparts.PREF_KCAL_CONT
import org.lineageos.fogosparts.PREF_KCAL_ENABLED
import org.lineageos.fogosparts.PREF_KCAL_HUE
import org.lineageos.fogosparts.PREF_KCAL_SAT
import org.lineageos.fogosparts.PREF_KCAL_VAL
import org.lineageos.fogosparts.R

class PictureControlFragment : SettingsBasePreferenceFragment(),
    Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private lateinit var enablePref: SwitchPreferenceCompat
    private lateinit var huePref: SliderPreference
    private lateinit var satPref: SliderPreference
    private lateinit var valPref: SliderPreference
    private lateinit var contPref: SliderPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.picture_control_settings, rootKey)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        enablePref = findPreference("kcal_enable")!!
        huePref = findPreference("kcal_hue")!!
        satPref = findPreference("kcal_sat")!!
        valPref = findPreference("kcal_val")!!
        contPref = findPreference("kcal_cont")!!
        val resetPref: Preference = findPreference("kcal_reset")!!

        configureSlider(huePref, KcalUtils.HUE_UI_MIN, KcalUtils.HUE_UI_MAX)
        configureSlider(satPref, KcalUtils.SAT_UI_MIN, KcalUtils.SAT_UI_MAX)
        configureSlider(valPref, KcalUtils.RED_UI_MIN, KcalUtils.RED_UI_MAX)
        configureSlider(contPref, KcalUtils.RED_UI_MIN, KcalUtils.RED_UI_MAX)

        enablePref.isChecked = prefs.getBoolean(PREF_KCAL_ENABLED, true)
        huePref.value = prefs.getInt(PREF_KCAL_HUE, 0)
        satPref.value = prefs.getInt(PREF_KCAL_SAT, 0)
        valPref.value = prefs.getInt(PREF_KCAL_VAL, 0)
        contPref.value = prefs.getInt(PREF_KCAL_CONT, 0)

        enablePref.onPreferenceChangeListener = this
        huePref.onPreferenceChangeListener = this
        satPref.onPreferenceChangeListener = this
        valPref.onPreferenceChangeListener = this
        contPref.onPreferenceChangeListener = this
        resetPref.onPreferenceClickListener = this
    }

    private fun configureSlider(pref: SliderPreference, min: Int, max: Int) {
        pref.min = min
        pref.max = max
        pref.sliderIncrement = 1
        pref.setTickVisible(false)
        pref.setHapticFeedbackMode(SliderPreference.HAPTIC_FEEDBACK_MODE_ON_TICKS)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val ok: Boolean
        when (preference.key) {
            "kcal_enable" -> {
                val on = newValue as Boolean
                ok = KcalUtils.setEnabled(on)
                if (ok) prefs.edit().putBoolean(PREF_KCAL_ENABLED, on).apply()
            }
            "kcal_hue" -> {
                val v = newValue as Int
                ok = KcalUtils.setHue(v)
                if (ok) prefs.edit().putInt(PREF_KCAL_HUE, v).apply()
            }
            "kcal_sat" -> {
                val v = newValue as Int
                ok = KcalUtils.setSat(v)
                if (ok) prefs.edit().putInt(PREF_KCAL_SAT, v).apply()
            }
            "kcal_val" -> {
                val v = newValue as Int
                ok = KcalUtils.setVal(v)
                if (ok) prefs.edit().putInt(PREF_KCAL_VAL, v).apply()
            }
            "kcal_cont" -> {
                val v = newValue as Int
                ok = KcalUtils.setCont(v)
                if (ok) prefs.edit().putInt(PREF_KCAL_CONT, v).apply()
            }
            else -> return false
        }
        if (!ok) {
            Toast.makeText(requireContext(), R.string.kcal_write_failed, Toast.LENGTH_SHORT).show()
        }
        return ok
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (preference.key == "kcal_reset") {
            val ok = KcalUtils.resetToDefaults()
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putInt(PREF_KCAL_HUE, 0)
                .putInt(PREF_KCAL_SAT, 0)
                .putInt(PREF_KCAL_VAL, 0)
                .putInt(PREF_KCAL_CONT, 0)
                .apply()
            huePref.value = 0
            satPref.value = 0
            valPref.value = 0
            contPref.value = 0
            Toast.makeText(
                requireContext(),
                if (ok) R.string.kcal_reset_done else R.string.kcal_write_failed,
                Toast.LENGTH_SHORT
            ).show()
            return true
        }
        return false
    }
}
