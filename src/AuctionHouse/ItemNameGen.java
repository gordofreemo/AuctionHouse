package AuctionHouse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for generating random item names.
 * Conjoins random adjectives together with random nouns.
 * The files representing the nouns/adjectives should be in the resources
 * folder and the filenames are specified in the constructor. Throws an exception
 * if there is an error with the file.
 *
 * call getItemName() to get your random item name.
 */

public class ItemNameGen {
    private ArrayList<String> nouns;
    private ArrayList<String> adjectives;

    public ItemNameGen(String nounFile, String adjFile) throws IOException {
        InputStream s1 = ClassLoader.getSystemResourceAsStream(nounFile);
        InputStream s2 = ClassLoader.getSystemResourceAsStream(adjFile);
        nouns = new ArrayList<>();
        adjectives = new ArrayList<>();
        parseNouns(s1);
        parseAdjectives(s2);
        s1.close();
        s2.close();
    }

    /**
     * @return - Random item name taken from the two word files
     */
    public String getItemName() {
        int r1 = (int)(Math.random() * nouns.size());
        int r2 = (int)(Math.random() * adjectives.size());
        return adjectives.get(r2) + ' ' + nouns.get(r1);
    }

    /**
     * Parse the noun file and add the nouns to a list.
     * @param stream - input stream of the noun file
     */
    private void parseNouns(InputStream stream) {
        Scanner sc = new Scanner(stream);
        while(sc.hasNextLine()) nouns.add(sc.nextLine());

    }

    /**
     * Parse the adjective file and add the adjectives to a list
     * @param stream - input stream of adjective file
     */
    private void parseAdjectives(InputStream stream) {
        Scanner sc = new Scanner(stream);
        while(sc.hasNextLine()) adjectives.add(sc.nextLine());
    }

}
