package tools.metrics;

public class Main {

	public static void main(String[] args) throws Exception {
		LogFileParser parser = new LogFileParser();
		
		parser.parseFile("simulation_log.txt");
	
		MetricsReport metrics = new MetricsReport(parser.getNumNodes(), 1, 5, parser.getVisitsList());
		
		System.out.println();
		System.out.println("METRICS:");
		System.out.println();
		
		System.out.println(" - Maximum interval: " + metrics.getMaxInterval());
		System.out.println(" - Average interval: " + metrics.getAverageInterval());
		System.out.println(" - Standard deviation of the intervals: " + metrics.getStdDevInterval());
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: " + metrics.getMaxInstantaeousIdleness());
		System.out.println(" - Average idleness: " + metrics.getAverageIdleness());
		System.out.println();
		System.out.println(" - Total number of visits: " + metrics.getTotalVisits());
		System.out.println(" - Average number of visits per node: " + metrics.getAverageVisits());
		System.out.println(" - Standard deviation of the number of visits per node: " + metrics.getStdDevVists());
	}
	
}
