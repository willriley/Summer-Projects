import java.util.stream.IntStream;
import java.lang.StringBuilder;

public class MarkovModel {
	private static final int ASCII = 128;

	private int k;
	private ST<String, int[]> st; 
	// symbol table will have kgrams for keys, int[128] for values
	// this way, i'll know how many times each ASCII char succeeds a given kgram
	
	// makes a Markov model of order k for the specified text
	public MarkovModel(String text, int order) {
		st = new ST<>();
		k = order;
		text += text.substring(0,k); // makes circular string–last char precedes first	
		
		int i = 0;
		for (int index = k; index<text.length(); index++) {
			// makes substring from [i,index)
			String kgram = text.substring(i,index);

			// if kgram's not already in ST, add it
			// kgram is then key to empty int[]
			if (!st.contains(kgram)) 
				st.put(kgram, new int[ASCII]);

			// takes the char after the kgram
			// casts char to int according to ASCII code
			// increments that index in the array
			char next = text.charAt(index);
			st.get(kgram)[next]++;

			// increment second index var
			// to keep kgram moving and of size k
			i++;
		}
	}

	// returns the order k of this Markov model
	public int order() {
		return k;
	}

	// returns how often given kgram appears in text
    public int freq(String kgram) {
    	// since a Markov model's order determines kgram size
    	// the kgram's length must be = to the order
    	if(kgram.length() != k) 
    		throw new RuntimeException("kgram isn't of length k");

    	// if there's an int[] returned by st.get(kgram)
    	// sum all elements in the array using an IntStream
    	// if there's no int[] returned (i.e. kgram doesn't appear in text)
    	// then the freq is 0, catching any NullPointerException
    	int freq = (st.get(kgram)!=null) ? 
    		IntStream.of(st.get(kgram)).sum() : 0;

    	return freq;
    }

    // returns how often char c follows given kgram in text
    public int freq(String kgram, char c) {
    	if(kgram.length() != k) 
    		throw new RuntimeException("kgram isn't of length k");

    	return st.get(kgram)[c];
    }

    // returns random char following given kgram in a text,
    // chosen w/ weight proport. to # times that char follows that kgram in text
    public char random(String kgram) {
    	if(kgram.length() != k) 
    		throw new RuntimeException("kgram isn't of length k");

    	if (freq(kgram)==0) 
    		throw new RuntimeException("kgram doesn't appear in text");

    	// discrete() reads int[] of frequencies, returns random int index
    	// then typecast that int into a char and return it
    	return (char)StdRandom.discrete(st.get(kgram));
    }

    // generates String of length T, using initial kgram to gen first char
    public String gen(String kgram, int T) {
    	if(kgram.length() != k) 
    		throw new RuntimeException("kgram isn't of length k");

    	StringBuilder sb = new StringBuilder(kgram);
    	int iterations = T - k; 

    	for (int i=0; i<iterations; i++) {
    		kgram = sb.substring(i, sb.length());
    		sb.append(random(kgram));
    	}

    	return sb.toString();

    }

    // unit tests this class
    public static void main(String[] args) {
    	String text1 = "banana";
        MarkovModel model1 = new MarkovModel(text1, 2);
        System.out.println("freq(\"an\", 'a')    = " + model1.freq("an", 'a'));
        System.out.println("freq(\"na\", 'b')    = " + model1.freq("na", 'b'));
        System.out.println("freq(\"na\", 'a')    = " + model1.freq("na", 'a'));
        System.out.println("freq(\"na\")         = " + model1.freq("na"));
        System.out.println();

        String text2 = "one fish two fish red fish blue fish"; 
        MarkovModel model2 = new MarkovModel(text2, 4);
        System.out.println("freq(\"ish \", 'r') = " + model2.freq("ish ", 'r'));
        System.out.println("freq(\"ish \", 'x') = " + model2.freq("ish ", 'x'));
        System.out.println("freq(\"ish \")      = " + model2.freq("ish "));
        System.out.println("freq(\"tuna\")      = " + model2.freq("tuna"));	
        System.out.println();

        String text3 = "gagggagaggcgagaaa";
        MarkovModel model3 = new MarkovModel(text3, 2);
		System.out.println("freq(\"aa\", 'a')    = " + model3.freq("aa", 'a'));
		System.out.println("freq(\"aa\", 'c')    = " + model3.freq("aa", 'c'));
		System.out.println("freq(\"aa\", 'g')    = " + model3.freq("aa", 'g'));
		System.out.println("freq(\"aa\")         = " + model3.freq("aa"));
		System.out.println();
		
		System.out.println("freq(\"ag\", 'a')    = " + model3.freq("ag", 'a'));
		System.out.println("freq(\"ag\", 'c')    = " + model3.freq("ag", 'c'));
		System.out.println("freq(\"ag\", 'g')    = " + model3.freq("ag", 'g'));
		System.out.println("freq(\"ag\")         = " + model3.freq("ag"));
		System.out.println();
		
		System.out.println("freq(\"cg\", 'a')    = " + model3.freq("cg", 'a'));
		System.out.println("freq(\"cg\", 'c')    = " + model3.freq("cg", 'c'));
		System.out.println("freq(\"cg\", 'g')    = " + model3.freq("cg", 'g'));
		System.out.println("freq(\"cg\")         = " + model3.freq("cg"));
		System.out.println();
		
		System.out.println("freq(\"ga\", 'a')    = " + model3.freq("ga", 'a'));
		System.out.println("freq(\"ga\", 'c')    = " + model3.freq("ga", 'c'));
		System.out.println("freq(\"ga\", 'g')    = " + model3.freq("ga", 'g'));
		System.out.println("freq(\"ga\")         = " + model3.freq("ga"));
		System.out.println();
		
		System.out.println("freq(\"gc\", 'a')    = " + model3.freq("gc", 'a'));
		System.out.println("freq(\"gc\", 'c')    = " + model3.freq("gc", 'c'));
		System.out.println("freq(\"gc\", 'g')    = " + model3.freq("gc", 'g'));
		System.out.println("freq(\"gc\")         = " + model3.freq("gc"));
		System.out.println();
		
		System.out.println("freq(\"gg\", 'a')    = " + model3.freq("gg", 'a'));
		System.out.println("freq(\"gg\", 'c')    = " + model3.freq("gg", 'c'));
		System.out.println("freq(\"gg\", 'g')    = " + model3.freq("gg", 'g'));
		System.out.println("freq(\"gg\")         = " + model3.freq("gg"));
		System.out.println();
    }
}