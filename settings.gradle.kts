enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        maven {
            url = uri("https://inexus.samentic.com/repository/samentic-android/")
            credentials {
                username = "vira"
                password = "w-!Mze&LY8MVEMG"
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://inexus.samentic.com/repository/samentic-android/")
            credentials {
                username = "vira"
                password = "w-!Mze&LY8MVEMG"
            }
        }
    }
}
rootProject.name = "ViraApp"
include(":app")
include(":design-system:pager")
include(":design-system:bottomsheet")
include(":design-system:components")
include(":design-system:theme")