package com.github.picto.network.http.stringify;


/**
 * Created by Pierre on 27/08/15.
 */
public class HashStringifier extends DefaultStringifier {
    @Override
    public String apply(Object o) {
        if (!(o instanceof byte[])) {
            throw new IllegalStateException("Impposible to stringify a hash that is not in String form");
        }
        // The hash is in \xAB\x10, we want it as an ascii string
        byte[] bytes = (byte[]) o;

        return byteArrayToURLString(bytes);
    }

    public static String byteArrayToURLString(byte bytes[]) {
        byte ch;
        int i = 0;

        String hex[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
        StringBuilder result = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            // First check to see if we need ASCII or HEX
            if ((b >= '0' && b <= '9')
                || (b >= 'a' && b <= 'z')
                || (b >= 'A' && b <= 'Z')
                || b == '$'
                || b == '-'
                || b == '_'
                || b == '.'
                || b == '!') {

                result.append((char) b);
            } else {
                // We need HEX
                result.append('%');
                ch = (byte) (b & 0xF0); // Strip off high nibble
                ch = (byte) (ch >>> 4); // shift the bits down
                ch = (byte) (ch & 0x0F);

                result.append(hex[(int) ch]); // convert the nibble to a hex string
                // String Character
                ch = (byte) (b & 0x0F); // Strip off low nibble
                result.append(hex[(int) ch]); // convert the nibble to a
                // String Character
            }
        }

        return result.toString();

    }
}
