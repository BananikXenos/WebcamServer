package xyz.synse.datacenter;

import xyz.synse.datacenter.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Mantle {
    public static void main(String[] args) throws IOException {
        // Call boot event
        MantleAPI.INSTANCE.onBoot();

        // Initialize reader to read console
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        // Loop while mantle is running
        while (MantleAPI.INSTANCE.running.get()) {
            // Read line raw
            String rawData = reader.readLine();

            // Execute command
            if (!MantleAPI.INSTANCE.commandManager.executeCommand(rawData)) {
                // No command found
                Logger.e("No command by name " + MantleAPI.INSTANCE.commandManager.extractName(rawData));
            }
        }
    }
}
