package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Reserva;

public class LocalCache {

    private static final String CACHE_FILE_RESERVAS = "reservas_cache.json";
    private static final String CACHE_FILE_FATURAS = "faturas_cache.json";

    private static File getCacheFile(Context context, String fileName) {
        return new File(context.getCacheDir(), fileName);
    }

    public static void saveReservas(Context context, List<Reserva> list) {
        saveList(context, list, CACHE_FILE_RESERVAS);
    }

    public static List<Reserva> loadReservas(Context context) {
        return loadList(context, CACHE_FILE_RESERVAS);
    }

    public static void saveFaturas(Context context, List<Reserva> list) {
        saveList(context, list, CACHE_FILE_FATURAS);
    }

    public static List<Reserva> loadFaturas(Context context) {
        return loadList(context, CACHE_FILE_FATURAS);
    }

    private static void saveList(Context context, List<Reserva> list, String fileName) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        File file = getCacheFile(context, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        } catch (IOException e) {
            Log.e("CACHE", "Erro ao salvar cache", e);
        }
    }

    private static List<Reserva> loadList(Context context, String fileName) {
        File file = getCacheFile(context, fileName);
        if (!file.exists()) return new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new Gson();
            return gson.fromJson(reader, new TypeToken<List<Reserva>>(){}.getType());
        } catch (IOException e) {
            Log.e("CACHE", "Erro ao ler cache", e);
            return new ArrayList<>();
        }
    }
}