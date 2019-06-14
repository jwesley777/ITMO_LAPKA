
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class JsonIO {
    private static BufferedReader reader;
    public static JSONObject readObjectFromFile(String fileName) {
        try {
            if (reader == null)
                reader = new BufferedReader(new FileReader(fileName));
            try {
                String line = "";
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null)
                    // lines += line;
                    sb.append(line);

                String lines = sb.toString();
                return (lines.length()!=0) ? new JSONObject(lines) : null;
            }
            catch (IOException e) {
                return null;
            }
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static JSONArray readArrayFromFile(String fileName) {
        try {
            if (true || reader == null)
                reader = new BufferedReader(new FileReader(fileName));
            try {
                String line = "";
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null)
                    // lines += line;
                    sb.append(line);

                String lines = sb.toString();
                return (lines.length()!=0) ? new JSONArray(lines) : null;
            }
            catch (IOException e) {
                //e.printStackTrace();
                return null;
            }
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException e) {}


        }
    }

    public static void writeToFile(JSONArray arr, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(arr.toString());
            writer.flush();
        }
        catch (IOException e) { System.out.println(e.getMessage()); }
    }

    public static void writeToFile(JSONObject obj, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(obj.toString());
            writer.flush();
        }
        catch (IOException e) { System.out.println(e.getMessage()); }
    }
}
