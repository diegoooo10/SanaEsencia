
package com.example.sanaescencia.data.remote.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalTime

class LocalTimeAdapter : TypeAdapter<LocalTime>() {

    override fun write(out: JsonWriter, value: LocalTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    override fun read(reader: JsonReader): LocalTime? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }

        return LocalTime.parse(reader.nextString())
    }
}
