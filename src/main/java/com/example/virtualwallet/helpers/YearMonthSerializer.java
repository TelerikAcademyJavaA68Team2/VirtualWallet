package com.example.virtualwallet.helpers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class YearMonthSerializer extends JsonSerializer<YearMonth> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    @Override
    public void serialize(YearMonth value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(FORMATTER));
    }
}
