/*
 * MoreMcmeta is a Minecraft mod expanding texture configuration capabilities.
 * Copyright (C) 2023 soir20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.moremcmeta.guiplugin;

import com.google.common.collect.ImmutableMap;
import io.github.moremcmeta.moremcmeta.api.client.metadata.AnalyzedMetadata;
import io.github.moremcmeta.moremcmeta.api.client.metadata.InvalidMetadataException;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the {@link GuiMetadataAnalyzer}.
 * @author soir20
 */
public final class GuiMetadataAnalyzerTest {
    private static final MetadataAnalyzer ANALYZER = new GuiMetadataAnalyzer();

    @Test
    public void analyze_MissingBlur_DefaultsEmpty() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "clamp", true
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);

        assertFalse(result.blur().isPresent());
    }

    @Test
    public void analyze_MissingClamp_DefaultsEmpty() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "blur", true
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);

        assertFalse(result.clamp().isPresent());
    }

    @Test
    public void analyze_TrueBlur_IsTrue() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "blur", true,
                "clamp", false
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);

        assertTrue(result.blur().orElseThrow());
    }

    @Test
    public void analyze_TrueClamp_IsTrue() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "blur", false,
                "clamp", true
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);

        assertTrue(result.clamp().orElseThrow());
    }

    @Test
    public void analyze_FalseBlur_IsFalse() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "blur", false,
                "clamp", true
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);

        assertFalse(result.blur().orElseThrow());
    }

    @Test
    public void analyze_FalseClamp_IsFalse() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "blur", true,
                "clamp", false
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);

        assertFalse(result.clamp().orElseThrow());
    }

}