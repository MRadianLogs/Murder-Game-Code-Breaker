import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CodeBreaker {

    public static void BreakCode(String codeToBreak) {

        Map<String, ArrayList<String>> ciphers = new HashMap<>();
        ciphers.put("=", new ArrayList<String>(Arrays.asList("L", "T", "V", "X")));// =
        ciphers.put("triangle", new ArrayList<String>(Arrays.asList("A", "F", "H", "I", "K", "N", "Y", "Z")));// triangle
        ciphers.put("square", new ArrayList<String>(Arrays.asList("E", "M", "W")));// square
        ciphers.put("c", new ArrayList<String>(Arrays.asList("C", "U")));// c
        ciphers.put("o", new ArrayList<String>(Arrays.asList("O", "S")));// o
        ciphers.put("hat", new ArrayList<String>(Arrays.asList("D", "G", "J", "P")));// hat
        ciphers.put("0", new ArrayList<String>(Arrays.asList("B", "Q")));// 0
        ciphers.put("hatline", new ArrayList<String>(Arrays.asList("R")));// hatline

        // Create Map of possible answers.
        HashMap<Integer, String> possibleAnswers = new HashMap<>();

        for (String codedChar : codeToBreak.split(" ")) {
            System.out.println("Decoding " + codedChar + "...");

            ArrayList<String> possibleLetters = ciphers.get(codedChar);

            if (possibleAnswers.size() <= 0) {
                for (String possibleLetter : possibleLetters) {
                    possibleAnswers.put(possibleAnswers.size(), possibleLetter);
                }
            } else {
                Map<Integer, String> tempPossibleAnswers = copy(possibleAnswers);
                for (int j = 0; j < possibleLetters.size(); j++) {
                    for (int i = 0; i < tempPossibleAnswers.size(); i++) {
                        if (j < 1) {
                            // Replace answer.
                            possibleAnswers.put(i, tempPossibleAnswers.get(i) + possibleLetters.get(j));
                        } else {
                            // Add new answer.
                            possibleAnswers.put(possibleAnswers.size(),
                                    tempPossibleAnswers.get(i) + possibleLetters.get(j));
                        }
                    }
                }
            }

        }

        System.out.println("Total number of combinations: " + possibleAnswers.size());
        System.out.println("Determining actual words...");
        ArrayList<String> actualWords = new ArrayList<>();

        for (String possibleAnswer : possibleAnswers.values()) {
            // System.out.println("Is \"" + possibleAnswer + "\" a word..?");
            // Make API call to see if it is a word..
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.dictionaryapi.dev/api/v2/entries/en/" + possibleAnswer))
                    // .header("X-RapidAPI-Host", "jokes-by-api-ninjas.p.rapidapi.com")
                    // .header("X-RapidAPI-Key", "your-rapidapi-key")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = null;

            try {
                response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                if (null != response) {
                    // System.out.println("Response for " + possibleAnswer + ": " +
                    // response.statusCode());
                    if (response.statusCode() == 200)
                        actualWords.add(possibleAnswer);
                }
            } catch (Exception e) {
                // Do nothing.
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("-------------------------------------------------");
        System.out.println("Possible answers: ");
        for (int i = 0; i < actualWords.size(); i++) {
            System.out.println("" + (i + 1) + ": " + actualWords.get(i));
        }

    }

    private static HashMap<Integer, String> copy(
            HashMap<Integer, String> original) {
        HashMap<Integer, String> copy = new HashMap<Integer, String>();
        for (Map.Entry<Integer, String> entry : original.entrySet()) {
            copy.put(entry.getKey(),
                    // Or whatever List implementation you'd like here.
                    new String(entry.getValue()));
        }
        return copy;
    }

    public static void main(String[] args) {
        // BreakCode("triangle triangle"); // Hi
        // BreakCode("hat triangle triangle ="); // Gift
        // BreakCode("c triangle hat = triangle triangle triangle"); // captain
        // BreakCode("0 o hatline square hat"); // bored
        BreakCode("c o o ="); // cool
    }
}