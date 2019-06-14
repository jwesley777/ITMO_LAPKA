import java.io.File;
import java.io.IOException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.concurrent.*;
/**
 * Collection, written to manage prisoners in jail. But it is so good, that I decided to make it generic and publish.
 *
 * @author Pavel Suprun
 *
 * @param <T> Type of elements. Must implement Comparable
 *
 */
public class Prisoners <T extends Comparable<T>> {
    private List<T> elements = Collections.synchronizedList(new LinkedList<T>());
    //private LinkedList<T> elements = new LinkedList<T>();
    Date date = new Date();

    /**
     * Return int value of the size of the collection
     * @return size of collection
     */
    public int size () { return elements.size(); }

    /**
     * Delete element from collection by its value
     * @param el Element to remove
     * @return true if any elements were removed
     */
    public String remove (T el) {
        if (elements.remove(el)) {
            //System.out.println(el.toString() + " - removed");
            return el.toString() + " - removed";
        }
        //System.out.println("Nothing was removed");
        return "Nothing was removing";
    }

    /**
     *
     * Print all elements of the collection by its String representation to stdout
     */
    public String show () {
        String toReturn = "";
        if (elements.size()==0)
            toReturn = "No elements to show";

        Collections.sort(elements);
        for (T el: elements)
            toReturn = toReturn + el + ";\n ";
        return toReturn;
    }

    /**
     * Add element to the collection if it will become the largest element of the collection
     * @param el Element to add and to compare with
     * @return true if element has been added, false otherwise
     */
    public String addIfMax (T el) {
        if (elements.size() < 1) {
            elements.add(el);
            return el + " added to collection";
        }
        Collections.sort(elements);
        if (elements.stream().max((p1,p2) -> p1.compareTo(p2)).get().compareTo(el) < 0) {
            elements.add(el);
            //System.out.println(el.toString()+" added to collection");
            return el.toString()+" added to collection";
        }
        //System.out.println("Nothing was added to collection");
        return "Nothing was added to collection";
    }

    /**
     * Remove all elements less than parameter element
     * @param el Element to compare with
     * @return true if any elements were removed
     */
    public String removeLower (T el) {
        if (elements.removeIf(p -> p.compareTo(el) < 0))
        {
            //System.out.println("Elements, lower than " + el.toString() + ", removed");
            return "Elements, lower than " + el.toString() + ", removed";
        }
        //System.out.println("Nothing was removed from collection");
        return "Nothing was removed from collection";
    }

    /**
     * Print some information (size of collection and type of elements, if there are ones) to stdout
     */
    public String info () {
        if (elements.size()==0) {
            //System.out.println("Empty collection");
            //System.out.println("Empty "+ elements.getClass().getName() +" of "+
            //        /*elements.get().getClass().getName() +*/ " "+date);
            return "Empty "+ elements.getClass().getName() +" of "+
                    /*elements.get().getClass().getName() +*/ " "+date;
        }
        return String.format("Collection of %d elements of type %s, %s, %s\n",
                elements.size(),
                elements.get(0).getClass().getName(),
                elements.getClass().getName(), date
        );
        //System.out.printf("Collection of %d elements of type %s, %s, %s\n",
        //        elements.size(),
        //        elements.get(0).getClass().getName(),
        //        elements.getClass().getName(), date
        //        );
    }

    /**
     * Clear the collection
     */
    public String clear () {
        //System.out.println("Collection is cleared");
        elements.clear();
        return "Collection is cleared";
    }

    /**
     * Add new element to the collection
     * @param el Element to add to the collection
     * @return true if collection changed as a result of the call
     */
    public String add (T el) {
        if (elements.add(el)) {
            //System.out.println(el.toString() + " added to collection");
            return el.toString() + " added to collection";
        }
        return "This should not be showed";
    }

    public void addSilent (T el) {
            elements.add(el);
    }

    // -----------------------------------------------------------------------------------------------

    public static JSONObject getArgument (String s) {
        try { return new JSONObject(s.substring(s.indexOf('{'))); }
        catch (JSONException e) { System.out.println("Wrong definition of JSON object"); }
        // catch (StringIndexOutOfBoundsException e) { return null; }
        catch (Throwable e) { return null; }
        return null;
    }

    public static Human jsonToHuman (JSONObject obj) {
        try {
            Human h = new Human(obj.getString("name"),
                    obj.getInt("size"),
                    obj.getEnum(Space.class, "currentSpace"),
                    obj.getInt("x"),
                    obj.getInt("y"));
            try {
                Resource r = new Resource(obj.getJSONObject("inHands").getString("name"));
                h.inHands = r;
            } catch (Throwable e) {}
            return h;
        } catch (JSONException e) {System.out.println("Wrong definition of JSON object"); }
        catch (Throwable e) {  }
        return null;
    }

    public static JSONObject humanToJson (Human h) {
        JSONObject obj = new JSONObject();
        obj.put("name", h.name);
        obj.put("size",h.size);
        obj.put("currentSpace", h.currentSpace);
        obj.put("x",h.x);
        obj.put("y",h.y);
        if (h.inHands != null)
            obj.put("inHands", new JSONObject().put("name", h.inHands.name));
        return obj;
    }

