package demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {

    private Integer id;

    // profile data
    private String name;
    private String username;
    private String profilePic;

    // studying data
    @JsonIgnoreProperties("members")
    private Team currentTeam;
    private Topic currentTopic;
    private Location currentLocation;

    @JsonIgnore
    private Map<SubTopic, Integer> talentLevels;

    @JsonIgnore
    private Set<Request> requests = new HashSet<>();

    private String token;

    public User() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // --- profile data --- //

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
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
            throw new RuntimeException(
                    "This method does not accept null as parameter!" + "You can use quitCurrentTeam instead!");
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

    public void addRequest(Request request) {
        if (!request.getRequester().equals(this)) {
            throw new RuntimeException("This request does not belong to this user!");
        }
        requests.add(request);
    }

    public void removeRequest(Request request) {
        requests.remove(request);
    }

    public Set<Request> getRequests() {
        return requests;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public JSONObject toJSONObject(String... ignore) {
        List<String> ignoreList = Arrays.asList(ignore);

        Map<String, Object> map = new HashMap<>();
        if (!ignoreList.contains("id"))
            map.put("id", getId() != null ? getId() : JSONObject.NULL);

        if (!ignoreList.contains("name"))
            map.put("name", getName() != null ? getName() : JSONObject.NULL);

        if (!ignoreList.contains("username"))
            map.put("username", getUsername() != null ? getUsername() : JSONObject.NULL);

        if (!ignoreList.contains("profilePic"))
            map.put("profilePic", getProfilePic() != null ? getProfilePic() : JSONObject.NULL);

        if (!ignoreList.contains("currentTeam"))
            map.put("currentTeam",
                    getCurrentTeam() != null ? getCurrentTeam().toJSONObject("members", "requests") : JSONObject.NULL);

        if (!ignoreList.contains("currentTopic"))
            map.put("currentTopic",
                    getCurrentTopic() != null ? getCurrentTopic().toJSONObject("location", "teams") : JSONObject.NULL);

        if (!ignoreList.contains("currentLocation"))
            map.put("currentLocation",
                    getCurrentLocation() != null ? getCurrentLocation().toJSONObject("topics") : JSONObject.NULL);

        if (!ignoreList.contains("requests")) {
            if (getRequests() != null) {
                map.put("requests", JSONObject.NULL);
            } else {
                List<JSONObject> requestsAsJSONObjects = new ArrayList<>();
                for (Request request : getRequests()) {
                    requestsAsJSONObjects.add(request.toJSONObject("requester"));
                }
                map.put("requests", requestsAsJSONObjects);
            }
        }

        if (!ignoreList.contains("token"))
            map.put("token", getToken() != null ? getToken() : JSONObject.NULL);

        return new JSONObject(map);
    }

    private static final long serialVersionUID = 1L;

}
