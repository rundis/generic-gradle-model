package org.gradle.tooling.model.generic

class DefaultGenericModel implements Serializable, GenericModel{
    Map rootDependencies
    List<Map> subprojectDependencies
    Map<String, List<String>> classpaths
}
