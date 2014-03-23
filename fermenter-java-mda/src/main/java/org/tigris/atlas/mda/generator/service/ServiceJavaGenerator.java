package org.tigris.atlas.mda.generator.service;

import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.tigris.atlas.mda.element.java.JavaService;
import org.tigris.atlas.mda.generator.AbstractJavaGenerator;
import org.tigris.atlas.mda.generator.GenerationContext;
import org.tigris.atlas.mda.generator.GenerationException;
import org.tigris.atlas.mda.metadata.MetadataRepository;
import org.tigris.atlas.mda.metadata.element.Service;

/**
 *  Iterates throught service instances, passing {@link JavaService}s instance to the templates. 
 *
 */
public class ServiceJavaGenerator extends AbstractJavaGenerator {

	/**
	 * {@inheritDoc}
	 */
	public void generate(GenerationContext context) throws GenerationException {
		String applicationName = context.getArtifactId();
		Map<String, Service> services = MetadataRepository.getInstance().getAllServices(applicationName);
				
		JavaService javaService;
		VelocityContext vc;
		String fileName;
		String basefileName = context.getOutputFile();
		basefileName = replaceBasePackage(basefileName, context.getBasePackageAsPath());
		
		for (Service service : services.values()) {
			javaService = new JavaService(service);
			
			vc = new VelocityContext();
			vc.put("service", javaService);
			vc.put("basePackage", context.getBasePackage());
			vc.put("artifactId", context.getArtifactId());
			vc.put("version", context.getVersion());
						
			fileName = replaceServiceName(basefileName, service.getName());
			context.setOutputFile(fileName);
			
			generateFile(context, vc);
		}
	}

}
