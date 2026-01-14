package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import java.util.Date;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class Converters {

    private static final Gson gson = new Gson();

    // Para Date <-> Long (timestamp)
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    // Para Sala_old <-> JSON String
    @TypeConverter
    public static String salaToJson(Sala sala) {
        return sala == null ? null : gson.toJson(sala);
    }

    @TypeConverter
    public static Sala jsonToSala(String json) {
        return json == null ? null : gson.fromJson(json, Sala.class);
    }
}