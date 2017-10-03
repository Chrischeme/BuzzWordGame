package data;

import components.AppDataComponent;
import components.AppFileComponent;
import sun.plugin2.message.Message;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ritwik Banerjee
 */
public class GameDataFile implements AppFileComponent {

    public static final String ID = "ID";
    public static final String PW = "PW";
    public static final String LV = "LEVEL";

   @Override
   public void saveData(AppDataComponent data, Path to) throws FileNotFoundException {
       GameData dataManager = (GameData) data;
       String id = dataManager.getID();
       MessageDigest digest = null;
       try {
           digest = MessageDigest.getInstance("MD5");
           String tempPwd = dataManager.getPW();
           digest.update(tempPwd.getBytes(), 0, tempPwd.length());
           String pwd = new BigInteger(1, digest.digest()).toString(16);
           JsonArray lev = makeJsonSetObjectL(dataManager);

           JsonObject saveJson = Json.createObjectBuilder()
                   .add (ID, id)
                   .add (PW, pwd)
                   .add (LV, lev).build();

           Map<String, Object> properties = new HashMap<>(1);
           properties.put(JsonGenerator.PRETTY_PRINTING, true);
           JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
           StringWriter sw = new StringWriter();
           JsonWriter jsonWriter = writerFactory.createWriter(sw);
           jsonWriter.writeObject(saveJson);
           jsonWriter.close();

           OutputStream os;
           if (to.toString().endsWith(".json")) {
               os = new FileOutputStream(to.toString());
           }
           else {
               os = new FileOutputStream(to.toString() + ".json");
           }
           JsonWriter jsonFileWriter = Json.createWriter(os);
           jsonFileWriter.writeObject(saveJson);
           String prettyPrinted = sw.toString();
           PrintWriter pw;
           if (to.toString().endsWith(".json")) {
               pw = new PrintWriter(to.toString());
           }
           else {
               pw = new PrintWriter(to.toString() + ".json");
           }
           pw.write(prettyPrinted);
           pw.close();
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       }
   }
    public void saveData1(AppDataComponent data, Path to) throws FileNotFoundException {
        GameData dataManager = (GameData) data;
        String id = dataManager.getID();
        MessageDigest digest = null;
            String pwd = dataManager.getPW();
            JsonArray lev = makeJsonSetObjectL(dataManager);

            JsonObject saveJson = Json.createObjectBuilder()
                    .add(ID, id)
                    .add(PW, pwd)
                    .add(LV, lev).build();

            Map<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = writerFactory.createWriter(sw);
            jsonWriter.writeObject(saveJson);
            jsonWriter.close();

            OutputStream os;
            if (to.toString().endsWith(".json")) {
                os = new FileOutputStream(to.toString());
            } else {
                os = new FileOutputStream(to.toString() + ".json");
            }
            JsonWriter jsonFileWriter = Json.createWriter(os);
            jsonFileWriter.writeObject(saveJson);
            String prettyPrinted = sw.toString();
            PrintWriter pw;
            if (to.toString().endsWith(".json")) {
                pw = new PrintWriter(to.toString());
            } else {
                pw = new PrintWriter(to.toString() + ".json");
            }
            pw.write(prettyPrinted);
            pw.close();

    }

    private JsonArray makeJsonSetObjectL(GameData dataManager) {
        int[] arr = dataManager.getLevels();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Integer c : arr) {
            JsonObject charJson = Json.createObjectBuilder()
                    .add(LV, c).build();
            arrayBuilder.add(charJson);
        }
        JsonArray charArray = arrayBuilder.build();
        return charArray;
    }

    @Override
    public void loadData(AppDataComponent data, Path from) throws IOException {

        GameData dataManager = (GameData) data;

        JsonObject json = loadJSONFile(from.toString());

        JsonString jsonString = json.getJsonString(ID);
        String id = jsonString.getString();
        dataManager.setID(id);

        jsonString = json.getJsonString(PW);
        String pw = jsonString.getString();
        dataManager.setPW(pw);

        JsonArray jsonArr = json.getJsonArray(LV);
        for (int i = 0; i < 4; i++) {
            JsonObject tempJson = jsonArr.getJsonObject(i);
            int leve = (int) tempJson.getJsonNumber(LV).intValue();
            dataManager.setLevels(i, leve);
        }
    }

    @Override
    public void exportData(AppDataComponent data, Path filePath) throws IOException {

    }

    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }
}
