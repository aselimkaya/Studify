package demo.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class User implements Serializable {
    
    private Integer id;
    
    // profile data
    private String name;
    private String username;
    private String profilePic;
    
    // studying data
    private Location currentLocation;
    
    private Topic currentTopic;    
    @JsonIgnoreProperties("members")
    private Team currentTeam;
    
    private Map<SubTopic, Integer> talentLevels;

    public User() {
	
    }

    public User(Integer id, String username, String name, String profilePic) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.profilePic = profilePic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // --- profile data --- //
    
    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
    
    // --- studying data --- //

    // location
    
    public Location getCurrentLocation() {
	return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
	this.currentLocation = currentLocation;
    }
    
    // topic

    public Topic getCurrentTopic() {
	return currentTopic;
    }

    public void setCurrentTopic(Topic topic) {
	if (!topic.getLocation().equals(getCurrentLocation())) {
	    throw new RuntimeException("Location of user and topic does not match!");
	}

	currentTopic = topic;
	topic.incrementUserCount();
	talentLevels = new HashMap<>();
	for (SubTopic subTopic : topic.getSubTopics()) {
	    talentLevels.put(subTopic, 0);
	}
    }
    
    public void quitCurrentTopic() {
	if (getCurrentTeam() != null) {
	    throw new RuntimeException("You need to first quit from team to quit from topic!");
	}
	currentTopic.decrementUserCount();
	currentTopic = null;
	talentLevels = null;
    }
    
    // team

    public Team getCurrentTeam() {
	return currentTeam;
    }
    
    public void setCurrentTeam(Team team) {
	if (team == null) {
	    throw new RuntimeException("This method does not accept null as parameter!" + 
		    "You can use quitCurrentTeam instead!");
	}
	if (!team.getTopic().equals(getCurrentTopic())) {
	    throw new RuntimeException("Topic of user and team does not match!");
	}
	
	currentTeam = team;
	
	if (!team.getMembers().contains(this)) {
	    team.addMember(this);
	}
    }
    
    public void quitCurrentTeam() {
	if (currentTeam == null) {
	    return;
	}
	Team previousTeam = currentTeam;
	currentTeam = null;
	previousTeam.removeMember(this);
    }
    
    // talent
    
    public void setTalentLevelOfSubTopic(SubTopic subTopic, Integer score) {
	talentLevels.put(subTopic, score);
    }
    
    public Integer getTalentLevel(SubTopic subTopic) {
	return talentLevels.get(subTopic);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    private static final long serialVersionUID = 1L;
     
}