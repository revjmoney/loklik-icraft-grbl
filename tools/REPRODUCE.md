# Reproduce the findings

Everything here can be reproduced from a retail LOKLiK iCraft with free tools. The ESP32 is
**not** flash-encrypted, so the firmware is readable.

## 1. Read the firmware off the device

```sh
# full 8 MB flash readback
esptool read_flash 0 0x800000 backup.bin

# or grab the per-version images the vendor's FlashTool downloads to:
#   %LOCALAPPDATA%\..\com.testingtool.app\resources\esp32\LOKLIK_CRAFTER_2\vNN\firmware.bin
```

## 2. Confirm the image and see its layout

```sh
esptool --chip esp32 image-info firmware_v06.bin
# -> ESP32 image, entry 0x400837b0, 6 segments, Chip ID 0 (ESP32)
#    Segments: DROM 0x3f400020, DRAM 0x3ffbdb60, IRAM 0x40080000/0x40080400/0x4008b9c4,
#              IROM 0x400d0018
```

## 3. Confirm what it says it is

```sh
strings firmware_v06.bin | grep -E "Grbl_ESP32|LOKLiK_iCraft|SJConfig|I2SOut"
```
Expect: `Grbl_ESP32 Ver %s Date %s`, `Using machine:%s` (-> `LOKLiK_iCraft`),
`Grbl_Esp32/src/I2SOut.cpp`, and the `SJConfig/*` settings.

## 4. Compare to upstream

At <https://github.com/bdring/Grbl_Esp32>:
- `Grbl_Esp32/src/Grbl.h` -> `GRBL_VERSION "1.3a"`, `GRBL_VERSION_BUILD "20211103"`
  (identical to the device boot banner).
- `Grbl_Esp32/src/SettingsDefinitions.cpp` -> the stock setting names. **None** of the
  `SJConfig/*` keys appear there; they are vendor additions.

## 5. Read the code in Ghidra (optional)

Ghidra 12+ ships Xtensa support. Import each `firmware_vNN.bin` as:

- **Format:** Raw Binary
- **Language:** `Xtensa:LE:32:default`

Then run **`ESP32_EsptoolLoad.java`** (Script Manager). It parses the esptool header and
lays out all six segments at their real load addresses, marks the entry point, and
disassembles it. Then `Analysis → Auto Analyze`. The boot-banner and `SJConfig` strings
cross-reference straight into the vendor's modified functions.
