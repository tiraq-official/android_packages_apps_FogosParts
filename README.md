# FogosParts

A small LineageOS-style "parts" app for the Moto G34 (fogos). It provides:

- **Bypass charging** (Settings -> Battery): run the phone off the charger
  without charging the battery.
- **Picture control** (Settings -> Display): KCAL colour tuning (hue,
  saturation, intensity, contrast) with an on/off switch and reset.

## How it works

It writes a single sysfs node:

    /sys/module/qpnp_adaptive_charge/parameters/charging_enabled

Kernel semantics (verified on the device):

    1 -> normal charging
    0 -> charging disabled, phone runs off the charger (true bypass)

The value is persisted in SharedPreferences and re-applied on boot, because the
module parameter resets to 1 every reboot.

## Contents

    Android.bp                     build rules (app + privapp whitelist)
    parts.mk                       PRODUCT_PACKAGES + sepolicy dir
    AndroidManifest.xml            Settings entry (Battery category) + QS tile
    res/                           strings, preference screen, tile icon
    src/org/lineageos/fogosparts/  Kotlin sources
    sepolicy/private/system_app.te SELinux allow for the node write
    permissions/                   privapp whitelist

## Integration

See the companion document `fogos-bypass-app-integration.md`.
