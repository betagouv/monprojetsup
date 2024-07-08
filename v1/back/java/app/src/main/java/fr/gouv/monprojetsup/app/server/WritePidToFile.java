package fr.gouv.monprojetsup.app.server;

import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WritePidToFile {

    /**
     * Write the current process id to a file
     */
    public static void write(String filePref) {
        // Get the current process handle
        val currentProcess = ProcessHandle.current();

        // Get the PID of the current process
        val pid = currentProcess.pid();

        // Define the path to the file where the PID should be written
        val filePath = filePref + ".pid";

        try {
            // Write the PID to the file
            Files.writeString(Paths.get(filePath), Long.toString(pid));
            System.out.println("Successfully wrote PID to file: $filePath");
        } catch (IOException e) {
            System.err.println("An error occurred while writing the PID to file: ");
        }

    }
}
