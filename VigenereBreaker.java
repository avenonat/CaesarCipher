import java.util.*;
import edu.duke.*;
import java.io.File;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder str = new StringBuilder();
        for (int i = whichSlice; i < message.length(); i +=totalSlices) {
            str.append(message.charAt(i));
        }
        return str.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        CaesarCracker cc = new CaesarCracker();
        for (int i = 0; i < klength; i++) {
            String s = sliceString(encrypted, i, klength);
            int a = cc.getKey(s);
            key[i] = a;
        }
        //WRITE YOUR CODE HERE
        return key;
    }

    public void breakVigenere (int keyLength) {
        FileResource fr = new FileResource();
        String str = fr.asString();
        int[] a = tryKeyLength(str, keyLength, 'e');
        VigenereCipher vc = new VigenereCipher(a);
        String msg = vc.decrypt(str);
        System.out.println(msg);
    }
    
    public HashSet<String> readDictionary(FileResource fr) {
        HashSet<String> str = new HashSet<String>();
        for (String s : fr.lines()) {
            s = s.toLowerCase();
            str.add(s);
        }
        return str;
    }
    
    public int countWords(String message, HashSet<String> dictionary) {
        String[] str = message.split("\\W+");
        int i = 0;
        for (String s : str) {
            String slower = s.toLowerCase();
            if (dictionary.contains(slower)) {
                i++;
            }
        }
        return (i);
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary) {
        int max = 0;
        int[] num = null;
        char c = mostCommonCharIn(dictionary);
        for (int i = 1; i <= 100; i++) {
            int[] key = tryKeyLength(encrypted, i, c);
            VigenereCipher vc = new VigenereCipher(key);
            String s = vc.decrypt(encrypted);
            int a = countWords(s, dictionary);
            if (a > max) {
                max = a;
                num = key;
            }
        }
        System.out.println("Number of same words is: " + max);
        System.out.println("The key: ");
        int len = 0;
        for (int n : num) {
            System.out.println(n + " ");
            len++;
        }
        System.out.println("The key length: " + len);
        for (int i = 1; i <= 100; i++) {
            int[] key = tryKeyLength(encrypted, i, c);
            VigenereCipher vc = new VigenereCipher(key);
            String s = vc.decrypt(encrypted);
            int a = countWords(s, dictionary);
            if (a == max) {
                return s;
            }
        }   
        return null;
    }
    
    public void breakVigenere() {
        FileResource fr = new FileResource();
        String s = fr.asString();
        DirectoryResource dr = new DirectoryResource();
        HashMap<String, HashSet<String>> hm = new HashMap<String, HashSet<String>>();
        for (File f : dr.selectedFiles()) {
            FileResource dick = new FileResource(f);
            hm.put(f.getName(), readDictionary(dick));
            System.out.println(f.getName() + " read.");
        }
        breakForAllLangs(s, hm);
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary) {
        HashMap<Character, Integer> letters = new HashMap<Character, Integer>();
        for (String s : dictionary) {
            String slower = s.toLowerCase();
            for (char c : slower.toCharArray()) {
                if (letters.containsKey(c)) {
                    letters.put(c, letters.get(c) + 1);
                }
                else {
                    letters.put(c, 1);
                }
            }
        }
        int max = 0;
        for (char c :letters.keySet()) {
            if (letters.get(c) > max) {
                max = letters.get(c);
            }
        }
        for (char c : letters.keySet()) {
            if (letters.get(c) == max) {
                return c;
            }
        }
        return 'n';
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages) {
        int max = 0;
        for (String language : languages.keySet()) {
            String s = breakForLanguage(encrypted, languages.get(language));
            int i = countWords(s, languages.get(language));
            if (i > max) {
                max = i;
            }
        }
        for (String language : languages.keySet()) {
            String s = breakForLanguage(encrypted, languages.get(language));
            int i = countWords(s, languages.get(language));
            if(i == max) {
                System.out.println(s + "\n" + language);
                break;
            }
        }
    }
}
