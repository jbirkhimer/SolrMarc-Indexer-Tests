package playground.solrmarc.index.mapping.impl;

import playground.solrmarc.index.mapping.AbstractSingleValueMapping;
import playground.solrmarc.index.mapping.AbstractMultiValueMapping;
import playground.solrmarc.index.mapping.AbstractValueMappingFactory;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TranslationMappingFactory extends AbstractValueMappingFactory {
    private final static Logger logger = Logger.getLogger(TranslationMappingFactory.class);
    private final static Map<String, Properties> translationMappingFiles = new HashMap<>();

    @Override
    public boolean canHandle(String mappingConfiguration) {
        return mappingConfiguration.endsWith(".properties") || (mappingConfiguration.contains(".properties(") && mappingConfiguration.endsWith(")"));
    }

    private Properties loadTranslationMappingFile(String translationMappingFileName, String subMappingName) {
        Properties properties = translationMappingFiles.get(translationMappingFileName + "(" + subMappingName + ")");
        if (properties != null) {
            return properties;
        }
        properties = translationMappingFiles.get(translationMappingFileName + "(null)");
        if (properties != null) {
            properties = getSubTranslationMapping(properties, subMappingName);
            translationMappingFiles.put(translationMappingFileName + "(" + subMappingName + ")", properties);
            return properties;
        }
        try {
            properties = new Properties();
            logger.debug("Load translation map: ./translation_maps/" + translationMappingFileName);
            FileInputStream inputStream = new FileInputStream("./translation_maps/" + translationMappingFileName);
            properties.load(inputStream);
            translationMappingFiles.put(translationMappingFileName + "(null)", properties);
            if (subMappingName != null) {
                properties = getSubTranslationMapping(properties, subMappingName);
                translationMappingFiles.put(translationMappingFileName + "(" + subMappingName + ")", properties);
            }
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Properties getSubTranslationMapping(Properties translationMapping, String mappingPrefix) {
        Properties mappings = new Properties();
        for (String key : translationMapping.stringPropertyNames()) {
            if (key.startsWith(mappingPrefix)) {
                String value = translationMapping.getProperty(key);
                if (value.equals("null")) {
                    value = null;
                }
                if (key.length() == mappingPrefix.length()) {
                    // remove prefix. There is no period.
                    mappings.setProperty("", value);
                } else {
                    // remove prefix and period.
                    mappings.setProperty(key.substring(mappingPrefix.length() + 1), value);
                }
            }
        }
        return mappings;
    }

    private String getTranslationMappingFileName(String mappingConfiguration) {
        int index = mappingConfiguration.indexOf('(');
        if (index != -1) {
            return mappingConfiguration.substring(0, index);
        } else {
            return mappingConfiguration;
        }
    }

    private String getSubMappingName(String mappingConfiguration) {
        int index = mappingConfiguration.indexOf('(');
        if (index != -1) {
            return mappingConfiguration.substring(index + 1, mappingConfiguration.length() - 1);
        } else {
            return null;
        }
    }

    @Override
    public AbstractSingleValueMapping createSingleValueMapping(String mappingConfiguration) {
        final String translationMappingFileName = getTranslationMappingFileName(mappingConfiguration);
        final String subMappingName = getSubMappingName(mappingConfiguration);
        final Properties translationMapping = loadTranslationMappingFile(translationMappingFileName, subMappingName);
        return new SingleValueTranslationMapping(translationMapping);
    }

    @Override
    public AbstractMultiValueMapping createMultiValueMapping(String mappingConfiguration) {
        String translationMappingFileName = getTranslationMappingFileName(mappingConfiguration);
        final String subMappingName = getSubMappingName(mappingConfiguration);
        Properties translationMapping = loadTranslationMappingFile(translationMappingFileName, subMappingName);
        return new MultiValueTranslationMapping(translationMapping);
    }
}
