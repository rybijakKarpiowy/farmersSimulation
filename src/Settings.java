import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Settings {
    private static volatile Settings instance;
    private Map<String, Map<String, String>> sections = new ConcurrentHashMap<>();
    private static String configPath = "src/settings/default.csv";

    private Settings() {
        loadSettings();
    }

    private Settings(String path) {
        configPath = path;
        this();
    }

    public static Settings getInstance() {
        if (instance == null) {
            synchronized (Settings.class) {
                if (instance == null) {
                    instance = new Settings();
                }
            }
        }
        return instance;
    }

    private void loadSettings() {
        try {
            String currentSection = null;
            for (String line : Files.readAllLines(Paths.get(configPath))) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (!line.contains(":")) {
                    currentSection = line;
                    sections.put(currentSection, new ConcurrentHashMap<>());
                } else if (currentSection != null) {
                    String[] parts = line.split(":", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    sections.get(currentSection).put(key, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Can not read settings: " + configPath);
        }
    }

    public void setSetting(String section, String key, String value) {
        Map<String, String> sectionMap = sections.computeIfAbsent(section, k -> new ConcurrentHashMap<>());
        sectionMap.put(key, value);
    }

    public String getSetting(String section, String key) {
        Map<String, String> sectionMap = sections.get(section);

        if (sectionMap == null) {
            return null;
        }

        return sectionMap.get(key);
    }

    public Map<String, String> getSection(String section) {
        Map<String, String> sectionMap = sections.get(section);

        if (sectionMap == null) {
            return new ConcurrentHashMap<>();
        }

        return sectionMap;
    }

    public void saveSettings() {
        String path = "src/settings/userSettings.csv";
        try {
            List<String> lines = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> section : sections.entrySet()) {
                lines.add(section.getKey());
                for (Map.Entry<String, String> entry : section.getValue().entrySet()) {
                    lines.add(entry.getKey() + ": " + entry.getValue());
                }
                lines.add("");
            }
            Files.write(Paths.get(path), lines);
        } catch (Exception e) {
            throw new RuntimeException("Can not write settings: " + path);
        }
    }
}