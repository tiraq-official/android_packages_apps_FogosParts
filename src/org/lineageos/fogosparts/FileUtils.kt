/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.fogosparts

import android.util.Log
import java.io.File

object FileUtils {
    private const val TAG = "FogosParts/FileUtils"

    fun readOneLine(path: String): String? {
        return runCatching {
            File(path).useLines { it.firstOrNull() }
        }.onFailure { e ->
            Log.e(TAG, "Could not read from $path", e)
        }.getOrNull()
    }

    fun writeLine(path: String, value: String): Boolean {
        return runCatching {
            File(path).writeText(value)
            true
        }.onFailure { e ->
            Log.e(TAG, "Could not write to $path", e)
        }.getOrDefault(false)
    }

    fun fileExists(path: String): Boolean = File(path).exists()
}
