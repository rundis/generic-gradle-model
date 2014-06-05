package org.gradle.tooling.model.generic

import org.gradle.tooling.model.Model

public interface GenericModel extends Model {

    List<Map> getDependencies()
}