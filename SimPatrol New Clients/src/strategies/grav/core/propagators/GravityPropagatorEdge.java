package strategies.grav.core.propagators;

import strategies.grav.core.GravityPropagator;
import util.graph2.Graph;


/**
 * Propagador de gravidades atuantes sobre as arestas de um grafo dado.
 * <br><br>
 * A gravidade sobre cada n� "atra�do" atua na dire��o da aresta do menor caminho at� o n�
 * "atrator", cuja massa produzia a gravidade. 
 * <br><br>
 * Em cada dire��o de caa aresta, v�rias gravidades (originadas de diferentes n�s) podem atuar. 
 * Cada subclasse define como o valor final da gravidade na aresta vai ser calculada, por meio
 * de alguma "combina��o" desses valores.
 * 
 * @see GravityPropagator 
 * @author Pablo A. Sampaio
 */
public abstract class GravityPropagatorEdge extends GravityPropagator {
	
	protected double[][] gravities;

	
	GravityPropagatorEdge(Graph g, double exponent) {
		super(g, exponent);
		
		int numVertices = g.getNumVertices();
		gravities = new double[numVertices][numVertices];
	}

	public double getGravity(int attracted, int neighbor) {
		return gravities[attracted][neighbor];
	}
	
	
}
