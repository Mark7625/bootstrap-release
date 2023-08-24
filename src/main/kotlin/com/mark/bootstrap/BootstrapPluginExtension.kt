package com.mark.bootstrap

import org.gradle.api.provider.Property

interface BootstrapPluginExtension {
    val uploadType: Property<UploadType>
    val buildType: Property<String>
    val customRepo: Property<String>
    val passiveMode: Property<Boolean>
}