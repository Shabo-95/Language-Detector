package de.hsrm.ads;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;

public class LanguageDetector {

	int ngrams;
	int size;
	HashMap<HashMap> tabelle1;
	ArrayList<String> sprachen;

	// Exercise 10.1: implement a hash map
	public static class HashMap<T> {

		// an entry in the hash table is a (key,value) pair.
		public class Entry {

			String key;
			T value;

			Entry(String key, T value) {
				this.key = key;
				this.value = value;
			}
		}

		// the table consists of entry objects
		Entry[] table;
		int basis;

		public HashMap(int N, int basis) {
			// N is the table size
			// basis is the basis used in the hash function.

			// creates the hash table (an array of Entry objects)
			// Do **not** change this line!
			table = (Entry[]) Array.newInstance(Entry[].class.getComponentType(), N);
			this.basis = basis;
		}

		public double fillRatio() {
			double m = 0;
			for (Entry e : table) if (e != null) m++;
			return m / table.length;
		}

		public int hashCode(String s) {
			if (s == null) return 0;

			var h = 0;
			char[] cs = s.toCharArray();
			for (char c : cs) h = (h * basis + c) % table.length; // Hashfunktion.

			// Wenn der Platz in der Map mit einem anderen Schl�ssel belegt ist,
			// wird sondiert.
			if (table[h] != null && !table[h].key.equals(s)) {
				for (int i = 0; table[h] != null && !table[h].key.equals(s); i++) {
					if (i > table.length) return -1; // Maximal N mal sondieren.
					h = (h + 2 * i + 1) % table.length; // Sondierungsfunktion.
				}
			}
			return h;
		}

		public T get(String key) {
			int h = hashCode(key);
			if (h < 0) return null; // Tabelle ist voll.
			Entry result = table[h];
			return result != null ? result.value : null;
		}

		public boolean add(String key, T value) {
			int h = hashCode(key);
			if (h < 0) return false; // Key ist nicht in der Tabelle.
			table[h] = new Entry(key, value);
			return true;
		}
	}

	public LanguageDetector(int n, int N) {
		// n = length of ngrams (e.g. for ngrams=3: wit, ith, th_, ...)
		// N = size of Hash table no. 2
		this.ngrams = n;
		this.size = N;
		this.tabelle1 = new HashMap<>(101, 31);
		this.sprachen = new ArrayList<>();
	}

	public void learnLanguage(String language, String text) {
		HashMap<Integer> ngramsMap = new HashMap<>(size, 31);
		tabelle1.add(language, ngramsMap);
		sprachen.add(language);

		var begin = 0;
		var end = ngrams;
		while (end <= text.length()) {
			String ngram = text.substring(begin, end);
			if (ngramsMap.get(ngram) == null) ngramsMap.add(ngram, 1); // Neues n-Gramm gefunden.
			else ngramsMap.add(ngram, ngramsMap.get(ngram) + 1);
			begin++;
			end++;
		}
	}

	public int getCount(String ngram, String language) {
		return tabelle1.get(language) == null // Wenn es die Sprache...
				|| tabelle1.get(language).get(ngram) == null ? 0 // und das n-Gramm gibt, ...
				: (int) tabelle1.get(language).get(ngram); // wird der Wert zur�ckgegeben.
	}

	public HashMap<Integer> apply(String text) {
		HashMap<Integer> values = new HashMap<>(101, 31);
		sprachen.forEach((t) -> values.add(t, 0)); // Alle Sprachen in die Values-Map eintragen.

		var begin = 0;
		var end = ngrams;
		while (end <= text.length()) {
			String ngram = text.substring(begin, end);

			// Maximalen Wert finden.
			var max = 0;
			for (String sprache : sprachen) {
				var value = tabelle1.get(sprache).get(ngram);
				if (value != null && (int) value > max) max = (int) value;
			}

			// Alle Sprachen mit dem maximalen Wert bekommen einen Punkt.
			for (String sprache : sprachen) {
				var value = tabelle1.get(sprache).get(ngram);
				if (value != null && (int) value == max) {
					values.add(sprache, values.get(sprache) + 1);
				}
			}
			begin++;
			end++;
		}
		return values;
	}

