import java.util.ArrayList;

/**
 * The Forum/Topics class
 *
 * <p>Purdue University -- CS18000 -- Spring 2021</p>
 *
 * @author Group 92
 * @version April 10, 2022
 */

public class Forum implements java.io.Serializable {
    private String courseName;
    private String topicName;
    public ArrayList<Post> posts;

    public Forum(String courseName, String topicName, Post post) { // forum
        this.courseName = courseName;
        this.topicName = topicName;
        this.posts = new ArrayList<>();
        this.posts.add(post);
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public String getTopicName() {
        return topicName;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    @Override
    public String toString() {
        String forumstring = "Course Name: " + courseName +
                "\nTopic Name: " + topicName +
                "\n" + this.getPostsForum();
        return forumstring;
    }

    public String getPostsForum() {
        StringBuilder res = new StringBuilder();
        for (Post post : this.posts)
            res.append(post.toString()).append("\n");
        return res.toString();
    }


}
