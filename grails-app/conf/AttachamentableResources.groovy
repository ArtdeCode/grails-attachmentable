modules = {
   	'jquery-multifile' {
		dependsOn 'jquery'
		resource url:[dir:'js/jquery-multifile', file:"jquery.form.js"], nominify: true
		resource url:[dir:'js/jquery-multifile', file:"jquery.MultiFile.js"], nominify: true
	}

}
