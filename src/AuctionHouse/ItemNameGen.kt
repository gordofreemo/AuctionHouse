package AuctionHouse


/**
 * Class for generating random item names.
 * Conjoins random adjectives together with random nouns.
 * The files representing the nouns/adjectives should be in the resources
 * folder and the filenames are specified in the constructor. Throws an exception
 * if there is an error with the file.
 *
 * call getItemName() to get your random item name.
 */
class ItemNameGen(nounFile: String, adjFile: String) {
    private val nouns = mutableListOf<String>().apply {
        addAll(ClassLoader.getSystemResourceAsStream(nounFile)!!.bufferedReader().lines().toList())
    }

    private val adjectives = mutableListOf<String>().apply {
        addAll(ClassLoader.getSystemResourceAsStream(adjFile)!!.bufferedReader().lines().toList())
    }

    /**
     * @return - Random item name taken from the two word files
     */
    val itemName: String
        get() = "${adjectives.random()} ${nouns.random()}"
}
