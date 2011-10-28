package open.FBA2;

import java.util.LinkedList;
import java.util.Vector;

import closed.FBA.ComplexBid;
import closed.FBA.SpeechAct;
import closed.FBA.SpeechActPerformative;

import util.graph.Node;

/**
 * Precalculated Group Open_FBA_Agent
 * 
 * Enter :  - each agent proposes the group of nodes that add the most path to the path it has to walk
 * 		(same as the Group agent)
 * 
 * QUIT  :  - The quitting agent calculates group of nodes by proximity, then advertise the groups
 * 				each agent gives a list of priorities over the groups, the preferred groups are those adding the smallest distance to their path
 * 			the quitting agent tries at best to respect these priorities, and distributes any remaining node by proximity to those already given
 * 
 * 
 * @author pouletc
 *
 */
public class Open_FBA_PrecalculatedGroup_Agent extends Open_FBA_Agent {

	Vector<String[]> quitting_node_group;
	
	public Open_FBA_PrecalculatedGroup_Agent(String id, double entering_time, double quitting_time, 
										int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction, 
										String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, 
					idleness_rate_for_path, idleness_rate_for_auction, society_id);
	}

	public Open_FBA_PrecalculatedGroup_Agent(String id, int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);
	}

	@Override
	protected SpeechAct enter_Message(int id) {
		return new SpeechAct(id, SpeechActPerformative.ENTER, this.agent_id, "all_agents");
	}

	@Override
	/**
	 * When receiving an ENTER message, the agent gives to the new agent some nodes
	 * the number of nodes given is chosen so that each agent in the system has the same number of nodes in average
	 * the nodes chosen are the group of nodes of the right size which, when deleted from the node list, 
	 * minimizes the new path cost 
	 */
	protected SpeechAct manage_Enter_Message(SpeechAct entering_act) {
		if(this.myNodes.size() <= 2)
			return new SpeechAct(entering_act.getTransactionId(), SpeechActPerformative.PROPOSE, 
					this.agent_id, entering_act.getSender(), new ComplexBid(null, null, null, null));
		
		int nb_nodes_to_send = (int)((double)this.myNodes.size() * (1 - (double)this.agents_num / (double)(this.agents_num + 1)));
		if(nb_nodes_to_send == 0)
				nb_nodes_to_send = 1;

		//
		LinkedList<String> worst_group = new LinkedList<String>();
		double cost, max_cost = 0;
		LinkedList<String> mynodes_copy;
		
		int[] current_index = new int[nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++)
			current_index[i] = i;
		
		while(current_index[0] < this.myNodes.size() - nb_nodes_to_send){
			mynodes_copy = this.getMyNodes();
			for(int j = 0; j < current_index.length; j++)
				mynodes_copy.remove(this.getMyNodes(current_index[j]));
			
			cost = this.CalculatePathCost(this.OrderNodes(mynodes_copy));
			
			if(cost > max_cost){
				max_cost = cost;
				worst_group.clear();
				for(int j = 0; j < current_index.length; j++)
					worst_group.add(this.getMyNodes(current_index[j]));
			}
			
			
			current_index[current_index.length - 1]++;
			for(int j = current_index.length - 1; j > 0; j--)
				if(current_index[j] >= this.myNodes.size() - (current_index.length - j)){
					current_index[j-1]++;
					for(int k = j; k < current_index.length; k++)
						current_index[k] = current_index[k-1] + 1;
				}	
		}
		
		
		
		String[] nodes_to_send = new String[2*nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++){
			nodes_to_send[2*i] = worst_group.get(i);
			nodes_to_send[2*i + 1] = String.valueOf((int)this.estimatedIdleness(worst_group.get(i)));
			this.removeFromMyNodes(worst_group.get(i));
		}
		
		ComplexBid bid = new ComplexBid(nodes_to_send, null, null, null);
		SpeechAct act = new SpeechAct(entering_act.getTransactionId(), SpeechActPerformative.PROPOSE, 
																	this.agent_id, entering_act.getSender(), bid);
		
		this.plan.clear();
		
		return act;
	}

	@Override
	protected void manage_Enter_Protocol() {
		if(this.ComplexPropositions != null && this.ComplexPropositions.size()>0){
			for(int i = 0; i < this.ComplexPropositions.size(); i++){
				String[] nodesToAdd = this.ComplexPropositions.get(i).getBidsForFirst();
				if(nodesToAdd != null && nodesToAdd.length > 0){
					String added_nodes = "";
					for(int j = 0; j < nodesToAdd.length; j+=2){
						this.addToMyNodes(nodesToAdd[j], Double.valueOf(nodesToAdd[j+1]));
						added_nodes += nodesToAdd[j] + " (" + nodesToAdd[j+1] + "), ";
					}

					if(!added_nodes.equals(""))
						added_nodes = added_nodes.substring(0, added_nodes.length() - 2) + ";";

					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, this.FBABuyers.get(i));
					this.SendSpeechAct(act);

					System.out.println("Enter : from " + this.FBABuyers.get(i) + " : " + added_nodes);
				}
				else {
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, this.FBABuyers.get(i));
					this.SendSpeechAct(act);
				}
			}
    		
        	if(FBABuyers != null)
        		FBABuyers.clear();
        	if(ComplexPropositions != null)
        		ComplexPropositions.clear();

        	this.myCurrentTransaction = -1;
        	this.received_answers = 0;
			
		}
	}
	
	@Override
	protected SpeechAct quit_Message(int id) {
		int nb_nodes_by_group = Math.max(this.myNodes.size() / (this.agents_num - 1), 1);
		String[] proposed_nodes = new String[0];
		proposed_nodes = this.getMyNodes().toArray(proposed_nodes);
		Vector<String[]> groups = new Vector<String[]>();
		
		// on fait des groupes de tailles égales
		while(proposed_nodes.length > 0){	
			if(proposed_nodes.length - nb_nodes_by_group <= 0){
				break;
				
			}
			else {		
		
				LinkedList<String> best_group = new LinkedList<String>();
				double cost, min_cost = Double.MAX_VALUE;
				LinkedList<String> current_group = new LinkedList<String>();
				
				int[] current_index = new int[nb_nodes_by_group];
				for(int i = 0; i < nb_nodes_by_group; i++)
					current_index[i] = i;
				
				while(current_index[0] < proposed_nodes.length - nb_nodes_by_group){
					current_group.clear();
					for(int j = 0; j < current_index.length; j++)
						current_group.add(proposed_nodes[current_index[j]]);
					
					cost = this.CalculatePathCost(this.OrderNodes(current_group));
					
					if(cost < min_cost){
						min_cost = cost;
						best_group.clear();
						for(int j = 0; j < current_index.length; j++)
							best_group.add(proposed_nodes[current_index[j]]);
					}
					
					
					current_index[current_index.length - 1]++;
					for(int j = current_index.length - 1; j > 0; j--)
						if(current_index[j] >= proposed_nodes.length - (current_index.length - j)){
							current_index[j-1]++;
							for(int k = j; k < current_index.length; k++)
								current_index[k] = current_index[k-1] + 1;
						}	
				}
				
				String[] newBid = new String[best_group.size()];
				for(int i = 0; i < best_group.size(); i++)
					newBid[i] = best_group.get(i);
				groups.add(newBid);
				
				String[] new_list = new String[proposed_nodes.length - best_group.size()];
				int i = 0;
				for(int j = 0; j < proposed_nodes.length; j++)
					if(!best_group.contains(proposed_nodes[j])){
						new_list[i] = proposed_nodes[j];
						i++;
					}
				proposed_nodes = new_list;           
				
			}
		}
		
		//on répartit ce qui reste
		if(proposed_nodes.length > 0){
			LinkedList<String> remaining_nodes = new LinkedList<String>();
			for(int i = 0; i < proposed_nodes.length; i++)
				remaining_nodes.add(proposed_nodes[i]);
			
			LinkedList<String> group = new LinkedList<String>();
			LinkedList<Integer> indexes = new LinkedList<Integer>();
			double cost, min_cost;
			while(remaining_nodes.size() > 0){
				min_cost = Double.MAX_VALUE;
				indexes.clear();
				for(int i = 0; i < groups.size(); i++){
					group.clear();
					for(int j = 0; j < groups.get(i).length; j++)
						group.add(groups.get(i)[j]);
					group.add(remaining_nodes.getFirst());
					
					cost = this.CalculatePathCost(this.OrderNodes(group));
					
					if(Math.abs(cost - min_cost) < 0.001){
						indexes.add(i);
					}
					else if(cost < min_cost){
						min_cost = cost;
						indexes.clear();
						indexes.add(i);
					}
				}
				
				int winner_index = indexes.get((int)(Math.random() * indexes.size()));
				String[] new_group = new String[groups.get(winner_index).length + 1];
				for(int j = 0; j < groups.get(winner_index).length; j++)
					new_group[j] = groups.get(winner_index)[j];
				new_group[groups.get(winner_index).length] = remaining_nodes.getFirst();
				
				groups.remove(winner_index);
				groups.insertElementAt(new_group, winner_index);
				remaining_nodes.removeFirst();
			}
				
		}
		
		this.quitting_node_group = groups;
		
		ComplexBid bid = new ComplexBid(null, null, null, groups);
		SpeechAct act = new SpeechAct(id, SpeechActPerformative.QUIT, this.agent_id, "all_agents", bid);
		
		return act;
	}

	@Override
	/**
	 * when receiving a QUIT message with a list of nodes, the agent answers by giving its preferences over sets of nodes
	 * it calculates the set of nodes which minimizes the new path cost, record it, delete the set from the list, then iterates.
	 * This does not list all sets of nodes but limits the combinatorial explosion by ensuring that each node appears only
	 * once in the list of preferences.
	 */
	protected SpeechAct manage_Quit_Message(SpeechAct quitting_act) {
		Vector<String[]> proposed_nodegroup = quitting_act.getComplexBid().getDoubleBidsForBoth();
		double[] cost_list = new double[proposed_nodegroup.size()];
		
		LinkedList<String> mynodes_copy;
		double cost;
		Vector<String[]> bids = new Vector<String[]>();
		
		for(int i = 0; i < proposed_nodegroup.size(); i++){
			mynodes_copy = this.getMyNodes();
			for(int j = 0; j < proposed_nodegroup.get(i).length; j++)
				mynodes_copy.add(proposed_nodegroup.get(i)[j]);
			
			cost_list[i] = this.CalculatePathCost(this.OrderNodes(mynodes_copy));
		}
			
		boolean[] used = new boolean[proposed_nodegroup.size()];
		boolean all_used = false;
		while(!all_used){
			double min_cost = Double.MAX_VALUE;
			int index = -1;
			for(int i = 0; i < proposed_nodegroup.size(); i++){
				if(!used[i] && cost_list[i] < min_cost){
					min_cost = cost_list[i];
					index = i;
				}
			}
			
			String[] bid = new String[2];
			bid[0] = Integer.toString(index);
			bid[1] = Double.toString(cost_list[index] - this.visit_cost);
			bids.add(bid);
			used[index] = true;
		
			all_used = true;
			for(int j = 0; j < used.length; j++)
				all_used &= used[j];
			
		}	
		
		ComplexBid mybid = new ComplexBid(null, null, null, bids);
		
		return new SpeechAct(quitting_act.getTransactionId(), SpeechActPerformative.PROPOSE, this.agent_id, 
									quitting_act.getSender(), mybid);
	}

	@Override
	protected void manage_Quit_Protocol() {
		for(int i = this.FBABuyers.size() - 1; i > 0; i--)
    		if(!this.known_agents.contains(this.FBABuyers.get(i))){
    			SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, FBABuyers.get(i), new LinkedList<String>(), 0);
    			this.SendSpeechAct(reject);
    			this.FBABuyers.remove(i);
    			this.ComplexPropositions.remove(i);
    		}
		
		String[] agents = new String[this.FBABuyers.size()];
		int[][] nodes_preferences = new int[agents.length][];
		double[][] nodes_pref_costs = new double[agents.length][];

    	for(int i = 0; i < agents.length; i++){
    		agents[i] = this.FBABuyers.get(i);

    		Vector<String[]> preferences = this.ComplexPropositions.get(i).getDoubleBidsForBoth();
    		nodes_preferences[i] = new int[preferences.size()];
    		nodes_pref_costs[i] = new double[preferences.size()];
    		for(int j = 0; j < preferences.size(); j++){
    			String[] bid = preferences.get(j);
    			nodes_preferences[i][j] = Integer.valueOf(bid[0]);
    			nodes_pref_costs[i][j] = Double.valueOf(bid[1]);
    		}

    	}
    	
    	
    	int[] current_index = new int[agents.length];
    	String[][] distributed_nodes = new String[agents.length][this.myNodes.size()];
    	LinkedList<String> distributed_list = new LinkedList<String>();
    	int[] current_distributed_index = new int[agents.length];
    	int[] real_distributed_index = new int[agents.length];
    	boolean[] served = new boolean[agents.length];
    	
    	boolean finished = false;
    	
    	while(!finished){
    		double min_cost = Double.MAX_VALUE;
    		int index = -1;
    		for(int agent = 0; agent < agents.length; agent++){
    			if(current_index[agent] < nodes_pref_costs[agent].length){
	    			double cost = nodes_pref_costs[agent][current_index[agent]];
	    			if(cost < min_cost){
	    				min_cost = cost;
	    				index = agent;
	    			}
    			}
    		}
    		
    		if(index == -1)
    			break;
    		
    		String[] minimal_cost_set = this.quitting_node_group.get(nodes_preferences[index][current_index[index]]);
    		boolean not_ok = false;
    		for(int i = 0; i < minimal_cost_set.length; i++)
    			if(distributed_list.contains(minimal_cost_set[i]))
    				not_ok = true;
    		
    		if(not_ok){
    			current_index[index]++;
    			continue;
    		}
    		
    		for(int i = 0; i < minimal_cost_set.length; i++){
    			distributed_nodes[index][i] = minimal_cost_set[i];
    			current_distributed_index[index]++;
    			real_distributed_index[index]++;
    			distributed_list.add(minimal_cost_set[i]);
    		}
    		current_index[index] = nodes_pref_costs[index].length + 1;
    		served[index] = true;
    				
    		finished = true;
    		for(int j = 0; j < agents.length; j++)
    			finished &= (served[j]);
    		
    	}
    	
    	// les préférences étant incomplètes, on attribue les noeuds qui restent par proximité
    	LinkedList<String> mynodes_copy = this.getMyNodes();
    	for(int i = 0; i < distributed_list.size(); i++)
    		mynodes_copy.remove(distributed_list.get(i));
    	
    	while(mynodes_copy.size() > 0){
    		String current = mynodes_copy.getFirst();
    		Double min_dist = Double.MAX_VALUE;
    		LinkedList<Integer> index = new LinkedList<Integer>();
    		
    		for(int i = 0; i < agents.length; i++){
    			double dist = this.get_minDistance_to_nodes(current, distributed_nodes[i], current_distributed_index[i]);
    			if(Math.abs(dist - min_dist) < 0.001){
    				index.add(i);
    			}
    			else if(dist < min_dist){
    				min_dist = dist;
    				index.clear();
    				index.add(i);
    			}
    		}
    		
    		int winning_index = index.get((int)(Math.random() * index.size()));
    		distributed_nodes[winning_index][current_distributed_index[winning_index]] = current;
    		mynodes_copy.remove(current);
    		current_distributed_index[winning_index]++;
    		real_distributed_index[winning_index]++;
    	}
    	
    	for(int j = 0; j < agents.length; j++){
    		String[] nodes_to_send = new String[2 * real_distributed_index[j]];
    		for(int k = 0; k < real_distributed_index[j]; k++){
    			nodes_to_send[2*k] = distributed_nodes[j][k];
    			nodes_to_send[2*k+1] = String.valueOf((int)this.estimatedIdleness(distributed_nodes[j][k]));
    			
    			this.removeFromMyNodes(distributed_nodes[j][k]);
    		}
    		
    		ComplexBid bid = new ComplexBid(null, nodes_to_send, null, null);
    		SpeechAct act = new SpeechAct(myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, agents[j], bid);
			this.SendSpeechAct(act);
    	}
    	
    	this.inactive = true;

	}

	
	
	protected double get_minDistance_to_nodes(String target, LinkedList<String> nodes){
		double min_dist = Double.MAX_VALUE;
		Node target_node = this.graph.getNode(target);
		
		for(int i = 0; i < nodes.size(); i++){
			double dist = this.graph.getDistance(target_node, this.graph.getNode(nodes.get(i)));
			if(dist < min_dist)
				min_dist = dist;
		}
		
		return min_dist;
	}
	
	protected double get_minDistance_to_nodes(String target, String[] nodes, int last_index){
		double min_dist = Double.MAX_VALUE;
		Node target_node = this.graph.getNode(target);
		
		for(int i = 0; i < last_index; i++){
			double dist = this.graph.getDistance(target_node, this.graph.getNode(nodes[i]));
			if(dist < min_dist)
				min_dist = dist;
		}
		
		return min_dist;
	}
	
	
	@Override
	protected boolean enterCondition() {
		return true;
	}

}