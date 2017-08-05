import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

public class NaiveBayes {
	
	private final HashMap<String, Double> hamProb;
	private final HashMap<String, Double> spamProb;
	private double hamTotalProb;
	private double spamTotalProb;
	
	public NaiveBayes()
	{
		hamProb = new HashMap();
		spamProb = new HashMap();
		
	}
	
	public void train(ArrayList<SimpleEntry<String, EmailClass>> examples)
	{
		hamProb.clear();
		spamProb.clear();
		
		int hamWords = 0;
		int spamWords = 0;
		
		for(SimpleEntry<String, EmailClass> example : examples)
		{
			String[] tokens = example.getKey().split(" ");
			
			for(String token : tokens)
			{
				if(!hamProb.containsKey(token))
				{
					//Start off with a Laplacian correction
					hamProb.put(token, 1.0);
					spamProb.put(token, 1.0);
					++hamWords;
					++spamWords;
					
				}
				
				if(example.getValue() == EmailClass.Ham)
				{
					hamProb.put(token, hamProb.get(token) + 1.0);
					++hamWords;
				}
				
				else
				{
					spamProb.put(token, spamProb.get(token) + 1.0);
					++spamWords;
				}
			}
		}
		
		//Calculate the probability as wordFrequency/wordCount
		for(String token : hamProb.keySet())
		{
			hamProb.put(token, hamProb.get(token)/(double)hamWords);
			spamProb.put(token, spamProb.get(token)/(double)spamWords);
		}
		
		hamTotalProb = (double)hamWords/(hamWords + spamWords);
		spamTotalProb = (double)spamWords/(hamWords + spamWords);
	}
	
	public EmailClass classify(String data)
	{
		String[] tokens = data.split(" ");
		
		//Calculate the word frequency in the message
		int wordCount = 0;
		HashMap<String, Integer> wordFreq = new HashMap();
		
		for(String token : tokens)
		{
			if(hamProb.containsKey(token))
			{
				if(!wordFreq.containsKey(token))
				{
					wordFreq.put(token, 0);
				}
				
				wordFreq.put(token, wordFreq.get(token) + 1);
				++wordCount;
			}
			
			//Else do not bother including it
		}
		
		double hamLikelihood = hamTotalProb;
		double spamLikelihood = spamTotalProb;
		
		//Apply Bayes Rule 
		for(String token : wordFreq.keySet())
		{
			int freq = wordFreq.get(token);
			//P(Ham | word) = (P(word | Ham) * P(Ham)) / P(word)
			//don't need to divide by P(word) because we just need to compare with spamlikelihood which has the same denominator
			hamLikelihood *= Math.pow(hamProb.get(token), freq);
			//P(Spam | word) = (P(word| Spam) * P(Spam))/ P(word)
			spamLikelihood *= Math.pow(spamProb.get(token), freq);
		}
		
		if(hamLikelihood > spamLikelihood)
			return EmailClass.Ham;
		return EmailClass.Spam;
	}
}
