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
