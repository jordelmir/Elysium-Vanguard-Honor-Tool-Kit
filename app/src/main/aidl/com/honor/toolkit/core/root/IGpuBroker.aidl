package com.honor.toolkit.core.root;

/**
 * IGpuBroker: The Sovereign Guard Interface.
 * Moves driver interaction to a privileged execution context.
 */
interface IGpuBroker {
    /**
     * Executes a raw KGSL IOCTL from a privileged context.
     * @param cmd The IOCTL command number.
     * @param payload The data buffer.
     * @return 0 on success, negative errno on failure.
     */
    int executeIoctl(int cmd, inout byte[] payload);
    
    /**
     * Scans for kernel leaks in a specific property.
     * @param propertyId The KGSL property type.
     * @return A list of leaked pointers or an empty array.
     */
    long[] scanLeak(int propertyId);
    
    /**
     * Gets the current context UID and SELinux label.
     */
    String getContextInfo();

    /**
     * Fetches logs from the broker's native buffer.
     */
    String getBrokerLog();

    /**
     * Verifies if the broker is running with root privileges.
     * @return true if root, false otherwise.
     */
    boolean verifyRoot();

    /**
     * Executes the Exploit State Machine (ESM) in the broker process.
     * @return true if launched.
     */
    boolean runEsm();

    /**
     * Gets the current ESM stage.
     */
    int getEsmStage();

    /**
     * Gets the leaked kernel pointer.
     */
    long getEsmLeak();

    /**
     * Executes a command as root once escalation is complete.
     */
    String requestSuSession(String cmd);
}
