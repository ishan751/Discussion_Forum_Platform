

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * The Main class
 *
 * <p>Purdue University -- CS18000 -- Spring 2021</p>
 *
 * @author Group 92
 * @version April 10, 2022
 */

// lines that need guis or 193,200 and the LoadForum method which was not there before, I added that because it was
// in the old code

public class Main {
    private static ArrayList<Forum> database;


    public static ArrayList<String> readFile(String fileName) throws FileNotFoundException {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader bfr = new BufferedReader(new FileReader(fileName));
        String line = "";
        try {
            while (line != null) {
                line = bfr.readLine();
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }

    private static void writeForum(ArrayList<Forum> forums, String filepath) {
        try {
            FileOutputStream fir = new FileOutputStream(filepath);
            ObjectOutputStream obj = new ObjectOutputStream(fir);
            obj.writeObject(forums);
            obj.close();
            fir.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Forum> loadForum(String filepath) throws IOException, ClassNotFoundException {
        ArrayList<Forum> forumval = new ArrayList<Forum>();

        try {

            FileInputStream file = new FileInputStream(filepath);
            ObjectInputStream in = new ObjectInputStream(file);
            PushbackInputStream input = new PushbackInputStream(file);

            if (input.available() == 0) {
                return new ArrayList<>();
            } else {
                JOptionPane.showMessageDialog(null, "About to see All Forums", "Create a Forum", JOptionPane.PLAIN_MESSAGE);
                ArrayList<Forum> forum = (ArrayList<Forum>) in.readObject();
                JOptionPane.showMessageDialog(null, forum, "Create a Forum", JOptionPane.PLAIN_MESSAGE);
                in.close();
                file.close();
                forumval = forum;
            }


        } catch (EOFException e) {
            // ... this is fine
        } catch (IOException e) {
            // handle exception which is not expected
            JOptionPane.showMessageDialog(null, "Issue Loading File!", "Importing a File", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return forumval;
    }

    private static User askuser() {
        Scanner scan = new Scanner(System.in);
        String username;
        String password;
        String role;
        username = JOptionPane.showInputDialog(null, "Please enter username:", "Username", JOptionPane.QUESTION_MESSAGE);
        password = JOptionPane.showInputDialog(null, "Please enter password:", "Password", JOptionPane.QUESTION_MESSAGE);
        role = JOptionPane.showInputDialog(null, "Please enter role ('teacher' or 'student')", "Username", JOptionPane.QUESTION_MESSAGE);
        User playmaker = new User(username, password, role);
        return playmaker;
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scan = new Scanner(System.in);
        Client players = new Client();
        String coursename = "CS 180";
        User player;
        // added
        String[] choice = {"Yes", "No"}; // ADD TO MAIN
        String[] loginMenu = {"Sign up", "Login"}; // ADD TO MAIN
        String[] createMenu = {"Write Manually","Import a File"};
        String[] editMenu = {"Edit a Post","Edit a Comment"};
        String[] deleteMenu = {"Delete a Post", "Delete a Comment"};
        database = loadForum("forum.db");
        String[] studentOption = {"0.Logout", "1. Create a Forum", "2. View a Forum", "3. Create a Post", "4. Reply to a Post",
                "5.Edit Login Info", "6.Delete Login Info", "7.Grade Students"};
        String[] teacherOption = {"0.Logout", "1. Create a Forum", "2. View a Forum", "3.Delete a Forum",
                "4. Create a Post", "5. Reply to a Post", "6. Edit a Post/Comment", "7. Delete a Post/Comment",
                "8.Edit Login Info", "9.Delete Login Info", "10.Grade Students"};

        int login = JOptionPane.showOptionDialog(null, "Login in or Sign-up", "Login Menu", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, loginMenu, null);
        boolean logcheckin = false;
        if (login == 1) {
            player = askuser();
            logcheckin = player.login();
        } else if (login == 0) {
            player = new User();
            player.createLoginFile();
            int loginaftersignup = JOptionPane.showOptionDialog(null, "Thanks for signing up! Would you like to login?", "Login Menu", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, choice, null);
            if (loginaftersignup == 0) {
                player = askuser();
                logcheckin = player.login();
            } else {
                writeForum(database, "forum.db");
                return;
            }

        } else {
            return;
        }

        boolean quit = false;
        while (!logcheckin && !quit) {
            try {
                int quitter = JOptionPane.showOptionDialog(null, "Invalid Login. Do you want to come back later?", "Login Menu", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, choice, null);
                if (quitter == 0) {
                    writeForum(database, "forum.db");
                    return;
                }
                player = askuser();
                logcheckin = player.login();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (player.getRole().equalsIgnoreCase("teacher")) {
            while (true) {
                String teachermen = (String) JOptionPane.showInputDialog(null, "Select your action", "Menu", JOptionPane.QUESTION_MESSAGE, null, teacherOption, teacherOption[0]);

                if (teachermen.equals(teacherOption[0])) {
                    Object [] input = {1,"teacher",1};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Logging Off", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                if (teachermen.equals(teacherOption[1])) {
                    String topicName = JOptionPane.showInputDialog(null, "What is the topic of the Forum you would like to create?", "Create a Forum", JOptionPane.QUESTION_MESSAGE);
                    String postbody = JOptionPane.showInputDialog(null, "What would you like to say in the initial Post?", "Create a Forum", JOptionPane.QUESTION_MESSAGE);
                    Post initpost = new Post(player.getUsername(), postbody);
                    Object [] input = {1,"teacher",2,coursename,topicName, initpost};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Create a Forum", JOptionPane.PLAIN_MESSAGE);

                }
                if (teachermen.equals(teacherOption[2])) {
                    String topicName = JOptionPane.showInputDialog(null, "What is the topic of the Forum you would like to view?", "View Forum", JOptionPane.QUESTION_MESSAGE);
                    Object [] input = {1,"teacher",3,topicName};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "View Forum", JOptionPane.PLAIN_MESSAGE);
                }
                if (teachermen.equals(teacherOption[3])) {
                    String topicName = JOptionPane.showInputDialog(null, "What is the topic of the Forum you would like to delete?", "Delete Forum", JOptionPane.QUESTION_MESSAGE);
                    Object [] input = {1,"teacher",4,topicName};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Delete Forum", JOptionPane.PLAIN_MESSAGE);
                }
                if (teachermen.equals(teacherOption[4])) {
                    boolean run = true;
                    do {
                        //adding a new topic with file imports
                        int fileorwrite = JOptionPane.showOptionDialog(null, "Would you like to import a file to create a post or write out the post?", "Create a Post", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, createMenu, null);
                        if (fileorwrite == 0) {
                            String topicName = JOptionPane.showInputDialog(null, "What is the topic of your post about?", "Writing a Post", JOptionPane.QUESTION_MESSAGE);
                            String postbody = JOptionPane.showInputDialog(null, "What would you like to say in the post?", "Writing a Post", JOptionPane.QUESTION_MESSAGE);
                            Object [] input = {1,"teacher",5,topicName, player.getUsername(), postbody};
                            JOptionPane.showMessageDialog(null, (String) players.innout(input), "Writing a Post", JOptionPane.PLAIN_MESSAGE);
                            run = false;
                        } else if (fileorwrite == 1) {
                            String topicName = JOptionPane.showInputDialog(null, "What is the topic of your post about?", "Importing a File", JOptionPane.QUESTION_MESSAGE);
                            String filename = JOptionPane.showInputDialog(null, "What is the the name of the file?", "Importing a File", JOptionPane.QUESTION_MESSAGE);;
                            File postin = new File(filename);
                            if(postin.exists()){
                                StringBuilder res = new StringBuilder();
                                try {
                                    for (int i = 0; i < readFile(filename).size() - 1; i++) {
                                        res.append((readFile(filename).get(i)));
                                    }
                                    String postbod = res.toString();
                                    Object [] input = {1,"teacher",5,topicName, player.getUsername(), postbod};
                                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Importing a File", JOptionPane.PLAIN_MESSAGE);
                                    run = false;
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    run = true;
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "File does not exist!", "Importing a File", JOptionPane.ERROR_MESSAGE);
                            }

                        } else {
                            run = true;
                        }
                    } while (run);

                }
                if (teachermen.equals(teacherOption[5])) {
                    // Reply to a Post
                    String postNum = "";
                    boolean databaseexists = false;
                    int val = 0;
                    String topicName = JOptionPane.showInputDialog(null, "What topic would you like to reply to", "Replying to a Post", JOptionPane.QUESTION_MESSAGE);
                    for (int i = 0; i < database.size(); i++) {
                        if (database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                            databaseexists = true;
                            val = i;
                        }
                    }
                    if (databaseexists) {
                        try {
                            postNum = JOptionPane.showInputDialog(null, "What post would you like to reply to, give an integer to represent the post number", "Replying to a Post", JOptionPane.QUESTION_MESSAGE);
                            int postNumber = Integer.parseInt(postNum);
                            while (!(postNumber > 0 && postNumber <= database.get(val).getPosts().size()+1)) {
                                postNum = JOptionPane.showInputDialog(null, "Post number is unavailable! Give an integer for the post number", "Replying to a Post", JOptionPane.QUESTION_MESSAGE);
                                postNumber = Integer.parseInt(postNum);

                            }
                            if (postNumber > 0 && postNumber <= database.get(val).getPosts().size()+1) {
                                String postbody = JOptionPane.showInputDialog(null, "What would you like to reply to the post?", "Replying to a Post", JOptionPane.QUESTION_MESSAGE);
                                Object [] input = {1,"teacher",6,topicName, player.getUsername(), postbody, postNumber};
                                JOptionPane.showMessageDialog(null, (String) players.innout(input), "Replying to a post", JOptionPane.PLAIN_MESSAGE);
                            }
                        } catch (NumberFormatException | InputMismatchException e) {
                            JOptionPane.showMessageDialog(null, "Post does not Exist!", "Replying to a post", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No forum dedicated to this topic", "Replying to a post", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (teachermen.equals(teacherOption[6])) {
                    String postNum = "";
                    int commentPost = 0;
                    String commentNum = "";
                    boolean databaseexists = false;
                    int val = 0;
                    String topicName = JOptionPane.showInputDialog(null, "What topic would you like to edit?", "Edit a Post", JOptionPane.QUESTION_MESSAGE);
                    for (int i = 0; i < database.size(); i++) {
                        if (database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                            databaseexists = true;
                            val = i;
                        }
                    }
                    if (databaseexists) {
                        try {
                            postNum = JOptionPane.showInputDialog(null, "What post would you like to change? give an integer to represent the post number", "Edit a Post/Comment", JOptionPane.QUESTION_MESSAGE);
                            int postNumber = Integer.parseInt(postNum);
                            while (!(postNumber > 0 && postNumber <= database.get(val).getPosts().size()+1)) {
                                postNum = JOptionPane.showInputDialog(null, "Post is unavailable! What post would you like to reply to, give an integer for the post number", "Edit a Post/Comment", JOptionPane.QUESTION_MESSAGE);
                                postNumber = Integer.parseInt(postNum);
                            }
                            if (postNumber > 0 && postNumber <= database.get(val).getPosts().size()+1) {
                                commentPost = JOptionPane.showOptionDialog(null, "Would you like to edit a comment in the post or the post itself?", "Edit a Post/Comment", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, editMenu, null);
                            }
                            if (commentPost == 0) {
                                String postbody = JOptionPane.showInputDialog(null, "What would you like to replace the post with?", "Editing a Post", JOptionPane.QUESTION_MESSAGE);
                                Object [] input = {1,"teacher",7,"post",topicName, player.getUsername(), postbody, postNumber};
                                JOptionPane.showMessageDialog(null, (String) players.innout(input), "Editing a Post", JOptionPane.PLAIN_MESSAGE);

                            }
                            if (commentPost == 1) {
                                commentNum = JOptionPane.showInputDialog(null, "What comment would you like to change? give an integer to represent the comment number", "Editing a Comment", JOptionPane.QUESTION_MESSAGE);
                                int commentNumber = Integer.parseInt(commentNum);
                                while (!(commentNumber > 0 && commentNumber <= database.get(val).getPosts().
                                        get(postNumber - 1).getComments().size()+1)) {
                                    commentNum = JOptionPane.showInputDialog(null, "Comment is unavailable! What comment would you like to reply to, give an integer for the post number", "Editing a Comment", JOptionPane.QUESTION_MESSAGE);
                                    commentNumber = Integer.parseInt(commentNum);
                                }
                                if (commentNumber > 0 && commentNumber <= database.get(val).getPosts().
                                        get(postNumber - 1).getComments().size()+1) {
                                    String commentbody = JOptionPane.showInputDialog(null, "What would you like to replace the comment with?", "Editing a Comment", JOptionPane.QUESTION_MESSAGE);
                                    Object [] input = {1,"teacher",7,"comment",topicName, player.getUsername(), commentbody, postNumber,commentNumber};
                                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Editing a Comment", JOptionPane.PLAIN_MESSAGE);

                                }
                            }

                        } catch (NumberFormatException | InputMismatchException e) {
                            JOptionPane.showMessageDialog(null, "Please Input an Integer!", "Edit a Post/Comment", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "There is no forum dedicated to this topic", "Edit a Post/Comment", JOptionPane.ERROR_MESSAGE);
                    }


                }
                if (teachermen.equals(teacherOption[7])) {
                    //Delete a Post/Comment
                    String postNum = "";
                    int commentPost = 0;
                    String commentNum = "";
                    boolean databaseexists = false;
                    int val = 0;
                    String topicName = JOptionPane.showInputDialog(null, "What topic has a post you would like to delete?", "Delete a Post", JOptionPane.QUESTION_MESSAGE);
                    for (int i = 0; i < database.size(); i++) {
                        if (database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                            databaseexists = true;
                            val = i;
                        }
                    }
                    if (databaseexists) {
                        try {
                            postNum = JOptionPane.showInputDialog(null, "What post/post that has a comment would you like to delete? give an integer to represent the post number", "Delete a Post/Comment", JOptionPane.QUESTION_MESSAGE);
                            int postNumber = Integer.parseInt(postNum);
                            while (!(postNumber > 0 && postNumber <= database.get(val).getPosts().size()+1)) {
                                postNum = JOptionPane.showInputDialog(null, "Post is unavailable! What post would you like to delete, give an integer for the post number", "Delete a Post/Comment", JOptionPane.QUESTION_MESSAGE);;
                                postNumber = Integer.parseInt(postNum);
                            }
                            if (postNumber > 0 && postNumber <= database.get(val).getPosts().size()+1) {
                                commentPost = JOptionPane.showOptionDialog(null, "Would you like to delete a comment in the post or the post itself?", "Delete a Post/Comment", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, deleteMenu, null);
                            }
                            if (commentPost == 0) {
                                String postbody = "This post has been deleted by " + player.getUsername();
                                Object [] input = {1,"teacher",8,"post",topicName, player.getUsername(), postbody, postNumber};
                                JOptionPane.showMessageDialog(null, (String) players.innout(input), "Deleting a Post", JOptionPane.PLAIN_MESSAGE);

                            }
                            if (commentPost == 1) {
                                postNum = JOptionPane.showInputDialog(null, "What comment would you like to delete? give an integer to represent the comment number", "Delete a Comment", JOptionPane.QUESTION_MESSAGE);
                                int commentNumber = Integer.parseInt(commentNum);
                                while (!(commentNumber > 0 && commentNumber <= database.get(val).getPosts().
                                        get(postNumber - 1).getComments().size()+1)) {
                                    commentNum = JOptionPane.showInputDialog(null, "Comment is unavailable! What post would you like to delete, give an integer for the post number", "Delete a Comment", JOptionPane.QUESTION_MESSAGE);;
                                    commentNumber = Integer.parseInt(commentNum);
                                }
                                if (commentNumber > 0 && commentNumber <= database.get(val).getPosts().
                                        get(postNumber - 1).getComments().size()+1) {
                                    String commentbody = "This comment has been deleted by " + player.getUsername();
                                    Object [] input = {1,"teacher",8,"comment",topicName, player.getUsername(), commentbody, postNumber,commentNumber};
                                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Deleting a Comment", JOptionPane.PLAIN_MESSAGE);

                                }
                            }

                        } catch (NumberFormatException | InputMismatchException e) {
                            JOptionPane.showMessageDialog(null, "Please Input an Integer!", "Edit a Post/Comment", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No forum dedicated to this topic", "Replying to a post", JOptionPane.ERROR_MESSAGE);
                    }

                }
                if (teachermen.equals(teacherOption[8])) {
                    player = askuser();
                    player.editFile(player.getUsername(), player.getPassword(), player.getRole());
                    Object [] input = {1,"teacher",9};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Edit Login Info", JOptionPane.PLAIN_MESSAGE);
                }
                if (teachermen.equals(teacherOption[9])) {
                    //delete login
                    player = askuser();
                    player.deletelogin(player.getUsername(), player.getPassword(), player.getRole());
                    Object [] input = {1,"teacher",10, player.getUsername(), player.getPassword(), player.getRole()};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Delete Login Info", JOptionPane.PLAIN_MESSAGE);

                }
                if (teachermen.equals(teacherOption[10])) {
                    // grade students
                    String studentname= JOptionPane.showInputDialog(null, "What student's work would you like to grade?", "Grade Students", JOptionPane.QUESTION_MESSAGE);
                    Object [] input = {1,"teacher",11, studentname};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Grade Students", JOptionPane.PLAIN_MESSAGE);
                }
            }

        }
        if (player.getRole().equalsIgnoreCase("student")) {
            while (true) {
                String studentmen = (String) JOptionPane.showInputDialog(null, "Select your action", "Menu", JOptionPane.QUESTION_MESSAGE, null, studentOption, teacherOption[0]);

                if (studentmen.equals(teacherOption[0])) {
                    Object [] input = {1,"teacher",1};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Logging Off", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                if (studentmen.equals(teacherOption[1])) {
                    String topicName = JOptionPane.showInputDialog(null, "What is the topic of the forum you would like to create?", "Writing a Post", JOptionPane.QUESTION_MESSAGE);
                    String postbody = JOptionPane.showInputDialog(null, "What would you like to say in your initial post?", "Writing a Post", JOptionPane.QUESTION_MESSAGE);
                    Post initpost = new Post(player.getUsername(), postbody);
                    Object [] input = {1,"student",2,coursename,topicName, initpost, player.getUsername()};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Writing a Post", JOptionPane.PLAIN_MESSAGE);

                }
                if (studentmen.equals(teacherOption[2])) {
                    String topicName = JOptionPane.showInputDialog(null, "What is the topic of the forum you would like to view?", "View a Forum", JOptionPane.QUESTION_MESSAGE);
                    Object [] input = {1,"student",3,topicName};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "View a Forum", JOptionPane.PLAIN_MESSAGE);
                }
                if (studentmen.equals(teacherOption[3])) {
                    // Create a Post
                    String topicName = JOptionPane.showInputDialog(null, "What is the topic of your post about?", "Writing a Post", JOptionPane.QUESTION_MESSAGE);
                    String postbody = JOptionPane.showInputDialog(null, "What would you like to say in the post?", "Writing a Post", JOptionPane.QUESTION_MESSAGE);
                    Object [] input = {1,"student",4,topicName, player.getUsername(), postbody};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Writing a Post", JOptionPane.PLAIN_MESSAGE);
                }
                if (studentmen.equals(teacherOption[4])) {
                    String postNum = "";
                    int commentPost = 1;
                    int commentNumber = 1;
                    boolean databaseexists = false;
                    int val = 0;
                    String topicName = JOptionPane.showInputDialog(null, "What topic would you like to reply to", "Replying to a Post", JOptionPane.QUESTION_MESSAGE);
                    for (int i = 0; i < database.size(); i++) {
                        if (database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                            databaseexists = true;
                            val = i;
                        }
                    }
                    if (databaseexists) {
                        try {
                            postNum = JOptionPane.showInputDialog(null, "What post would you like to reply to, give an integer to represent the post number", "Replying to a Post", JOptionPane.QUESTION_MESSAGE);
                            int postNumber = Integer.parseInt(postNum);
                            while (!(postNumber > 0 && postNumber <= database.get(val).getPosts().size()+1)) {
                                postNum = JOptionPane.showInputDialog(null, "Post is unavailable! What post would you like to delete, give an integer for the post number", "Replying to a Post", JOptionPane.QUESTION_MESSAGE);;
                                postNumber = Integer.parseInt(postNum);
                            }
                            if (postNumber > 0 && postNumber <= database.get(val).getPosts().size()+1) {
                                boolean runner = true;
                                do {
                                    int fileorreply = JOptionPane.showOptionDialog(null, "Would you like to reply by typing or import a file?", "Replying to a Post", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, createMenu, null);
                                    if (fileorreply == 0) {
                                        String postbody = JOptionPane.showInputDialog(null, "What would you like to reply to the post?", "Replying to a Post", JOptionPane.QUESTION_MESSAGE);
                                        Object [] input = {1,"student",5,topicName, player.getUsername(), postbody, postNumber};
                                        JOptionPane.showMessageDialog(null, (String) players.innout(input), "Reolying to a Post", JOptionPane.PLAIN_MESSAGE);
                                        runner = false;
                                    } else if (fileorreply == 1) {
                                        String filename = JOptionPane.showInputDialog(null, "What is the the name of the file?", "Importing a File", JOptionPane.QUESTION_MESSAGE);;
                                        StringBuilder fileread = new StringBuilder();
                                        try {
                                            for (int i = 0; i < readFile(filename).size() - 1; i++) {
                                                fileread.append((readFile(filename).get(i)));
                                            }
                                            String replybod = fileread.toString();
                                            Object [] input = {1,"student",5,topicName, player.getUsername(), replybod, postNumber};
                                            JOptionPane.showMessageDialog(null, (String) players.innout(input), "Importing a File", JOptionPane.PLAIN_MESSAGE);;
                                            runner = false;
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                            runner = true;
                                        }
                                    } else {
                                        runner = true;
                                    }

                                } while (runner);
                            }
                        } catch (NumberFormatException | InputMismatchException e) {
                            JOptionPane.showMessageDialog(null, "Please Input an Integer!", "Edit a Post/Comment", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "There is no forum dedicated to this topic", "Edit a Post/Comment", JOptionPane.ERROR_MESSAGE);
                    }

                }
                if (studentmen.equals(teacherOption[5])) {
                    player = askuser();
                    player.editFile(player.getUsername(), player.getPassword(), player.getRole());
                    Object [] input = {1,"student",6};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Edit Login Info", JOptionPane.PLAIN_MESSAGE);
                }
                if (studentmen.equals(teacherOption[6])) {
                    //delete login
                    player = askuser();
                    player.deletelogin(player.getUsername(), player.getPassword(), player.getRole());
                    Object [] input = {1,"student",7};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "Delete Login Info", JOptionPane.PLAIN_MESSAGE);
                }
                if (studentmen.equals(teacherOption[7])) {
                    String usernamestudent = JOptionPane.showInputDialog(null, "What is your username?", "View Grades", JOptionPane.QUESTION_MESSAGE);
                    Object [] input = {1,"student",8, usernamestudent};
                    JOptionPane.showMessageDialog(null, (String) players.innout(input), "View Grades", JOptionPane.PLAIN_MESSAGE);

                }

            }

        }

    }
}