package com.conan.router.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.add('router',RouterExtension)
        def android = project.extensions.getByName('android')
        def routerTransform = new RouterTransform(project)
        android.registerTransform(routerTransform)
    }
}
