# NOTICE — licensing of the contents of this repository

This repository contains material under more than one license. Please read this before
reusing anything.

## Firmware binaries (`evidence/firmware_v01.bin` … `firmware_v08.bin`)

These are builds of **Grbl_Esp32**, which is licensed under the **GNU General Public License,
version 3 (GPLv3)**. They are therefore themselves covered by the GPLv3 and are redistributed
here under that license, which expressly permits redistribution.

- Upstream project: https://github.com/bdring/Grbl_Esp32 (Barton Dring / "bdring")
- Grbl_Esp32 is a port of Grbl: https://github.com/gnea/grbl (Sungeon "Sonny" Jeon)
- GPLv3 text: https://www.gnu.org/licenses/gpl-3.0.html

**Important:** redistributing GPLv3 *binaries* carries the GPLv3 §6 obligation to make the
**Complete Corresponding Source** available. The corresponding source for the *unmodified*
Grbl_Esp32 portion is public at the upstream link above. The **vendor's modifications** (the
`SJConfig` settings layer, the `LOKLiK_iCraft` machine definition, and the build/flash
configuration) are **not public** — obtaining that source from the vendor is the purpose of
this project. These binaries are provided as evidence and as the verbatim copies the
recipient (the repository author) lawfully received.

## Tools (`tools/`)

`tools/ESP32_EsptoolLoad.java` is original work by the repository author, provided for
analysis convenience. Licensed **GPL-3.0-or-later** for simplicity and compatibility.

## Documentation, photos, and writeup (`README.md`, `REPORT.md`, `evidence/*.png`, `evidence/*.jpg`)

Original work by the repository author (Rev — JMS CNC). Licensed
**Creative Commons Attribution 4.0 International (CC BY 4.0)** —
https://creativecommons.org/licenses/by/4.0/ — except for any third-party UI shown in
screenshots, which appears solely for purposes of criticism, commentary, and reporting.

## Trademarks

"LOKLiK", "iCraft", and any product names are trademarks of their respective owners and are
used here only nominatively, to identify the products under discussion. No endorsement is
implied.
