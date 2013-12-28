package benblamey.sutime;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.time.XMLUtils;
import edu.stanford.nlp.util.CollectionFactory;
import edu.stanford.nlp.util.CollectionValuedMap;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.HasInterval;
import edu.stanford.nlp.util.ValuedInterval;

public class SUTimeMain2 {

    public static List<Node> createTimexNodes(String str, Integer charBeginOffset, List<CoreMap> timexAnns) {
        List<ValuedInterval<CoreMap, Integer>> timexList = new ArrayList<ValuedInterval<CoreMap, Integer>>(timexAnns.size());
        for (CoreMap timexAnn : timexAnns) {
            timexList.add(new ValuedInterval<CoreMap, Integer>(timexAnn,
                    MatchedExpression.COREMAP_TO_CHAR_OFFSETS_INTERVAL_FUNC.apply(timexAnn)));
        }
        Collections.sort(timexList, HasInterval.CONTAINS_FIRST_ENDPOINTS_COMPARATOR);
        return createTimexNodesPresorted(str, charBeginOffset, timexList);
    }

    private static List<Node> createTimexNodesPresorted(String str, Integer charBeginOffset, List<ValuedInterval<CoreMap, Integer>> timexList) {
        if (charBeginOffset == null) {
            charBeginOffset = 0;
        }
        List<Node> nodes = new ArrayList<Node>();
        int previousEnd = 0;
        List<Element> timexElems = new ArrayList<Element>();
        List<ValuedInterval<CoreMap, Integer>> processed = new ArrayList<ValuedInterval<CoreMap, Integer>>();
        CollectionValuedMap<Integer, ValuedInterval<CoreMap, Integer>> unprocessed =
                new CollectionValuedMap<Integer, ValuedInterval<CoreMap, Integer>>(CollectionFactory.<ValuedInterval<CoreMap, Integer>>arrayListFactory());
        for (ValuedInterval<CoreMap, Integer> v : timexList) {
            CoreMap timexAnn = v.getValue();
            int begin = timexAnn.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) - charBeginOffset;
            int end = timexAnn.get(CoreAnnotations.CharacterOffsetEndAnnotation.class) - charBeginOffset;
            if (begin >= previousEnd) {
                // Add text
                nodes.add(XMLUtils.createTextNode(str.substring(previousEnd, begin)));
                // Add timex
                Timex timex = timexAnn.get(TimeAnnotations.TimexAnnotation.class);
                Element timexElem = timex.toXmlElement();
                nodes.add(timexElem);
                previousEnd = end;

                // For handling nested timexes
                processed.add(v);
                timexElems.add(timexElem);
            } else {
                unprocessed.add(processed.size() - 1, v);
            }
        }
        if (previousEnd < str.length()) {
            nodes.add(XMLUtils.createTextNode(str.substring(previousEnd)));
        }
        for (Integer i : unprocessed.keySet()) {
            ValuedInterval<CoreMap, Integer> v = processed.get(i);
            String elemStr = v.getValue().get(CoreAnnotations.TextAnnotation.class);
            int charStart = v.getValue().get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
            List<Node> innerElems = createTimexNodesPresorted(elemStr, charStart, (List<ValuedInterval<CoreMap, Integer>>) unprocessed.get(i));
            Element timexElem = timexElems.get(i);
            XMLUtils.removeChildren(timexElem);
            for (Node n : innerElems) {
                timexElem.appendChild(n);
            }
        }
        return nodes;
    }
    
    public static class ProcessTextResult {
    	
    	String textAreaText;
    	String highlightedHtml;
		final List<AnnotationViewModel> viewmodels  = new ArrayList<AnnotationViewModel>();
    }
    
    public static ProcessTextResult processText(AnnotationPipeline pipeline, String text, String date) throws IOException {
        text = text.replace("{", "(").replace("}", ")");
        
        System.err.println("Processing line: " + text);

        Annotation annotation = textToAnnotation(pipeline, text, date);
        
        //String text2 = annotation.get(CoreAnnotations.TextAnnotation.class);

        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        for (CoreLabel token : tokens) {
        	//Integer start = token.get(CoreAnnotations.TokenBeginAnnotation.class);
        	//Integer end = token.get(CoreAnnotations.TokenEndAnnotation.class);
        	//String tokenText = text.substring(start, end);
        	System.err.println("Token: " + token);
        }
        
        List<CoreMap> timexes = annotation.get(
        		TimeAnnotations.TimexAnnotations.class);
        //for (CoreMap timexannotation : timexes) {
            //Integer characterOffsetStart = timexannotation.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
            //Integer characterOffsetEnd = timexannotation.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
       // }

        // Sort the timex annotations according to character offsets.
        List<ValuedInterval<CoreMap, Integer>> timexList = new ArrayList<ValuedInterval<CoreMap, Integer>>(timexes.size());
        for (CoreMap timexAnn : timexes) {
            timexList.add(new ValuedInterval<CoreMap, Integer>(timexAnn,
                MatchedExpression.COREMAP_TO_CHAR_OFFSETS_INTERVAL_FUNC.apply(timexAnn)));
        }
        Collections.sort(timexList, HasInterval.CONTAINS_FIRST_ENDPOINTS_COMPARATOR);
        
        
        {
        	annotation.get(CoreAnnotations.SentencesAnnotation.class);
        }
        
        // Reverse so that we start with the annotations at the end of the text.
       // Collections.reverse(timexList);
        
        StringBuilder newText = new StringBuilder();
        
        int lastIndex= 0;
        
        for (ValuedInterval<CoreMap, Integer> vi : timexList) {
            int characterOffsetStart = vi.getValue().get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
            int characterOffsetEnd = vi.getValue().get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
            
            if (characterOffsetStart > 0 &&  (characterOffsetStart > lastIndex)) {
            	newText.append(text.substring(lastIndex, characterOffsetStart));
            }
            
            newText.append("{"); //  
            newText.append(text.substring(characterOffsetStart, characterOffsetEnd));
            newText.append("}"); // 
            
            lastIndex = characterOffsetEnd;
    	}
                
        newText.append(text.substring(lastIndex));
        
        System.out.println(newText);
        

        ProcessTextResult ptr = new ProcessTextResult();
        ptr.textAreaText = StringEscapeUtils.escapeHtml4(text);
        
        String highlightedHtml = newText.toString(); // Has { } to indicate annotations at correct indices.
        
        // Now parsed, so can escape HTML entities in the incoming string.
        highlightedHtml = StringEscapeUtils.escapeHtml4(highlightedHtml);
        
        highlightedHtml = highlightedHtml.replace("{", "<span class=\"highlight\">");
        highlightedHtml = highlightedHtml.replace("}", "</span>");
        ptr.highlightedHtml = highlightedHtml;
        
        // Insert tags for carriage returns in HTML.
        ptr.highlightedHtml = ptr.highlightedHtml.replaceAll("\\r?\\n", "<br/>\n");

    	List<Node> timexNodes = createTimexNodes(
          annotation.get(CoreAnnotations.TextAnnotation.class),
          annotation.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
          timexes);
    	for (Node node : timexNodes) {
    		if (node.getNodeName().equals("TIMEX3")) {
    			ptr.viewmodels.add(new AnnotationViewModel(node));
    		}
    	}

        return ptr;
    }

    public static Annotation textToAnnotation(AnnotationPipeline pipeline, String text, String date) {
        Annotation annotation = new Annotation(text);
        annotation.set(CoreAnnotations.DocDateAnnotation.class, date);
        pipeline.annotate(annotation);
        return annotation;
    }

//        //String in = "1 ___  ben 2004\n1 ___  Summer 2005";
//        String in = IOUtils.slurpFile("C:/work/data/output/tempex/albumnames_final_sorted_dev_sample.txt");
//        //String in = "Winter .\n ben .\n ben 2004 .\n Christmas Day 2001 .\n ben '04 .\n '04"; //"Can't wait for the Summer! Christmas \n Christmas \n ben \n halloweeen"; //props.getProperty("i");
//        //String in = "ben '04";

}
