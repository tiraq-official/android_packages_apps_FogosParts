/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.fogosparts.power

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import org.lineageos.fogosparts.R

class BypassChargingTileService : TileService() {

    private fun updateUI(mode: Int) {
        qsTile?.let { tile ->
            when (mode) {
                BypassChargingUtils.MODE_BYPASS -> {
                    tile.state = Tile.STATE_ACTIVE
                    tile.subtitle = getString(R.string.bypass_charging_mode_1_short)
                }
                else -> {
                    tile.state = Tile.STATE_INACTIVE
                    tile.subtitle = getString(R.string.bypass_charging_mode_0_short)
                }
            }
            tile.updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        updateUI(BypassChargingUtils.getMode(this))
    }

    override fun onClick() {
        super.onClick()
        val currentMode = BypassChargingUtils.getMode(this)
        val nextMode = if (currentMode == BypassChargingUtils.MODE_BYPASS) {
            BypassChargingUtils.MODE_NORMAL
        } else {
            BypassChargingUtils.MODE_BYPASS
        }
        BypassChargingUtils.setMode(this, nextMode)
        updateUI(nextMode)
    }
}
