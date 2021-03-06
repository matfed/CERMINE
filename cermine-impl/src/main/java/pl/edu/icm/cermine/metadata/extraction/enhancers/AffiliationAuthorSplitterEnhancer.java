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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.tools.BxBoundsBuilder;

/**
 * @author krusek
 * @author Dominika Tkaczyk
 */
public class AffiliationAuthorSplitterEnhancer extends AbstractSimpleEnhancer {

    public AffiliationAuthorSplitterEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.MET_AFFILIATION));
    }

    private static final Set<String> keywords = Sets.newHashSet(
            "department", "departament", "universit", "institute", "school", "college", 
            "univ.", "instituto", "facultad", "universidad", "center", "labs"
            );
    
    @Override
    protected boolean enhanceMetadata(BxDocument document, DocumentMetadata metadata) {
        for (BxZone zone : document.getPages().get(0).getZones()) {
            if (BxZoneLabel.MET_AUTHOR.equals(zone.getLabel())) {
                return false;
            }
        }
        boolean inLine = false;
        for (BxZone zone1 : filterZones(document.getPages().get(0))) {
            for (BxZone zone2 : filterZones(document.getPages().get(0))) {
                if (!zone1.equals(zone2) && Math.abs(zone1.getY()-zone2.getY()) < 10) {
                    inLine = true;
                }
            }
        }
        if (inLine) {
            return false;
        }
        ReadingOrderResolver roResolver = new HierarchicalReadingOrderResolver();
        
        BxZone toDel = null;
        BxZone toAdd1 = null;
        BxZone toAdd2 = null;
        
        for (BxZone zone : filterZones(document.getPages().get(0))) {
            BxZone z1 = new BxZone();
            z1.setLabel(BxZoneLabel.MET_AUTHOR);
            BxBoundsBuilder b1 = new BxBoundsBuilder();
            BxZone z2 = new BxZone();
            z2.setLabel(BxZoneLabel.MET_AFFILIATION);
            BxBoundsBuilder b2 = new BxBoundsBuilder();
            boolean wasAff = false;
            BxLine prev = null;
            for (BxLine line : zone.getLines()) {
                String lineText = line.toText().toLowerCase();
                if (prev != null && 
                        (!prev.getMostPopularFontName().equals(line.getMostPopularFontName())
                        || prev.getHeight() - line.getHeight() > 1)) {
                    for (String keyword : keywords) {
                        if (lineText.contains(keyword)) {
                            wasAff = true;
                        }
                    }
                }
                if (wasAff) {
                    z2.addLine(line);
                    b2.expand(line.getBounds());
                } else {
                    z1.addLine(line);
                    b1.expand(line.getBounds());
                }
                prev = line;
            }
            z1.setBounds(b1.getBounds());
            z2.setBounds(b2.getBounds());
            if (!z1.getLines().isEmpty() && !z2.getLines().isEmpty()) {
                toDel = zone;
                toAdd1 = z1;
                toAdd2 = z2;
            }
        }
        
        if (toDel != null) {
            List<BxZone> list = new ArrayList<BxZone>();
            list.addAll(document.getPages().get(0).getZones());
            list.remove(toDel);
            list.add(toAdd1);
            list.add(toAdd2);
            document.getPages().get(0).setZones(list);
            try {
                roResolver.resolve(document);
            } catch (AnalysisException ex) {}
        }
                   
        return false;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.noneOf(EnhancedField.class);
    }

}