    public static void printGuideToStdOut () {
        System.out.println();
        System.out.println("FORMAT - {element}:{\"name\":\"NAME\",\"size\":VALUE,\"x\":VALUE, \"y\":VALUE, \"currentSpace\":\"SPACE_NAME\" }");
        System.out.println("remove {login} {password} {element}: delete element(s) from collection by its value");
        System.out.println("show: print all elements to stdout");
        System.out.println("add_if_max {login} {password} {element}: add element if it is larger than all elements in collection");
        System.out.println("remove_lower {login} {password} {element}: remove elements of collection less than element");
        //System.out.println("info: print information about collection");
        System.out.println("clear: clear collection");
        System.out.println("add {login} {password} {element}: add element to collection");
        System.out.println("stop: exit app");
        //System.out.println("import <filename>: import objects from client's file to server");
        //System.out.println("load <filename>: load objects from server's file from server");
        //System.out.println("save <filename>: save file on server");
        System.out.println();
        System.out.println("Waiting for command...");
    }

    private static void pressEnterKey (Scanner in) {
        System.out.println("Press Enter to continue");
        try
        {
            in.nextLine();
        }
        catch(Exception e)
        {}
    }

    public static boolean testFilename(String fileName) {
        File test = new File(fileName);
        if (!test.exists()) try { test.createNewFile(); System.out.println("New file created"); }
        catch (IOException e) {  }
        return test.canRead() && test.canWrite();
    }

    public static Prisoners<Human> fromFile (String fileName) {
        JSONArray file = JsonIO.readArrayFromFile(fileName);
        Prisoners<Human> p = new Prisoners<>();
        try {
            if (file != null && file.length() > 0) {
                System.out.println("Opening " + fileName + "...");
                for (Object obj : file) {
                    p.add(jsonToHuman((JSONObject) obj));
                }
                System.out.println(fileName + " opened");
            }
        } catch (Throwable e) {
            System.out.println("Something wrong with file");
            System.exit(42);
        }
        return p;
    }
    public static Prisoners<Human> fillFromFile (Prisoners<Human> p, String fileName) {
        JSONArray file = JsonIO.readArrayFromFile(fileName);

        try {
            if (file != null && file.length() > 0) {
                System.out.println("Opening " + fileName + "...");
                for (Object obj : file) {
                    p.add(jsonToHuman((JSONObject) obj));
                }
                System.out.println(fileName + " opened");
            }
        } catch (Throwable e) {
            System.out.println("Something wrong with file");
            System.exit(42);
        }
        return p;
    }
    public static Human[] toHumanArray(Prisoners<Human> p) {
        Human[] humans = new Human[p.size()];
        for (int i = 0; i < p.size(); i++) {
            humans[i] = p.elements.get(i);
        }
        return humans;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("You have to type name of file");
            return;
        }
        String fileName = args[0];
        if (!testFilename(fileName)) {
            System.out.println("Something wrong with file");
            return;
        }
        String input = "";
        JSONObject arg;
        String firstWord = "";
        Scanner in = new Scanner(System.in);
        //JSONArray file;
        //Prisoners<Human> p;

        JSONArray file = JsonIO.readArrayFromFile(fileName);

        Prisoners<Human> p = new Prisoners<>();
        try {
            if (file != null && file.length() > 0) {
                System.out.println("Opening " + fileName + "...");
                for (Object obj : file) {
                    p.add(jsonToHuman((JSONObject) obj));
                }
                System.out.println(fileName + " opened");
            }
        } catch (Throwable e) {
            System.out.println("Something wrong with file");
            return;
        }

        while (!input.equals("stop")) {
            try {
                printGuideToStdOut();
                input = in.nextLine();
                firstWord = input.split(" ")[0];
                arg = getArgument(input);
                Human h;
                //read
                /*
                file = JsonIO.readArrayFromFile(fileName);

                p = new Prisoners<Human>();
                try {
                    if (file != null && file.length() > 0) {
                        for (Object obj : file) {
                            p.addSilent(jsonToHuman((JSONObject) obj));
                        }
                    }
                } catch (Throwable e) {
                    System.out.println("Something wrong with file");
                    return;
                }
                */
                //
                switch (firstWord) {
                    case "remove":
                        if (arg==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        h = jsonToHuman(arg);
                        if (h==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        p.remove(h);
                        break;
                    case "show":
                        p.show();
                        break;
                    case "add_if_max":
                        if (arg==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        h = jsonToHuman(arg);
                        if (h==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        p.addIfMax(h);
                        break;
                    case "remove_lower":
                        if (arg==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        h = jsonToHuman(arg);
                        if (h==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        p.removeLower(h);
                        break;
                    case "info":
                        p.info();
                        break;
                    case "clear":
                        p.clear();
                        break;
                    case "add":
                        if (arg==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        h = jsonToHuman(arg);
                        if (h==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        p.add(h);
                        break;
                    case "stop":
                        System.out.println("Remind: you're leaving the application");
                        break;
                    default:
                        System.out.println("Unknown command");
                }

                JSONArray newFile = new JSONArray();
                if (p.size()>0) {
                    for (Human hh: p.elements) {
                        JSONObject temp = humanToJson(hh);
                        newFile = newFile.put(temp);
                    }
                }
                JsonIO.writeToFile(newFile, fileName);
                pressEnterKey(in);
                //input = in.next();
            }
            catch (Throwable e) {
                System.out.println("Something went wrong");

            }


        }
    }
    public static void saveToJsonFile(Prisoners<Human> p, String fileName) {
        JSONArray newFile = new JSONArray();
        if (p.size()>0) {
            for (Human hh: p.elements) {
                JSONObject temp = humanToJson(hh);
                newFile = newFile.put(temp);
            }
        }
        JsonIO.writeToFile(newFile, fileName);
    }
}
