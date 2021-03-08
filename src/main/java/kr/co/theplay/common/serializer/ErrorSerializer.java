package kr.co.theplay.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent
public class ErrorSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();

        value.getFieldErrors().forEach(e -> {
            try {
                gen.writeStartObject();

                /*"field": "sex",
                "objectName": "signUpDto",
                "code": "NotEmpty",
                "defaultMessage": "반드시 값이 존재하고 길이 혹은 크기가 0보다 커야 합니다."*/

                gen.writeStringField("field", e.getField());
                //gen.writeStringField("objectName", e.getObjectName());
                //gen.writeStringField("code", e.getCode());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                Object rejectedValue =  e.getRejectedValue();
                if (rejectedValue != null) {
                    gen.writeStringField("rejectedValue", rejectedValue.toString());
                }

                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        value.getGlobalErrors().forEach(e->{
            try {
                gen.writeStartObject();

                //gen.writeStringField("objectName", e.getObjectName());
                //gen.writeStringField("code", e.getCode());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());

                gen.writeEndObject();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });


        gen.writeEndArray();
    }
}
