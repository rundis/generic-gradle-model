package org.gradle.tooling.model.generic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
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
        String nodeId (node) {
            "${node.name}:${node.group}:${node.version}"
        }

        private Map getDependencyInfo(ResolvedDependency dep) {
            [
                    name    : dep.moduleName,
                    group   : dep.moduleGroup,
                    version : dep.moduleVersion,
                    type    : "dependency",
                    children: dep.children.collect { getDependencyInfo(it) }.unique()
            ]
        }

        private Map getProjectDepInfo(ProjectDependency dep) {
            [
                name: dep.name,
                group: dep.group,
                version: dep.version,
                type: "project"
            ]
        }

        List<Map> collectNodeEntry(dep) {
            [dep.subMap(["name", "group", "version", "type"])] +
                dep.children.collect{collectNodeEntry(it)}
        }

        List<Map> collectEdge(aId, b) {
            [[a: aId, b: nodeId(b)]] + b.children.collect {collectEdge(nodeId(b), it)}
        }

        Map confDiGraph(Configuration conf) {
            def nodeTree = conf.allDependencies
                .findAll {it instanceof ProjectDependency}
                .collect {getProjectDepInfo(it as ProjectDependency)} +
                conf.resolvedConfiguration
                    .firstLevelModuleDependencies
                    .collect { getDependencyInfo(it) }


            def nodes = nodeTree.collect {collectNodeEntry(it)}.flatten().unique {nodeId(it)}

            def edges = nodeTree.collect {
                collectEdge(conf.name, it)
            }.flatten().unique()

            [nodes: nodes, edges: edges]
        }


        Map projectDeps(Project project) {
            [
                name: project.name,
                group: project.group,
                version: project.version,
                configurations: project.configurations.collectEntries{Configuration conf ->
                    [conf.name, confDiGraph(conf)]
                }
            ]
        }

        Map<String, List<String>> getClasspaths(Project project) {
            if(!project.hasProperty("sourceSets")) {return [:]}

            project.sourceSets.collectEntries {
                [it.name, it.runtimeClasspath.files.collect {it.path}]
            }
        }


        public boolean canBuild(String modelName) {
            modelName.equals(GenericModel.class.getName())
        }

        public Object buildAll(String modelName, Project project) {
            new DefaultGenericModel(
                rootDependencies: projectDeps(project),
                subprojectDependencies: project.subprojects.collect {projectDeps(it)},
                classpaths: getClasspaths(project))
        }
    }
}
