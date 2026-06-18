# The LOKLiK iCraft Runs GPLv3 Software — And the Vendor Won't Release the Source

**A firmware provenance & GPLv3 compliance report**

- **Subject device:** LOKLiK iCraft desktop cutting machine
- **Vendor:** LOKLiK Inc. / HTVRONT
- **Upstream software:** `Grbl_Esp32` (GPLv3), by bdring (Barton Dring) — a port of Grbl, originally by Sungeon "Sonny" Jeon
- **Author of report:** Rev — JMS CNC (full legal name available to the vendor / FSF / SFC on request)
- **Contact:** therealrevjmoney@gmail.com
- **Date:** June 2026
- **Status:** Formal GPLv3 §6 source request sent to vendor; report shared with the FSF and the Software Freedom Conservancy.

---

## Executive summary

The LOKLiK iCraft is sold and marketed as a closed, app-driven cutting machine. Its
controller firmware is in fact a **modified build of `Grbl_Esp32`, an open-source CNC
firmware licensed under the GNU General Public License, version 3 (GPLv3).**

This is not an inference from behavior — the firmware **identifies itself as Grbl_Esp32**,
by name and version, in three independent places:

1. On the serial console at power-up: `[MSG:Grbl_ESP32 Ver 1.3a Date 20211103]`
2. In printable strings inside every firmware binary the vendor distributes.
3. By a **character-for-character match** to the upstream Grbl_Esp32 version constants.

LOKLiK distributes this GPLv3 software to customers in binary form, inside a product, via
a login-gated application — **with no source code, no written offer of source, and no
attribution to Grbl_Esp32 or its authors anywhere in the product or marketing.** The
firmware also contains a substantial vendor-added modification layer (an `SJConfig`
settings subsystem) that is part of the work and is therefore part of the Corresponding
Source the GPLv3 requires them to provide.

This report documents the evidence and the methodology so that any third party — the
copyright holders, the FSF, the Software Freedom Conservancy, or another owner — can
reproduce every finding independently.

---

## 1. The device

| Property | Value |
|---|---|
| Product | LOKLiK iCraft cutting machine |
| Vendor | LOKLiK Inc. / HTVRONT |
| Controller board | Silkscreen `MAIN-CUT-G2 V1.0`, dated `2024-07` (photo: `evidence/board.jpg`) |
| MCU | Espressif **ESP32-D0WD-V3**, 8 MB SPI flash |
| Flash encryption | **None** (firmware is plaintext and readable) |
| Motion | 3 axes (X/Y/Z), Trinamic **TMC2209** stepper drivers over UART |
| Vendor tooling | LOKLiK IdeaStudio (login-gated desktop app) + a standalone "FlashTool" updater |

The machine was acquired by the author via retail purchase (Amazon, March 2026; order
details available to the vendor and to the SFC on request). The author is therefore a
lawful recipient of the distributed binary and has standing to request source under GPLv3.

---

## 2. Finding 1 — The firmware is Grbl_Esp32 1.3a (GPLv3)

### 2.1 Primary evidence: the boot banner

Connected to the controller's serial port, the firmware announces itself on every boot:

```
[MSG:Grbl_ESP32 Ver 1.3a Date 20211103]
[MSG:Compiled with ESP32 SDK:v3.2.3-14-gd3e562907]
[MSG:Using machine:LOKLiK_iCraft]
[MSG:Axis count 3]
[MSG:X  Axis motor Trinamic TMC2209 Step:GPIO(19) Dir:GPIO(18) UART1 Rx:GPIO(16) Tx:GPIO(17) Addr:0 ...]
[MSG:Y  Axis motor Trinamic TMC2209 Step:GPIO(2)  Dir:GPIO(4)  UART1 ... Addr:1 ...]
[MSG:Z  Axis motor Trinamic TMC2209 Step:GPIO(33) Dir:GPIO(32) UART1 ... Addr:2 ...]
[MSG:X  Axis limit switch on pin GPIO(25)]
[MSG:Z  Axis limit switch on pin GPIO(39)]
Grbl 1.3a ['$' for help]
[MSG:Ready]
```

(Full serial capture: `evidence/bootlog.png`.) `Grbl_ESP32` is the name of the upstream project. `1.3a` is its version. `20211103` is its
build date. `Using machine:` is Grbl_Esp32's own machine-definition mechanism.

### 2.2 Corroborating evidence: strings inside the binaries

Every firmware image the vendor distributes (see §3) contains the project's own identifying
strings. Representative examples extracted directly from the binary:

