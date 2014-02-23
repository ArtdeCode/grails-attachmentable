package com.macrobit.grails.plugins.attachmentable.provider

import grails.util.Holders;

import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.jets3t.service.S3Service
import org.jets3t.service.acl.AccessControlList
import org.jets3t.service.impl.rest.httpclient.RestS3Service
import org.jets3t.service.model.S3Bucket
import org.jets3t.service.model.S3Object
import org.jets3t.service.security.AWSCredentials

import com.macrobit.grails.plugins.attachmentable.core.PublishingProvider
import com.macrobit.grails.plugins.attachmentable.domains.Attachment

class S3PublishingProvider implements PublishingProvider {

	@Override
	public String getName() {
		return 's3';
	}

	@Override
	public void publish(Attachment attachment, File file) {
		
		S3Service s3Service = getS3Service();
		
		S3Object fileObject = new S3Object(file);
		
		def acl = Holders.config.grails.attachmentable.pusblishProvider.aws.acl
		
		fileObject.acl = (acl) ? AccessControlList."${acl}" : AccessControlList.REST_CANNED_PUBLIC_READ
		
		fileObject.contentType = attachment.contentType;
		
		fileObject.contentLength = file.length()
		
		fileObject.lastModifiedDate = new Date(file.lastModified());
		
		S3Bucket s3Bucket = s3Service.getBucket(Holders.config.grails.attachmentable.pusblishProvider.aws.bucketName);
		
		fileObject = s3Service.putObject(s3Bucket, fileObject);

		attachment.provider = getName();
		
		attachment.url =  s3Service.createUnsignedObjectUrl(s3Bucket.name, fileObject.key, false, false, false)
		
		if (Holders.config.grails.attachmentable.pusblishProvider.removeLocal) {
			file.delete();
		}
		
	}

	private S3Service getS3Service() {
		
		def config = ConfigurationHolder.config
		
		String awsAccessKey = config.grails.attachmentable.pusblishProvider.aws.accessKey;

		String awsSecretKey = config.grails.attachmentable.pusblishProvider.aws.secretKey;
		
		AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);

		S3Service s3Service = new RestS3Service(awsCredentials)
		
		return s3Service
	}

	@Override
	public void unpublish(Attachment attachment) {

		S3Service s3Service = getS3Service();
		
		S3Bucket s3Bucket = s3Service.getBucket(Holders.config.grails.attachmentable.pusblishProvider.aws.bucketName);
		
		s3Service.deleteObject(s3Bucket, attachment.name)
				
	}	
}
