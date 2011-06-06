package gravitational.gravity_manager;

import util.graph2.Graph;


/**
 * Com esta classe, a gravidade atuando sobre uma dire��o espec�fica
 * de uma aresta � dada pela SOMA das gravidades presentes naquela 
 * dire��o da aresta.
 * 
 * ATEN��O: Esta implementa��o assume que o grafo � sim�trico.
 * 
 * @author Pablo
 */
public class GravityManagerAdder extends GravityManager {
	
	public GravityManagerAdder(Graph graph, double exponent) {
		super(graph, exponent);
	}

	public void applyGravity(int origin, double originMass) {
		assert (originMass >= 0.0d);
		assert (masses[origin] == -1.0d);

		applyGravitiesInternal(origin, originMass);
		
		masses[origin] = originMass;
	}
	
	public void undoGravity(int origin) {
		assert (masses[origin] >= 0.0d);

		applyGravitiesInternal(origin, -masses[origin]);

		masses[origin] = -1.0d;
	}
	
	//TODO: criar vers�o especializada?
	//@Override
	//public void undoAllGravities() {
	
	private void applyGravitiesInternal(int origin, double originMass) {
		int numVertices = gravities.length;

		int destinyPredecessor;

		for (int destiny = 0; destiny < numVertices; destiny++) {
			if (destiny != origin) {
				destinyPredecessor = shortestPaths.getDestinyPredecessor(origin, destiny);

				gravities[destiny][destinyPredecessor] += originMass * propagationFactor[origin][destiny]; 
			}
		}
	}

}
