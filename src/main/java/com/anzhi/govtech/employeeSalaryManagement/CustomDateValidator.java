package com.anzhi.govtech.employeeSalaryManagement;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CustomDateValidator extends StdDeserializer<LocalDate> {
    private static final long serialVersionUID = 1L;

    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd",
            "dd-MMM-yy"
    };

    public CustomDateValidator() {
        this(null);
    }

    public CustomDateValidator(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        final String date = node.textValue();

        for (String DATE_FORMAT : DATE_FORMATS) {
            try {
                return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
            } catch (Exception e) {
            }
        }
        return null;
    }

}