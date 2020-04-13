package Crawler;

import NLP.StanfordNLP;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class crawler {

    public static boolean ReadFile(File urls) throws IOException {
        if (urls.exists()) {
            Scanner reader = new Scanner(urls);
            while (reader.hasNext()) {
                urlToVisit.add(reader.nextLine());
            }
            return true;
        } else {
            System.out.println("The file doesn't exist.");
            return false;
        }
    }

    public static boolean GetSentences(String article, boolean stopSign) throws IOException {
        ArrayList<String> sentences_array=new ArrayList<>();
        article = article.replaceAll("\\[.*?\\]","");
        article = article.replaceAll("\\(.*?\\) ?", "");
        SENTENCE = article.split("(?<=[.!?^;])\\s*");
        for (int i=0;i<SENTENCE.length;i++) {
            SENTENCE[i] = SENTENCE[i].replaceAll("[^a-zA-Z0-9,.\\s ]","");
            if(SENTENCE[i].length()>50)
                sentences_array.add(SENTENCE[i]);
        }

        for(int i=0;!stopSign && i < sentences_array.size();i++){
            // System.out.println("Sentence " + (i+1) +" " + sentences_array.get(i).length()+": " + sentences_array.get(i));
            //StanfordNLP NLP = new StanfordNLP();
            //StanfordCoreNLP pipline = NLP.initialStanfordNLP();
            System.out.println(System.currentTimeMillis());
            List<List<String>> wordlist = NLP.getRelation(pipline,sentences_array.get(i));
            System.out.println(System.currentTimeMillis());
            System.out.println(wordlist);
            for(List<String> wordrelation:wordlist){
                String isrelation  = jedis.hget(wordrelation.get(0),wordrelation.get(1));
                if(isrelation==null)
                    jedis.hset(wordrelation.get(0),wordrelation.get(1),wordrelation.get(2));
                else if(isrelation!=null)
                {
                    isrelation = isrelation+" "+wordrelation.get(2);
                    jedis.hset(wordrelation.get(0),wordrelation.get(1),isrelation);
                }
            }
            System.out.println(System.currentTimeMillis());
            String memory = jedis.info();
            String[] memory_info = memory.split("\r\n");

            //  For windows, index = 18 . For mac, 33
            String[] temp = memory_info[18].split(":");
            String peak = temp[1];
            Integer memory_peak = Integer.valueOf(peak);
            System.out.println(memory_peak);

            if( memory_peak >= 2000000) {
                stopSign = true;
                System.out.println(memory_peak);
                return stopSign;
            }

        }
        return stopSign;
    }

    public static void GetText(Queue<String> urlToVisit) throws IOException{
        boolean stopSign = false;
        while (!urlToVisit.isEmpty() && stopSign == false) {
            final String url_s = urlToVisit.poll();
            if (url_s != null) {
                bf.add(url_s);
                System.out.println("Poll : " + url_s);
                try {
                    URL url_u = new URL(url_s);
                    //String path = url_u.getFile().substring(0, url_u.getFile().lastIndexOf('/'));
                    String base = url_u.getProtocol() + "://" + url_u.getHost();

                String html = Jsoup.connect(url_s).ignoreHttpErrors(true)
                        .header("connection", "Keep-Alive")
                        .get().html();
                Document doc = Jsoup.parse(html);
                Elements links = doc.getElementsByTag("a");
                for (Element link : links) {
                    String linkHref = link.attr("href");
                    if (!linkHref.startsWith("http")) {
                        linkHref = base + linkHref;
                    }
                    if (!bf.contains(linkHref)) {
                        urlToVisit.add(linkHref);
                        bf.add(linkHref);
                        System.out.println("Add : " + linkHref);
                    } else {
                        System.out.println("Duplicate : " + linkHref);
                    }
                }
                //System.out.println(counter + doc.text());
                System.out.println(counter + doc.select("p").text());
                String article = doc.select("p").text();
                stopSign = GetSentences(article, stopSign);
                counter++;
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                continue;
            }
        }
    }
    public static void main(String args[]) throws IOException {
        // open file
        File urls = new File(args[0]);
        //File urls = new File("src/Crawler/test.txt");
        boolean isExist = ReadFile(urls);
        if (isExist) {
            GetText(urlToVisit);
        }


    }



    private static StanfordNLP NLP = new StanfordNLP();
    private static StanfordCoreNLP pipline = NLP.initialStanfordNLP();
    public static Jedis jedis = new Jedis();
    private static Queue<String> urlToVisit = new ArrayDeque<>();
    private static int counter = 1;
    private static String[] SENTENCE;
    private static BloomFilter bf = new BloomFilter(10000, 1<<25);
}




