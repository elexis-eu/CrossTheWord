package it.uniroma1.lcl.crucyservlet.mongodb.crossword;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Domains;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Language;
import it.uniroma1.lcl.crucyservlet.ClassiBuilder.enumerazioni.Size;
import it.uniroma1.lcl.crucyservlet.mongodb.Difficulty;
import it.uniroma1.lcl.crucyservlet.mongodb.exception.ParsingExeption;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;

public class Crossword {

    // DB info
    //private String id ;
    private Difficulty difficulty = Difficulty.EASY;
    private Domains domain = Domains.MIXED;
    private Size size;
    private Language language;
    private int level = -1;

    private int rows;
    private int columns;

    private ObjectId id;

    // Crossword info
    private List<WordPosition> definitions;

    private Crossword(String crosswordString, Language language) throws ParsingExeption {
        this.definitions = new ArrayList<>();
        this.language = language;
        this.id = new ObjectId();
        carica(crosswordString);
        //this.id = Integer.toString(crosswordString.hashCode());
    }

    private Crossword (ObjectId id,Difficulty difficulty, Domains domain, int rows, int columns, List<WordPosition> definitions, Language language) {
        this.id = id;
        this.language = language;
        this.difficulty = difficulty;
        this.domain = domain;
        this.rows = rows;
        this.columns = columns;
        this.definitions = definitions;
        if (rows*columns > 50) size = Size.BIG;
        else if (rows*columns > 30) size = Size.MEDIUM;
        else size = Size.SMALL;
    }

    private Crossword (ObjectId id,Difficulty difficulty, Domains domain, int rows, int columns, List<WordPosition> definitions, Language language, int level) {
        this(id, difficulty, domain, rows, columns, definitions, language);
        if (level > 0) this.level = level;
    }

    public static Crossword loadCrossword(Path path, Language language) throws IOException, ParsingExeption {
        BufferedReader reader = Files.newBufferedReader(path);
        String cross = reader.lines().collect(Collectors.joining("\n"));
        Crossword crossword = new Crossword(cross, language);
        return crossword;
    }

    public static Crossword parseCrossword(String crosswordString, Language language) throws ParsingExeption {
        return new Crossword(crosswordString, language);
    }

