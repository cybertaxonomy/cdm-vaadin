/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.registration;

import java.util.UUID;

import eu.etaxonomy.cdm.model.occurrence.DerivationEventType;

/**
 * @author a.kohlbecker
 * @since Jun 20, 2017
 *
 */
public class DerivationEventTypes {

    private static final UUID UUID_PUBLISHED_IMAGE = UUID.fromString("b8cba359-4202-4741-8ed8-4f17ae94b3e3");
    private static final UUID UUID_UNPUBLISHED_IMAGE = UUID.fromString("6cd5681f-0918-4ed6-89a8-bda1480dc890");

    private static DerivationEventType publishedImage = null;

    private static DerivationEventType unpublishedImage = null;

    public static DerivationEventType PUBLISHED_IMAGE() {
        if(publishedImage == null){
            publishedImage = DerivationEventType.NewInstance("Published image", "Published image", "PUBIMG");
            publishedImage.setUuid(UUID_PUBLISHED_IMAGE);
        }
        return publishedImage;
    }

    public static DerivationEventType UNPUBLISHED_IMAGE() {
        if(unpublishedImage == null){
            unpublishedImage = DerivationEventType.NewInstance("Unpublished image", "Unpublished image", "UNPUBIMG");
            unpublishedImage.setUuid(UUID_UNPUBLISHED_IMAGE);
        }
        return unpublishedImage;
    }
}
