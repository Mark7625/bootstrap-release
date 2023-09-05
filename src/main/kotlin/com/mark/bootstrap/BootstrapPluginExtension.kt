package com.mark.bootstrap

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.File

interface BootstrapPluginExtension {
    val uploadType: Property<UploadType>
    val buildType: Property<String>
    val customRepo: Property<String>
    val passiveMode: Property<Boolean>
    val externalLibs : ListProperty<File>
}