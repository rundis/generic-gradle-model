package org.gradle.tooling.model.generic

import org.gradle.tooling.model.Model

public interface GenericModel extends Model {

    Map getRootDependencies()

    List<Map> getSubprojectDependencies()


    Map<String, List<String>> getClasspaths()
}