#
# Copyright (C) 2026 The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

PRODUCT_PACKAGES += \
    FogosParts \
    privapp_whitelist_org.lineageos.fogosparts.xml

# SEPolicy for the sysfs node writes
BOARD_VENDOR_SEPOLICY_DIRS += packages/apps/FogosParts/sepolicy/vendor
