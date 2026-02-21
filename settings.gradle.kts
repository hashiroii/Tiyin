pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tiyin"
include(":app")
include(":core:data")
include(":core:domain")
include(":feature:home")
include(":core:designsystem")
include(":core:ui")
include(":core:navigation")
include(":feature:profile")
include(":feature:settings")
include(":feature:groups")
include(":feature:analytics")
include(":feature:subscription-manager")
include(":feature:auth")
