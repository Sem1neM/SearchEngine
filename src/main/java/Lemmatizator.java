import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Lemmatizator {
    public static void main(String[] args) throws IOException{
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        String text = "Повторное появление леопарда в Осетии позволяет предположить, что леопард постоянно обитает в некоторых районах Северного Кавказа.";
        try {
            HashMap<String, Integer> words = getLemmaSet(text);

            words.entrySet().forEach(entry ->{
                System.out.println(entry.getKey() + '-' + entry.getValue());
            });

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static HashMap<String, Integer> getLemmaSet(String text) throws IOException {
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        text = text.toLowerCase();
        text = text.replaceAll("[,./;?!:\\'\\\"]", "");
        HashMap<String, Integer> collectLemmas = new HashMap<>();
        String[] words = text.split("\\s");

        for(String word : words) {
                List<String> wordBaseForm = luceneMorphology.getNormalForms(word);
                for (String lemma : wordBaseForm) {
                    if (isCorrectWord(lemma)) {
                        if (collectLemmas.containsKey(lemma)) {
                            collectLemmas.replace(lemma, collectLemmas.get(lemma) + 1);
                        } else {
                            collectLemmas.put(lemma, 1);
                        }
                    }
                }
        }
            return collectLemmas;
    }
    private static boolean isCorrectWord(String word) throws IOException {
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        AtomicBoolean status = new AtomicBoolean(true);
        luceneMorphology.getMorphInfo(word).forEach(lemma -> {
            if (lemma.contains("СОЮЗ") || lemma.contains("МЕЖД") || lemma.contains("ПРЕД") || lemma.contains("ЧАСТ")) {
            status.set(false);
            }
        });
        return status.get();

    }
}
