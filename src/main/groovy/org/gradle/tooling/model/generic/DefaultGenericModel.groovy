package org.gradle.tooling.model.generic

import org.gradle.api.Project

class DefaultGenericModel implements Serializable, GenericModel{
    List<Map> dependencies

    @Override
    List<Map> getDependencies() {
        return dependencies
    }
}
