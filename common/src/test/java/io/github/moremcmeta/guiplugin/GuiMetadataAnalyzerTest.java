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
import io.github.moremcmeta.moremcmeta.api.client.metadata.GuiScaling;
import io.github.moremcmeta.moremcmeta.api.client.metadata.InvalidMetadataException;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Tests the {@link GuiMetadataAnalyzer}.
 * @author soir20
 */
public final class GuiMetadataAnalyzerTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    private static final MetadataAnalyzer ANALYZER = new GuiMetadataAnalyzer();

    @Test
    public void analyze_MissingScaling_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of());

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_UnknownType_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "unknown",
                        "width", 10,
                        "height", 10
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_StretchNoWidthNoHeight_HasStretchType() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "stretch"
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.Stretch(), result.guiScaling().get());
        assertFalse(result.frameWidth().isPresent());
        assertFalse(result.frameHeight().isPresent());
    }

    @Test
    public void analyze_StretchHasWidthHasHeight_HasStretchType() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "stretch",
                        "width", 10,
                        "height", 10
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.Stretch(), result.guiScaling().get());
        assertFalse(result.frameWidth().isPresent());
        assertFalse(result.frameHeight().isPresent());
    }

    @Test
    public void analyze_TileNoWidthHasHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "tile",
                        "height", 20
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_TileHasWidthNoHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "tile",
                        "width", 10
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_TileNegativeWidthHasHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "tile",
                        "width", -2,
                        "height", 10
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_TileHasWidthNegativeHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "tile",
                        "width", 10,
                        "height", -2
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_TileZeroWidthHasHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "tile",
                        "width", 0,
                        "height", 10
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_TileHasWidthZeroHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "tile",
                        "width", 10,
                        "height", 0
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_TileHasWidthHasHeight_HasTileTypeAndFrameSize() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "tile",
                        "width", 10,
                        "height", 20
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.Tile(), result.guiScaling().get());
        assertEquals(10, (int) result.frameWidth().get());
        assertEquals(20, (int) result.frameHeight().get());
    }

    @Test
    public void analyze_NineSliceNoWidthHasHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "height", 20,
                        "border", 1
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceHasWidthNoHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "border", 1
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceNegativeWidthHasHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", -2,
                        "height", 20,
                        "border", 1
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceHasWidthNegativeHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", -2,
                        "border", 1
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceZeroWidthHasHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 0,
                        "height", 20,
                        "border", 1
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceHasWidthZeroHeight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 0,
                        "border", 1
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceNoBorder_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 10
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceNegativeBorder_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", -1
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceZeroBorder_HasZeroBorder() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", 0
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.NineSlice(0, 0, 0, 0), result.guiScaling().get());
        assertEquals(10, (int) result.frameWidth().get());
        assertEquals(20, (int) result.frameHeight().get());
    }

    @Test
    public void analyze_NineSlicePositiveBorder_HasPositiveBorder() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", 3
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.NineSlice(3, 3, 3, 3), result.guiScaling().get());
        assertEquals(10, (int) result.frameWidth().get());
        assertEquals(20, (int) result.frameHeight().get());
    }

    @Test
    public void analyze_NineSliceMissingLeft_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "right", 2,
                                "top", 3,
                                "bottom", 4
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceNegativeLeft_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", -1,
                                "right", 2,
                                "top", 3,
                                "bottom", 4
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceZeroLeft_HasLeft() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 0,
                                "right", 2,
                                "top", 3,
                                "bottom", 4
                        ))
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.NineSlice(0, 2, 3, 4), result.guiScaling().get());
        assertEquals(10, (int) result.frameWidth().get());
        assertEquals(20, (int) result.frameHeight().get());
    }

    @Test
    public void analyze_NineSliceMissingRight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "top", 3,
                                "bottom", 4
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceNegativeRight_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", -2,
                                "top", 3,
                                "bottom", 4
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceZeroRight_HasRight() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", 0,
                                "top", 3,
                                "bottom", 4
                        ))
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.NineSlice(1, 0, 3, 4), result.guiScaling().get());
        assertEquals(10, (int) result.frameWidth().get());
        assertEquals(20, (int) result.frameHeight().get());
    }

    @Test
    public void analyze_NineSliceMissingTop_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", 2,
                                "bottom", 4
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceNegativeTop_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", 2,
                                "top", -3,
                                "bottom", 4
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceZeroTop_HasTop() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", 2,
                                "top", 0,
                                "bottom", 4
                        ))
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.NineSlice(1, 2, 0, 4), result.guiScaling().get());
        assertEquals(10, (int) result.frameWidth().get());
        assertEquals(20, (int) result.frameHeight().get());
    }

    @Test
    public void analyze_NineSliceMissingBottom_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", 2,
                                "top", 3
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceNegativeBottom_InvalidMetadataException() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", 2,
                                "top", 3,
                                "bottom", -4
                        ))
                ))
        ));

        expectedException.expect(InvalidMetadataException.class);
        ANALYZER.analyze(metadata, 100, 100);
    }

    @Test
    public void analyze_NineSliceZeroBottom_HasBottom() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", 2,
                                "top", 3,
                                "bottom", 0
                        ))
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.NineSlice(1, 2, 3, 0), result.guiScaling().get());
        assertEquals(10, (int) result.frameWidth().get());
        assertEquals(20, (int) result.frameHeight().get());
    }

    @Test
    public void analyze_NineSliceAllPositive_HasAll() throws InvalidMetadataException {
        MetadataView metadata = new MockMetadataView(ImmutableMap.of(
                "scaling", new MockMetadataView(ImmutableMap.of(
                        "type", "nine_slice",
                        "width", 10,
                        "height", 20,
                        "border", new MockMetadataView(ImmutableMap.of(
                                "left", 1,
                                "right", 2,
                                "top", 3,
                                "bottom", 4
                        ))
                ))
        ));

        AnalyzedMetadata result = ANALYZER.analyze(metadata, 100, 100);
        assertEquals(new GuiScaling.NineSlice(1, 2, 3, 4), result.guiScaling().get());
        assertEquals(10, (int) result.frameWidth().get());
        assertEquals(20, (int) result.frameHeight().get());
    }

}