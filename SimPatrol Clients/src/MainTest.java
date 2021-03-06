import gray_box_learner.GrayBoxLearnerClient;
import heuristic_cognitive_coordinated.HeuristicCognitiveCoordinatedClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import random_reactive.RandomReactiveClient;
import util.file.FileReader;
import util.graph.Graph;
import util.graph.GraphTranslator;
import cognitive_coordinated.CognitiveCoordinatedClient;
import conscientious_reactive.ConscientiousReactiveClient;
import cycled.CycledClient;


/**
 * This class executes any of the available agents with a test configuration. 
 * 
 * @author Pablo A. Sampaio
 */
public class MainTest {

	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Choose the number of the type of agent you want to run:");
		System.out.println("\t 1 - Cognitive Coordinated");
		System.out.println("\t 2 - Conscientious Reactive");
		System.out.println("\t 3 - TSP - Single Cycle");
		System.out.println("\t 4 - Heuristic Pathfinder Cognitive Coordinated");

		System.out.println("\t 7 - Generalized Gray Box Learner (learning)");
		System.out.println("\t 8 - Generalized Gray Box Learner");
		System.out.println("\t 9 - Random Reactive");
		System.out.print  ("> ");
		
		char type = reader.readLine().trim().charAt(0);
		
		switch (type) {

		case '1':
			CognitiveCoordinatedClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"examples\\cc_test.xml",
					"tmp\\cc_log.txt",
					"100",
					"false"
				});
			break;
		
		case '2':
			ConscientiousReactiveClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"examples\\cr_test.xml",
					"tmp\\cr_log.txt",
					"100",
					"false"
				});
			break;
		
		case '3':
			CycledClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"examples\\sc_test.xml",
					"tmp\\sc_log.txt",
					"100",
					"false"
				});
			break;
		
		case '4':
			HeuristicCognitiveCoordinatedClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"examples\\hpcc_test.xml",
					"tmp\\hpcc_log.txt",
					"100",
					"false"
				});
			break;
		
		case '7':
			// parameters taken from Satana's MSc
			GrayBoxLearnerClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"examples\\gbla_test.xml",
					"tmp\\gbla_learning_log.txt",
					"2000",
					"false",
					"0.1",  // epsilon (exploration probability)
					"0.05", // alfa decay (decay of the learning rate)
					"0.9",  // gama (discount factor)
					"tmp",
					"true", // learning
					"true"  // generalized
				});
			break;
		
		case '8':
			// parameters taken from Satana's MSc
			GrayBoxLearnerClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"examples\\gbla_test.xml",
					"tmp\\gbla_log.txt",
					"1000",
					"false",
					"0.01", // epsilon (exploration probability)
					"0.0",  // alfa decay (decay of the learning rate)
					"0.9",  // gama (discount factor)
					"tmp",
					"false", // evaluating
					"true"   // generalized
				});
			break;
			
		case '9':
			RandomReactiveClient.main(new String[]{
					"127.0.0.1",
					"5000",
					"examples\\random_test.xml",
					"tmp\\random_log.txt",
					"100",
					"false"
				});
			break;
		
		default:
			System.out.println("Invalid option!");
			
		}
		
	}
	
}
