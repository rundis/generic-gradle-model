package org.gradle.tooling.model.generic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.tooling.provider.model.ToolingModelBuilder
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry

import javax.inject.Inject

class GenericGradleModelPlugin implements Plugin<Project> {
    final ToolingModelBuilderRegistry registry;

    @Inject
    public GenericGradleModelPlugin(ToolingModelBuilderRegistry registry) {
        this.registry = registry;
    }

    @Override
    void apply(Project project) {
        registry.register(new CustomToolingModelBuilder())
    }


    private static class CustomToolingModelBuilder implements ToolingModelBuilder {
        private def getDependencyInfo(ResolvedDependency dep) {
            [
                    name    : dep.moduleName,
                    group   : dep.moduleGroup,
                    version : dep.moduleVersion,
                    children: dep.children.collect { getDependencyInfo(it) }.unique()
            ]
        }

        public boolean canBuild(String modelName) {
            modelName.equals(GenericModel.class.getName())
        }

        public Object buildAll(String modelName, Project project) {
            // Build the model...
            def deps = project.configurations.collect { Configuration conf ->
                [
                        configuration: conf.name,
                        dependencies : conf.resolvedConfiguration.firstLevelModuleDependencies.collect { getDependencyInfo(it) }
                ]
            }

            // return it with everything nicely serializable
            new DefaultGenericModel(dependencies: deps)
        }
    }
}
