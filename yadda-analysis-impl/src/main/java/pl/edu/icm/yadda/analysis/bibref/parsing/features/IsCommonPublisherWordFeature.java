package pl.edu.icm.yadda.analysis.bibref.parsing.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.yadda.analysis.hmm.features.FeatureCalculator;
/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsCommonPublisherWordFeature implements FeatureCalculator<CitationToken, Citation> {

    private static String featureName = "IsCommonPublisherWord";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    private static List<String> keywords = Arrays.asList(
            "academic",
            "birkhäuser",
            "cambridge", "company",
            "dunod",
            "france",
            "gauthier",
            "hermann", "holland",
            "interscience",
            "john",
            "masson", "math",
            "north", "nostrand",
            "paris", "polytechnique", "press", "princeton", "publ", "publishers",
            "sons", "springer",
            "univ", "université", "university",
            "verlag", "villars",
            "wiley", "world"
            );

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (keywords.contains(object.getText().toLowerCase())) ? 1 : 0;
    }
}