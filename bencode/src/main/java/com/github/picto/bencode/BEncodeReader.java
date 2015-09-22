package com.github.picto.bencode;

import com.github.picto.bencode.exception.CannotReadBencodedException;
import com.github.picto.bencode.exception.CannotReadTokenException;
import com.github.picto.bencode.type.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Reads a bencoded String and returns an object model.
 *
 * Created by Pierre on 24/08/15.
 */
public class BEncodeReader {

    private final static char SEPARATOR = ':';

    private int index;

    private byte[] content;
    private char token;

    private InputStream inputStream;

    public BEncodeReader(final InputStream inputStream) {

        this.inputStream = inputStream;
        index = -1;
    }

    private void readAll() throws IOException {
        content = IOUtils.toByteArray(inputStream);
        inputStream.close();
    }

    private char read() throws IOException {
        index += 1;
        return (char) content[index];
    }

    /**
     * Reads a token from the input.
     *
     * BEncodeable objects are recursive by nature, and the reader will generate the full Object with all it's BEncoded
     * attributes. Byte arrays don't have a specific end or begin token, and must be parsed differently.
     *
     * A BEncoded object cannot be unmarshalled if it's not a dictionnary.
     *
     *
     * @return The BEncodeable object represented by the stream, or a primitive type.
     */
    private BEncodeTypeToken tokenize() throws CannotReadTokenException {
        try {
            // We read the first character which will decide the token
            token = read();
            return BEncodeTypeToken.findToken(token);
        } catch (IOException e) {
            throw new CannotReadTokenException(String.format("Impossible to read the provided token: %c", token), e);
        }

    }



    /**
     * Reads a length information encoded in base 10 ASCII, starting by adding to an initial one.
     * @param initialLength The intial length to add to
     * @return The length read on the input stream.
     * @throws java.io.IOException
     */
    private int readLength(int initialLength) throws IOException {
        char content;
        while((content = read()) != SEPARATOR) {
            // We are still reading the length
            initialLength *= 10;
            initialLength += Character.getNumericValue(content);
        }
        return initialLength;
    }



    /**
     * Reads an integer formatted as such i<value>e
     * @return The integer value located at the beginning of the input
     * @throws java.io.IOException
     */
    private BEncodeableInteger readInteger() throws IOException {
        long result = 0;
        int multiplier = 1;

        // First read can determine if integer is actually negative.
        char content = read();

        if (content == '-') {
            multiplier = -1;
            content = read();
        }


        while(content != BEncodeTypeToken.END.getToken()) {
            result *= 10;
            result += multiplier * Character.getNumericValue(content);
            content = read();
        }
        return new BEncodeableInteger(result);
    }

    /**
     * Reads a bencoded string (or byte array) located at the beginning of the input. String are represented
     * by a length information, followed by a separator and a content. The content isn't a character string per se
     * but can be any byte array.
     * @param initialLength The initial length read before the String parsing started
     * @return A byte array (charset agnostic) representing the String or payload read.
     * @throws java.io.IOException
     */
    private BEncodeableByteArray readString(final int initialLength) throws IOException {
        final int length = readLength(initialLength);

        // We reached the separator
        byte[] contentArray = new byte[length];
        for(int i = 0; i < length; i++) {
            contentArray[i] = (byte) read();
        }
        return new BEncodeableByteArray(contentArray);
    }

    /**
     * A bencoded representation of a list of elements of identical types, either be it byte[], int, BEncodeable or
     * List
     * @return
     */
    private BEncodeableList readList() throws CannotReadBencodedException, CannotReadTokenException {
        BEncodeableList bencodeableList = new BEncodeableList(BEncodeableType.class);

        BEncodeTypeToken tokenType = tokenize();

        boolean firstElement = true;
        while (tokenType != BEncodeTypeToken.END) {
            BEncodeableType element = readElement(tokenType);

            if (firstElement) {
                firstElement = false;
                bencodeableList = new BEncodeableList(element.getClass());
            }

            bencodeableList.add(element);

            tokenType = tokenize();
        }
        return bencodeableList;
    }

    /**
     * Reads a dictionary into the relevant type.
     * A dictionary is a key=>value map, a key being a bencoded string.
     *
     * The dictionary is the entry point to create complexe object.
     *
     * @return A POJO representation of the dictionary and the stream is pointed at the end of the dictionary
     */
    private BEncodeableDictionary readDictionary() throws CannotReadTokenException, CannotReadBencodedException {
        BEncodeableDictionary dictionary = new BEncodeableDictionary();

        int startIndex = index;

        // We now have to read key=>value pairs
        BEncodeTypeToken tokenType = tokenize();
        while(tokenType != BEncodeTypeToken.END) {
            if (tokenType == BEncodeTypeToken.STRING_START) {
                BEncodeableByteArray key = (BEncodeableByteArray) readElement(tokenType);
                BEncodeableType value = readElement(tokenize());

                // We know the key is always a UTF-8 String
                String keyString;
                try {
                    keyString = new String(key.getBytes(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new CannotReadBencodedException("A dictionary key is malformed.", e);
                }
                dictionary.put(keyString, value);
            }
            tokenType = tokenize();
        }

        int endIndex = index;

        dictionary.setBEncodedBytes(Arrays.copyOfRange(content, startIndex, endIndex + 1));

        return dictionary;
    }

    /**
     * Reads an element on the input according to the token type read during last tokenization.
     * @param tokenType
     * @return
     * @throws CannotReadBencodedException
     */
    private BEncodeableType readElement(final BEncodeTypeToken tokenType) throws CannotReadBencodedException {
        try {
            switch (tokenType) {
                case DICTIONARY_START:
                    return readDictionary();
                case STRING_START:
                    return readString(Character.getNumericValue(token));
                case LIST_START:
                    return readList();
                case INTEGER_START:
                    return readInteger();
                default:
                    throw new CannotReadTokenException(String.format("Bad token in input: %s", tokenType));
            }
        } catch (CannotReadTokenException | IOException e) {
            throw new CannotReadBencodedException("The input cannot be read into a bencoded element.", e);
        }
    }

    /**
     * Reads an InputStream as a BEncodeable object.
     *
     * The BEncoded object is of same type as the class passed to the constructor.
     *
     * BEncodeable objects are recursive by nature, and the reader will generate the full Object with all it's BEncoded
     * attributes. Detection of attribute can only be made on dictionaries, which are the root type anyway.
     *
     * A BEncoded object cannot be unmarshalled if it's not a dictionnary.
     *
     *
     * @return The BEncodeable object represented by the stream, or a primitive type.
     */
    public BEncodeableType readBencodable() throws CannotReadBencodedException, CannotReadTokenException {
        try {
            readAll();
        } catch (IOException e) {
            throw new CannotReadBencodedException("Impossible to read the input stream.", e);
        }

        BEncodeTypeToken tokenType = tokenize();
        BEncodeableType result = readElement(tokenType);
        try {
            inputStream.close();
            return result;
        } catch (IOException e) {
            throw new CannotReadBencodedException("Impossible to close the input stream.", e);
        }
    }

}
