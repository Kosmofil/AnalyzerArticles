import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;

public class Main {

    public static void main(String[] args) throws IOException {

        double startTime = System.currentTimeMillis();
        Main.startAnalysis();
        double result = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println(result + "seconds" + " or " + result / 60 + " minuts");

    }


    private static final int ONE_WORD = 1;
    private static final String SPACE = " ";
    private static final String APOSTROPHE = "[’]";// все испольуемые апострофы
    private static final String TRUE_APOSTROPHE = "'";// применяемый, стандартный апостроф
    private static final String ONLY_LATIN_CHARACTERS = "[^a-z\\s']";//не маленькие латинские буквы, не пробел и не апостраф
    private static final String SPACES_MORE_ONE = "\\s{2,}";

    private static void startAnalysis() throws IOException {

        List<Document> documents = new ArrayList<>();
        final String url = "https://habr.com/en/top/alltime/";
        rangeClosed(1, 50).forEach(i -> {
            documents.add(getElements(url + "page" + i).get());
        });
        Collection<String> articleText = documents.stream()//получили весь текст со статей
                .map(i -> i.select(".post__title_link"))//+
                .flatMap(w -> w.stream()
                        .map(q -> q.absUrl("href")))//получили список ссылок на каждую статью
                .map(t -> getElements(t).get().select(".post_show"))
                .map(z -> getTextFromElements(z, "div[class=post__body post__body_full]"))//получаем текст статей
                .map(t -> t.toLowerCase()
                        .replaceAll(APOSTROPHE, TRUE_APOSTROPHE)
                        .replaceAll(ONLY_LATIN_CHARACTERS, SPACE)
                        .replaceAll(SPACES_MORE_ONE, SPACE))
                .collect(Collectors.toList());

        //ниже выводим количество слов
        Map<String, Integer> countWordMaps = getWordsMap(articleText.toString());
        countWordMaps.entrySet().forEach(System.out::println);

    }

    private static String getTextFromElements(Elements element, String cssQuery) {//получаем текст
        Elements result = element.select(cssQuery);
        if (result != null) {
            return result.text();
        }
        return null;//null продумать
    }

    private static Optional<Document> getElements(String url) {
        Optional<Document> documents = Optional.empty();
        try {
            documents = Optional.ofNullable(Jsoup.connect(url).get());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return documents;
    }

    private static Map<String, Integer> getWordsMap(String text) {

        Map<String, Integer> wordsMap = new HashMap<>();

        String newWord;
        Pattern patternWord = Pattern.compile("(?<word>[a-z']+)");
        Matcher matcherWord = patternWord.matcher(text);

        while (matcherWord.find()) {
            newWord = matcherWord.group("word");

            if (wordsMap.containsKey(newWord)) {
                wordsMap.replace(newWord, wordsMap.get(newWord) + ONE_WORD); // если слово уже есть в Map то увеличиваеи его количество на 1
            } else {
                wordsMap.put(newWord, ONE_WORD);// если слова в Map нет то добавляем его со значением 1
            }
        }
        return wordsMap;
    }
}