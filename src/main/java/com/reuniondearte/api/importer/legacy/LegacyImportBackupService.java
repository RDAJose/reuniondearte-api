package com.reuniondearte.api.importer.legacy;

import com.reuniondearte.api.config.StorageProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class LegacyImportBackupService {
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private final StorageProperties storageProperties;

    public LegacyImportBackupService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    public Path createPostgresBackup() throws IOException, InterruptedException {
        Path backupRoot = Path.of(storageProperties.backupRoot()).toAbsolutePath().normalize();
        Files.createDirectories(backupRoot);
        String stamp = FILE_TIMESTAMP.format(ZonedDateTime.now(ZoneId.systemDefault()));
        String filename = "reuniondearte-before-legacy-import-" + stamp + ".backup";
        String containerPath = "/tmp/" + filename;
        Path localPath = backupRoot.resolve(filename);

        run(List.of("docker", "exec", "reuniondearte-postgres", "pg_dump", "-U", "reuniondearte", "-d", "reuniondearte", "--format=custom", "--file=" + containerPath));
        run(List.of("docker", "cp", "reuniondearte-postgres:" + containerPath, localPath.toString()));
        return localPath;
    }

    private void run(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exit = process.waitFor();
        if (exit != 0) {
            throw new IllegalStateException("Backup command failed (" + String.join(" ", command) + "): " + output);
        }
    }
}
