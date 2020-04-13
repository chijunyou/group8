package NLP;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// Create StanfordNLP class
public class StanfordNLP {
    // A function for initializing pipeline to load Stanford NLP Core package
    public StanfordCoreNLP initialStanfordNLP() {
        // Set up pipeline properties
        Properties props = new Properties();
        // Set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,depparse");
        // Set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");
        // Build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP();
        return pipeline;
    }
    // A function for getting grammar relations between words
    public static List<List<String>> getRelation(StanfordCoreNLP pipeline, String input) {
        // Convert input to CoreDocument type
        CoreDocument document1 = new CoreDocument(input);
        // Annotate Document
        pipeline.annotate(document1);
        // Get grammar relations between words
        String sentenceText = document1.sentences().get(0).text();
        CoreSentence sentence = document1.sentences().get(0);
        List<SemanticGraphEdge> dependencylist = sentence.dependencyParse().edgeListSorted();
        List<List<String>> rtn = new ArrayList();
        // Store them in a List<List<String>>
        for (int i = 0; i < dependencylist.size(); i++) {
            List<String> temp = new ArrayList();
            String dependent = dependencylist.get(i).getDependent().toString() ;
            String governor = dependencylist.get(i).getGovernor().toString() ;
            String relation = dependencylist.get(i).getRelation().getShortName()  ;
            Integer index1 = dependencylist.get(i).getDependent().index();
            //String tt = dependencylist.get(i).getDependent().lemma();
            //String tag = dependencylist.get(i).getDependent().tag();
            Integer index2 = dependencylist.get(i).getGovernor().index();
            //System.out.println(dependent+" "+governor+" "+index+" "+tt+" "+uu+" "+xx);
            //String relation = dependencylist.get(i).getRelation().getShortName()  ;
            temp.add(dependent);
            temp.add(governor);
            temp.add(relation);
            temp.add(index1.toString());
            //temp.add(tag);
            temp.add(index2.toString());
            rtn.add(temp);
        }

        return rtn;
    }

}
