
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static int currentLexemesIndex = 0;

    private static List<String> LexicalArray = new ArrayList<>();

    private static char[] SYMBOLS = {
            '#','=',';','{','}','(',')','<','>','%','\\','+',
    };

    private static String[] KEYWORDS = {
            "int", "char","return","include","stdio.h","main","printf"
    };

    static String STRING_LITERAL_REG = "\".*?\"";

    static String VARIABLE_REG = "([a-zA-Z]+[\\w]*[ ])";

    static String NUMBER_REG = "[+\\-]?[\\d]+";


    public static void main(String[] args) {


        System.out.println("\n\n");

        populateLexicalArray();
        readFile();

        //System.out.println("Char Id = " + indexOfID("x1"));
        printLexicalArray();
    }

    private static void populateLexicalArray(){

        for (String s : KEYWORDS){
            String[] stringArray = s.split("");
            LexicalArray.addAll(Arrays.asList(stringArray));
            LexicalArray.add("\\s");
        }
    }

    private static String addVariableToLexemes(String s){
        String[] stringArray = s.replaceAll(" ","").split("");
        LexicalArray.addAll(Arrays.asList(stringArray));
        LexicalArray.add("\\s");

        return String.valueOf(LexicalArray.size() - (stringArray.length + 2));
    }

    private static int indexOfID(String id){
        String[] idArray = id.split("");
        int initialIndex = 0;
        boolean found = false;

        for (int i = 0; i < idArray.length; i++) {
            String x = idArray[i];
            for (int j = 0; j < LexicalArray.size(); j++) {
                String y = LexicalArray.get(j);
                if (x.equals(y)) {
                    found = true;
                    initialIndex = j;
                    for (int a = j + 1; a < LexicalArray.size(); a++) {
                        if (idArray[i+1].equals(LexicalArray.get(j+1))){
                            return initialIndex;
                        }else{
                            found = false;

                        }
                    }
                }

            }

        }
        if (!found){
            return -1;
        }else{
            return initialIndex;
        }

    }


    private static void readFile(){
        Scanner sc;
        String text;
        try {
            sc = new Scanner(new File(  "lexical.c"));
            int line = 1;
            while (sc.hasNextLine()) {
                text = sc.nextLine();
                tokenize(text,line);
                System.out.println();
                line++;
            }

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("error pls check file");
        }
    }

    private static void printLexicalArray(){
        System.out.println("\n\nLexemes Array");
        for (String lexeme : LexicalArray){
            System.out.printf(" %s ,",lexeme);
        }
    }




    private static void tokenize (String s, int line_number){

        String line = String.valueOf(line_number);
        char[] charArray = s.toCharArray();

        String currentString = "";
        for (char c : charArray){
            //After a token has been identified, the space should be ignored.
            if (c == ' ') {
                if(!currentString.equals("")) 
                currentString = currentString.concat(String.valueOf(c));
            }else
                currentString = currentString.concat(String.valueOf(c));

            if (currentString.length() == 1){
                if (contains(SYMBOLS,currentString.charAt(0))){
                    printToken(currentString,"OPERATOR",line);
                    currentString = "";
                }
            }else{
                if (contains(KEYWORDS,currentString)){
                    printToken(currentString,"KEYWORD",line);
                    currentString = "";
                }else{
                    if((Pattern.compile(STRING_LITERAL_REG).matcher(currentString).matches())){
                        printToken(currentString,"String",line);
                        currentString = "";
                    }else if((Pattern.compile(NUMBER_REG).matcher(currentString).matches())){
                        printToken(currentString,"NUMBER",line);
                        currentString = "";
                    }else if (Pattern.compile(VARIABLE_REG).matcher(currentString).matches()){
                        int id = indexOfID(currentString);
                        if (id == -1){
                            printToken(currentString,"VARIABLE",addVariableToLexemes(currentString));
                        }else{
                            printToken(currentString,"VARIABLE",String.valueOf(id));
                        }

                        currentString = "";
                    }
                }
            }

        }
//        System.out.println("");

    }


    private static boolean contains(String[] array, String character){
        boolean found = false;
        for(String string : array){
            if (string.equals(character)){
                found = true;
            }
        }
        return found;
    }



    private static boolean contains(char[] array, char character){
        boolean found = false;
        for(char string : array){
            if (string == character){
                found = true;
            }
        }
        return found;
    }

    private static void printToken(String token,String type,String line){
        switch (type) {
            case "OPERATOR":
                String operatorName = "";
                switch (token.charAt(0)) {
                    case '#':
                        operatorName = "HASH";
                        break;
                    case '=':
                        operatorName = "EQUAL";
                        break;
                    case ';':
                        operatorName = "SEMICOLON";
                        break;
                    case '{':
                        operatorName = "LBRACE";
                        break;
                    case '}':
                        operatorName = "RBRACE";
                        break;
                    case '(':
                        operatorName = "LPAREN";
                        break;
                    case ')':
                        operatorName = "RPAREN";
                        break;
                    case '<':
                        operatorName = "LESS";
                        break;
                    case '>':
                        operatorName = "GREAT";
                        break;
                }
                System.out.printf("<OP_%s>  ", operatorName);
                break;
            case "VARIABLE":
                System.out.printf("<ID_%s>  ", line);
                break;
            default:
                System.out.printf("<%s>  ", token);
                break;
        }

    }



}