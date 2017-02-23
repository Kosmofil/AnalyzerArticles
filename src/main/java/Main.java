import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;

public class Main {

    static List<String> testPrint;

    public static void main(String[] args) throws IOException {

        Main.get();

    }

    public static void get() throws IOException {
        // TODO: 22.02.17 разобраться заменить forEach
        // TODO: 22.02.17 переделать метод по совету NS (без интернета!)

        List<Document> documents = new ArrayList<>();
        final String url = "https://habrahabr.ru/top/alltime/";
                rangeClosed(0,3).forEach(i ->{
                    try {
                        documents.add(Jsoup.connect(url + "page" + i).userAgent("Mozilla").get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                Collection<String> row = documents.stream()
                        .map(i->i.select(".post__title_link"))
                        .map(w->w.outerHtml())//далее проход по ссылкам и обработка текста.
                        .collect(Collectors.toList());

    }



    private static List<Elements> getPost(String cssQuery){

        List<Elements> list = new ArrayList<>();
        list.stream().map(i->i.select(cssQuery));
        return list;
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
