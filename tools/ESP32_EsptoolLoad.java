// Lay out an ESP32 (esptool) firmware image with correct memory segments.
//
// Use this AFTER importing the firmware.bin as:
//   Format   = Raw Binary
//   Language = Xtensa:LE:32:default
// (ignore the base address at import time -- this script fixes the memory map).
//
// Then run it from the Script Manager. It reads the esptool header straight from
// the imported file bytes, deletes the flat block, and recreates one memory block
// per segment at its real load address (DROM/DRAM/IRAM/IROM), sets R/W/X
// correctly, marks the entry point, and disassembles it.
//
// Works for any plain ESP32 (chip id 0) image -- all 8 LOKLIK_CRAFTER_2 versions.
//@category ESP32
//@menupath Tools.ESP32.Load esptool image

import java.util.List;

import ghidra.app.script.GhidraScript;
import ghidra.program.database.mem.FileBytes;
import ghidra.program.model.address.Address;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.mem.MemoryBlock;

public class ESP32_EsptoolLoad extends GhidraScript {

    private FileBytes fb;

    private int u8(long off) throws Exception {
        return fb.getOriginalByte(off) & 0xff;
    }

    private long u32(long off) throws Exception {
        return ((long) u8(off)
                | ((long) u8(off + 1) << 8)
                | ((long) u8(off + 2) << 16)
                | ((long) u8(off + 3) << 24)) & 0xffffffffL;
    }

    @Override
    public void run() throws Exception {
        Memory mem = currentProgram.getMemory();

        List<FileBytes> fbs = mem.getAllFileBytes();
        if (fbs.isEmpty()) {
            println("ERROR: no FileBytes found. Re-import the .bin as 'Raw Binary' first.");
            return;
        }
        fb = fbs.get(0);

        int magic = u8(0);
        if (magic != 0xE9) {
            println(String.format("ERROR: not an esptool image (magic 0x%02x, expected 0xE9)", magic));
            return;
        }

        int segCount = u8(1);
        long entry = u32(4);
        println(String.format("esptool image: %d segments, entry 0x%08x", segCount, entry));

        // Drop the flat raw block(s) so we can rebuild the real map.
        for (MemoryBlock b : mem.getBlocks()) {
            mem.removeBlock(b, monitor);
        }

        long off = 0x18; // ESP32 24-byte header; first segment header follows
        for (int i = 0; i < segCount; i++) {
            long load = u32(off);
            long length = u32(off + 4);
            long dataOff = off + 8;

            String name;
            boolean ex, wr;
            if (load >= 0x40000000L) {          // IRAM / IROM -> executable code
                name = String.format("code_%d_%08x", i, load);
                ex = true;  wr = false;
            } else if (load >= 0x3ff80000L) {   // DRAM -> read/write data
                name = String.format("dram_%d_%08x", i, load);
                ex = false; wr = true;
            } else {                            // DROM -> read-only data
                name = String.format("drom_%d_%08x", i, load);
                ex = false; wr = false;
            }

            Address start = toAddr(load);
            MemoryBlock blk = mem.createInitializedBlock(name, start, fb, dataOff, length, false);
            blk.setRead(true);
            blk.setWrite(wr);
            blk.setExecute(ex);
            println(String.format("  seg %d  %-20s 0x%08x  size 0x%x  (file 0x%x)  X=%b W=%b",
                    i, name, load, length, dataOff, ex, wr));

            off = dataOff + length;
        }

        // Mark + disassemble the entry point so auto-analysis has a seed.
        Address ep = toAddr(entry);
        addEntryPoint(ep);
        createLabel(ep, "entry_point", true);
        disassemble(ep);
        println("Done. Now run  Analysis > Auto Analyze  (accept the defaults).");
    }
}
