import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

public class Weka {

	public static void buildClassifierSMO(String trainFile, String modelFile) throws Exception {
		// create SMO
		 SMO smo = new SMO();
		 Remove rm = new Remove();
		 // train
		 Instances inst = new Instances(
		                    new BufferedReader(
		                      new FileReader(trainFile)));
		 inst.setClassIndex(inst.numAttributes() - 1);
		 rm.setAttributeIndices(Integer.toString((inst.numAttributes() - 2)));
		 FilteredClassifier cls = new FilteredClassifier();
		 cls.setFilter(rm);
		 cls.setClassifier(smo);

		 cls.buildClassifier(inst);

		 // serialize model
		 weka.core.SerializationHelper.write(modelFile, cls);
	}
	public static void buildClassifierLog(String trainFile, String modelFile) throws Exception {
		// create logistic
		 Logistic log = new Logistic();
		 Remove rm = new Remove();
		 // train
		 Instances inst = new Instances(
		                    new BufferedReader(
		                      new FileReader(trainFile)));
		 inst.setClassIndex(inst.numAttributes() - 1);
		 rm.setAttributeIndices(Integer.toString((inst.numAttributes() - 2)));
		 FilteredClassifier cls = new FilteredClassifier();
		 cls.setFilter(rm);
		 cls.setClassifier(log);

		 cls.buildClassifier(inst);

		 // serialize model
		 weka.core.SerializationHelper.write(modelFile, cls);
	}	
	public static void buildClassifierNB(String trainFile, String modelFile) throws Exception {
		// create NB
		 NaiveBayes nb = new NaiveBayes();
		 Remove rm = new Remove();
		 // train
		 Instances inst = new Instances(
		                    new BufferedReader(
		                      new FileReader(trainFile)));
		 inst.setClassIndex(inst.numAttributes() - 1);
		 rm.setAttributeIndices(Integer.toString((inst.numAttributes() - 2)));
		 FilteredClassifier cls = new FilteredClassifier();
		 cls.setFilter(rm);
		 cls.setClassifier(nb);

		 cls.buildClassifier(inst);

		 // serialize model
		 weka.core.SerializationHelper.write(modelFile, cls);
	}

	public static void classify(String trainFile, String testFile, String modelFile) throws Exception {
		// deserialize model
		 Classifier cls = (Classifier) weka.core.SerializationHelper.read(modelFile);
		 // train
		 Instances train = new Instances(
		                    new BufferedReader(
		                      new FileReader(trainFile)));
		 train.setClassIndex(train.numAttributes() - 1);
		 // test
		 Instances test = new Instances(
		                    new BufferedReader(
		                      new FileReader(testFile)));
		 test.setClassIndex(test.numAttributes() - 1);
		// evaluate classifier and print some statistics
		 
		 // create copy
		 Instances labeled = new Instances(test);
		 
		 // label instances
		 for (int i = 0; i < test.numInstances(); i++) {
		   double clsLabel = cls.classifyInstance(test.instance(i));
		   labeled.instance(i).setClassValue(clsLabel);
		 }
		 // save labeled data

		 
		 Evaluation eval = new Evaluation(train);
		 eval.evaluateModel(cls, test);
		 
		 BufferedWriter writer = new BufferedWriter(new FileWriter(modelFile + ".txt"));
		 writer.write("True Positives: " + eval.numTruePositives(0) +"\tTrue Negatives: " + eval.numTrueNegatives(0) + "\tFalse Positives: " + eval.numFalsePositives(0) + "\tFalse Negatives: " + eval.numFalseNegatives(0) + "\n");
		 writer.write("Precision: " + eval.precision(0) + "\tRecall: " + eval.recall(0) + "\tF1-score: " + eval.fMeasure(0) + "\n");
		 
		 //System.out.println("True Positives: " + eval.numTruePositives(0) +"\tTrue Negatives: " + eval.numTrueNegatives(0) + "\tFalse Positives: " + eval.numFalsePositives(0) + "\tFalse Negatives: " + eval.numFalseNegatives(0));
		 //System.out.println("Precision: " + eval.precision(0) + "\tRecall: " + eval.recall(0) + "\tF1-score: " + eval.fMeasure(0));
		 
		 for (int i = 0; i < labeled.numInstances(); i++) {
			 writer.write(labeled.instance(i).toString(300) + "\t");
			 writer.write(labeled.instance(i).toString(301));
			 writer.write("\n");
		 }
		 writer.flush();
		 writer.close();
	}
	
