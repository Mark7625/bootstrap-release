package com.mark.bootstrap

import com.mark.bootstrap.utils.Keys
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import java.io.File

class BootstrapPlugin : Plugin<Project> {
    override fun apply(project: Project) : Unit = with(project) {
        val extension = project.extensions.create<BootstrapPluginExtension>("releaseSettings")

        val bootstrapDependencies by configurations.creating {
            isCanBeConsumed = false
            isCanBeResolved = true
            isTransitive = false
        }

        project.task("publishClient") {
            this.group = "client update"
            this.description = "Publishes Client to your ftp or aws"
            dependsOn("release")

            doLast {
                BootstrapTask(extension, project).init()
            }
        }

        project.task("generateKeys") {
            this.group = "client update"
            this.description = "Generates the Security Keys for the Client"
            doLast {
                val saveLocations = File("${System.getProperty("user.home")}/.gradle/releaseClient/${project.name}/")
                Keys.generateKeys(saveLocations)
            }
        }

        project.task("release") {
            this.group = "client update"
            this.description = "Generates Client Files for Uploading"
            dependsOn(bootstrapDependencies)
            dependsOn("jar")

            doLast {
                File("${buildDir}/bootstrap/").listFiles()?.forEach { it.delete() }
                BootstrapTask(extension, project).init(false)

            }
        }

    }
}