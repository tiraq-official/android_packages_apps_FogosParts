/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.fogosparts

const val PREF_BYPASS_CHARGING_MODE = "bypass_charging_mode"

// Moto G34 (fogos) adaptive charging driver.
// Kernel semantics: "1" = charging enabled (normal), "0" = charging disabled (bypass).
const val BYPASS_CHARGING_NODE =
    "/sys/module/qpnp_adaptive_charge/parameters/charging_enabled"

// --- Picture control (KCAL) ---
// Exposed by the SDE display driver at /sys/devices/platform/kcal_ctrl.0/
// (kernel: techpack: display: sde: Introduce KCAL color control)
const val KCAL_DIR = "/sys/devices/platform/kcal_ctrl.0"

const val PREF_KCAL_ENABLED = "kcal_enabled"
const val PREF_KCAL_HUE = "kcal_hue"
const val PREF_KCAL_SAT = "kcal_sat"
const val PREF_KCAL_VAL = "kcal_val"
const val PREF_KCAL_CONT = "kcal_cont"
