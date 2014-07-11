package com.macrobit.grails.plugins.attachmentable.provider

import grails.util.Holders

import org.springframework.context.ApplicationContext

import com.macrobit.grails.plugins.attachmentable.core.DownloaderProvider
import com.macrobit.grails.plugins.attachmentable.domains.Attachment

class AuthDownloaderProvider implements DownloaderProvider {

	@Override
	public String getName() {
		return "Auth";
	}

	@Override
	public boolean isAuthorized(Attachment attachment) {
		
		try {
						
			ApplicationContext ctx = Holders.grailsApplication.mainContext
			
			boolean auth = ctx.getBean('springSecurityService').loggedIn;
						
			return auth;
		
		}
		catch(Exception exception) {
			exception.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean download(Attachment attachment, File file) {
		return true;
	}
	

}
