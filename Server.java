// Server program

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class Server {
    protected static ArrayList<Forum> database;
    private static ArrayList<Forum> loadForum(String filepath) throws IOException, ClassNotFoundException {
        ArrayList<Forum> forumval = new ArrayList<Forum>();

        try {

            FileInputStream file = new FileInputStream(filepath);
            ObjectInputStream in = new ObjectInputStream(file);
            PushbackInputStream input = new PushbackInputStream(file);

            if (input.available() == 0) {
                return new ArrayList<>();
            } else {
                JOptionPane.showMessageDialog(null, "About to see the forum!", "Importing a File", JOptionPane.PLAIN_MESSAGE);
                ArrayList<Forum> forum = (ArrayList<Forum>) in.readObject();
                JOptionPane.showMessageDialog(null, forum, "Importing a File", JOptionPane.PLAIN_MESSAGE);
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


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int portnum =7777;
        database = loadForum("forum.db");
        ServerSocket serverSocket = new ServerSocket(portnum);
        while(true){
            try{
                Socket socket = serverSocket.accept();
                JOptionPane.showMessageDialog(null, "Client Connected", "Connection", JOptionPane.INFORMATION_MESSAGE);
                new ServerTh(socket).start();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
class ServerTh extends Thread{
    Socket socket;
    public ServerTh(Socket socket){
        this.socket= socket;
    }

    public void run() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            Object[] valued = (Object[]) objectInputStream.readObject();
            while (valued != null) {

                Object response = sense(valued);

                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                valued = (Object[]) objectInputStream.readObject();
            }

            objectOutputStream.close();
            objectInputStream.close();
        } catch (Exception ignored) {
        }
    }



    private static void addcommentstofile(String username, String topicName,
                                          String reply, boolean postorreply) throws IOException {
        String filename = "studentresponses/" + username + ".txt";
        File addcommfile = new File(filename);
        boolean file = false;
        Date timestamp = new Date(System.currentTimeMillis());
        while (!file) {
            if (!addcommfile.exists()) {
                addcommfile.createNewFile();
                file = true;
            }
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(addcommfile, true));
                pw.println(topicName);
                if (postorreply) {
                    pw.println("Post");
                } else {
                    pw.println("Reply");
                }
                pw.println(timestamp);
                pw.println(reply);
                pw.println("----------");
                pw.close();
                file = true;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String editStudentGrades(String studentusername) {
        String[] choices = {"Yes", "No"};
        File studentval = new File("studentresponses/" + studentusername + ".txt");
        Scanner scanner = new Scanner(System.in);
        List<String> lines = new ArrayList<>();
        try {
            int linesdown = 0;
            if (studentval.exists()) {
                lines = Files.readAllLines(Paths.get("studentresponses/" +
                        studentusername + ".txt"), StandardCharsets.UTF_8);
                for (String line : new ArrayList<>(lines)) {
                    linesdown++;
                    JOptionPane.showMessageDialog(null, line, "Grading Menu", JOptionPane.PLAIN_MESSAGE);
                    if (line.equals("----------")) {
                        int gradebool = JOptionPane.showOptionDialog(null,"Would you like to give " + studentusername + " a grade for this response?" , "Grading Menu", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
                        if (gradebool == 0) {
                            String gradeval = JOptionPane.showInputDialog(null, "What grade would you like to give to this assignment?(Out of 100)", "Grading Menu", JOptionPane.QUESTION_MESSAGE);
                            lines.add(linesdown - 1, "Grade:" + gradeval);
                        } else if (gradebool == 1) {
                            int continuelook = JOptionPane.showOptionDialog(null,"Would you like to continue reviewing this students work?" , "Grading Menu", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);
                            if (continuelook == 0) {
                                continue;
                            } else {
                                return "Thanks for grading";
                            }
                        }
                    }
                }
            } else {
                return "Student does not exist try a different username";
            }
            PrintWriter pw = new PrintWriter(new FileOutputStream(studentval, false));
            for (String line : lines) {
                pw.println(line);
            }
            pw.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return "Thank you for grading";
    }

    private static String viewStudentGrades(String studentusername) {
        File studentval = new File("studentresponses/" + studentusername + ".txt");
        try {
            if (studentval.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(studentval));
                String eachline = br.readLine();
                StringBuilder res = new StringBuilder();
                while (eachline != null) {
                    res.append(eachline);
                    eachline = br.readLine();
                }
                return res.toString();
            } else {
                return "The username is wrong. Try again";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "These are your grades";


    }


    private static String writeForum(ArrayList<Forum> forums, String filepath) {
        try {
            FileOutputStream fir = new FileOutputStream(filepath);
            ObjectOutputStream obj = new ObjectOutputStream(fir);
            obj.writeObject(forums);
            obj.close();
            fir.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  "The forum was written";
    }

    private static String createForum(String courseName, String topicName, Post post) {
        Server.database.add(new Forum(courseName, topicName, post));
        return "You have created a forum";
    }

    private static String viewForum(String topicName) throws IOException, ClassNotFoundException {
        boolean found = false;
        for (int i = 0; i < Server.database.size(); i++) {
            if (Server.database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                found = true;
                return "Database:" + (Server.database.get(i).toString());


            }
        }
            return "There is no forum dedicated to this topic.";

    }

    private static String deleteForum(String topicName) {
        boolean found = false;
        for (int i = 0; i < Server.database.size(); i++) {
            if (Server.database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                Server.database.remove(i);
                found = true;
                return "You deleted a forum";
            }
        }
            return "There is no forum dedicated to this topic.";

    }

    private static String createPost(String topicName, String name, String body) {
        boolean found = false;
        int i=0;
        for (Forum forum : Server.database) {
            i++;
            if (forum.getTopicName().equalsIgnoreCase(topicName)) {
                forum.posts.add(new Post(name, body));
                found = true;
                return "The post was created." + Server.database.get(i-1).toString();
            }
        }
            return "There is no forum dedicated to this topic.";
    }


    private static String editPost(String topicName, String name, String body, int postvalue) {
        Post editpost = new Post(name, body);
        boolean found = false;
        int i=0;
        for (Forum forum : Server.database) {
            i++;
            if (forum.getTopicName().equalsIgnoreCase(topicName)) {
                forum.getPosts().set(postvalue - 1, editpost);
                found = true;
                return "The post was succesfully edited." + Server.database.get(i-1).toString();
            }
        }
            return "There is no forum dedicated to this topic.";

    }

    private static String editComment(String topicName, String name, String body, int postvalue, int commentvalue) {
        Comment comm = new Comment(name, body);
        boolean found = false;
        int i=0;
        for (Forum forum : Server.database) {
            i++;
            if (forum.getTopicName().equalsIgnoreCase(topicName)) {
                forum.getPosts().get(postvalue - 1).getComments().set(commentvalue - 1, comm);
                found = true;
                return "Your comment was successfully edited" + Server.database.get(i-1).toString();
            }
        }
            return "There is no forum dedicated to this topic.";
    }

    private static String replyPost(String topicName, String name, String body, int postvalue) {
        Comment comm = new Comment(name, body);
        boolean found = false;
        int i=0;
        for (Forum forum : Server.database) {
            i++;
            if (forum.getTopicName().equalsIgnoreCase(topicName)) {
                forum.getPosts().get(postvalue - 1).getComments().add(comm);
                found = true;
                return "You successfully replied to a post."+ Server.database.get(i-1).toString();

            }
        }
            return "There is no forum dedicated to this topic.";

    }

    private static String deleteComment(String topicName, String name, String body, int postvalue, int commentvalue) {
        Comment comm = new Comment(name, body);
        for (int i = 0; i < Server.database.size(); i++) {
            if (Server.database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                Server.database.get(i).getPosts().get(postvalue - 1).getComments().set(commentvalue - 1, comm);
                return "Your comment was successfully deleted";
            }
        }
        return "There is no forum dedicated to this topic.";
    }

    private static String deletePost(String topicName, String name, String body, int postvalue) {
        Post editpost = new Post(name, body);
        for (int i = 0; i < Server.database.size(); i++) {
            if (Server.database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                Server.database.get(i).getPosts().set(postvalue - 1, editpost);
                return "Your post was successfully deleted";

            }
        }
        return "There is no forum dedicated to this topic.";

    }


    public Object sense(Object[] inputter) throws IOException, ClassNotFoundException {
        String coursename = "CS 180";
        User player= new User();
        int login = 0;

        int whattodo=(int)inputter [2];

        switch (whattodo) {
            case 1:
                 writeForum(Server.database, "forum.db");
                 return "You have logged out";

            case 2:
                String sort = (String) inputter [1];
                if(sort.equalsIgnoreCase("teacher")){
                    String topicName = (String) inputter[4];
                    Post initpost =  (Post) inputter[5];
                     return createForum(coursename, topicName, initpost);
                } else {
                    String topicName = (String) inputter[4];
                    Post initpost =  (Post) inputter[5];
                    String name = (String) inputter[6];
                    String postbody = initpost.getBody();
                    addcommentstofile(name, topicName, postbody, true);
                    return createForum(coursename, topicName, initpost);
                }

            case 3:
                String topicName = (String) inputter[3];
                return viewForum(topicName);
            case 4:
                sort = (String) inputter[1];
                if(sort.equalsIgnoreCase("teacher")){
                    topicName = (String) inputter[3];
                   return deleteForum(topicName);

                }else {
                    topicName = (String) inputter[3];
                    String name = (String) inputter[4];
                    String postbody = (String) inputter[5];
                    addcommentstofile(name, topicName, postbody, true);
                     return createPost(topicName, name, postbody);
                }
            case 5:
                int postNumber = 1;
                int val = 0;
                sort = (String) inputter[1];
                if(sort.equalsIgnoreCase("teacher")){
                    topicName = (String) inputter[3];
                    String name = (String) inputter[4];
                    String postbody = (String) inputter[5];
                    return createPost(topicName, name, postbody);

                } else {
                    String name = (String) inputter[4];
                    topicName = (String) inputter[3];
                    postNumber = (int) inputter[6];
                    String postbody = (String) inputter[5];
                    addcommentstofile(name, topicName, postbody, false);
                    return replyPost(topicName, name, postbody, postNumber);
                }
            case 6:
                sort = (String) inputter[1];
                if(sort.equalsIgnoreCase("teacher")){
                    String name = (String) inputter[4];
                    topicName = (String) inputter[3];
                    postNumber = (int) inputter[6];
                    String postbody = (String) inputter[5];
                    return replyPost(topicName, name, postbody, postNumber);

                }else {
                    return "You edited your login credentials";
                }
            case 7:
                sort = (String) inputter[1];
                int commentPost = 1;
                int commentNumber = 1;
                val = 0;
                if(sort.equalsIgnoreCase("teacher")){
                    topicName = (String) inputter[4];
                    String porc= (String) inputter [3];
                    postNumber = (int) inputter[7];
                    if (porc.equalsIgnoreCase("post")) {
                        String name = (String) inputter[5];
                        String postbody = (String) inputter[6];
                        return editPost(topicName, name, postbody, postNumber);
                    }
                    if (porc.equalsIgnoreCase("comment")) {
                        commentNumber = (int) inputter[8];
                        String name = (String) inputter[5];
                        String commentbody = (String) inputter[6];
                        editComment(topicName, name, commentbody, postNumber, commentNumber);
                        return editComment(topicName, name, commentbody, postNumber, commentNumber);
                    }
                } else {
                    writeForum(Server.database, "forum.db");

                    return "You deleted your login credentials";
                }
            case 8:
                //Delete a Post/Comment
                sort = (String) inputter[1];
                if(sort.equalsIgnoreCase("teacher")){
                    topicName = (String) inputter[4];
                    String porc= (String) inputter [3];
                    postNumber = (int) inputter[7];
                    if (porc.equalsIgnoreCase("post")) {
                        String name = (String) inputter[5];
                        String postbody = (String) inputter[6];
                        return deletePost(topicName, name, postbody, postNumber);


                    }
                    if (porc.equalsIgnoreCase("comment")) {
                        commentNumber = (int) inputter[8];
                        String name = (String) inputter[5];
                        String commentbody = (String) inputter[6];
                        return deleteComment(topicName, name, commentbody, postNumber, commentNumber);

                    }
                } else {
                    String usernamestudent = (String) inputter[3];
                     return viewStudentGrades(usernamestudent);

                }
            case 9:
                return "You edited your login credentials";
            case 10:
                writeForum(Server.database, "forum.db");
                return "You deleted your login credentials";
            case 11:
                String studentName = (String) inputter [3];
                return editStudentGrades(studentName);
            default:
                return 0;

        }

    }
}
