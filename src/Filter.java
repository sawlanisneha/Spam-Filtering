import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

public class Filter {
	
	public static String readAll(String filename)
	{
		BufferedReader reader;
		StringBuilder builder = new StringBuilder();
		
		try{
			reader = new BufferedReader(new FileReader(filename));
			
			String line;
			
			while( (line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			
			reader.close();
			
			return builder.toString();
		}catch(FileNotFoundException exc)
		{
			System.out.println("The specified file " + filename + "  was not found ");
			return null;
		}catch(IOException esc)
		{
			System.out.println("An error occured while reading the file " + filename);
			return null;
		}
	}
	
	public static ArrayList<SimpleEntry<String, EmailClass>> readTrainingData(String folderPath)
	{
		File folder = new File(folderPath);
		
		assert folder.exists();
		assert folder.isDirectory();
		
		ArrayList<SimpleEntry<String, EmailClass>> output = new ArrayList();
		
		File[] trainingFiles = folder.listFiles();
		
		for(int i = 0; i < trainingFiles.length; ++i)
		{
			String trainingFilePath = trainingFiles[i].getPath();
			String data = readAll(trainingFilePath);
			
			//Add the new data to the output dictionary, use filename as an indicator of class
			EmailClass emailClass = (trainingFilePath.contains("ham")) ? EmailClass.Ham : EmailClass.Spam;
			output.add(new SimpleEntry(data, emailClass));
		}
		
		return output;
	}
	
	public static void main(String[] args)
	{
		if(args.length < 2)
		{
			System.out.println("Argument 0 required: training folder path");
			System.out.println("Argument 1 required: target filename argument");
			return;
		}
		
		String trainingFolder = args[0];
		String targetFileName = args[1];
		
		NaiveBayes classifier = new NaiveBayes();
		classifier.train(readTrainingData(trainingFolder));
		
		String data = readAll(targetFileName);
		
		EmailClass output = classifier.classify(data);
		System.out.println(output);
	}

}
