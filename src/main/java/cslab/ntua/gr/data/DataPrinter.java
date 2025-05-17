package cslab.ntua.gr.data;

import java.util.List;

import cslab.ntua.gr.entities.Agent;

public class DataPrinter extends DataGenerator
{
    int counter;
    Agent[] agents;

	public DataPrinter(Agent[] agents)
    {
        super(agents.length);
        this.counter = -1;
        this.agents = agents;
    }
	
	@Override
	protected List<Integer> line()
	{
		counter += 1;
		return agents[counter].getPrefList();
	}

}
