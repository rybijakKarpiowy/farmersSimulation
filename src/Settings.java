import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


public class Settings {
    private static volatile Settings instance;
    private final Map<String, Map<String, Object>> sections;
    private final CountDownLatch initializationLatch;
    private volatile boolean isInitialized = false;
    private static String configPath = "default.csv";

    private Settings() {
        sections = new ConcurrentHashMap<>();
        initializationLatch = new CountDownLatch(1);
        loadSettings();
    }

    public static void setConfigPath(String path) {
        if (instance != null) {
            throw new IllegalStateException("Cannot change config path after Settings initialization");
        }
        configPath = path;
    }

    public static Settings getInstance() {
        Settings result = instance;
        if (result == null) {
            synchronized (Settings.class) {
                result = instance;
                if (result == null) {
                    instance = result = new Settings();
                }
            }
        }
        try {
            result.initializationLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Settings initialization interrupted", e);
        }
        return result;
    }

    private void loadSettings() {
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(configPath))) {
                String currentSection = null;
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty()) {
                        continue;
                    }

                    if (!line.contains(":")) {
                        currentSection = line;
                        continue;
                    }

                    if (currentSection != null) {
                        String[] parts = line.split(":");
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();

                            try {
                                if (value.contains(".")) {
                                    setSetting(currentSection, key, Double.parseDouble(value));
                                } else {
                                    setSetting(currentSection, key, Integer.parseInt(value));
                                }
                            } catch (NumberFormatException e) {
                                setSetting(currentSection, key, value);
                            }
                        }
                    }
                }
                isInitialized = true;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load settings from " + configPath, e);
        } finally {
            initializationLatch.countDown();
        }
    }

    public void setSetting(String section, String key, Object value) {
        checkInitialization();
        sections.computeIfAbsent(section, k -> new ConcurrentHashMap<>())
                .put(key, value);
    }

    public Object getSetting(String section, String key) {
        checkInitialization();
        Map<String, Object> sectionMap = sections.get(section);
        return sectionMap != null ? sectionMap.get(key) : null;
    }

    public Object getSetting(String section, String key, Object defaultValue) {
        checkInitialization();
        Object value = getSetting(section, key);
        return value != null ? value : defaultValue;
    }

    public Map<String, Object> getSection(String section) {
        checkInitialization();
        return sections.getOrDefault(section, new ConcurrentHashMap<>());
    }

    private void checkInitialization() {
        if (!isInitialized) {
            throw new IllegalStateException("Settings are not fully initialized yet");
        }
    }

    public boolean hasSection(String section) {
        checkInitialization();
        return sections.containsKey(section);
    }

    public void clearSection(String section) {
        checkInitialization();
        sections.remove(section);
    }

    public void clearAll() {
        checkInitialization();
        sections.clear();
    }
}