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

        List<Document> documents = new ArrayList<>();
        final String url = "https://habrahabr.ru/top/alltime/";
        rangeClosed(0, 1).forEach(i -> {
            try {
                documents.add(Jsoup.connect(url + "page" + i).userAgent("Mozilla").get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Collection<String> text = documents.stream()//получили весь текст со статей
                .map(i -> i.select(".post__title_link"))//+
                .flatMap(w -> w.stream()
                        .map(q -> q.absUrl("href")))//получили список ссылок на каждую статью +
                .map(t -> {
                    try {
                        return Jsoup.connect(t).get().select(".post_show");//без коннекта, разобраться с парсером
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;//убрать нул
                })
                .map(z -> getNewText(z, "div[class=post__body post__body_full]"))
                // TODO: 28.02.17 нужен метод красиво обрабатывающий текст
                .collect(Collectors.toList());

        // TODO: 28.02.17 сделать метод выводящий статистику

    }


    private static List<Elements> getPost(String cssQuery) {
        List<Elements> list = new ArrayList<>();
        list.stream().map(i -> i.select(cssQuery));
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