	public static void main(String[] args) {
		runTest(".\\alice", 4, 120001);
	}
	/*
	 * 
	 * THE FOLLOWING CODE IS USED FOR THE EXPERIMENT (EXERCISE 10.4).
	 * 
	 * DO *NOT* CHANGE THE CODE BELOW.
	 */

	public static LanguageDetector runTest(String BASE, int n, int N) {

		// create a lot of test sentences in 6 languages.

		String[] sentencesEnglish = new String[] { "I'm going to make him an offer he can't refuse.",
				"Toto, I've got a feeling we're not in Kansas anymore.", "May the Force be with you.",
				"If you build it, he will come.", "I'll have what she's having.", "A martini. Shaken, not stirred.",
				"Some people can’t believe in themselves until someone else believes in them first.",
				"I feel the need - the need for speed!",
				"Carpe diem. Seize the day, boys. Make your lives extraordinary.", "Nobody puts Baby in a corner.",
				"I'm king of the world!" };

		String[] sentencesGerman = new String[] { "Aber von jetzt an steht ihr alle in meinem Buch der coolen Leute.",
				"Wäre, wäre, Fahradkette", "Sehe ich aus wie jemand, der einen Plan hat?",
				"Erwartet mein Kommen, beim ersten Licht des fünften Tages.", "Du bist terminiert!",
				"Ich hab eine Wassermelone getragen.", "Einigen wir uns auf Unentschieden!",
				"Du wartest auf einen Zug, ein Zug der dich weit weg bringen wird.",
				"Ich bin doch nur ein Mädchen, das vor einem Jungen steht, und ihn bittet, es zu lieben.",
				"Ich genoss seine Leber mit ein paar Fava-Bohnen, dazu einen ausgezeichneten Chianti.",
				"Dumm ist der, der Dummes tut.", };

		String[] sentencesEsperanto = new String[] { "Al du sinjoroj samtempe oni servi ne povas.",
				"Al la fiŝo ne instruu naĝarton.", "Fiŝo pli granda malgrandan englutas.", "Kia patrino, tia filino.",
				"La manĝota fiŝo estas ankoraŭ en la rivero.", "Ne kotas besto en sia nesto.",
				"Ne singardema kokino fidas je vulpo.", "Por sperto kaj lerno ne sufiĉas eterno.",
				"Unu hako kverkon ne faligas." };

		String[] sentencesFinnish = new String[] { "Hei, hauska tavata.", "Olen kotoisin Suomesta.",
				"Yksi harrastuksistani on lukeminen.", "Nautin musiikin kuuntelusta.",
				"Juhannusperinteisiin kuuluu juhannussauna tuoreiden saunavihtojen kera, sekä pulahtaminen järveen.",
				"Aamu on iltaa viisaampi.", "Työ tekijäänsä neuvoo.", "Niin metsä vastaa, kuin sinne huudetaan." };

		String[] sentencesFrench = new String[] { "Franchement, ma chère, c’est le cadet de mes soucis.",
				"À tes beaux yeux.", "Si j’aurais su, j’aurais pas v’nu!",
				"Merci la gueuse. Tu es un laideron mais tu es bien bonne.", "Vous croyez qu’ils oseraient venir ici?",
				"La barbe ne fait pas le philosophe.", "Inutile de discuter.", "Paris ne s’est pas fait en un jour!",
				"Quand on a pas ce que l’on aime, il faut aimer ce que l’on a." };

		String[] sentencesItalian = new String[] { "Azzurro, il pomeriggio è troppo azzurro e lungo per me",
				"Con te, cos lontano e diverso Con te, amico che credevo perso ",
				"è restare vicini come bambini la felicità", "Buongiorno, Principessa!", "Ho Ucciso Napoleone.",
				"L’amore vince sempre.", "La semplicità è l’ultima sofisticazione.",
				"Una cena senza vino e come un giorno senza sole.",
				"Se non hai mai pianto, i tuoi occhi non possono essere belli." };

		ArrayList<String> sentences = new ArrayList<String>();
		ArrayList<String> labels = new ArrayList<String>();

		for (int i = 0; i < sentencesEnglish.length; ++i) {
			labels.add("english");
			sentences.add(sentencesEnglish[i]);
		}
		for (int i = 0; i < sentencesGerman.length; ++i) {
			labels.add("german");
			sentences.add(sentencesGerman[i]);
		}
		for (int i = 0; i < sentencesEsperanto.length; ++i) {
			labels.add("esperanto");
			sentences.add(sentencesEsperanto[i]);
		}
		for (int i = 0; i < sentencesFinnish.length; ++i) {
			labels.add("finnish");
			sentences.add(sentencesFinnish[i]);
		}
		for (int i = 0; i < sentencesFrench.length; ++i) {
			labels.add("french");
			sentences.add(sentencesFrench[i]);
		}
		for (int i = 0; i < sentencesItalian.length; ++i) {
			labels.add("italian");
			sentences.add(sentencesItalian[i]);
		}

		long startTime = System.currentTimeMillis();

		// add the 6 languages to the language detector
		String[] languages = new String[] { "german", "english", "esperanto", "finnish", "french", "italian" };
		LanguageDetector ld = new LanguageDetector(n, N);
		// System.out.println("File !!!!!!!!!!!!!");
		ld.learnLanguage("english", read(BASE + "/alice.en.txt"));
		ld.learnLanguage("german", read(BASE + "/alice.de.txt"));
		ld.learnLanguage("esperanto", read(BASE + "/alice.eo.txt"));
		ld.learnLanguage("finnish", read(BASE + "/alice.fi.txt"));
		ld.learnLanguage("french", read(BASE + "/alice.fr.txt"));
		ld.learnLanguage("italian", read(BASE + "/alice.it.txt"));

		int correct = 0;
		int m = labels.size();

		// apply the language detector to the test sentences...
		for (int i = 0; i < m; ++i) {

			HashMap<Integer> votes = ld.apply(sentences.get(i));

			String result = getMax(languages, votes);
			String label = labels.get(i);

			System.out.println("language=" + label + ", result=" + result + " : " + sentences.get(i));
			

			// ... and count the correct sentences.
			correct += result.equals(label) ? 1 : 0;
		}

		long endTime = System.currentTimeMillis();

		// output result.
		System.out.printf("%d of %d samples correct. Accuracy=%.1f percent. Experiment took %d ms.\n", correct, m,
				100. * correct / m, endTime - startTime);

		return ld;
	}

	private static String read(String filename) {
		// help method (reads a file)

		try {

			StringBuilder sb = new StringBuilder();
			Reader in = new InputStreamReader(new FileInputStream(filename), "UTF-8");
			BufferedReader reader = new BufferedReader(in);

			String s;
			while ((s = reader.readLine()) != null) {
				// ignore blanks + comments
				if (s.length() != 0 && s.charAt(0) != '#') {
					sb.append(s);
				}
			}

			reader.close();
			return sb.toString();

		} catch (IOException e) {
			String msg = "I/O-Fehler bei " + filename + "\n" + e.getMessage();
			throw new RuntimeException(msg);
		}

	}

	public static String getMax(String[] languages, HashMap<Integer> votes) {
		// help method: takes a result of getCount()
		// and computes the language with most votes.
		String best = "";
		int cbest = -1;
		for (String l : languages) {
			int c = votes.get(l);
			if (c > cbest) {
				best = l;
				cbest = c;
			}
		}
		return best;
	}
}
