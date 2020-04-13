package Checker;

import NLP.StanfordNLP;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static Crawler.crawler.jedis;

public class nlpchecker {
    //public static Jedis jedis = new Jedis();
    public static void ReadFile(File urls) throws IOException {
        if(urls.exists()){
            Path path = Paths.get(String.valueOf(urls));
            byte[] bytes = Files.readAllBytes(path);
            input_strings = Files.readAllLines(path, StandardCharsets.UTF_8);
            System.out.println("length of line "+ input_strings.size());
            for(String temp:input_strings)
            {
                System.out.println("the line is "+temp);
            }
        }
        /*if (urls.exists()) {

            Scanner reader = new Scanner(urls);
            while (reader.hasNext()) {
                input_strings.add(reader.nextLine());
                System.out.println(reader.nextLine());
            }
        } else {
            System.out.println("The file doesn't exist.");
        }*/
    }

    public static boolean checkrelation(List<String> wordrelation){
        String wordone = wordrelation.get(0);
        String wordtwo = wordrelation.get(1);
        String wordrela = wordrelation.get(2);
        String relationn  = jedis.hget(wordone,wordtwo);
        System.out.println("relation   "+wordone+" "+wordtwo+" "+relationn);
        boolean check = false;
        if(relationn!=null){
        if(relationn.contains(wordrela)) {
            check = true;
        }
        }
        return check;
    }

    public static Integer checkcorrection(List<Integer> list,List<Boolean> check)
    {
        Integer false_result = 0;
        for(Integer num:list)
        {
            if(!check.get(num))
                false_result++;
        }
        float result = ((float)false_result/(float)list.size())*100;
        int correction = Math.round(result);
        System.out.println("correction is "+correction);
        return correction;
    }

    public static String getpharse(List<String> words,Integer small,Integer large)
    {
        String pharse = new String();
        pharse = "";
        for(int i=small-1;i<large-1;i++)
        {
            //System.out.println("i is "+i+" "+words.get(i));
            if(words.get(i).equals(","))
                continue;
            else
                pharse = pharse + words.get(i)+" ";
        }
        pharse = pharse + words.get(large-1);
        System.out.println("pharse is "+pharse);
        //System.out.println("i is "+(large-1)+" "+words.get(large-1));
        return pharse;
    }

    public static void getjsonresult(List<List<String>> wordlist, String text1) throws IOException {
        List<Boolean> check_result = new ArrayList();
        List<List<String>> wrong = new ArrayList<>();
        int num = 0;
        for(List<String> wordrelation : wordlist){
            boolean ifrelation = checkrelation(wordrelation);
            check_result.add(ifrelation);
            if(!ifrelation) {
                num = num + 1;
                wrong.add(wordrelation);
                System.out.println("wrong"+wordrelation);
            }
        }
        if(num>0) {
            System.out.println(check_result);

            float result = ((float) (num) / (float) wordlist.size()) * 100;
            int correction = Math.round(result);
            //JSONObject json = new JSONObject();
            //JSONObject json1 = new JSONObject();
            //json.put(text1,Integer.toString(correction));
            json.put(text1, correction);
            //JSONObject final_result = new JSONObject();
            //final_result.put("sentences",json);
            //System.out.println(final_result.toString());
            ArrayList<String> total_words = new ArrayList<>();
            String[] sentences = text1.split(",");
            for (int i = 0; i < sentences.length; i++) {
                System.out.println(sentences[i]);
                String[] words = sentences[i].split("\\s+");
                //System.out.println("this is first");
                for (String word : words) {
                    System.out.println(word);
                    if (word.equals("")) {
                        //System.out.println("this is empty");
                        continue;
                    } else {
                        total_words.add(word);
                    }
                    //total_words.add(word);
                    //System.out.println(word);
                }
                if (i < (sentences.length - 1)) {
                    total_words.add(",");
                }

            }
            int len = total_words.size();
            List<Integer> sep = new ArrayList<>();
            for(int i=0;i<total_words.size();i++)
            {
                String ss = total_words.get(i);
                if(ss.equals(","))
                    sep.add(i);
            }
            //System.out.println("sep "+sep);
            if (total_words.get(len - 1).contains(".")) {
                //System.out.println("contains .");
                String temp = total_words.get(len - 1).replace(".", "");
                //System.out.println("temp is" + temp);
                total_words.set(len - 1, temp);
                total_words.add(".");
            }
            /*
            System.out.println(total_words);
            for (String gg : total_words)
                System.out.println(gg);*/
            //System.out.println("wrong part" + wrong);
            for (List<String> wrong_part : wrong) {
                //System.out.println("wrong part is " + wrong_part);
                Integer wrong1 = Integer.valueOf(wrong_part.get(3));
                Integer wrong2 = Integer.valueOf(wrong_part.get(4));
                Integer small, large;
                if (wrong1 > wrong2) {
                    small = wrong2;
                    large = wrong1;
                } else {
                    small = wrong1;
                    large = wrong2;
                }
            /*
            String pharse = new String();
            pharse = "";
            for(int i=small-1;i<large-1;i++)
            {
                System.out.println("i is "+i+" "+total_words.get(i));
                pharse = pharse + total_words.get(i)+" ";
            }
            pharse = pharse + total_words.get(large-1);
            System.out.println("i is "+(large-1)+" "+total_words.get(large-1));*/
                //System.out.println("wrong part is " + wrong_part + " small is " + small + " large is " + large);
                Integer sep1=0,sep2=wordlist.size()-1;
                List<Integer> before = new ArrayList();
                List<Integer> in = new ArrayList();
                List<Integer> after = new ArrayList();
                List<String> list = new ArrayList();
                for (int i = 0; i < wordlist.size(); i++) {
                    list = wordlist.get(i);
                    Integer num1 = Integer.valueOf(list.get(3));
                    Integer num2 = Integer.valueOf(list.get(4));
                    Integer min, max;
                    if (num1 > num2) {
                        min = num2;
                        max = num1;
                    } else {
                        min = num1;
                        max = num2;
                    }
                    //Integer sep1=0,sep2=wordlist.size()-1;
                    for(Integer ss:sep)
                    {
                        if(ss<small&&ss>sep1)
                            sep1 = ss;
                        if(ss>large&&ss<sep2)
                            sep2 = ss;
                    }
                    //System.out.println(list+" this part small is "+min+" large is "+max+" wrong part small is "+ small+ " large is "+large +" sep1 is"+sep1+" sep2 is "+sep2);
                    if (max <= small&&min>=sep1)
                        before.add(i);
                    else if (min >= large&&max<=sep2)
                        after.add(i);
                    else if (small <= min && min <= large && small <= max && max <= large)
                        in.add(i);
                }
                //System.out.println("be" + before.toString());
                //System.out.println("in " + in.toString());
                //System.out.println("after " + after.toString());
                //getpharse(total_words,1,small-1);
                //checkcorrection(before,check_result);
                if(before.size()!=0)
                    json1.put(getpharse(total_words, sep1+1, small - 1), checkcorrection(before, check_result));
                //getpharse(total_words,small,large);
                //checkcorrection(in,check_result);
                if(in.size()!=0)
                    json1.put(getpharse(total_words, small, large), checkcorrection(in, check_result));
                //getpharse(total_words,large+1,total_words.size());
                //checkcorrection(after,check_result);
                if(after.size()!=0)
                    json1.put(getpharse(total_words, large + 1, sep2), checkcorrection(after, check_result));
            }
        }
        /*
        final_result.put("phrases",json1);
        //String prejson = final_result.toJSONString();
        System.out.println(final_result.toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(final_result);
        System.out.println(prettyJson);
        FileUtils.writeStringToFile(new File("result.txt"), prettyJson);*/
    }

