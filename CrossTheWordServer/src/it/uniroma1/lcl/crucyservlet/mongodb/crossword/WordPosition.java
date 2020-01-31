package it.uniroma1.lcl.crucyservlet.mongodb.crossword;

import com.google.gson.*;
import it.uniroma1.lcl.crucyservlet.mongodb.Difficulty;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.lang.reflect.Type;
import static it.uniroma1.lcl.crucyservlet.utils.Parameters.*;


public class WordPosition {
    private int row;
    private int col;

    private final String ID;
    private String word;
    private String wordDef;
    private boolean horizontal;
    private Difficulty diff;
    private String domain;

    public WordPosition (String ID, int row, int col, String word, String wordDef, boolean horizontal, String domain, Difficulty diff) {
        this.ID = ID;
        this.row = row;
        this.col = col;
        this.word = word;
        this.wordDef = wordDef;
        this.horizontal = horizontal;
        this.diff = diff;
        this.domain = domain;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getID() {
        return ID;
    }

    public String getWord() {
        return word;
    }

    public String getWordDef() {
        return wordDef;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public Difficulty getDiff() {
        return diff;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d) %s::%s::%s::%s::%s",this.row, this.col, this.ID, this.word, this.wordDef, this.domain, this.diff);
    }

    public class WordPositionSerializer implements JsonSerializer<WordPosition>, JsonDeserializer<WordPosition> {


        @Override
        public JsonElement serialize(WordPosition wordPosition, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result = new JsonObject();
            result.add(DEFINITION_ID, new JsonPrimitive(wordPosition.getID()));
            result.add(ROW , new JsonPrimitive(wordPosition.getRow()));
            result.add(COLUMN , new JsonPrimitive(wordPosition.getCol()));
            result.add(DIFFICULT , new JsonPrimitive(wordPosition.getDiff().toString()));
            result.add(DOMAIN , new JsonPrimitive(wordPosition.getDomain().toString()));
            result.add(WORD , new JsonPrimitive(wordPosition.getWord()));
            result.add(DEFINITION , new JsonPrimitive(wordPosition.getWordDef()));
            result.add(HORIZONTAL , new JsonPrimitive(wordPosition.isHorizontal()));

            return result;
        }

        @Override
        public WordPosition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject json = jsonElement.getAsJsonObject();
            return new WordPosition(json.get(DEFINITION_ID).getAsString(), json.get(ROW).getAsInt(), json.get(COLUMN).getAsInt(), json.get(WORD).getAsString(), json.get(DEFINITION).getAsString(),
                    json.get(HORIZONTAL).getAsBoolean(),json.get(DOMAIN).getAsString(), Difficulty.valueOf(json.get(DIFFICULT).getAsString()));
        }
    }

    public static class WordPositionCodec implements Codec<WordPosition> {

        @Override
        public WordPosition decode(final BsonReader reader, final DecoderContext decoderContext) {
            return new WordPosition(
                    reader.readString(DEFINITION_ID),
                    reader.readInt32(ROW),
                    reader.readInt32(COLUMN),
                    reader.readString(WORD),
                    reader.readString(DEFINITION),
                    reader.readBoolean(HORIZONTAL),
                    reader.readString(DOMAIN),
                    Difficulty.valueOf(reader.readString(DIFFICULT))
            );
        }

        @Override
        public void encode(BsonWriter bsonWriter, WordPosition wordPosition, EncoderContext encoderContext) {
            bsonWriter.writeStartDocument();
            bsonWriter.writeString(DEFINITION_ID,wordPosition.getID());
            bsonWriter.writeInt32(ROW , wordPosition.getRow());
            bsonWriter.writeInt32(COLUMN , wordPosition.getCol());
            bsonWriter.writeString(DIFFICULT , wordPosition.getDiff().toString());
            bsonWriter.writeString(DOMAIN , wordPosition.getDomain());
            bsonWriter.writeString(WORD , wordPosition.getWord());
            bsonWriter.writeString(DEFINITION , wordPosition.getWordDef());
            bsonWriter.writeBoolean(HORIZONTAL , wordPosition.isHorizontal());
            bsonWriter.writeEndDocument();
        }

        @Override
        public Class<WordPosition> getEncoderClass() {
            return WordPosition.class;
        }

    }

}
