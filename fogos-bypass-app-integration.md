# FogosParts — Bypass charging app for Moto G34 (fogos)

Integration guide for the `FogosParts.zip` source drop.

## What this is

A standalone ROM app in the same shape as `packages_apps_OnePlusParts` — a small
privileged "parts" app that exposes a **Bypass charging** screen in
Settings → Battery plus a Quick Settings tile, and does one job: write the
adaptive-charging node directly.

The OnePlus app writes `/sys/class/oplus_chg/battery/mmi_charging_enable`
(their kernel: `1` = normal, `0` = bypass). Your device's node has **identical
semantics**:

```
/sys/module/qpnp_adaptive_charge/parameters/charging_enabled
    1 -> normal charging
    0 -> charging disabled; phone runs off the charger (true bypass)
```

You already verified this by hand (`echo 0` → battery current ~0, status
`Not charging`, phone still powered). This app just makes that a switch.

## Why it fits cleanly

You already did the two hard parts in your earlier commits:

1. The node is a real, writable target (the health-HAL path fix).
2. `sepolicy/vendor/genfs_contexts` labels
   `/sys/module/qpnp_adaptive_charge/parameters` as **`sysfs_battery_supply`**.

Because of (2), the app's SELinux rule is a one-liner that reuses the existing
label — no new `genfscon`, no new file type, and no conflict with the health
HAL (which is allowed to write the same label in LineageOS's shared qcom
sepolicy). Motorola's `init.mmi.chipset.rc` already does
`chown system system` + `chmod 0660` on the node every boot, and this app runs
as `android.uid.system`, so DAC is satisfied too.

## 1. Place the sources

Pick one:

**A. As its own repo** (matches OnePlusParts). Push `FogosParts/` to e.g.
`tiraq-official/packages_apps_FogosParts`, add it to your local manifest
(`packages/apps/FogosParts`), then:

```make
# in android_device_motorola_fogos/device.mk (or sm6375-common/common.mk)
include packages/apps/FogosParts/parts.mk
```

**B. Inside the device tree** (no manifest change). Copy the folder to
`android_device_motorola_fogos/FogosParts/`, then in `device.mk`:

```make
include $(LOCAL_PATH)/FogosParts/parts.mk
```

…and in `parts.mk` fix the sepolicy path to
`$(LOCAL_PATH)/FogosParts/sepolicy/private`.

## 2. parts.mk does the rest

It adds the app and its privapp whitelist to `PRODUCT_PACKAGES`, and points
`SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS` at `sepolicy/private/`, which contains:

```
allow system_app sysfs_battery_supply:dir search;
allow system_app sysfs_battery_supply:file rw_file_perms;
```

## 3. Build

Build as usual. Nothing else to wire — the Settings entry registers itself via
`com.android.settings.action.IA_SETTINGS` with
`com.android.settings.category.ia.battery`, so it appears under
Settings → Battery → **Bypass charging**.

## What you get

- **Settings → Battery → Bypass charging**: a Normal (0) / Bypass (1) slider
  with descriptions.
- **Quick Settings tile**: tap to toggle normal ↔ bypass.
- **Boot persistence**: the module parameter resets to 1 on reboot, so a
  `BootCompletedReceiver` re-applies your choice.
- The app reads the live kernel value, so state stays truthful even if
  something else wrote the node.

## Verify

```sh
# after enabling Bypass in the UI (charger connected)
cat /sys/module/qpnp_adaptive_charge/parameters/charging_enabled   # -> 0
cat /sys/class/power_supply/battery/current_now                    # ~0
cat /sys/class/power_supply/battery/status                         # Not charging
dmesg | grep adap_chg | tail -2                                    # "charging_enabled set to 0"
```

Toggle back to Normal → `status` returns to `Charging`, current climbs.

## Caveats

- Only meaningful while plugged in; writing it unplugged is harmless (it just
  takes effect on the next plug-in).
- "Never touches the battery" is true in normal use. Under a load that exceeds
  the charger's ~18 W, the PMIC briefly supplements from the cell. All battery
  safety (temperature, voltage, critical-low) stays active.
- Don't run this **and** the stock "Charging control → Limit charging" feature
  at the same time: both write this node, so they'll fight. Use one or the
  other. (Limit mode is the automatic version; this app is the manual switch.)

## Possible extensions (not included)

- **Gaming mode**: the OnePlus app's mode 2 additionally clears CPU/GPU thermal
  throttling. On fogos that means stopping `thermal-engine` — the same daemon
  that derates charging. That's a real heat/safety trade-off and needs root/init
  work, so it's deliberately left out. Say the word if you want it and we'll do
  it properly.
- **Smart bypass with upper/lower limit**: the driver also exposes
  `upper_limit` / `lower_limit` parameters (charge to N %, then hold, resume
  below M %). That could become a "hold at 80 %" slider.
