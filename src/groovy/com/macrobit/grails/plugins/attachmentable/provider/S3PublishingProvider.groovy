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
		
		fileObject.acl = AccessControlList.REST_CANNED_PUBLIC_READ
		
		fileObject.contentType = attachment.contentType;
		
		fileObject.contentLength = file.length()
		
		fileObject.lastModifiedDate = new Date(file.lastModified());
		
		S3Bucket s3Bucket = s3Service.getBucket(Holders.config.grails.attachmentable.pusblishProvider.aws.bucketName);
		
		fileObject = s3Service.putObject(s3Bucket, fileObject);

		attachment.provider = getName();
		
		attachment.url =  s3Service.createUnsignedObjectUrl(s3Bucket.name, fileObject.key, false, false, false)
		
		file.delete();
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
	
	static main(args) {
		
		println "Start"
		
		String awsAccessKey = "AKIAI6WJ4KFAE4V3UMMA";
		
		String awsSecretKey = "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";
		
		AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);
		
		RestS3Service s3Service = new RestS3Service(awsCredentials)

		File file = new File('/home/emilio/Pictures/entrega4.png')
				
		S3Object fileObject = new S3Object(file);
	
		fileObject.acl = AccessControlList.REST_CANNED_PUBLIC_READ
		
		fileObject.contentType = 'image/jpeg';
		
		fileObject.contentLength = file.length()
		
		fileObject.lastModifiedDate = new Date(file.lastModified());
		
		S3Bucket s3Bucket = s3Service.getBucket('homes4trips');
		
		fileObject = s3Service.putObject(s3Bucket, fileObject);
		
		println fileObject.key;
		
		println fileObject.bucketName
		
		println fileObject
		
		println s3Service.createUnsignedObjectUrl(s3Bucket.name, fileObject.key, false, false, false)
		
		//s3Service.deleteObject(s3Bucket, fileObject.key)
	}
	
}