```
Grbl_ESP32 Ver %s Date %s
Grbl %s ['$' for help]
[VER:%s.%s:%s]
[OPT:
Grbl_Esp32/src/I2SOut.cpp          <- upstream source path baked into the binary
) # FW target:grbl-embedded  # FW HW:
'$H'|'$X' to unlock
```

The string `Grbl_Esp32/src/I2SOut.cpp` is a path from the upstream project's own source
tree, embedded by the compiler — direct evidence the binary was built from Grbl_Esp32
source. The build also leaks ESP-IDF / Arduino-ESP32 toolchain paths of the form
`/home/runner/work/esp32-arduino-lib-builder/...`, identifying the standard ESP32 build
environment used.

The firmware's entire configuration system uses Grbl_Esp32's setting-key vocabulary —
e.g. `Homing/Feed`, `Homing/Pulloff`, `Spindle/Delay/SpinUp`, `GCode/LaserMode`,
`Stepper/IdleTime`, `X/StepsPerMm`. These match the upstream `SettingsDefinitions.cpp`
exactly.

### 2.3 Decisive evidence: exact version-constant match

The upstream Grbl_Esp32 source (`Grbl_Esp32/src/Grbl.h`) defines:

```c
const char* const GRBL_VERSION       = "1.3a";
const char* const GRBL_VERSION_BUILD = "20211103";
```

The LOKLiK device reports `Ver 1.3a Date 20211103`. The version **and** the build-date
string are identical, down to the character. This establishes that the LOKLiK firmware was
compiled from the upstream Grbl_Esp32 source at that exact version — not an independent
or coincidental implementation.

### 2.4 The license

Every Grbl_Esp32 source file carries the standard GPL header:

> "Grbl is free software: you can redistribute it and/or modify it under the terms of the
> GNU General Public License as published by the Free Software Foundation, either version 3
> of the License, or (at your option) any later version."

Source: <https://github.com/bdring/Grbl_Esp32> (`Grbl_Esp32/src/Grbl.h`).

**Conclusion of Finding 1:** the LOKLiK iCraft firmware is a build of GPLv3-licensed
Grbl_Esp32 1.3a.

---

## 3. Finding 2 — It is a *modified* build, distributed in eight versions

LOKLiK's standalone FlashTool downloads and stores **eight** firmware versions for this
machine (internally labelled `LOKLIK_CRAFTER_2`, versions v01–v08). All eight:

- identify as `Grbl_ESP32 Ver 1.3a`,
- target the machine definition `LOKLiK_iCraft` / `LK_iCraft`,
- and contain a vendor-added settings namespace, **`SJConfig`**, that does **not** exist
  anywhere in upstream Grbl_Esp32.

The `SJ` prefix matches the vendor's software namespace `com.sjtech`, used in their
desktop application's data directories — tying the modification to the vendor's own
toolchain.

### 3.1 The vendor's modification layer: `SJConfig`

These vendor-added settings keys were extracted from the binaries. None appear in the
upstream `SettingsDefinitions.cpp`:

```
SJConfig/SN                  SJConfig/ProductName        SJConfig/BlueName
SJConfig/BTRATE              SJConfig/MateDist           SJConfig/MateLoadSpeed
SJConfig/PointSet            SJConfig/XPulloffDist
SJConfig/AxisOffset_X/Y/Z    SJConfig/AxisCompens_X/Y
SJConfig/LeftPenOffset_X/Y   SJConfig/RightPenOffset_X/Y SJConfig/SPSOffset_X/Y
```

These reveal vendor-specific functionality grafted onto the GPL codebase: a stored serial
number and product name, Bluetooth naming/rate, dual-tool ("left/right pen") offsets,
material-load distance and speed, and per-axis offset and compensation.

### 3.2 The modification is actively maintained and growing

Versions v07/v08 expand the `SJConfig` axis-compensation feature from a single value per
axis into an **11-point compensation table** per axis (`AxisCompens_X0` … `AxisCompens_X10`,
and the same for Y), growing the `SJConfig` key set from 18 keys (v01–v06) to 62 keys
(v07–v08). This is ongoing, deliberate development of a derivative work — exactly the kind
of modification the GPL requires be released as source.

### 3.3 A note on the v06→v07 size change

v01–v06 are ~1.56 MB; v07–v08 drop to ~1.22 MB. A raw byte comparison of the two builds is
uninformative (different builds relocate essentially all code). The meaningful, named-symbol
difference is the **expansion** of the `SJConfig` calibration subsystem described above — the
firmware did not lose vendor features; it gained them.

