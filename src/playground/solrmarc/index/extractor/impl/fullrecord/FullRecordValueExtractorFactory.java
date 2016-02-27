package playground.solrmarc.index.extractor.impl.fullrecord;

import playground.solrmarc.index.utils.StringReader;
import playground.solrmarc.index.extractor.AbstractValueExtractor;
import playground.solrmarc.index.extractor.AbstractValueExtractorFactory;


public class FullRecordValueExtractorFactory extends AbstractValueExtractorFactory {

    @Override
    public boolean canHandle(final String solrFieldName, final String mappingConfiguration) {
        final String mapping = mappingConfiguration.toLowerCase().trim();
        return mapping.startsWith("FullRecordAs".toLowerCase())
                || mapping.startsWith("xml")
                || mapping.startsWith("raw")
                || mapping.startsWith("json")
                || mapping.startsWith("json2");
    }

    @Override
    public AbstractValueExtractor<?> createExtractor(final String solrFieldName, final StringReader mappingConfiguration) {
        final String mapping = mappingConfiguration.readAll().toLowerCase();
        if (mapping.startsWith("raw")
                || mapping.startsWith("FullRecordAsMARC".toLowerCase())) {
            return new FullRecordAsMarcValueExtractor();
        } else if (mapping.startsWith("xml")
                || mapping.startsWith("FullRecordAsXML".toLowerCase())) {
            return new FullRecordAsXMLValueExtractor();
        } else if (mapping.startsWith("json2")
                || mapping.startsWith("FullRecordAsJSON2".toLowerCase())) {
            return new FullRecordAsJSON2ValueExtractor();
        } else if (mapping.startsWith("json")
                || mapping.startsWith("FullRecordAsJSON".toLowerCase())) {
            return new FullRecordAsJSONValueExtractor();
        } else if (mapping.startsWith("text")
                || mapping.startsWith("FullRecordAsText".toLowerCase())) {
            return new FullRecordAsTextValueExtractor();
        }
        throw new IllegalArgumentException("Unknown impl configuration: " + solrFieldName + " = " + mappingConfiguration);
    }
}
