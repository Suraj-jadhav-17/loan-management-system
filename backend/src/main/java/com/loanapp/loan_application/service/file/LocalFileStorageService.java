package com.loanapp.loan_application.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService {
    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "png", "jpg", "jpeg");

    @Value("${app.file-storage.base-dir:uploads}")
    private String baseDirectory;

    public String store(MultipartFile file, String moduleFolder, Long ownerId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File must be 10 MB or smaller.");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename());
        String extension = extensionOf(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only PDF, PNG, JPG and JPEG files are allowed.");
        }

        try {
            Path root = rootPath();
            Path ownerFolder = root.resolve(moduleFolder).resolve(String.valueOf(ownerId)).normalize();
            if (!ownerFolder.startsWith(root)) {
                throw new IllegalArgumentException("Invalid file storage location.");
            }
            Files.createDirectories(ownerFolder);
            String generatedName = UUID.randomUUID() + "." + extension;
            Path destination = ownerFolder.resolve(generatedName).normalize();
            if (!destination.startsWith(ownerFolder)) {
                throw new IllegalArgumentException("Invalid file name.");
            }
            file.transferTo(destination.toFile());
            return destination.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save uploaded file.", ex);
        }
    }

    public Resource loadAsResource(String storedPath) {
        if (!StringUtils.hasText(storedPath)) {
            throw new IllegalArgumentException("No file is attached to this record.");
        }
        try {
            Path root = rootPath();
            Path file = Paths.get(storedPath).toAbsolutePath().normalize();
            if (!file.startsWith(root)) {
                throw new IllegalArgumentException("File path is outside the configured storage folder.");
            }
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("Stored file could not be found.");
            }
            return resource;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read stored file.", ex);
        }
    }

    public void deleteQuietly(String storedPath) {
        if (!StringUtils.hasText(storedPath)) return;
        try {
            Path root = rootPath();
            Path file = Paths.get(storedPath).toAbsolutePath().normalize();
            if (file.startsWith(root)) Files.deleteIfExists(file);
        } catch (Exception ignored) {
            // A failed physical file delete must not expose a server path or break another operation.
        }
    }

    private Path rootPath() {
        return Paths.get(baseDirectory).toAbsolutePath().normalize();
    }

    private String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
