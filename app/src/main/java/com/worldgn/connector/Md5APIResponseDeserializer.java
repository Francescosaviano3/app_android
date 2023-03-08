package com.worldgn.connector;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class Md5APIResponseDeserializer extends TypeAdapter<Md5APIResponse>{

    private Gson gson = new Gson();

    @Override
    public void write(JsonWriter jsonWriter, Md5APIResponse md5APIResponse) throws IOException {
        gson.toJson(md5APIResponse, Md5APIResponse.class, jsonWriter);
    }

    @Override
    public Md5APIResponse read(JsonReader jsonReader) throws IOException {

        Md5APIResponse md5APIResponse = null;

        jsonReader.beginObject();
        jsonReader.nextName();
        if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
//            md5APIResponse =
        } else if(jsonReader.peek() == JsonToken.BEGIN_OBJECT) {

        } else {

        }
        return md5APIResponse;
    }
}
