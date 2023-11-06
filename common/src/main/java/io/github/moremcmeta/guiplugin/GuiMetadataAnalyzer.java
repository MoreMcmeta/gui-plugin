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

import io.github.moremcmeta.moremcmeta.api.client.metadata.AnalyzedMetadata;
import io.github.moremcmeta.moremcmeta.api.client.metadata.GuiScaling;
import io.github.moremcmeta.moremcmeta.api.client.metadata.InvalidMetadataException;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;

import java.util.Optional;

/**
 * Reads blur and clamp information from the given metadata.
 * @author soir20
 */
public final class GuiMetadataAnalyzer implements MetadataAnalyzer {
    @Override
    public AnalyzedMetadata analyze(MetadataView metadata, int imageWidth, int imageHeight) throws InvalidMetadataException {
        String scalingSectionName = "scaling";
        MetadataView scalingSection = metadata.subView(scalingSectionName)
                .orElseThrow(() -> new InvalidMetadataException("Missing scaling section"));
        String rawScaling = scalingSection.stringValue("type")
                .orElseThrow(() -> new InvalidMetadataException("Missing type field in scaling section"));

        GuiScaling scaling;
        Optional<Integer> frameWidth;
        Optional<Integer> frameHeight;

        if ("stretch".equals(rawScaling)) {
            scaling = new GuiScaling.Stretch();
            frameWidth = Optional.empty();
            frameHeight = Optional.empty();
        } else {
            frameWidth = scalingSection.integerValue("width");
            frameHeight = scalingSection.integerValue("height");

            if (frameWidth.isEmpty()) {
                throw new InvalidMetadataException("Missing width field in scaling section");
            }
            if (frameWidth.get() <= 0) {
                throw new InvalidMetadataException("Frame width must be positive");
            }

            if (frameHeight.isEmpty()) {
                throw new InvalidMetadataException("Missing height field in scaling section");
            }
            if (frameHeight.get() <= 0) {
                throw new InvalidMetadataException("Frame height must be positive");
            }

            if ("tile".equals(rawScaling)) {
                scaling = new GuiScaling.Tile();
            } else if ("nine_slice".equals(rawScaling)) {
                int left;
                int right;
                int top;
                int bottom;

                String borderSectionName = "border";
                if (scalingSection.subView(borderSectionName).isPresent()) {
                    MetadataView borderSection = scalingSection.subView(borderSectionName).get();
                    left = requireNonNegative(borderSection, "left", borderSectionName);
                    right = requireNonNegative(borderSection, "right", borderSectionName);
                    top = requireNonNegative(borderSection, "top", borderSectionName);
                    bottom = requireNonNegative(borderSection, "bottom", borderSectionName);
                } else {
                    int borderSize = requireNonNegative(scalingSection, "border", scalingSectionName);
                    left = borderSize;
                    right = borderSize;
                    top = borderSize;
                    bottom = borderSize;
                }

                scaling = new GuiScaling.NineSlice(left, right, top, bottom);
            } else {
                throw new InvalidMetadataException("Unknown scaling type " + rawScaling);
            }
        }

        return new AnalyzedMetadata() {
            @Override
            public Optional<Integer> frameWidth() {
                return frameWidth;
            }

            @Override
            public Optional<Integer> frameHeight() {
                return frameHeight;
            }

            @Override
            public Optional<GuiScaling> guiScaling() {
                return Optional.of(scaling);
            }
        };
    }

    /**
     * Retrieves an integer value from a metadata section that must be present and non-negative.
     * @param section       section to retrieve the value from
     * @param key           key of the integer value to retrieve
     * @param sectionName   name of the section containing the value
     * @return the value, if present and non-negative
     * @throws InvalidMetadataException  if the value is missing or negative
     */
    private int requireNonNegative(MetadataView section, String key, String sectionName) throws InvalidMetadataException {
        int value  = section.integerValue(key)
                .orElseThrow(() -> new InvalidMetadataException(
                        String.format("Missing %s field in %s section", key, sectionName)
                ));

        if (value < 0) {
            throw new InvalidMetadataException(String.format("%s is negative", key));
        }

        return value;
    }

}
