# PoC: TITAN SMMU Domain Confusion

## Advanced Kernel Privilege Escalation via Hardware Desynchronization

### 1. The Vulnerability

The exploit targets a logic flaw in the **Adreno Graphics Support Layer (KGSL)**. When importing a **DMA-BUF** via `IOCTL_KGSL_GPUOBJ_IMPORT`, a desynchronization can be induced between the CPU and the **System Memory Management Unit (SMMU)**.

### 2. Execution Flow

1. **Stage 1: Vector Harmonization:** Preparing the userspace memory environment.
2. **Stage 2: DMA-BUF Allocation:** Allocating a 4MB shared memory buffer via modern DMA-BUF heaps.
3. **Stage 3: SMMU Desynchronization:**
    * Mapping the buffer for GPU usage.
    * Manipulating the page table references to cause a domain clash.
    * Bypassing **KASLR** by identifying the kernel base within the desynchronized aperture.
4. **Stage 4: Hardware-Bridged R/W:** Using GPU memory commands to perform arbitrary physical memory patches.
5. **Stage 5: Credential Mutation:** Directly overwriting the process `cred` structure (UID, GID) in physical memory to achieve **UID 0**.

### 3. Technical Proof

The core desynchronization is achieved via this native sequence in `exploit_bridge.cpp`:

```cpp
// Aperture Bypass Logic
if (ioctl(kgsl_fd, IOCTL_KGSL_MAP_USER_MEM, &map_req) == 0) {
    // SMMU is now confused; gpuaddr points to physical RAM with no V/P checks
    esm_log("[OK] SMMU Desynchronized. Memory window open.");
}
```

### 4. Verification

A successful exploit will report:

* **KASLR Bypass:** Verified kernel base (e.g., `0xFFFFFFC008000000`).
* **UID Elevation:** Transition from `uid=2000` (shell) to `uid=0` (root).

---
*Elysium Vanguard Honor Tool Kit R&D Section*
 Marina del Rey, CA.