    private void carica(String crosswordString) throws ParsingExeption {
        String[] lines = crosswordString.split("\n");
        for (int i = 0; i < lines.length; i++) lines[i] = lines[i].trim();

        Pattern pattern = Pattern.compile("([0-9]*)x([0-9]*)");

        String headerLine = lines[0];
        Matcher match = pattern.matcher(headerLine);
        if (!match.find()) throw new ParsingExeption("Header not found");

        int rows = Integer.parseInt(match.group(1));
        int cols = Integer.parseInt(match.group(2));

        this.rows = rows;
        this.columns = cols;

        /*if (rows*cols > 50) size = Size.BIG;
        else if (rows*cols > 30) size = Size.MEDIUM;
        else size = Size.SMALL;*/

        System.out.println(rows);
        System.out.println(cols);
        int index = 1;

        char[][] matrix = new char[rows][cols];

        while (!lines[index].equals("Horizontal:")) {
            for (int i = 0; i < rows ; i++) {
                String[] cells = lines[index].split(",");
                System.out.println(lines[index]);
                for (int j = 0; j < cols; j++) {
                    char character = cells[j].charAt(0);
                    if (cells[j].length() == 1) {
                        if (character == '*')
                            matrix[i][j] = '*';
                        else
                            matrix[i][j] = ' ';
                    }
                }
                index++;
            }
        }


        index++;
        while (!lines[index].equals("Vertical:")) {
            addDefinition(lines[index], true);
            index++;
        }

        index++;
        while (index < lines.length) {
            addDefinition(lines[index], false);
            index++;
        }

        System.out.println(this.definitions);


    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Language getLanguage() {
        return language;
    }

    public Domains getDomain() {
        return domain;
    }

    public Size getSize() {
        return size;
    }

    public ObjectId getId() {
        return id;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public List<WordPosition> getDefinitions() {
        return new ArrayList<>(definitions);
    }

    private void addDefinition(String line, boolean horizontal) throws ParsingExeption {
        Pattern pattern = Pattern.compile("\\(([0-9]*),([0-9]*)\\) (bn:[0-9]*[a-z])::(.*)::(.*)::(.*)::((EASY)|(MEDIUM)|(HARD))");

        Matcher match = pattern.matcher(line);
        if (!match.find()) throw new ParsingExeption("Definition syntax error");

        int row = Integer.parseInt(match.group(1));
        int col = Integer.parseInt(match.group(2));

        definitions.add(new WordPosition(match.group(3), row, col, match.group(4), match.group(5), horizontal, match.group(6), Difficulty.valueOf(match.group(7))));
    }

    public void saveInDB(MongoCollection<Document> crosswords) {
        System.out.println(language.toString());
        crosswords.insertOne(new Document()
                .append(CROSSWORD_ID, id)
                .append(DEFINITIONS, definitions)
                //.append(SIZE, size.name())
                .append(ROWS, rows)
                .append(COLUMNS, columns)
                .append(LANGUAGE, language.toString())
                .append(DOMAIN, domain.toString())
                .append(LEVEL, level)
                .append(DIFFICULT, difficulty.toString())); //TODO Vedere come aggiungere la difficoltà
    }

    public static List<Crossword> getCrosswords(MongoCollection<Document> crosswords, Bson query, int limit) {
        FindIterable<Document> iterableCrosswords;

        if (limit <= 0) {
            iterableCrosswords = crosswords.find(query);
        } else {
            iterableCrosswords = crosswords.find(query).limit(limit);
        }

        List<Crossword> out = new ArrayList<>();
        for (Document doc : iterableCrosswords) {

            List<Document> definitions = (List<Document>) doc.get(DEFINITIONS);
            List<WordPosition> definitionsW = new ArrayList<>();

            for (Document wp : definitions) {
                definitionsW.add(new WordPosition(
                        wp.getString(DEFINITION_ID),
                        wp.getInteger(ROW),
                        wp.getInteger(COLUMN),
                        wp.getString(WORD),
                        wp.getString(DEFINITION),
                        wp.getBoolean(HORIZONTAL),
                        wp.getString(DOMAIN),
                        Difficulty.valueOf(wp.getString(DIFFICULT))
                ));
            }

            out.add(new Crossword(doc.getObjectId(CROSSWORD_ID), Difficulty.valueOf(doc.getString(DIFFICULT)),
                    Domains.valueOf(doc.getString(DOMAIN)), doc.getInteger(ROWS),
                    doc.getInteger(COLUMNS),definitionsW, Language.valueOf(doc.getString(LANGUAGE)),
                    doc.getInteger(LEVEL)));

        }
        return out;
    }


    public static void main(String[] args) throws ParsingExeption, IOException {
        //Crossword.loadCrossword("prova_crossword.txt");
    }

    private String gridToString(char[][] grid) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                strBuilder.append(grid[i][j]).append(",");
            }
            strBuilder.append("\n");
        }
        return strBuilder.toString();
    }

    @Override
    public String toString() {
        char[][] matrix = new char[rows][columns];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = '*';
            }
        }

        List<WordPosition> horizontal = new ArrayList<>();
        List<WordPosition> vertical = new ArrayList<>();

        for (WordPosition def : definitions) {
            String word = def.getWord();
            int len = word.length();

            if (def.isHorizontal()) {
                horizontal.add(def);
                for (int col = def.getCol(); col < def.getCol() + len; col++) {
                    matrix[def.getRow()][col] = word.charAt(col - def.getCol() );
                }
            } else {
                vertical.add(def);
                for (int row = def.getRow(); row < def.getRow() + len; row++) {
                    matrix[row][def.getCol()] = word.charAt(row - def.getRow());
                }
            }
        }

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(rows).append('x').append(columns).append('\n');
        strBuilder.append(gridToString(matrix));
        strBuilder.append("Horizontal:\n");
        horizontal.forEach((str) -> strBuilder.append(str).append('\n'));
        strBuilder.append("Vertical:\n");
        vertical.forEach((str) -> strBuilder.append(str).append('\n'));
        return strBuilder.toString();

    }


}
