rootProject.name = "dev-practice-commerce"

include(
    "core:core-enum",
    "core:core-api",
    "storage:db-core",
    "support:logging",
    "support:monitoring",
)

pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.springframework.boot" -> useVersion(springBootVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
            }
        }
    }
}