---

## 4. Finding 3 — Distribution without source, attribution, or offer

- **Binary-only distribution.** Firmware reaches customers exclusively through the
  login-gated LOKLiK IdeaStudio application and the standalone FlashTool updater. No source
  accompanies the binaries.
- **No written offer.** Neither application, nor the product, nor the vendor's website
  provides the Corresponding Source or a written offer to supply it, as GPLv3 §6 requires
  for binary distribution.
- **No attribution.** The product packaging, the marketing, and the application make no
  mention of Grbl, Grbl_Esp32, or the GPL. The desktop application *does* ship license
  notices for various other open-source components it uses — demonstrating the vendor is
  aware of, and capable of, open-source attribution — yet the GPLv3 firmware that is the
  machine's actual brain is absent from those notices.

The combined effect is that a customer cannot learn the firmware is free software, cannot
obtain its source, and is steered to use only the vendor's application.

---

## 5. The obligation under GPLv3

By conveying Grbl_Esp32 (a GPLv3 work) to customers in binary form inside a product, LOKLiK
is bound by the GPLv3, including:

- **§6 (Conveying Non-Source Forms):** must provide the **Complete Corresponding Source** for
  the exact binary conveyed — including the modified Grbl_Esp32 sources (the `SJConfig` layer
  and the `LOKLiK_iCraft` machine definition), and the **scripts and configuration** needed to
  build and install a working equivalent on the device.
- **§5 (Modified Source):** the modified work must itself be licensed GPLv3 and carry
  appropriate notices.
- **§4 / §10:** license and warranty-disclaimer notices must be preserved and conveyed;
  recipients automatically receive a license from the original licensors.

The Corresponding Source obligation runs to **each recipient** of the binary. The author,
as a lawful purchaser, is entitled to it.

---

## 6. The ask

LOKLiK should come into compliance by releasing the **Complete Corresponding Source for the
firmware of the LOKLiK iCraft ecosystem** — all distributed versions (v01–v08 and any
successors), including the modified Grbl_Esp32 tree, the `LOKLiK_iCraft` machine definition,
the `SJConfig` subsystem, and the build/flash scripts and configuration — under the GPLv3,
with proper attribution to the upstream authors.

The preferred outcome is cooperative and simple: **publish the source.** It costs the vendor
nothing they are entitled to keep, and it is what the license they chose to build on requires.

---

## 7. Methodology — how to reproduce every finding

All findings are independently reproducible from a retail device, using only free tools.

1. **Read the firmware** from the ESP32 (no flash encryption is in use):
   ```
   esptool read_flash 0 0x800000 loklik_fw_backup.bin
   ```
   Or obtain the per-version images the vendor's FlashTool downloads to its `resources`
   directory.
2. **Confirm the image identity and segment layout:**
   ```
   esptool --chip esp32 image-info firmware_v06.bin
   ```
3. **Extract identifying strings** (`strings`, or any binary string extractor) and observe
   the `Grbl_ESP32 Ver`, `LOKLiK_iCraft`, `SJConfig/*`, and `Grbl_Esp32/src/...` strings.
4. **Compare to upstream** at <https://github.com/bdring/Grbl_Esp32> — the version constants
   in `Grbl.h` and the setting names in `SettingsDefinitions.cpp`.
5. **Read the code** in Ghidra: import as Raw Binary with language `Xtensa:LE:32:default`,
   lay out the six esptool segments at their load addresses (DROM `0x3f400020`,
   DRAM `0x3ffbdb60`, IRAM `0x40080000`/`0x40080400`/`0x4008b9c4`, IROM `0x400d0018`),
   then auto-analyze. The boot-banner and `SJConfig` strings cross-reference directly into
   the vendor's modified functions.

---

## 8. Evidence appendix

### 8.1 Integrity hashes (SHA-256) of the eight distributed firmware images

```
2091abe2949bc5a19ba1baebda5dcd7b1173fcbd1af43fe0575b578b2905ca1c  firmware_v01.bin  (1,562,832 B)
218426d65c6057940f03fd80e165c11d5268fb5c0a94bd209e709a04cef6864d  firmware_v02.bin  (1,563,824 B)
8648ca52a90fe5c31ac668e3a0f4a3642f40d28c5fea3204c91430a5f801cd53  firmware_v03.bin  (1,565,200 B)
8bd858845cdbf8b0d3d6373a674ac2cf4a1a5bcb512af781d88766c10e97ea83  firmware_v04.bin  (1,565,232 B)
57d3e4dafdb08fa4652a5e72ea6b4b0575bd0d973527701107d9769a093c07a9  firmware_v05.bin  (1,565,664 B)
7a30fc95315b5c442ef9aaf7a37efe65ebf4b058f0d96e0d80512e279bbac23c  firmware_v06.bin  (1,565,728 B)
0380548b95299aa98e24cf02308924a5d579356a0aa7114c550524db76e97ff1  firmware_v07.bin  (1,219,808 B)
9f8d03d1ff7cbee238b5a5ba03f901ddc7a4617fd586e400dbe14eada6cec098  firmware_v08.bin  (1,219,744 B)
```

