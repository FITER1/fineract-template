/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.documentmanagement.service;

import java.io.InputStream;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.domain.Base64EncodedImage;
import org.apache.fineract.infrastructure.documentmanagement.api.ImagesApiResource.EntityTypeForImages;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.domain.Image;
import org.apache.fineract.infrastructure.documentmanagement.domain.ImageRepository;
import org.apache.fineract.infrastructure.documentmanagement.domain.StorageType;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.staff.domain.StaffRepositoryWrapper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.settings.appimage.domain.AppImage;
import org.apache.fineract.settings.appimage.domain.AppImagesRepository;
import org.apache.fineract.settings.appimage.exception.AppImagesNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageWritePlatformServiceJpaRepositoryImpl implements ImageWritePlatformService {

    private final ContentRepositoryFactory contentRepositoryFactory;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final ImageRepository imageRepository;
    private final StaffRepositoryWrapper staffRepositoryWrapper;
    private final AppImagesRepository appImagesRepository;

    @Autowired
    public ImageWritePlatformServiceJpaRepositoryImpl(final ContentRepositoryFactory documentStoreFactory,
                                                      final ClientRepositoryWrapper clientRepositoryWrapper, final ImageRepository imageRepository,
                                                      StaffRepositoryWrapper staffRepositoryWrapper, AppImagesRepository appImagesRepository) {
        this.contentRepositoryFactory = documentStoreFactory;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.imageRepository = imageRepository;
        this.staffRepositoryWrapper = staffRepositoryWrapper;
        this.appImagesRepository = appImagesRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(String entityName, final Long clientId, final String imageName,
                                                     final InputStream inputStream, final Long fileSize) {
        Object owner = deletePreviousImage(entityName, clientId);

        final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
        final String imageLocation = contentRepository.saveImage(inputStream, clientId, imageName, fileSize, entityName);
        return updateImage(owner, imageLocation, contentRepository.getStorageType());
    }

    @Transactional
    @Override
    public CommandProcessingResult saveOrUpdateImage(String entityName, final Long clientId, final Base64EncodedImage encodedImage) {
        Object owner = deletePreviousImage(entityName, clientId);

        final ContentRepository contenRepository = this.contentRepositoryFactory.getRepository();
        final String imageLocation = contenRepository.saveImage(encodedImage, clientId, "image", entityName);

        return updateImage(owner, imageLocation, contenRepository.getStorageType());
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteImage(String entityName, final Long clientId) {
        Object owner = null;
        Image image = null;
        if (EntityTypeForImages.CLIENTS.toString().equals(entityName)) {
            owner = this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            Client client = (Client) owner;
            image = client.getImage();
            client.setImage(null);
            this.clientRepositoryWrapper.save(client);

        } else if (EntityTypeForImages.STAFF.toString().equals(entityName)) {
            owner = this.staffRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            Staff staff = (Staff) owner;
            image = staff.getImage();
            staff.setImage(null);
            this.staffRepositoryWrapper.save(staff);

        } else if (EntityTypeForImages.APP.toString().equals(entityName)) {
            owner = this.appImagesRepository.findById(clientId).orElseThrow(() -> new AppImagesNotFoundException(clientId));
            AppImage appImages = (AppImage) owner;
            image = appImages.getImage();
            appImages.setImage(null);
            this.appImagesRepository.save(appImages);

        }
        // delete image from the file system
        if (image != null) {
            final ContentRepository contentRepository = this.contentRepositoryFactory
                    .getRepository(StorageType.fromInt(image.getStorageType()));
            contentRepository.deleteImage(image.getLocation());
            this.imageRepository.delete(image);
        }

        return CommandProcessingResult.resourceResult(clientId);
    }

    /**
     * @param entityName
     * @param entityId
     * @return
     */
    private Object deletePreviousImage(String entityName, final Long entityId) {
        Object owner = null;
        Image image = null;
        if (EntityTypeForImages.CLIENTS.toString().equals(entityName)) {
            Client client = this.clientRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            image = client.getImage();
            owner = client;
        } else if (EntityTypeForImages.STAFF.toString().equals(entityName)) {
            Staff staff = this.staffRepositoryWrapper.findOneWithNotFoundDetection(entityId);
            image = staff.getImage();
            owner = staff;
        }
        else if (EntityTypeForImages.APP.toString().equals(entityName)) {
            AppImage appImages = this.appImagesRepository.findById(entityId).orElseThrow(() -> new AppImagesNotFoundException(entityId));
            image = appImages.getImage();
            owner = appImages;
        }
        if (image != null) {
            final ContentRepository contentRepository = this.contentRepositoryFactory
                    .getRepository(StorageType.fromInt(image.getStorageType()));
            contentRepository.deleteImage(image.getLocation());
        }
        return owner;
    }

    private CommandProcessingResult updateImage(final Object owner, final String imageLocation, final StorageType storageType) {
        Image image = null;
        Long clientId = null;
        if (owner instanceof Client client) {
            image = client.getImage();
            clientId = client.getId();
            image = createImage(image, imageLocation, storageType);
            client.setImage(image);
            this.clientRepositoryWrapper.save(client);
        } else if (owner instanceof Staff staff) {
            image = staff.getImage();
            clientId = staff.getId();
            image = createImage(image, imageLocation, storageType);
            staff.setImage(image);
            this.staffRepositoryWrapper.save(staff);
        } else if (owner instanceof AppImage appImages) {
            image = appImages.getImage();
            clientId = appImages.getId();
            image = createImage(image, imageLocation, storageType);
            appImages.setImage(image);
            this.appImagesRepository.save(appImages);
        }

        if (image != null) {
            this.imageRepository.save(image);
        }
        return CommandProcessingResult.resourceResult(clientId);
    }

    private Image createImage(Image image, final String imageLocation, final StorageType storageType) {
        if (image == null) {
            image = new Image(imageLocation, storageType);
        } else {
            image.setLocation(imageLocation);
            image.setStorageType(storageType.getValue());
        }
        return image;
    }

}