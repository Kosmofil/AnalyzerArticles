import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    static List<String> testPrint;

    public static void main(String[] args) throws IOException {

        Main text = new Main();
        text.get();

    }

    public void get() throws IOException {

        List<Document> pages = new ArrayList<>();
        // TODO: 22.02.17 разобраться заменить forEach
        IntStream.range(1, 3).forEach((i) -> {
            try {
                pages.add(Jsoup.connect("https://habrahabr.ru/top/alltime/" + "page" + i).userAgent("Mozilla").get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // TODO: 22.02.17 переделать метод по совету NS (без интернета!)

        //Elements element = doc.select(".shortcuts_item");//    List<Element> element = doc.select(".shortcuts_item");

        List<Elements> row = pages.stream()
                .map((q) -> q.select(".shortcuts_item"))
                .collect(Collectors.toList());

        List<String> title = row.stream()
                .map((s) -> getNewText(s, "a[class=post__title_link]"))
                .collect(Collectors.toList());

        // TODO: 21.02.17 продумать вход в каждую статью 
        List<String> text = row.stream()
                .map((s) -> getNewText(s, "div[class=content html_format]"))
                .collect(Collectors.toList());

    }

    private static String getNewText(Elements element, String cssQuery) {//получаем текст

        Elements result = element.select(cssQuery);
        if (result != null) {
            return result.text();
        }
        return null;//null продумать
    }

    //тестовый метод не забыть убрать
    public static void showAll(Collection<EntryData> saver) {
        List<EntryData> select = saver.stream().filter((s) -> s.getTitle() != null).collect(Collectors.toList());
        System.out.println(select);
    }
}
