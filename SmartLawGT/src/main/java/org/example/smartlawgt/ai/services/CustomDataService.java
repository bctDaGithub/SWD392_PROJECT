package org.example.smartlawgt.ai.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class CustomDataService {

    public String loadCustomData() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/data.txt")));
    }
}
