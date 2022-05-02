// Server program

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


    public static void main(String[] args) throws IOException {
        int portnum =8888;
        ServerSocket serverSocket = new ServerSocket(portnum);
        while(true){
            try{
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");
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
                System.out.printf("Received from client:\n%s\n", valued);

                Object response = sense(valued);

                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
                System.out.printf("Sent to client:\n%s\n", response);
                valued = (Object[]) objectInputStream.readObject();
            }

            objectOutputStream.close();
            objectInputStream.close();
        } catch (Exception ignored) {
        }
    }
    private static ArrayList<Forum> database;


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

    private static void editStudentGrades(String studentusername) {
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
                    System.out.println(line);
                    if (line.equals("----------")) {
                        System.out.println("Would you like to give " +
                                studentusername + " a grade for this response?\n1. Yes\n 2.No");
                        int gradebool = scanner.nextInt();
                        scanner.nextLine();
                        while (!(gradebool == 1 || gradebool == 2)) {
                            System.out.println("Not an option. Try Again");
                            System.out.println("Would you like to give " +
                                    studentusername + " a grade for this response?\n1. Yes\n 2.No");
                            gradebool = scanner.nextInt();
                        }
                        if (gradebool == 1) {
                            System.out.println("What grade would you like to give to this assignment?(Out of 100)");
                            String gradeval = scanner.nextLine();
                            lines.add(linesdown - 1, "Grade:" + gradeval);
                        } else if (gradebool == 2) {
                            System.out.println("Would you like to continue " +
                                    "reviewing this students work?\n1.Yes\n2.No");
                            int continuelook = scanner.nextInt();
                            while (!(continuelook == 1 || continuelook == 2)) {
                                System.out.println("Not an option. Try Again");
                                System.out.println("Would you like to give " +
                                        studentusername + " a grade for this response?\n1. Yes\n 2.No");
                                continuelook = scanner.nextInt();
                            }
                            if (continuelook == 1) {
                                continue;
                            } else {
                                return;
                            }
                        }
                    }
                }
            } else {
                System.out.println("Student does not exist try a different username");
            }
            PrintWriter pw = new PrintWriter(new FileOutputStream(studentval, false));
            for (String line : lines) {
                pw.println(line);
            }
            pw.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void viewStudentGrades(String studentusername) {
        File studentval = new File("studentresponses/" + studentusername + ".txt");
        try {
            if (studentval.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(studentval));
                String eachline = br.readLine();
                while (eachline != null) {
                    System.out.println(eachline);
                    eachline = br.readLine();
                }
            } else {
                System.out.println("The username is wrong. Try again");
            }
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
                System.out.println("All Forums:");
                ArrayList<Forum> forum = (ArrayList<Forum>) in.readObject();
                System.out.println(forum);
                in.close();
                file.close();
                forumval = forum;
            }


        } catch (EOFException e) {
            // ... this is fine
        } catch (IOException e) {
            // handle exception which is not expected
            System.out.println("Issue loading in the file");
            e.printStackTrace();
        }
        return forumval;
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

    private static void createForum(String courseName, String topicName, Post post) {
        Forum forum = new Forum(courseName, topicName, post);
        database.add(forum);
        System.out.println("You have created a forum");
    }

    private static String viewForum(String topicName) {
        boolean found = false;
        for (int i = 0; i < database.size(); i++) {
            if (database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                System.out.println(database.get(i).toString());
                found = true;

            }
        }
        if (!found) {
            return "There is no forum dedicated to this topic.";
        } else {
            return "The forum was found.";
        }
    }

    private static void deleteForum(String topicName) {
        boolean found = false;
        for (int i = 0; i < database.size(); i++) {
            if (database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                database.remove(i);
                found = true;
            }
        }
        if (!found) {
            System.out.println("There is no forum dedicated to this topic.");
        }
    }

    private static void createPost(String topicName, String name, String body) {
        boolean found = false;
        for (Forum forum : database) {
            if (forum.getTopicName().equalsIgnoreCase(topicName)) {
                forum.posts.add(new Post(name, body));
                found = true;
                System.out.println("The post was created.");

            }
        }
        if (!found) {
            System.out.println("There is no forum dedicated to this topic.");
        }
    }


    private static void editPost(String topicName, String name, String body, int postvalue) {
        Post editpost = new Post(name, body);
        boolean found = false;
        for (Forum forum : database) {
            if (forum.getTopicName().equalsIgnoreCase(topicName)) {
                forum.getPosts().set(postvalue - 1, editpost);
                System.out.println("Your post was successfully edited");
                found = true;
            }
        }
        if (!found) {
            System.out.println("There is no forum dedicated to this topic.");
        }


    }

    private static void editComment(String topicName, String name, String body, int postvalue, int commentvalue) {
        Comment comm = new Comment(name, body);
        boolean found = false;
        for (Forum forum : database) {
            if (forum.getTopicName().equalsIgnoreCase(topicName)) {
                forum.getPosts().get(postvalue - 1).getComments().set(commentvalue - 1, comm);
                System.out.println("Your comment was successfully edited");
                found = true;
            }
        }
        if (!found) {
            System.out.println("There is no forum dedicated to this topic.");
        }

    }

    private static void replyPost(String topicName, String name, String body, int postvalue) {
        Comment comm = new Comment(name, body);
        boolean found = false;
        for (Forum forum : database) {
            if (forum.getTopicName().equalsIgnoreCase(topicName)) {
                forum.getPosts().get(postvalue - 1).getComments().add(comm);
                System.out.println("You successfully replied to a post.");
                found = true;

            }
        }
        if (!found) {
            System.out.println("There is no forum dedicated to this topic.");
        }


    }

    private static void deleteComment(String topicName, String name, String body, int postvalue, int commentvalue) {
        Comment comm = new Comment(name, body);
        for (int i = 0; i < database.size(); i++) {
            if (database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                database.get(i).getPosts().get(postvalue - 1).getComments().set(commentvalue - 1, comm);
                System.out.println("Your comment was successfully deleted");
                return;
            }
        }
        System.out.println("There is no forum dedicated to this topic.");
    }

    private static void deletePost(String topicName, String name, String body, int postvalue) {
        Post editpost = new Post(name, body);
        for (int i = 0; i < database.size(); i++) {
            if (database.get(i).getTopicName().equalsIgnoreCase(topicName)) {
                database.get(i).getPosts().set(postvalue - 1, editpost);
                System.out.println("Your post was successfully deleted");
                return;
            }
        }
        System.out.println("There is no forum dedicated to this topic.");

    }

    private static User askuser() {
        Scanner scan = new Scanner(System.in);
        String username;
        String password;
        String role;
        System.out.println("What is your username?");
        username = scan.nextLine();
        System.out.println("What is your password?");
        password = scan.nextLine();
        System.out.println("What is your role?(Student or teacher)");
        role = scan.nextLine();
        User playmaker = new User(username, password, role);
        return playmaker;
    }


    public Object sense(Object[] inputter) throws IOException, ClassNotFoundException {
        database = loadForum("forum.db");
            String coursename = "CS 180";
            User player= new User();
            int login = 0;

            int whattodo=(int)inputter [2];

            switch (whattodo) {
                case 1:
                    System.out.println("You have logged out");
                    writeForum(database, "forum.db");
                    return 0;
                case 2:
                    String sort = (String) inputter [1];
                    if(sort.equalsIgnoreCase("teacher")){
                    String topicName = (String) inputter[4];
                    Post initpost =  (Post) inputter[5];
                    createForum(coursename, topicName, initpost);
                        return new Object[]{topicName};
                    }
                    if(sort.equalsIgnoreCase("student")){
                        String topicName = (String) inputter[4];
                        Post initpost =  (Post) inputter[5];
                        String name = (String) inputter[6];
                        String postbody = initpost.getBody();
                        createForum(coursename, topicName, initpost);
                        addcommentstofile(name, topicName, postbody, true);
                        return new Object[]{topicName};
                    }
                    return 0;

                case 3:
                    String topicName = (String) inputter[3];
                    return viewForum(topicName);

                case 4:
                     sort = (String) inputter[1];
                    if(sort.equalsIgnoreCase("teacher")){
                        topicName = (String) inputter[3];
                        deleteForum(topicName);
                        return new Object[]{topicName};
                    }
                    if(sort.equalsIgnoreCase("student")){
                        topicName = (String) inputter[3];
                        String name = (String) inputter[4];
                        String postbody = (String) inputter[5];
                        createPost(topicName, name, postbody);
                        addcommentstofile(name, topicName, postbody, true);
                        return new Object[]{topicName};
                    }
                    return 0;
                case 5:
                    int postNumber = 1;
                    int val = 0;
                    sort = (String) inputter[1];
                    if(sort.equalsIgnoreCase("teacher")){
                        topicName = (String) inputter[3];
                        String name = (String) inputter[4];
                        String postbody = (String) inputter[5];
                        createPost(topicName, name, postbody);
                        return new Object[]{topicName};
                    }
                    if(sort.equalsIgnoreCase("student")){
                        String name = (String) inputter[4];
                        topicName = (String) inputter[3];
                        postNumber = (int) inputter[6];
                        String postbody = (String) inputter[5];
                        replyPost(topicName, name, postbody, postNumber);
                        addcommentstofile(name, topicName, postbody, false);
                        return new Object[]{topicName};
                    }
                    return 0;
                case 6:
                    postNumber = 1;
                    val = 0;
                    sort = (String) inputter[1];
                    if(sort.equalsIgnoreCase("teacher")){
                        String name = (String) inputter[4];
                        topicName = (String) inputter[3];
                        postNumber = (int) inputter[6];
                        String postbody = (String) inputter[5];
                        replyPost(topicName, name, postbody, postNumber);
                        return new Object[]{topicName};
                    }
                    if(sort.equalsIgnoreCase("student")){
                        String name = (String) inputter[3];
                        String password = (String) inputter [4];
                        String role = (String) inputter [5];
                        player.editFile(name, password, role);
                        return new Object[]{name};
                    }
                    return 0;
                case 7:
                    sort = (String) inputter[1];
                    postNumber = 1;
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
                            editPost(topicName, name, postbody, postNumber);
                            return new Object[]{topicName};
                        }
                        if (porc.equalsIgnoreCase("comment")) {
                            commentNumber = (int) inputter[8];
                            String name = (String) inputter[5];
                            String commentbody = (String) inputter[6];
                            editComment(topicName, name, commentbody, postNumber, commentNumber);
                            return new Object[]{topicName};
                        }
                    }
                    if(sort.equalsIgnoreCase("student")){
                        String name = (String) inputter[3];
                        String password = (String) inputter [4];
                        String role = (String) inputter [5];
                        writeForum(database, "forum.db");
                        player.deletelogin(name, password, role);
                        return new Object[]{name};
                    }
                    return 0;
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
                            deletePost(topicName, name, postbody, postNumber);
                            return new Object[]{topicName};

                        }
                        if (porc.equalsIgnoreCase("comment")) {
                            commentNumber = (int) inputter[8];
                            String name = (String) inputter[5];
                            String commentbody = (String) inputter[6];
                            deleteComment(topicName, name, commentbody, postNumber, commentNumber);
                            return new Object[]{topicName};
                        }
                    }
                    if(sort.equalsIgnoreCase("student")){
                        String usernamestudent = (String) inputter[3];
                        viewStudentGrades(usernamestudent);
                        return new Object[]{usernamestudent};
                    }
                    return 0;
                case 9:
                    String name = (String) inputter[3];
                    String password = (String) inputter [4];
                    String role = (String) inputter [5];
                    player.editFile(name, password, role);
                    return new Object[]{name};
                case 10:
                     name = (String) inputter[3];
                     password = (String) inputter [4];
                     role = (String) inputter [5];
                    writeForum(database, "forum.db");
                    player.deletelogin(name, password, role);
                    return new Object[]{name};
                case 11:
                    String studentName = (String) inputter [3];
                    editStudentGrades(studentName);
                    return new Object[]{studentName};
                default:
                    return 0;

            }

    }
}
