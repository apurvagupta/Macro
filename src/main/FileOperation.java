package main;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Sequence;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.numbers.Numbers.range;

public class FileOperation {
    JSONObject parse;
    JSONObject actionVariables;
    Scanner scanner;
    private static Logger logger = Logger.getLogger("FileOperation");

    public FileOperation() {
        JSONParser jsonParser = new JSONParser();
        scanner = new Scanner(System.in);
        try {
            this.parse = (JSONObject) jsonParser.parse(new FileReader("resource/functions.json"));
        } catch (IOException e) {
            logger.info(e.getMessage());
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
    }

    public JSONObject getParse() {
        return parse;
    }

    public JSONObject getActionVariables(String action) {
        return (JSONObject) parse.get(action);
    }

    public int getNumberOfFiles() {
        return Integer.parseInt((String) actionVariables.get("numberOfFiles"));
    }

    public int getLines() {
        return Integer.parseInt((String) actionVariables.get("lineNumber"));
    }

    public int getCursorPosition() {
        return Integer.parseInt((String) actionVariables.get("cursorPosition"));
    }

    public String getText() {
        return (String) actionVariables.get("text");
    }

    public void input() {
        Scanner scanner = new Scanner(System.in);
        JSONObject parse = getParse();
        System.out.println("Choose what you want to perform: \n" + parse.keySet());
        String action = scanner.nextLine();

        actionVariables = getActionVariables(action);
        Iterator keys = actionVariables.keySet().iterator();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            System.out.println(String.format("Enter value for %s: ", key));
            String keyValue = scanner.nextLine();
            actionVariables.put(key, keyValue);
        }

        List<String> files = new ArrayList<String>();
        for (int i = 0; i < getNumberOfFiles(); i++) {
            System.out.println(String.format("Enter path file[%d]", i));
            files.add(scanner.nextLine());
        }

        performAction(actionVariables, files);

    }

    private void performAction(final JSONObject actions, final List onFiles) {

        logger.info(String.format("perform these actions %s on these files %s", actions, onFiles));

        Sequence<Number> numbers = range(0, onFiles.size() - 1);
        List<String> output = sequence(numbers).map(new Function1<Number, String>() {
            @Override
            public String call(Number index) throws Exception {
                File file = new File((String) onFiles.get(index.intValue()));
                RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
                skipLines(randomFile);
                randomFile.write(getText().getBytes());
                logger.info("done");
                return null;
            }
        }).toList();

        logger.info("some output" + output);
    }

    private void skipLines(RandomAccessFile file) {
        int index = 0;
        try {
            RandomAccessFile tempFile = new RandomAccessFile(new File(File.createTempFile("kachara", "txt").getAbsolutePath()), "rw");
            while (file.readLine() != null) {
                if (getLines() - 1 > index) {
                    tempFile.write(file.readLine().getBytes());
                }else if(getLines() - 1 == index){
                    String line = file.readLine();
                    String substring = line.substring(0, getCursorPosition() -1);
                    tempFile.write(substring.getBytes());
                }
            }
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage());
        } catch (IOException e) {
            logger.info(e.getMessage());
        }

    }
}
