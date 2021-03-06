/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.metadata.zoneclassification;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.DocumentsExtractor;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.general.ZipExtractor;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class SVMZoneClassifierDemo {

	protected static final String hmmTrainingFile = "/pl/edu/icm/cermine/tools/classification/svm/xmls.zip";
	private static final String hmmTestFile = "/pl/edu/icm/cermine/tools/classification/svm/test.xml";
    
	public static BxDocument getTestFile() throws TransformationException, AnalysisException {
        InputStream is = SVMZoneClassifierDemo.class.getResourceAsStream(hmmTestFile);
        InputStreamReader isr = new InputStreamReader(is);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        List<BxPage> pages = reader.read(isr);
        BxDocument testDocument = new BxDocument().setPages(pages);
        return testDocument;
	}
  
    public static void main(String[] args) throws Exception {
        
        // 1.1 construct vector of features builder
    	List<FeatureCalculator<BxZone, BxPage>> featureCalculators = Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
        		new AbstractFeature(),
        		new AcknowledgementFeature(),
        		new AffiliationFeature(),
                new AtCountFeature(),
                new AtRelativeCountFeature(),
                new AuthorFeature(),
                new BibinfoFeature(),
        		new BracketRelativeCount(),
        		new BracketedLineRelativeCount(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
        		new ContainsCuePhrasesFeature(),
        		new CuePhrasesRelativeCountFeature(),
        		new DateFeature(),
                new DigitCountFeature(),
                new DigitRelativeCountFeature(),
        		new DistanceFromNearestNeighbourFeature(),
        		new DotCountFeature(),
        		new DotRelativeCountFeature(),
                new EmptySpaceRelativeFeature(),
        		new FontHeightMeanFeature(),
        		new FigureFeature(),
        		new FreeSpaceWithinZoneFeature(),
                new HeightFeature(),
                new HeightRelativeFeature(),
        		new HorizontalRelativeProminenceFeature(),
        		new IsFirstPageFeature(),
        		new IsFontBiggerThanNeighboursFeature(),
        		new IsHighestOnThePageFeature(),
        		new IsLastPageFeature(),
        		new IsLowestOnThePageFeature(),
        		new IsItemizeFeature(),
                new KeywordsFeature(),
                new LineCountFeature(),
                new LineRelativeCountFeature(),
                new LineHeightMeanFeature(),
                new LineWidthMeanFeature(),
                new LineXPositionMeanFeature(),
                new LineXPositionDiffFeature(),
                new LineXWidthPositionDiffFeature(),
                new LetterCountFeature(),
                new LetterRelativeCountFeature(),
                new LowercaseCountFeature(),
                new LowercaseRelativeCountFeature(),
                new ContainsPageNumberFeature(),
                new ProportionsFeature(),
                new PunctuationRelativeCountFeature(),
                new ReferencesFeature(),
                new ReferencesTitleFeature(),
                new StartsWithDigitFeature(),
                new UppercaseCountFeature(),
                new UppercaseRelativeCountFeature(),
                new UppercaseWordCountFeature(),
                new UppercaseWordRelativeCountFeature(),
        		new VerticalProminenceFeature(),
                new WidthFeature(),
                new WordCountFeature(),
                new WordCountRelativeFeature(),
        		new WordWidthMeanFeature(),
        		new WordLengthMeanFeature(),
        		new WordLengthMedianFeature(),
        		new WhitespaceCountFeature(),
        		new WhitespaceRelativeCountLogFeature(),
                new WidthRelativeFeature(),
                new XPositionFeature(),
                new XPositionRelativeFeature(),
                new YPositionFeature(),
                new YPositionRelativeFeature(),
                new YearFeature()
                );
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new FeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(featureCalculators);

        /* import training documents */
        DocumentsExtractor extractor = new ZipExtractor(hmmTrainingFile);
        List<BxDocument> trainingList = extractor.getDocuments();
        
        /* open test file */
        //BxDocument testDocument = getTestFile();
        //Random randomGenerator = new Random();
        int testDocIdx = 41;//randomGenerator.nextInt(trainingList.size());
        BxDocument testDocument = trainingList.get(testDocIdx);

        /* generate training set based on sequences and vector of features */
        List<TrainingSample<BxZoneLabel>> trainingSamplesUnrevised = 
                BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(trainingList, vectorBuilder, BxZoneLabel.getLabelToGeneralMap());
        
        Map<BxZoneLabel, Integer> labelCount = new EnumMap<BxZoneLabel, Integer>(BxZoneLabel.class);
        labelCount.put(BxZoneLabel.GEN_BODY, 0);
        labelCount.put(BxZoneLabel.GEN_METADATA, 0);
        labelCount.put(BxZoneLabel.GEN_OTHER, 0);
        labelCount.put(BxZoneLabel.GEN_REFERENCES, 0);
        
        for(TrainingSample<BxZoneLabel> elem: trainingSamplesUnrevised) {
        	labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        }
        
        Integer max = Integer.MAX_VALUE;
        for(Entry<BxZoneLabel, Integer> entry: labelCount.entrySet()) {
        	if(entry.getValue() < max) {
        		max = entry.getValue();
            }
        	System.out.println(entry.getKey() + " " + entry.getValue());
        }
        
        labelCount.put(BxZoneLabel.GEN_BODY, 0);
        labelCount.put(BxZoneLabel.GEN_METADATA, 0);
        labelCount.put(BxZoneLabel.GEN_OTHER, 0);
        labelCount.put(BxZoneLabel.GEN_REFERENCES, 0);
        List<TrainingSample<BxZoneLabel>> trainingSamples = new ArrayList<TrainingSample<BxZoneLabel>>();
        
        for(TrainingSample<BxZoneLabel> elem: trainingSamplesUnrevised) {
        	if(labelCount.get(elem.getLabel()) < max*1.3) {
        		trainingSamples.add(elem);
        		labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        	}
        }
        System.out.println(max);
        System.out.println(trainingSamples.size());

        List<BxDocument> testList = new ArrayList<BxDocument>(1);
        testList.add(testDocument);
        List<TrainingSample<BxZoneLabel>> testElement = 
                BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(testList, vectorBuilder, BxZoneLabel.getLabelToGeneralMap());

        /* build a classifier */
        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(vectorBuilder);
        zoneClassifier.buildClassifier(trainingSamples);
        
        /* classify zones from the test file */
        zoneClassifier.classifyZones(testDocument);
        assert testDocument.asZones().size() == testList.size();
        for(int zoneIdx=0; zoneIdx < testDocument.asZones().size(); ++zoneIdx) {
        	System.out.println("Recognized label: " + testDocument.asZones().get(zoneIdx).getLabel() + "[" + testElement.get(zoneIdx).getLabel() + "]");
        	System.out.println(testDocument.asZones().get(zoneIdx).toText());
        }
    }
}
