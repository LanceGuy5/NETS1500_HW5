import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScraper {

    private static ClassNode getNode (List<ClassNode> nodes, String code) {
        for (ClassNode iter: nodes) {
            if (iter.getCode().equals(code)) {
                return iter;
            }
        }
        return null;
    }


    public static ClassGraph scrapeMajor(String majorCode) {
        majorCode = majorCode.toLowerCase();

        URLGetter webPage;
        String content = "";
        HashMap<String, String> majors = new HashMap<>();

        try {
            webPage = new URLGetter("https://catalog.upenn.edu/courses/");
            List<String> contents = webPage.getContents();
            for (String line: contents) {
                content += line;
            }

            String urlPat = "(href=\")(/[a-z]+/" + majorCode + ")";
            Pattern url = Pattern.compile(urlPat);
            Matcher urlMatch = url.matcher(content);
            urlMatch.find();
            String majorUrl = "https://catalog.upenn.edu" + urlMatch.group(2);
            webPage = new URLGetter(majorUrl);
            contents = webPage.getContents();


            ClassNode root = new ClassNode("Major Requirements", null);

            ClassGraph fin = new ClassGraph();
            fin.setRoot(root);

            String classStr = "(<p class=\"courseblocktitle noindent\"><strong>)"
                    + "(" + majorCode + ".[0-9]+)" +
                    "..(([a-zA-Z]+[^<]{1,2})+)";
            Pattern classPat = Pattern.compile(classStr, Pattern.CASE_INSENSITIVE);
            ClassNode temp = null;


            String prereqStr = "(Prerequisite:.+)";
            Pattern prereqPat = Pattern.compile(prereqStr, Pattern.CASE_INSENSITIVE);

            String prereqStrTwo = "(<a href=\"[^\"]+\".{1,2}title=\")("
                    + majorCode + ".[0-9]+)";
            Pattern prereqPatTwo = Pattern.compile(prereqStrTwo, Pattern.CASE_INSENSITIVE);

            List<ClassNode> classNodes = new LinkedList<>();

            for (String line: contents) {
                Matcher classMatch = classPat.matcher(line);
                Matcher prereqMatch = prereqPat.matcher(line);
                if (classMatch.find()) {
                    String currCode = classMatch.group(2);
                    String currName = classMatch.group(3);
                    ClassNode curr = getNode(classNodes, currCode);
                    if (curr != null) {
                        curr.setName(currName);
                    } else {
                        curr = new ClassNode(currName, currCode);
                        root.addChild(curr);
                        classNodes.add(curr);
                    }


                    temp = curr;
                }
                if (prereqMatch.find()) {
                    String currText = prereqMatch.group();
                    Matcher prereqMatchTwo = prereqPatTwo.matcher(currText);

                    while (prereqMatchTwo.find()) {
                        String currCode = prereqMatchTwo.group(2);

                        ClassNode curr = getNode(classNodes, currCode);
                        if (curr == null) {
                            curr = new ClassNode(currCode, currCode);
                            root.addChild(curr);
                            classNodes.add(curr);
                        }
                        curr.addChild(temp);
                    }


                }
            }


            return fin;


        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }





    }

    public static String scrapeClassInfo(String classCode) {
        String majorStr = "[a-z]+";
        Pattern majorPat = Pattern.compile(majorStr, Pattern.CASE_INSENSITIVE);
        Matcher majorMatch = majorPat.matcher(classCode);
        majorMatch.find();
        String majorCode = majorMatch.group();
        majorCode = majorCode.toLowerCase();


        String classNumStr = "[0-9]+";
        Pattern classPat = Pattern.compile(classNumStr);
        Matcher classMatch = classPat.matcher(classCode);
        classMatch.find();
        String classNum = classMatch.group();

        URLGetter webPage;
        String content = "";
        HashMap<String, String> majors = new HashMap<>();

        try {
            webPage = new URLGetter("https://catalog.upenn.edu/courses/");
            List<String> contents = webPage.getContents();
            for (String line: contents) {
                content += line;
            }

            String urlPat = "(href=\")(/[a-z]+/" + majorCode + ")";
            Pattern url = Pattern.compile(urlPat);
            Matcher urlMatch = url.matcher(content);
            urlMatch.find();
            String majorUrl = "https://catalog.upenn.edu" + urlMatch.group(2);
            webPage = new URLGetter(majorUrl);
            contents = webPage.getContents();
            content = "";
            for (String line: contents) {
                content += line;
            }

            String classStr = "(<p class=\"courseblocktitle noindent\"><strong>)("
                     + majorCode + "." + classNum +
                    ")..(([a-zA-Z]+[^<]{1,2})+</strong></p>)" +
                    "(<p class=\"courseblockextra noindent\">)" +
                    "([^<]+)";
            Pattern classPattern = Pattern.compile(classStr, Pattern.CASE_INSENSITIVE);
            Matcher classMatcher = classPattern.matcher(content);
            classMatcher.find();
            System.out.println(classMatcher.group(6));

            return classMatcher.group(6);


            
        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

}
