package algorithm;

import java.util.List;

import model.Team;
import model.Topic;
import model.User;

public abstract class MatchingAlgorithm {

    protected Topic topic;
    protected List<User> users;
    
    public MatchingAlgorithm(Topic topic, List<User> users) {
	this.topic = topic;
	this.users = users;
    }
    
    public abstract List<Team> match();

}