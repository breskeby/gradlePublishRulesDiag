package org.acme.demo

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.ivy.IvyPublication
import org.gradle.api.publish.ivy.plugins.IvyPublishPlugin
import org.gradle.api.publish.ivy.tasks.PublishToIvyRepository
/**
 * Created by Rene on 05/05/14.
 */
class CustomPlugin implements Plugin<Project> {

    Project project

    @Override
    void apply(Project project) {
        this.project = project
        project.plugins.apply(IvyPublishPlugin)
        createRepository()
        project.extensions.add("providedInterfaces", project.container(ProvidedInterface))

        project.gradle.projectsEvaluated{
            addPublishProvidedInterfacesTask(project)
        }
    }

    def createRepository() {
        project.repositories{
            ivy{
                name = "localProvidedInterfaces"
                url = "file:///Users/Rene/ivy"
            }
        }
    }
/**
     * Adds the publishProvidedInterfaces task to the given project.
     *
     * @param project is used to add the publishProvidedInterfaces task
     */
    void addPublishProvidedInterfacesTask(Project project) {
        project.task("publishProvidedInterfaces")
        project.providedInterfaces.all { provided ->

            println "providedInterface: $provided"
            def providedPublication = project.publishing.publications.create("ivy_${provided.name}", IvyPublication)

            //providedPublication.revision = provided.version
            providedPublication.module = provided.name
            providedPublication.descriptor.withXml {
                provided.requiredInterfaceList.each {
                    asNode().dependencies[0].appendNode("dependency", [org: project.group, name: it.name, rev: it.version])
                }
            }

            PublishToIvyRepository publishTask = project.tasks.create("publish_${provided.name}", PublishToIvyRepository)
            publishTask.repository = project.repositories["localProvidedInterfaces"]
            publishTask.publication = providedPublication
            publishTask.publication.descriptor

            project.publishProvidedInterfaces.dependsOn publishTask
        }
    }

}