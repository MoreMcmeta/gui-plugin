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
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataAnalyzer;
import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;

import java.util.Optional;

/**
 * Reads blur and clamp information from the given metadata.
 * @author soir20
 */
public final class GuiMetadataAnalyzer implements MetadataAnalyzer {
    @Override
    public AnalyzedMetadata analyze(MetadataView metadata, int imageWidth, int imageHeight) {
        return new AnalyzedMetadata() {
            @Override
            public Optional<Boolean> blur() {
                return metadata.booleanValue("blur");
            }

            @Override
            public Optional<Boolean> clamp() {
                return metadata.booleanValue("clamp");
            }
        };
    }
}
