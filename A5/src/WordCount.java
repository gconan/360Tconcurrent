import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.io.NullWritable;

public class WordCount {

  public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>{

    private Text word = new Text();

    /**
     * stores ("darcy", ch1) ("darcy", ch1) ("the", ch1)....an occurance for each word in the chapter
     */
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      Text chapter = new Text(((FileSplit)context.getInputSplit()).getPath().getName());//get chapter number
      
      //a little string parsing to obtain the same key word for items like darcy, darcy's, Darcy...
      while (itr.hasMoreTokens()) {
    	String string = itr.nextToken();
//    	string= string.replaceAll("[^a-zA-Z]", "").toLowerCase();
//    	word.set(string);
//      context.write(word, chapter);
        
        String[] y = string.split("[^a-zA-Z]");
		for(String z:y){
			z=z.trim();
			if(!z.equals("")){
				z= z.replaceAll("[^a-zA-Z]", "").toLowerCase();
		    	word.set(z);
		        context.write(word, chapter);
			}
		}
        
        
//    	String[] spaceKeeper = string.split(" ");
//    	for(int i=0; i<spaceKeeper.length; i++){
//    		spaceKeeper[i] = spaceKeeper[i].replaceAll("[^a-zA-Z]", "").toLowerCase();
//	        word.set(spaceKeeper[i]);
//	        context.write(word, chapter);
//    	}
      }
    }
  }

  /**
   * 
   * Reducer for WordCount
   * NullWritable was used to insert a "blank" value, so that we could print single words and 
   * spaces to the output.
   * @author conangammel, michaelwehrmeister
   *
   */
  public static class IntSumReducer extends Reducer<Text,Text,Text,NullWritable> {

	/**
	 * Receives Iterable<Text> values which correspond to every word's occurrence in each chapter.
	 * ie: if darcy occurred twice in ch1 and once in ch2; values could be {ch1, ch2, ch1}
	 * @param key
	 * @param values
	 * @param context
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
    	Map<String, Integer> chapterValues = new HashMap<String, Integer>();
    	
    	for(Text value: values){
    		Integer totalOcc = chapterValues.get(value.toString());
    		if(totalOcc==null){
    			totalOcc=0;
    		}
    		chapterValues.put(value.toString(), totalOcc+1);
    	}
    	
    	//we now have a map that could look like this: {<ch2,1>, <ch1,2>,... }(unsorted)
    	//so, for each key in the map we need to write
    	//stack overflow says to overwrite comparable with your own comparable then use sort
    	//which means we need to convert the key,value pairs to a single object for comparing
    	ArrayList<ChapterValuePair> listToSort = new ArrayList<ChapterValuePair>();
    	
    	for(String stringKey: chapterValues.keySet()){
    		listToSort.add(new ChapterValuePair(stringKey, chapterValues.get(stringKey)));
    	}
    	
    	Collections.sort(listToSort, new Comparator() {
            public int compare(Object o1, Object o2) {
               ChapterValuePair p1 = (ChapterValuePair)o1;
               ChapterValuePair p2 = (ChapterValuePair)o2;
               
               if(p1.value>p2.value){
            	   return -1;
               }else if(p2.value>p1.value){
            	   return 1;
               }else{
            	   String ch1Num = p1.key.substring(4);
            	   String ch2Num = p2.key.substring(4);
            	   Integer p1ch = Integer.parseInt(ch1Num);
            	   Integer p2ch = Integer.parseInt(ch2Num);
            	   
            	   if(p1ch<p2ch){
            		   return -1;
            	   }else if(p1ch>p2ch){
            		   return 1;
            	   }else return 0;
               }
               
            }
    	});
    	
    	context.write(key, NullWritable.get());
    	for(ChapterValuePair p: listToSort){
    		context.write(new Text("<"+p.key+", "+p.value+">"),NullWritable.get());
    	}
    	
    	//write blank line for next reduced word
    	context.write(new Text(""), NullWritable.get());
  }
}
  
  public static class ChapterValuePair{
	  String key;
	  Integer value;
	  public ChapterValuePair(String key, Integer value){
		  this.key = key;
		  this.value = value;
	  }
  }

  public static void main(String[] args) throws Exception {
//	  for(String j: args){
//		  System.out.println("arg: "+j);//TODO remove
//	  }
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[1]));
    FileOutputFormat.setOutputPath(job, new Path(args[2]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}