	public static void compare(String trainFile, String testFile, String modelFile1, String modelFile2, String modelFile3) throws Exception {
	
		 Classifier cls1 = (Classifier) weka.core.SerializationHelper.read(modelFile1);
		 Classifier cls2 = (Classifier) weka.core.SerializationHelper.read(modelFile2);
		 Classifier cls3 = (Classifier) weka.core.SerializationHelper.read(modelFile3);
		 
		 Instances train = new Instances(
                 new BufferedReader(
                   new FileReader(trainFile)));
		 train.setClassIndex(train.numAttributes() - 1);
		 // test
		 Instances test = new Instances(
                 new BufferedReader(
                   new FileReader(testFile)));
		 test.setClassIndex(test.numAttributes() - 1);
		 // evaluate classifier and print some statistics

		 // create copy
		 Evaluation eval1 = new Evaluation(train);
		 eval1.evaluateModel(cls1, test);
		 Evaluation eval2 = new Evaluation(train);
		 eval2.evaluateModel(cls2, test);
		 Evaluation eval3 = new Evaluation(train);
		 eval3.evaluateModel(cls3, test);
		 
		 BufferedWriter writer = new BufferedWriter(new FileWriter("compare.txt"));
		 writer.write("Model Type:" + "\tTrue Positives" + "\tTrue Negatives" + "\tFalse Positives" + "\tFalse Negatives" + "\n");
		 writer.write("SMO:\t\t\t" + eval1.numTruePositives(0) +"\t\t" + eval1.numTrueNegatives(0) + "\t\t" + eval1.numFalsePositives(0) + "\t\t" + eval1.numFalseNegatives(0) + "\n");
		 writer.write("Log Regression:\t" + eval2.numTruePositives(0) +"\t\t" + eval2.numTrueNegatives(0) + "\t\t" + eval2.numFalsePositives(0) + "\t\t" + eval2.numFalseNegatives(0) + "\n");
		 writer.write("Naive Baines:\t" + eval3.numTruePositives(0) +"\t\t" + eval3.numTrueNegatives(0) + "\t\t" + eval3.numFalsePositives(0) + "\t\t" + eval3.numFalseNegatives(0) + "\n");

		 writer.write("\nModel Type: " + "Precision" + "\tRecall" + "\tF1-score" + "\n");
		 writer.write("SMO:\t\t\t" + eval1.precision(0) + "\t\t" + eval1.recall(0) + "\t\t" + eval1.fMeasure(0) + "\n");
		 writer.write("Log Regression:\t" + eval2.precision(0) + "\t\t" + eval2.recall(0) + "\t\t" + eval2.fMeasure(0) + "\n");
		 writer.write("Naive Baines:\t" + eval3.precision(0) + "\t\t" + eval3.recall(0) + "\t\t" + eval3.fMeasure(0) + "\n");

		 writer.flush();
		 writer.close();

	}

	public static void main(String[] args) throws Exception {
		buildClassifierSMO("sample_train.arff", "test_java_SMO.model");
		buildClassifierLog("sample_train.arff", "test_java_log.model");
		buildClassifierNB("sample_train.arff", "test_java_NB.model");
		classify("sample_train.arff", "eval.arff", "test_java_SMO.model");
		classify("sample_train.arff", "eval.arff", "test_java_log.model");
		classify("sample_train.arff", "eval.arff", "test_java_NB.model");
		compare("sample_train.arff", "eval.arff", "test_java_SMO.model", "test_java_log.model", "test_java_NB.model");

	}
}