    private static FileWriter file;
    public static List<String> input_strings = new ArrayList<>();
    public static JSONObject json = new JSONObject();
    public static JSONObject json1 = new JSONObject();
    public static JSONObject final_result = new JSONObject();
    public static String text1 = "Both names derive from Anglia, a peninsula in the Baltic Sea. " ;
    public static String text2 = "It is one about six official languages of the United Nations." ;
    //public static String text3 = "derive from Anglia," ;
    //public static String text4 = ",a peninsula in the Baltic Sea." ;
    /*
    public static String text2 = "In 2017, he went to Paris, France in the summer. " ;
    public static String text3 = "His flight left at 3:00pm on July 10th, 2017. " ;
    public static String text4 ="After eating some escargot for the first time, Joe said, \"That was delicious!\" " ;
    public static String text5 = "He sent a postcard to his sister Jane Smith. " ;
    public static String text6 ="The quick brown fox jumped over the lazy dog." ;*/
    public static void main(String[] args) throws IOException {

        StanfordNLP NLP = new StanfordNLP();
        StanfordCoreNLP pipline = NLP.initialStanfordNLP();
        File urls = new File(args[0]);
        //File urls = new File("src/Checker/input.txt");
        ReadFile(urls);

        for(String input:input_strings) {
            System.out.println(input);
            if(input!=" "&&input!=""&&input!="\n"){
                List<List<String>> wordlist = NLP.getRelation(pipline,input);
                System.out.println(wordlist);
                getjsonresult(wordlist,input);
            }
            //List<List<String>> wordlist = NLP.getRelation(pipline,input);
            //System.out.println(wordlist);
            //getjsonresult(wordlist,input);
        }

        //System.out.println(input_strings.size());
        //System.out.println(input_strings.get(0));
        //List<List<String>> wordlist = NLP.getRelation(pipline,input_strings.get(0));
        //System.out.println(wordlist);
        //getjsonresult(wordlist,input_strings.get(0));
        //System.out.println(input_strings.get(1));
        //wordlist = NLP.getRelation(pipline,input_strings.get(1));
        //System.out.println(wordlist);
        //getjsonresult(wordlist,input_strings.get(1));
        /*
        for(int i=1;i<input_strings.size();i++){
            System.out.println(input_strings.get(i));
            wordlist = NLP.getRelation(pipline,input_strings.get(i));
            System.out.println(wordlist);
            getjsonresult(wordlist,input_strings.get(i));
        }*/
        //List<List<String>> wordlist = NLP.getRelation(pipline,text2);
        //System.out.println(wordlist);
        //getjsonresult(wordlist,text2);

        final_result.put("sentences",json);
        final_result.put("phrases",json1);
        //String prejson = final_result.toJSONString();
        System.out.println(final_result.toString());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(final_result);
        System.out.println(prettyJson);
        FileUtils.writeStringToFile(new File("result.txt"), prettyJson);
        /*
        wordlist = NLP.getRelation(pipline,text2);
        System.out.println(wordlist);
        getjsonresult(wordlist,text2);

        wordlist = NLP.getRelation(pipline,text3);
        System.out.println(wordlist);
        getjsonresult(wordlist,text3);

        wordlist = NLP.getRelation(pipline,text4);
        System.out.println(wordlist);
        getjsonresult(wordlist,text4);*/


    }

}