Full-flash readback of the author's own device:
```
da647a9ab5048efef5a252b4060fe4911f79f65180f4f6c1a1df948a43920e73  loklik_fw_backup.bin  (8,388,608 B)
```

### 8.2 esptool image layout (firmware_v06.bin)

```
ESP32 image, version 1, entry 0x400837b0, 6 segments, Chip ID 0 (ESP32)
Seg 0  0x3f400020  0x40d90  DROM
Seg 1  0x3ffbdb60  0x03884  DRAM
Seg 2  0x40080000  0x00400  IRAM
Seg 3  0x40080400  0x0b5c4  IRAM
Seg 4  0x400d0018  0x11f3a8 IROM  (bulk of code)
Seg 5  0x4008b9c4  0x0f034  IRAM
Validation hash: valid
```

### 8.3 Per-version identification summary

| Version | Size (B) | Grbl ver | Machine | `SJConfig` keys |
|---|---|---|---|---|
| v01 | 1,562,832 | 1.3a | LOKLiK_iCraft | 18 |
| v02 | 1,563,824 | 1.3a | LOKLiK_iCraft | 18 |
| v03 | 1,565,200 | 1.3a | LOKLiK_iCraft | 18 |
| v04 | 1,565,232 | 1.3a | LOKLiK_iCraft | 18 |
| v05 | 1,565,664 | 1.3a | LOKLiK_iCraft | 18 |
| v06 | 1,565,728 | 1.3a | LOKLiK_iCraft | 18 |
| v07 | 1,219,808 | 1.3a | LOKLiK_iCraft | 62 |
| v08 | 1,219,744 | 1.3a | LOKLiK_iCraft | 62 |

### 8.4 Photographic evidence

- `evidence/board.jpg` — the controller board (silkscreen `MAIN-CUT-G2 V1.0`, `2024-07`), showing the
  Espressif ESP32 module and the three Trinamic TMC2209 stepper drivers.
- `evidence/bootlog.png` — full serial-console capture of the power-up boot log, including the
  `Grbl_ESP32 Ver 1.3a Date 20211103` and `Using machine:LOKLiK_iCraft` banner lines.
- `evidence/firmware_v01.bin` … `firmware_v08.bin` — the eight distributed firmware images (GPLv3),
  matching the SHA-256 values in §8.1.
- `evidence/ghidra_strings_grbl.png` — Ghidra string listing: the `Grbl_ESP32` banner strings and the
  leaked `Grbl_Esp32/src/I2SOut.cpp` build path.
- `evidence/ghidra_sjconfig.png` — Ghidra strings filtered to `SJConfig`, the vendor-added settings layer.
- `evidence/ghidra_path_i2sout.png` — the leaked upstream source path in context.
- `evidence/ghidra_decompiler.png` — the firmware open in Ghidra (Xtensa) — listing + decompiler.
- `evidence/flashtool_versions.png` — LOKLiK's FlashTool listing versions v01–v08 with update dates
  (2024-09 through 2026-01).
- `evidence/flashtool_models.png` — the FlashTool model picker (Crafter / iCraft / iPaint / iEngrave),
  showing the shared firmware tooling across the product line.

### 8.5 References

- Upstream project: <https://github.com/bdring/Grbl_Esp32>
- Version constants: `Grbl_Esp32/src/Grbl.h` → `GRBL_VERSION "1.3a"`, `GRBL_VERSION_BUILD "20211103"`
- Stock settings (no `SJConfig`): `Grbl_Esp32/src/SettingsDefinitions.cpp`
- Original Grbl: <https://github.com/gnea/grbl> (Sungeon "Sonny" Jeon)
- GNU GPL v3: <https://www.gnu.org/licenses/gpl-3.0.html>

---

*This report concerns software-license compliance. It documents that a GPLv3 work is being
distributed in a product, and asks the vendor to meet the obligations of the license they
built upon. The author would prefer a cooperative resolution.*
